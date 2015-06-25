/*
 * Direitos Autorais Reservados (c) 2011 Juno Roesler
 * Contato: juno.rr@gmail.com
 * 
 * Esta biblioteca é software livre; você pode redistribuí-la e/ou modificá-la sob os
 * termos da Licença Pública Geral Menor do GNU conforme publicada pela Free
 * Software Foundation; tanto a versão 2.1 da Licença, ou qualquer
 * versão posterior.
 * 
 * Esta biblioteca é distribuída na expectativa de que seja útil, porém, SEM
 * NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE
 * OU ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA. Consulte a Licença Pública
 * Geral Menor do GNU para mais detalhes.
 * 
 * Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto
 * com esta biblioteca; se não, acesse 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html, 
 * ou escreva para a Free Software Foundation, Inc., no
 * endereço 59 Temple Street, Suite 330, Boston, MA 02111-1307 USA.
 */

package us.pserver.revok.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketAddress;
import static us.pserver.chk.Checker.nullarg;
import us.pserver.log.Log;
import us.pserver.log.LogFactory;
import us.pserver.revok.MethodChain;
import us.pserver.revok.MethodInvocationException;
import us.pserver.revok.OpResult;
import us.pserver.revok.RemoteMethod;
import us.pserver.revok.channel.Channel;
import us.pserver.revok.container.AuthenticationException;
import us.pserver.revok.container.ObjectContainer;
import us.pserver.revok.reflect.Invoker;

/**
 * <code>RunnableConnectionHandler</code> handle client requests 
 * for remote methods invocation.
 * 
 * @author Juno Roesler - juno@pserver.com
 * @version 1.1 - 201506
 */
public class RunnableConnectionHandler implements Runnable {
  
  /**
   * <code>
   *  READ_ERROR = "Invalid length readed"
   * </code><br>
   * Message for channel reading errors.
   */
  public static final String READ_ERROR = "Invalid length readed";
  
  /**
   * <code>
   *  CONN_RESET = "Connection reset"
    * </code><br>
   * Message for connection reset errors.
   */
  public static final String CONN_RESET = "Connection reset";
  
  private SocketAddress client;
  
  private Channel channel;
  
  private ObjectContainer container;
  
  private Log log;
  
  private boolean closed;
  
  
  /**
   * Default construtor receives the network communication
   * channel and the server Http connection 
   * <code>HttpServerConnection</code>.
   * @param ch The network communication channel.
   * <code>HttpServerConnection</code>.
   * @param cont The <code>ObjectContainer</code> that holds the objects.
   */
  public RunnableConnectionHandler(Channel ch, SocketAddress cli, ObjectContainer cont) {
    if(ch == null)
      throw new IllegalArgumentException(
          "[HttpConnectionHandler()] "
              + "Invalid Channel {"+ ch+ "}");
    
    if(cont == null)
      throw new IllegalArgumentException(
          "[HttpConnectionHandler()] "
              + "Invalid ObjectContainer {"+ cont+ "}");
    
    //Set the internal properties and log the connection.
    channel = ch;
    closed = false;
    container = cont;
    client = cli;
    log = LogFactory.getSimpleLog(RunnableConnectionHandler.class);
  }
  
  
  public RunnableConnectionHandler(Channel ch, ObjectContainer cont) {
    this(ch, null, cont);
  }
  
  
  /**
   * Get the network communication channel.
   * @return The network communication channel.
   */
  public Channel getChannel() {
    return channel;
  }


  /**
   * Set the network communication channel.
   * Define o canal de comunicação de objetos na rede.
   * @param ch The network communication channel.
   * @return This modified <code>HttpConnectionHandler</code> instance.
   */
  public RunnableConnectionHandler setChannel(Channel ch) {
    this.channel = ch;
    return this;
  }


  /**
   * Reads a <code>Transport</code> object from the network channel.
   * @return A <code>Transport</code> object readed from the network channel.
   */
  public Transport read() {
    try {
      // Try to read and return the Transport object from the channel.
      return channel.read();
    } catch(Exception e) {
      // Log an occurred error on channel reading, 
      // but only if the error is a reading error or 
      // connection reset error. Other less important 
      // errors are ignored.
      String msg = e.getMessage();
      if(msg != null 
          && !msg.contains(READ_ERROR)
          && !msg.contains(CONN_RESET)) {
        log.error("Error on channel read: {}", e.toString());
        if(log.outputs().isEmpty())
          throw new RuntimeException("Error reading from channel", e);
      }
      return null;
    }
  }
    
    
  /**
   * Writes a <code>Transport</code> object on the network channel.
   * @param trp The <code>Transport</code> object to be writed on 
   * the network channel.
   */
  public void write(Transport trp) {
    if(trp == null) return;
    
    try {
      // writes the Transport object on the channel
      // and log the result sent.
      channel.write(trp);
      //log.info("Response sent: "+ trp.getObject());
    } catch(IOException e) {
      // Log an error occurred on channel writing.
      log.warn(
          new IOException("Error writing response", e), false);
      if(log.outputs().isEmpty())
        throw new RuntimeException("Error writing response", e);
    }
  }
    
    
  /**
   * Handle the method invocation request.
   * @param rm Remote method to be invoked.
   * @return An operation result <code>OpResult</code> object.
   */
  private OpResult invoke(RemoteMethod rm) {
    // Check for null argument
    nullarg(RemoteMethod.class, rm);
    OpResult op = new OpResult();
    try {
      // Create an Invoker instance for doing
      // the reflection invocation work.
      Invoker iv = new Invoker(container, rm.getCredentials());
      // Set the method returned object in the operation result.
      op.setReturn(iv.invoke(rm));
      op.setSuccessOperation(true);
    }
    catch(Exception e) {
      // Set the error occurred in the operation result
      // and log the error.
      op.setSuccessOperation(false);
      op.setError(e);
      log.warn("Error invoking method {"+ rm+ "}")
          .warn(e, !(e instanceof AuthenticationException));
    }
    return op;
  }
    
    
  /**
   * Handle the method chain invocation request.
   * @param chain Chain of methods to be invoked.
   * @return An operation result <code>OpResult</code> object.
   */
  private OpResult invoke(MethodChain chain) {
    // Check for null argument
    nullarg(MethodChain.class, chain);
    OpResult op = new OpResult();
    try {
      // Check if chain of methos is empty an throw an error.
      if(chain.current() == null)
        throw new MethodInvocationException(
            "Empty MethodChain. No method to invoke");
      // Create an Invoker instance for doing the reflection work.
      Invoker iv = new Invoker(container, chain.current().getCredentials());
      Object obj = iv.getObject(chain.current());
      do {
        // Loop the chain of methods doing the invocation over 
        // the returned objects.
        this.logChain(obj, chain);
        obj = iv.invoke(chain.current());
      } while(chain.next() != null);
      op.setSuccessOperation(true);
      op.setReturn(obj);
    } 
    catch(AuthenticationException | MethodInvocationException e) {
      // Catch, log and set an occurred error in the operation result.
      op.setSuccessOperation(false);
      op.setError(e);
      log.warn("Error invoking method ["+ chain.current()+ "]")
          .warn(e, true);
      if(log.outputs().isEmpty())
        throw new RuntimeException("Error invoking method ["+ chain.current()+ "]", e);
    }
    return op;
  }
   
    
  /**
   * Log a chain of methods invocation request.
   * @param obj The object on the methods chain will be invoked.
   * @param chain The chain of methods.
   */
  private void logChain(Object obj, MethodChain chain) {
    if(obj == null || chain == null 
        || chain.current() == null) return;
    String msg = "";
    if(chain.current().objectName() == null)
      msg = obj.getClass().getSimpleName();
    msg += chain.current().toString();
    log.info("Invoking: "+ msg);
  }
  
  
  /**
   * Pack the result of method invocation on a <code>Transport</code> object.
   * @param op The operation result of method invocation.
   * @return The packed <code>Transport</code> object.
   */
  private Transport pack(OpResult op) {
    nullarg(OpResult.class, op);
    Transport t = new Transport();
    Object ret = op.getReturn();
    // Check for InputStream reference in the returned object 
    // and set it to be the embedded http response stream.
    if(ret != null && InputStream.class
        .isAssignableFrom(ret.getClass())) {
      t.setInputStream((InputStream) ret);
      op.setReturn(new FakeInputStreamRef());
    }
    t.setObject(op);
    return t;
  }
  
  
  /**
   * Handle the readed <code>Transport</code> 
   * object for method invocation.
   * @param trp The readed <code>Transport</code> 
   * object for method invocation.
   * @return A new <code>Transport</code> object with 
   * the method invocation result.
   */
  public Transport handleInvoke(Transport trp) {
    if(trp == null || trp.getObject() == null) 
      return null;
    // Log the remote method invocation request
    //log.info("<- Remote request: "+ trp.getObject());
    // Handle the readed invocation request according
    // if it is a single remote request or a chain of 
    // methods request
    if(trp.isObjectFromType(RemoteMethod.class)) {
      RemoteMethod rm = trp.castObject();
      this.checkInputStreamReference(rm, trp);
      return pack(invoke(rm));
    }
    else if(trp.isObjectFromType(MethodChain.class)) {
      MethodChain chain = trp.castObject();
      this.checkInputStreamReference(chain.current(), trp);
      return pack(invoke(chain));
    }
    else return invalidType(trp);
  }
  
  
  /**
   * Create and pack an error operation result
   * for unknown object type readed.
   * @param t The <code>Transport</code> object 
   * readed from the channel.
   * @return A new <code>Transport</code> object
   * with the operation error result.
   */
  private Transport invalidType(Transport t) {
    OpResult op = new OpResult();
    op.setSuccessOperation(false);
    op.setError(new MethodInvocationException(
        "[HttpConnectionHandler.InvalidType( Transport )] "
            + "Server can not handle this object type: "
            + t.getObject().getClass()));
    return pack(op);
  }
  
  
  public String getClientAddress() {
    if(client == null) return "Request: ";
    return "{".concat(
        client.toString().replace("/", ""))
        .concat("}");
  }
  
  
  /**
   * Handle the Http connection request.
   */
  @Override
  public void run() {
    // Check if connection is closed.
    if(isClosed()) {
      close();
      return;
    }
  
    // Reads the Transport object from the channel.
    long start = System.nanoTime();
    Transport trp = this.read();
    
    Object req = trp.getObject();
    // Check for error reading from the channel and log it.
    if(trp == null) {
      log.debug("Connection closed by client.");
      close();
      return;
    }
    // Handle the invocation request and write the 
    // result on the channel.
    trp = handleInvoke(trp);
    double time = (System.nanoTime() - start) / 1000000.0;
    
    log.info("{}  {}  \t->  {}  \t({} ms){}", 
        getClientAddress(), req, trp.getObject(), round(time, 1), 
        (time > 200.0 ? "+" : ""));
    
    this.write(trp);
    // If is a persistent Http connection, try
    // to continue the communication with the client
    // over the same connection. Close it otherwise.
    if(channel.isValid())
      this.run();
    else
      this.close();
  }
  
  
  private double round(double arg, int dec) {
    long i = (long) arg;
    long d = Math.round((arg - i) * Math.pow(10, dec));
    return i + d / Math.pow(10, dec);
  }
  
  
  /**
   * Verify if the server Http connection is closed.
   * @return <code>true</code> if the server Http 
   * connection is closed, <code>false</code> otherwise.
   */
  public boolean isClosed() {
    return closed;
  }
  
  
  /**
   * Check for InputStream objects references in the 
   * remote method arguments, replacing these references
   * for the embedded Http request InputStream content.
   * @param rmt The remote method invocation request.
   * @param trp The <code>Transport</code> object readed 
   * from the channel.
   */
  public void checkInputStreamReference(RemoteMethod rmt, Transport trp) {
    if(trp == null 
        || !trp.hasContentEmbedded() 
        || rmt == null 
        || rmt.args() == null
        || rmt.args().isEmpty())
      return;
    
    for(int i = 0; i < rmt.args().size(); i++) {
      Object o = rmt.args().get(i);
      if(o != null && FakeInputStreamRef.class
          .isAssignableFrom(o.getClass())) {
        rmt.args().set(i, trp.getInputStream());
      }
    }//for
  }
  
  
  /**
   * Close the server Http connection and finish this handler.
   */
  public void close() {
    closed = true;
    try { channel.close(); }
    catch(Exception e) {}
  }
  
}
