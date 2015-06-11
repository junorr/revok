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

package us.pserver.revok.http;

/**
 * Constants values used in HTTP communication protocol.
 * 
 * @author Juno Roesler - juno@pserver.com
 * @version 1.1 - 201506
 */
public interface HttpConsts {

  /**
   * <code>
   *  STATUS_200 = 200
   * </code><br>
   * Http 200 Response Code.
   */
  public static final int STATUS_200 = 200;
  
  /**
   * <code>
   *  STATUS_OK = "OK"
   * </code><br>
   * Http OK Response Reason.
   */
  public static final String STATUS_OK = "OK";
  
  /**
   * <code>
   *  AMPERSAND = "&amp;"
   * </code><br>
   * '&amp;'.
   */
  public static final String AMPERSAND = "&";

  /**
   * <code>
   *  QUERY = "?"
   * </code><br>
   * '?'.
   */
  public static final String QUERY = "?";

  /**
   * <code>
   *  CRLF = "\r\n"
   * </code><br>
   * Carriage return and line break characters.
   */
  public static final String CRLF = "\r\n";
  
  /**
   * <code>
   *  GET = "GET"
   * </code><br>
   * String for Http GET Method.
   */
  public static final String GET = "GET";
  
  /**
   * <code>
   *  POST = "POST"
   * </code><br>
   * String for Http POST Method.
   */
  public static final String POST = "POST";

  /**
   * <code>
   *  EQ = "="
   * </code><br>
   * Equal signal.
   */
  public static final String EQ = "=";

  /**
   * <code>
   *  SEMICOLON = ";"
   * </code><br>
   * String for Semicolon char.
   */
  public static final String SEMICOLON = ";";

  /**
   * <code>
   *  COLON = ":"
   * </code><br>
   * String for Colon char.
   */
  public static final String COLON = ":";

  /**
   * <code>
   *  SP = " "
   * </code><br>
   * String for white space char.
   */
  public static final String SP = " ";

  /**
   * <code>
   *  HTTP = "http://"
   * </code><br>
   * Http URI start.
   */
  public static final String HTTP = "http://";

  /**
   * <code>
   *  SLASH = "/"
   * </code><br>
   * String for slash char.
   */
  public static final String SLASH = "/";

  /**
   * <code>
   *  DASH = "-"
   * </code><br>
   * String for Dash char.
   */
  public static final String DASH = "-";
  
  /**
   * <code>
   *  HD_VAL_USER_AGENT = "Mosilla/5.0"
   * </code><br>
   * Http User-Agent Header Value.
   */
  public static final String HD_VAL_USER_AGENT = "Mosilla/5.0";

  /**
   * <code>
   *  HD_ACCEPT = "Accept"
   * </code><br>
   * Http Accept Header.
   */
  public static final String HD_ACCEPT = "Accept";

  /**
   * <code>
   *  HD_VAL_ACCEPT = "text/xml,application/x-java-rob"
   * </code><br>
   * Http Accept Header Value.
   */
  public static final String HD_VAL_ACCEPT = "text/xml,application/x-java-rob";

  /**
   * <code>
   *  HD_CONT_ENCODING = "Content-Encoding"
   * </code><br>
   * Http Content-Encoding Header.
   */
  public static final String HD_CONT_ENCODING = "Content-Encoding";

  /**
   * <code>
   *  HD_VAL_DEF_ENCODING = "deflate"
   * </code><br>
   * Http Content-Encoding Header Value.
   */
  public static final String HD_VAL_DEF_ENCODING = "deflate";

  /**
   * <code>
   *  HD_VAL_GZIP_ENCODING = "gzip"
   * </code><br>
   * Http Content-Encoding Header Value.
   */
  public static final String HD_VAL_GZIP_ENCODING = "gzip";

  /**
   * <code>
   *  HD_VAL_SERVER = "httpcore-revok"
   * </code><br>
   * Http Content-Encoding Header Value.
   */
  public static final String HD_VAL_SERVER = "httpcore-revok";

  /**
   * <code>
   *  HD_PROXY_AUTHORIZATION = "Proxy-Authorization"
   * </code><br>
   * Http Proxy-Authorization header.
   */
  public static final String HD_PROXY_AUTH = "Proxy-Authorization";

  /**
   * <code>
   *  UTF8 = "UTF-8"
   * </code><br>
   * UTF-8 Character encoding.
   */
  public static final String UTF8 = "UTF-8";
  
}
