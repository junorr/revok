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

package us.pserver.revok.factory;

import org.apache.http.HttpServerConnection;
import us.pserver.cdr.crypt.CryptAlgorithm;
import us.pserver.revok.HttpConnector;
import us.pserver.revok.channel.HttpRequestChannel;
import us.pserver.revok.channel.HttpResponseChannel;
import us.pserver.revok.protocol.ObjectSerializer;


/**
 * Default builder of channels factory.
 * 
 * @author Juno Roesler - juno@pserver.com
 * @version 1.1 - 201506
 */
public class ChannelFactoryBuilder {
  
  private boolean gzip, crypt;
  
  private CryptAlgorithm algo;
  
  
  /**
   * Default constructor without arguments.
   */
  public ChannelFactoryBuilder() {
    gzip = true; 
    crypt = true;
    algo = CryptAlgorithm.AES_CBC_PKCS5;
  }
  
  
  /**
   * Configure GZIP compression on the factory.
   * @return This instance of HttpFactoryProvider.
   */
  public ChannelFactoryBuilder setGZipCoderEnabled(boolean enabled) {
    gzip = enabled;
    return this;
  }
  
  
  /**
   * Configure cryptography on the factory.
   * @return This instance of HttpFactoryProvider.
   */
  public ChannelFactoryBuilder setCryptCoderEnabled(boolean enabled) {
    crypt = enabled;
    return this;
  }
  
  
  public boolean isGZipCoderEnabled() {
    return gzip;
  }
  
  
  public boolean isCryptCoderEnabled() {
    return crypt;
  }
  
  
  /**
   * Return a new instance of HttpFactoryBuilder.
   * @return A new instance of HttpFactoryBuilder.
   */
  public static ChannelFactoryBuilder builder() {
    return new ChannelFactoryBuilder();
  }
  
  
  /**
   * Create a HTTP request channel factory.
   * @return ChannelFactory&lt;HttpConnector&gt;
   */
  public ChannelFactory<HttpConnector> createHttpRequestChannelFactory() {
    return new ChannelFactory<HttpConnector>() {
      @Override
      public HttpRequestChannel createChannel(HttpConnector conn) {
        if(conn == null) {
          throw new IllegalArgumentException(
              "[ChannelFactory.createChannel( NetConnector )] "
                  + "Invalid NetConnector {conn="+ conn+ "}");
        }
        return new HttpRequestChannel(conn)
            .setCryptAlgorithm(algo)
            .setEncryptionEnabled(crypt)
            .setGZipCompressionEnabled(gzip);
      }
      @Override
      public HttpRequestChannel createChannel(HttpConnector conn, ObjectSerializer serial) {
        if(conn == null) {
          throw new IllegalArgumentException(
              "[ChannelFactory.createChannel( NetConnector )] "
                  + "Invalid NetConnector {conn="+ conn+ "}");
        }
        return new HttpRequestChannel(conn, serial)
            .setCryptAlgorithm(algo)
            .setEncryptionEnabled(crypt)
            .setGZipCompressionEnabled(gzip);
      }
    };
  }
  
  
  /**
   * Create a HTTP response channel factory.
   * @return ChannelFactory&lt;HttpServerConnection&gt;
   */
  public ChannelFactory<HttpServerConnection> createHttpResponseChannelFactory() {
    return new ChannelFactory<HttpServerConnection>() {
      @Override
      public HttpResponseChannel createChannel(HttpServerConnection conn) {
        if(conn == null || !conn.isOpen()) {
          throw new IllegalArgumentException(
              "[ChannelFactory.createChannel( HttpServerConnection )] "
              + "Invalid HttpServerConnection {conn="+ conn+ "}");
        }
        return new HttpResponseChannel(conn);
      }
      @Override
      public HttpResponseChannel createChannel(HttpServerConnection conn, ObjectSerializer serial) {
        if(conn == null || !conn.isOpen()) {
          throw new IllegalArgumentException(
              "[ChannelFactory.createChannel( HttpServerConnection )] "
              + "Invalid HttpServerConnection {conn="+ conn+ "}");
        }
        return new HttpResponseChannel(conn, serial);
      }
    };
  }
  
}
