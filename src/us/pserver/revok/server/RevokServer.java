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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.file.Paths;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.http.HttpServerConnection;
import org.apache.http.impl.DefaultBHttpServerConnection;
import us.pserver.log.Log;
import us.pserver.log.LogFactory;
import us.pserver.log.output.FileLogOutput;
import us.pserver.log.output.LogOutput;
import us.pserver.revok.HttpConnector;
import us.pserver.revok.container.ObjectContainer;
import us.pserver.revok.factory.ChannelFactory;
import us.pserver.revok.factory.ChannelFactoryBuilder;
import us.pserver.revok.protocol.RunnableConnectionHandler;
import us.pserver.revok.protocol.JsonSerializer;
import us.pserver.revok.protocol.ObjectSerializer;

/**
 * Network HTTP object server for remote method invocation.
 * 
 * @author Juno Roesler - juno@pserver.com
 * @version 1.1 - 201506
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
    factory = ChannelFactoryBuilder.builder()
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
    log.clearOutputs();
    return this;
  }
  
  
  /**
   * Enable stdout and errout logging.
   * @return This modified <code>RevokServer</code> instance.
   */
  public RevokServer enableLogging() {
    log = LogFactory.getSimpleLog(this.getClass());
    LogFactory.putCached("us.pserver.revok", log);
    return this;
  }
  
  
  /**
   * Enable file, stdout and errout logging.
   * @param path Path to the log file.
   * @return This modified <code>RevokServer</code> instance.
   */
  public RevokServer enableFileLogging(String path) {
    if(path != null && !path.trim().isEmpty()) {
      log = LogFactory.getSimpleLog(this.getClass(), Paths.get(path));
      LogFactory.putCached("us.pserver.revok", log);
    }
    return this;
  }
  
  
  /**
   * Disable logging to file and enable stdout and errout log (same as <code>enableLogging()</code>).
   * @return This modified <code>RevokServer</code> instance.
   */
  public RevokServer disableFileLogging() {
    if(log != null) {
      Optional<Entry<String, LogOutput>> flog = 
          log.outputsMap().entrySet().stream().filter(e->
              FileLogOutput.class.isAssignableFrom(
                  e.getValue().getClass())).findFirst();
      if(flog.isPresent()) {
        log.remove(flog.get().getKey());
        LogFactory.putCached("us.pserver.revok", log);
      }
    }
    else enableLogging();
    return this;
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
          log.info("------------------------------")
              .info("Handling socket: "+ conn.toString());
          exec.submit(new RunnableConnectionHandler(
              factory.createChannel(conn, serial), container));
          // Catch socket timeout exceptions and continue 
          // accepting other connections
        } catch(SocketTimeoutException se) {}
      }//while
    } catch(IOException e) {
      // Catch and log other error occurred accepting connections.
      // Errors over server listening connections are fatal
      // and irrecoverable.
      log.error(
          new IOException("Error running RevokServer", e), true);
      if(log.outputs().isEmpty())
        throw new RuntimeException("Error running RevokServer", e);
    }
    // Shutdown the server and log when it not should be running anymore
    log.info("Stopping ExecutorService...");
    exec.shutdown();
    log.info("RevokServer Shutdown!");
  }
  
}
