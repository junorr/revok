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

package us.pserver.revok.channel;

import us.pserver.revok.protocol.Transport;
import java.io.IOException;
import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpServerConnection;
import org.apache.http.HttpVersion;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import us.pserver.cdr.crypt.CryptKey;
import us.pserver.revok.http.HttpEntityFactory;
import us.pserver.revok.http.HttpEntityParser;
import us.pserver.revok.http.HttpConsts;
import us.pserver.revok.protocol.JsonSerializer;
import us.pserver.revok.protocol.ObjectSerializer;


/**
 * HTTP protocol communication channel.
 * Implements the server side (HTTP response) 
 * of the network communication, handling POST 
 * requests.
 * The implementation of HTTP protocol used in all 
 * classes of Revok project, is the high performance 
 * Apache Http Core 4.4.1 library.
 * 
 * @author Juno Roesler - juno.rr@gmail.com
 * @version 1.0 - 21/01/2014
 */
public class HttpResponseChannel implements Channel {
  
  private CryptKey key;
  
  private boolean valid;
  
  private boolean gzip;
  
  private HttpServerConnection conn;
  
  private HttpProcessor processor;
  
  private HttpCoreContext context;
  
  private ObjectSerializer serial;
  
  
  /**
   * Default constructor which receives a 
   * <code>HttpServerConnection</code> for
   * network communication.
   * @param hsc <code>HttpServerConnection</code>.
   */
  public HttpResponseChannel(HttpServerConnection hsc) {
    if(hsc == null || !hsc.isOpen())
      throw new IllegalArgumentException(
          "[HttpResponseChannel( HttpServerConnection )] "
          + "Invalid Connection {"+ hsc+ "}");
    
    conn = hsc;
    key = null;
    valid = true;
    gzip = true;
    serial = new JsonSerializer();
    init();
  }
  
  
  /**
   * Constructor which receives  
   * <code>HttpServerConnection</code> 
   * and <code>ObjectSerializer</code>
   * for network communication.
   * @param hsc <code>HttpServerConnection</code>.
   * @param os <code>ObjectSerializer</code> for object serialization.
   */
  public HttpResponseChannel(HttpServerConnection hsc, ObjectSerializer os) {
    this(hsc);
    if(os == null) os = new JsonSerializer();
    serial = os;
  }
  
  
  /**
   * Init some objects for http communication.
   */
  private void init() {
    context = HttpCoreContext.create();
    processor = HttpProcessorBuilder.create()
        .add(new ResponseServer(HttpConsts.HD_VAL_SERVER))
        .add(new ResponseDate())
        .add(new ResponseContent())
        .add(new ResponseConnControl())
        .build();
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
   * @return This modified <code>HttpResponseChannel</code> instance.
   */
  public HttpResponseChannel setObjectSerializer(ObjectSerializer serializer) {
    if(serializer != null) {
      serial = serializer;
    }
    return this;
  }
  
  
  /**
   * Get the <code>HttpServerConnection</code> object.
   * @return <code>HttpServerConnection</code> object.
   */
  public HttpServerConnection getHttpConnection() {
    return conn;
  }
  
  
  /**
   * Enable GZIP compression of the data transmitted on the channel.
   * @param bool <code>true</code> for enable GZIP compression, <code>false</code> to disable it.
   * @return This modified <code>HttpResponseChannel</code> instance.
   */
  public HttpResponseChannel setGZipCompressionEnabled(boolean bool) {
    gzip = bool;
    return this;
  }
  
  
  /**
   * Verifies if GZIP compression is enalbed.
   * @return <code>true</code> if GZIP compression is enabled, <code>false</code> otherwise.
   */
  public boolean isGZipCompressionEnabled() {
    return gzip;
  }
  
  
  /**
   * Get the criptography key.
   * @return criptography key.
   */
  public CryptKey getCryptKey() {
    return key;
  }
  
  
  /**
   * Create the HTTP response, encoding the 
   * <code>Transport</code> object, criptography key
   * and eventual stream content in the Http 
   * response body.
   * @param trp <code>Transport</code> object.
   * @return <code>HttpResponse</code>.
   * @throws IOException In case of error creating the response.
   */
  private HttpResponse createResponse(Transport trp) throws IOException {
    if(trp == null) return null;
    HttpResponse response = new BasicHttpResponse(
        HttpVersion.HTTP_1_1, 
        HttpConsts.STATUS_200, 
        HttpConsts.STATUS_OK);
    
    String contenc = HttpConsts.HD_VAL_DEF_ENCODING;
    if(gzip) contenc = HttpConsts.HD_VAL_GZIP_ENCODING;
    response.addHeader(HttpConsts.HD_CONT_ENCODING, contenc);
    
    HttpEntityFactory fac = HttpEntityFactory.instance(serial);
    if(gzip) fac.enableGZipCoder();
    if(key != null) fac.enableCryptCoder(key);
    fac.put(trp.getWriteVersion());
    if(trp.getInputStream() != null) {
      fac.put(trp.getInputStream());
    }
    response.setEntity(fac.create());
    return response;
  }
  
  
  @Override
  public void write(Transport trp) throws IOException {
    HttpResponse response = createResponse(trp);
    if(response == null) return;
    try {
      processor.process(response, context);
      conn.sendResponseHeader(response);
      conn.sendResponseEntity(response);
      conn.flush();
    }
    catch(HttpException e) {
      throw new IOException(e.toString(), e);
    }
  }
  
  
  @Override
  public Transport read() throws IOException {
    if(conn == null || !conn.isOpen())
      return null;
    try {
      HttpRequest basereq = conn.receiveRequestHeader();
      if(basereq == null 
          || !HttpEntityEnclosingRequest.class
              .isAssignableFrom(basereq.getClass())) {
        throw new IOException("[HttpResponseChannel.read()] "
            + "Invalid HttpRequest without content received");
      }
      
      HttpEntityEnclosingRequest request = (HttpEntityEnclosingRequest) basereq;
      conn.receiveRequestEntity(request);
      HttpEntity content = request.getEntity();
      if(content == null) return null;
      
      HttpEntityParser par = HttpEntityParser.instance(serial);
      if(gzip) par.enableGZipCoder();
      par.parse(content);
      key = par.getCryptKey();
      Transport t = (Transport) par.getObject();
      if(par.getInputStream() != null)
        t.setInputStream(par.getInputStream());
      return t;
    }
    catch(ConnectionClosedException e) {
      return null;
    }
    catch(HttpException e) {
      throw new IOException(e.toString(), e);
    }
  }
  
  
  @Override
  public boolean isValid() {
    return valid;
  }
  
  
  @Override
  public void close() {
    try {
      if(conn != null) {
        conn.close();
      }
    } catch(IOException e) {}
  }
  
}
