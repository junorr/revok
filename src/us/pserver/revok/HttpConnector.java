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

package us.pserver.revok;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.http.HttpClientConnection;
import org.apache.http.impl.DefaultBHttpClientConnection;
import us.pserver.cdr.b64.Base64StringCoder;
import us.pserver.revok.http.HttpConsts;
import static us.pserver.revok.http.HttpConsts.COLON;
import static us.pserver.revok.http.HttpConsts.SLASH;


/**
 * Encapsulates network informations, like
 * connection port and address and network proxy.
 * It has utility methods for <code>Socket</code>,
 * <code>ServerSocket</code>, <code>InetAddress</code>
 * and <code>HttpClientConnection</code> creation.
 * 
 * @author Juno Roesler - juno.rr@gmail.com
 * @version 1.1 - 20150422
 */
public class HttpConnector {
  
  /**
   * <code>
   *  DEFAULT_PORT = 9099
   * </code><br>
   * Default network port.
   */
  public static final int DEFAULT_PORT = 9099;
  
  /**
   * <code>
   *  HTTP_CONN_BUFFER_SIZE = 8*1024
   * </code><br>
   * Default buffer size.
   */
  public static final int HTTP_CONN_BUFFER_SIZE = 8*1024;
  
  
  private String address;
  
  private String proto;
  
  private String path;
  
  private int port;
  
  private String proxyAddr;
  
  private int proxyPort;
  
  private String proxyAuth;
  
  private Base64StringCoder cdr;
  
  
  /**
   * Default no arguments constructor,
   * uses localhost:9099 network address.
   */
  public HttpConnector() {
    address = null;
    port = DEFAULT_PORT;
    proto = HttpConsts.HTTP;
    path = null;
    proxyAddr = null;
    proxyPort = 0;
    proxyAuth = null;
    cdr = new Base64StringCoder();
  }
  
  
  /**
   * Receives connection port and address.
   * @param address Network address <code>String</code>.
   * @param port Network port <code>int</code>.
   */
  public HttpConnector(String address, int port) {
    if(address == null)
      throw new IllegalArgumentException("[HttpConnector( String, int )] "
          + "Invalid address ["+ address+ "]");
    if(port < 0 || port > 65535)
      throw new IllegalArgumentException("[HttpConnector( String, int )] "
          + "Port out of range 1-65535 {"+ port+ "}");
    this.address = address;
    this.port = port;
    proto = HttpConsts.HTTP;
    path = null;
    proxyAddr = null;
    proxyPort = 0;
    proxyAuth = null;
    cdr = new Base64StringCoder();
  }
  

  /**
   * Creates an <code>InetSocketAddress</code>
   * from this HttpConnector informations.
   * @return <code>InetSocketAddress</code>
   */
  public InetSocketAddress createSocketAddress() {
    if(address != null)
      return new InetSocketAddress(address, port);
    else {
      address = "0.0.0.0";
      return new InetSocketAddress(port);
    }
  }
  
  
  /**
   * Set network (http) address.
   * @param addr <code>String</code>.
   * @return This modified <code>HttpConnector</code> instance.
   */
  public HttpConnector setAddress(String addr) {
    if(addr == null) 
      throw new IllegalArgumentException(
          "[HttpConnector.setAddress( String )] "
              + "Invalid address ["+ addr+ "]");
    
    if(addr.startsWith("http")) {
      address = addr.substring(addr.indexOf(COLON)+3);
      proto = addr.substring(0, addr.indexOf(COLON)+3);
    }
    else {
      address = addr;
    }
    
    if(address.contains(SLASH)) {
      int is = address.indexOf(SLASH);
      path = address.substring(is);
      address = address.substring(0, is);
    } 
    else {
      path = SLASH;
    }
    return this;
  }


  /**
   * Return the network (http) address.
   * @return Network address <code>String</code>.
   */
  public String getAddress() {
    return address;
  }


  /**
   * Set Network (http) address.
   * @param address <code>String</code>
   * @param port <code>int</code>
   * @return This modified <code>HttpConnector</code> instance.
   */
  public HttpConnector setAddress(String address, int port) {
    if(port <= 0)
      return setAddress(address);
    else {
      if(address.endsWith(SLASH))
        address = address.substring(0, address.length() -1);
      return setAddress(address + COLON + String.valueOf(port));
    }
  }
  
  
  /**
   * Return the network protocol from address (i.e: <b>https://</b>localhost:8080/post/).
   * @return The protocol address part (i.e: <b>https://</b>localhost:8080/post/).
   */
  public String getProtocol() {
    return proto;
  }
  
  
  /**
   * Return the address path (i.e: https://localhost:8080<b>/post/</b>).
   * @return Address path (i.e: https://localhost:8080<b>/post/</b>).
   */
  public String getPath() {
    return path;
  }
  
  
  /**
   * Return the full address.
   * @return Full address <code>String</code>.
   */
  public String getFullAddress() {
    if(address == null || address.trim().isEmpty()
        && path != null) 
      return path;
    else 
      return proto + address + path;
  }
  
  
  /**
   * Return the network port.
   * @return Network port <code>int</code>.
   */
  public int getPort() {
    return port;
  }


  /**
   * Set the network port.
   * @param port Network port <code>int</code>.
   * @return This modified <code>HttpConnector</code> instance.
   */
  public HttpConnector setPort(int port) {
    if(port < 0 || port > 65535)
      throw new IllegalArgumentException("[HttpConnector.setPort( int )] "
          + "Port not out of range 1-65535 {"+ port+ "}");
    this.port = port;
    return this;
  }


  /**
   * Return the network proxy address.
   * @return Network proxy address <code>String</code>.
   */
  public String getProxyAddress() {
    return proxyAddr;
  }


  /**
   * Set the network proxy address.
   * @param proxyAddr Network proxy address <code>String</code>.
   * @return This modified <code>HttpConnector</code> instance.
   */
  public HttpConnector setProxyAddress(String proxyAddr) {
    this.proxyAddr = proxyAddr;
    return this;
  }


  /**
   * Return the network proxy port.
   * @return Network proxy port <code>int</code>.
   */
  public int getProxyPort() {
    return proxyPort;
  }


  /**
   * Set the network proxy port
   * @param proxyPort Network proxy port <code>int</code>.
   * @return This modified <code>HttpConnector</code> instance.
   */
  public HttpConnector setProxyPort(int proxyPort) {
    this.proxyPort = proxyPort;
    return this;
  }


  /**
   * Return the network proxy authorization (&lt;user:password&gt;).
   * @return Network proxy authorization (&lt;user:password&gt;) <code>String</code>.
   */
  public String getProxyAuthorization() {
    return proxyAuth;
  }


  /**
   * Set network proxy authorization (&lt;user:password&gt;).
   * @param auth Network proxy authorization (&lt;user:password&gt;) <code>String</code>.
   * @return This modified <code>HttpConnector</code> instance.
   */
  public HttpConnector setProxyAuthorization(String auth) {
    if(auth != null)
      proxyAuth = "Basic " + cdr.encode(auth);
    return this;
  }
  
  
  /**
   * Return URI String address.
   * @return URI String address. 
   */
  public String getURIString() {
    String uri = proto.concat(address == null ? "localhost" : address);
    if(port > 0) {
      if(address.endsWith(HttpConsts.SLASH))
        uri = uri.substring(0, uri.length() -1);
      uri = uri.concat(HttpConsts.COLON).concat(String.valueOf(port));
    }
    return uri.concat(HttpConsts.SLASH);
  }
  
  
  /**
   * Create a bounded <code>ServerSocket</code> 
   * connection with this HttpConnector informations.
   * @return <code>ServerSocket</code>.
   * @throws IOException In case of creation error.
   */
  public ServerSocket connectServerSocket() throws IOException {
    ServerSocket sc = new ServerSocket();
    sc.bind(this.createSocketAddress());
    return sc;
  }
  
  
  /**
   * Create a bounded network <code>Socket</code> 
   * with this HttpConnector informations.
   * @return <code>Socket</code>.
   * @throws IOException In case of creation error.
   */
  public Socket connectSocket() throws IOException {
    Socket sc = new Socket();
    String addr = (address == null ? "127.0.0.1" : address);
    int prt = port;
    if(proxyAddr != null && proxyPort > 0) {
      addr = proxyAddr;
      prt = proxyPort;
    }
    sc.connect(new InetSocketAddress(addr, prt));
    return sc;
  }
  
  
  /**
   * Create a bounded <code>HttpClientConnection</code> 
   * with this HttpConnector informations.
   * @return Bounded <code>HttpClientConnection</code>.
   * @throws IOException In case of creation error.
   */
  public HttpClientConnection connectHttp() throws IOException {
    Socket s = connectSocket();
    DefaultBHttpClientConnection conn = 
        new DefaultBHttpClientConnection(
            HTTP_CONN_BUFFER_SIZE);
    conn.bind(s);
    return conn;
  }
  
  
  @Override
  public String toString() {
    return "HttpConnector{ " + 
        (address == null ? "*" : address) 
        + ":" + port + " }";
  }
  
}
