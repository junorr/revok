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

package us.pserver.revok.server;

import us.pserver.revok.reflect.Invoker;
import us.pserver.revok.protocol.FakeInputStreamRef;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.http.HttpServerConnection;
import org.apache.http.impl.DefaultBHttpServerConnection;
import static us.pserver.chk.Checker.nullarg;
import us.pserver.log.Log;
import us.pserver.log.SimpleLog;
import us.pserver.log.SimpleLogFactory;
import us.pserver.revok.HttpConnector;
import us.pserver.revok.MethodChain;
import us.pserver.revok.MethodInvocationException;
import us.pserver.revok.OpResult;
import us.pserver.revok.RemoteMethod;
import us.pserver.revok.channel.Channel;
import us.pserver.revok.protocol.Transport;
import us.pserver.revok.container.AuthenticationException;
import us.pserver.revok.container.ObjectContainer;
import us.pserver.revok.factory.ChannelFactory;
import us.pserver.revok.factory.HttpFactoryBuilder;
import us.pserver.revok.protocol.JsonSerializer;
import us.pserver.revok.protocol.ObjectSerializer;

/**
 * Network HTTP object server for remoting method invocation.
 * 
 * @author Juno Roesler - juno.rr@gmail.com
 * @version 1.1 - 20150422
 */
public class RevokServer extends AbstractServer {
  
  /**
   * <code>
   *   SERVER_KEY = RevokServer.class.getSimpleName();
   * </code><br>
   * Key used to store the server on ObjectContainer.
   */
  public static final String SERVER_KEY = RevokServer.class.getSimpleName();
  
  /**
   * <code>
   *   SOCK_SO_TIMEOUT = 500;
   * </code><br>
   * Default socket timeout.
   */
  public static final int SOCK_SO_TIMEOUT = 500;
  
  /**
   * <code>
   *  HTTP_CONN_BUFFER_SIZE = 8*1024
   * </code><br>
   * Default HTTP buffer size.
   */
  public static final int HTTP_CONN_BUFFER_SIZE = 8*1024;
  
  
  private transient HttpConnector con;
  
  private transient ChannelFactory<HttpServerConnection> factory;
  
  private ExecutorService exec;
  
  private ObjectSerializer serial;
  
  private Log log;
  
  
  /**
   * Default constructor receives the <code>ObjectContainer</code>
   * with the objects whose methods will be invoked.
   * @param cont The <code>ObjectContainer</code>
   * with the objects whose methods will be invoked.
   * @see us.pserver.revok.container.ObjectContainer
   */
  public RevokServer(ObjectContainer cont) {
    super(cont);
    this.enableLogging();
    cont.put(ObjectContainer.NAMESPACE_GLOBAL, SERVER_KEY, this);
    con = new HttpConnector();
    factory = HttpFactoryBuilder.builder()
        .enableGZipCompression()
        .enableCryptography()
        .createHttpResponseChannelFactory();
    serial = new JsonSerializer();
  }
  
  
  /**
   * Constructor which receives the <code>ObjectContainer</code>
   * with the objects whose methods will be invoked and the
   * network information object <code>HttpConnector</code>.
   * @param cont The <code>ObjectContainer</code>
   * with the objects whose methods will be invoked.
   * @param hcon The network information object 
   * <code>HttpConnector</code>.
   * @see us.pserver.revok.container.ObjectContainer
   * @see us.pserver.revok.HttpConnector
   */
  public RevokServer(ObjectContainer cont, HttpConnector hcon) {
    this(cont);
    if(con == null) throw new
        IllegalArgumentException(
            "[RevokServer( ObjectContainer, HttpConnector )] "
                + "Invalid NetConnector: "+ con);
    this.con = hcon;
  }
  
  
  /**
   * Constructor which receives the <code>ObjectContainer</code>,
   * the network information <code>HttpConnector</code> and
   * the default object serializer for encoding transmitted objects.
   * @param cont The <code>ObjectContainer</code>
   * with the objects whose methods will be invoked.
   * @param hcon The network information object 
   * <code>HttpConnector</code>.
   * @param serial The default object serializer for encoding 
   * transmitted objects.
   */
  public RevokServer(ObjectContainer cont, HttpConnector hcon, ObjectSerializer serial) {
    this(cont, hcon);
    if(serial == null)
      serial = new JsonSerializer();
    this.serial = serial;
  }
  
  
  /**
   * Get the <code>ObjectSerializer</code> for objects serialization.
   * @return <code>ObjectSerializer</code> for objects serialization.
   */
  public ObjectSerializer getObjectSerializer() {
    return serial;
  }
  
  
  /**
   * Set the <code>ObjectSerializer</code> for objects serialization.
   * @param serializer <code>ObjectSerializer</code> for objects serialization.
   * @return This modified <code>RevokServer</code> instance.
   */
  public RevokServer setObjectSerializer(ObjectSerializer serializer) {
    if(serializer != null) {
      serial = serializer;
    }
    return this;
  }
  
  
  /**
   * Get the network information object <code>HttpConnector</code>.
   * @return The network information object <code>HttpConnector</code>.
   */
  public HttpConnector getConnector() {
    return con;
  }


  /**
   * Set the network information object <code>HttpConnector</code>.
   * @param con The network information object <code>HttpConnector</code>.
   * @return This modified <code>RevokServer</code> instance.
   */
  public RevokServer setConnector(HttpConnector con) {
    this.con = con;
    return this;
  }


  /**
   * Get the network channel factory object.
   * @return The network channel factory object.
   */
  public ChannelFactory<HttpServerConnection> getChannelFactory() {
    return factory;
  }


  /**
   * Set the network channel factory object.
   * @param fact The network channel factory object.
   * @return This modified <code>RevokServer</code> instance.
   */
  public RevokServer setChannelFactory(ChannelFactory<HttpServerConnection> fact) {
    if(fact != null)
      this.factory = fact;
    return this;
  }
  
  
  /**
   * Get the log system.
   * @return Log
   */
  public Log getLog() {
    return log;
  }
  
  
  /**
   * Set the log system.
   * @param log Log
   * @return This modified <code>RevokServer</code> instance.
   */
  public RevokServer setLog(Log log) {
    if(log != null)
      this.log = log;
    return this;
  }
  
  
  /**
   * Completly disable logging.
   * @return This modified <code>RevokServer</code> instance.
   */
  public RevokServer disableLogging() {
    log = new SimpleLog().reset().clearOutputs();
    return this;
  }
  
  
  /**
   * Enable stdout and errout logging.
   * @return This modified <code>RevokServer</code> instance.
   */
  public RevokServer enableLogging() {
    log = SimpleLogFactory.instance().reset()
        .newStdOutput()
        .enableNonErrorLevels()
        .add()
        .newErrOutput()
        .enableErrorLevels()
        .add()
        .create();
    return this;
  }
  
  
  /**
   * Enable file, stdout and errout logging.
   * @param path Path to the log file.
   * @return This modified <code>RevokServer</code> instance.
   */
  public RevokServer enableFileLogging(String path) {
    if(path != null && !path.trim().isEmpty()) {
      log = SimpleLogFactory.instance().reset()
          .newStdOutput()
          .enableNonErrorLevels()
          .debug(false)
          .add()
          
          .newErrOutput()
          .enableErrorLevels()
          .add()
          
          .newFileOutput(path)
          .enableAllLevels()
          .add()
          .create();
    }
    return this;
  }
  
  
  /**
   * Disable logging to file and enable stdout and errout log (same as <code>enableLogging()</code>).
   * @return This modified <code>RevokServer</code> instance.
   */
  public RevokServer disableFileLogging() {
    return enableLogging();
  }
  
  
  /**
   * Validates and start the necessary components for server execution.
   */
  private void preStart() {
    if(con == null)
      throw new IllegalStateException("[RevokServer.preStart()] "
          + "Invalid NetConnector ["+ con + "]");
    if(factory == null)
      throw new IllegalArgumentException("[RevokServer.preStart()] "
          + "Invalid ChannelFactory ["+ factory+ "]");
    if(container == null)
      throw new IllegalArgumentException("[RevokServer.preStart()] "
          + "Invalid ObjectContainer ["+ container+ "]");
    
    log.info("Starting RevokServer...");
    setRunning(true);
    exec = Executors.newFixedThreadPool(availableThreads);
  }
  
  
  @Override
  public void start() {
    preStart();
    run();
  }
  
  
  /**
   * Starts the server execution in a new <code>Thread</code>.
   * @return This modified <code>RevokServer</code> instance.
   */
  public RevokServer startNewThread() {
    preStart();
    new Thread(this, "RevokServer").start();
    return this;
  }
  
  
  /**
   * Not invoke directly. Executes server routines.
   */
  @Override
  public void run() {
    // Stablish a listen server connection
    try(ServerSocket server = con.connectServerSocket();) {
      server.setSoTimeout(SOCK_SO_TIMEOUT);
      // Log the server start
      log.info("Listening on: "+ con.toString());
      log.info("RevokServer started!\n");
      
      // Loop while the server should be running
      while(isRunning()) {
        try {
          // Accept a client TCP connection
          Socket sock = server.accept();
          // Create and bind an HTTP connection
          // over the TCP connection received.
          DefaultBHttpServerConnection conn = 
              new DefaultBHttpServerConnection(
                  HTTP_CONN_BUFFER_SIZE);
          conn.bind(sock);
          // Submit for Thread worker execution
          // a HttpConnectionHandler for handling 
          // the HTTP connection
          exec.submit(new HttpConnectionHandler(
              factory.createChannel(conn, serial), conn, log));
          // Catch socket timeout exceptions and continue 
          // accepting other connections
        } catch(SocketTimeoutException se) {}
      }//while
    } catch(IOException e) {
      // Catch and log other error occurred accepting connections.
      // Errors over server listening connections are fatal
      // and irrecoverable.
      log.fatal(
          new IOException("Error running RevokServer", e), true);
      if(log.outputs().isEmpty())
        throw new RuntimeException("Error running RevokServer", e);
    }
    // Shutdown the server and log when it not should be running anymore
    log.info("Stopping ExecutorService...");
    exec.shutdown();
    log.info("RevokServer Shutdown!");
  }
  
  
  
/*****************************************************************/
/**   START OF HttpConnectionHandler INNER CLASS DECLARATION    **/
/*****************************************************************/

  
  
  /**
   * <code>HttpConnectionHandler</code> handle client requests 
   * for remote methods invocation.
   */
  public class HttpConnectionHandler implements Runnable {
    
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
    
    
    private Channel channel;
    
    private HttpServerConnection conn;
    
    private Log log;
    
    
    /**
     * Default construtor receives the network communication
     * channel and the server Http connection 
     * <code>HttpServerConnection</code>.
     * @param ch The network communication channel.
     * @param hsc he server Http connection 
     * <code>HttpServerConnection</code>.
     */
    public HttpConnectionHandler(Channel ch, HttpServerConnection hsc, Log log) {
      if(ch == null)
        throw new IllegalArgumentException(
            "[HttpConnectionHandler( Channel, HttpServerConnection, Log )] "
                + "Invalid Channel {"+ ch+ "}");
      
      if(hsc == null || !hsc.isOpen())
        throw new IllegalArgumentException(
            "[HttpConnectionHandler( Channel, HttpServerConnection, Log )] "
                + "Invalid HttpServerConnection {"+ hsc+ "}");
      
      if(log == null)
        throw new IllegalArgumentException(
            "[HttpConnectionHandler( Channel, HttpServerConnection, Log )] "
                + "Invalid Log {"+ log+ "}");
      
      //Set the internal properties and log the connection.
      conn = hsc;
      channel = ch;
      this.log = log;
      log.info("------------------------------")
          .info("Handling socket: "+ conn.toString());
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
    public HttpConnectionHandler setChannel(Channel ch) {
      this.channel = ch;
      return this;
    }


    /**
     * Get the server Http connection.
     * @return The server Http connection.
     */
    public HttpServerConnection getConnection() {
      return conn;
    }


    /**
     * Reads a <code>Transport</code> object from the network channel.
     * @return A <code>Transport</code> object readed from the network channel.
     */
    public Transport read() {
      try {
        // Try tp read and return the Transport object from the channel.
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
          log.error("Error reading from channel")
              .error(e, true);
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
      if(trp == null || !conn.isOpen()) return;
      
      try {
        // writes the Transport object on the channel
        // and log the result sent.
        channel.write(trp);
        log.info("Response sent: "+ trp.getObject());
      } catch(IOException e) {
        // Log an error occurred on channel writing.
        log.warning(
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
        log.warning("Error invoking method {"+ rm+ "}")
            .warning(e, !(e instanceof AuthenticationException));
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
        log.warning("Error invoking method ["+ chain.current()+ "]")
            .warning(e, true);
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
    private Transport handleInvoke(Transport trp) {
      if(trp == null || trp.getObject() == null) 
        return null;
      // Log the remote method invocation request
      log.info("<- Remote request: "+ trp.getObject());
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
      Transport trp = this.read();
      // Check for error reading from the channel and log it.
      if(trp == null) {
        log.info("Connection closed by client.");
        close();
        return;
      }
      // Handle the invocation request and write the 
      // result on the channel.
      this.write( handleInvoke(trp) );
      // If is a persistent Http connection, try
      // to continue the communication with the client
      // over the same connection. Close it otherwise.
      if(channel.isValid())
        this.run();
      else
        this.close();
    }
    
    
    /**
     * Verify if the server Http connection is closed.
     * @return <code>true</code> if the server Http 
     * connection is closed, <code>false</code> otherwise.
     */
    public boolean isClosed() {
      return conn == null
          || !conn.isOpen();
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
      try { channel.close(); }
      catch(Exception e) {}
      try { 
        conn.close();
      } catch(Exception e) {}
    }
    
  }
  

/***************************************************************/
/**   END OF HttpConnectionHandler INNER CLASS DECLARATION    **/
/***************************************************************/

}
