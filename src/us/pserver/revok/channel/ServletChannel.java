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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import us.pserver.cdr.crypt.CryptKey;
import us.pserver.revok.http.HttpConsts;
import us.pserver.revok.http.HttpEntityFactory;
import us.pserver.revok.http.HttpEntityParser;
import us.pserver.revok.protocol.ObjectSerializer;
import us.pserver.revok.protocol.Transport;
import us.pserver.streams.IO;

/**
 * Network channel for communication throught 
 * HttpServletRequest and HttpServletResponse.
 * 
 * @author Juno Roesler - juno@pserver.com
 * @version 1.1 - 201506
 */
public class ServletChannel implements Channel {
  
  private HttpServletRequest request;
  
  private HttpServletResponse response;
  
  private ObjectSerializer serial;
  
  private OutputStream output;
  
  private CryptKey key;
  
  private boolean gzip;
  
  private boolean valid;
  
  
  /**
   * Default constructor.
   * @param req The http request <code>HttpServletRequest</code>.
   * @param resp The response <code>HttpServletResponse</code>.
   * @param os <code>ObjectSerializer</code> for objects serializing.
   * @throws ServletException  In case of arguments error.
   */
  public ServletChannel(HttpServletRequest req, HttpServletResponse resp, ObjectSerializer os) throws ServletException {
    if(req == null)
      throw new ServletException("Invalid HttpServletRequest: "+ req);
    if(resp == null)
      throw new ServletException("Invalid HttpServletResponse: "+ resp);
    if(os == null)
      throw new ServletException("Invalid ObjectSerializer: "+ os);
    request = req;
    response = resp;
    serial = os;
    output = null;
    valid = true;
    key = null;
    gzip = false;
  }


  @Override
  public void write(Transport trp) throws IOException {
    HttpEntityFactory fact = HttpEntityFactory.instance(serial);
    if(gzip) {
      fact.enableGZipCoder();
      response.addHeader(HttpConsts.HD_CONT_ENCODING, 
          HttpConsts.HD_VAL_GZIP_ENCODING);
    } else {
      response.addHeader(HttpConsts.HD_CONT_ENCODING, 
          HttpConsts.HD_VAL_DEF_ENCODING);
    }
    if(key != null) fact.enableCryptCoder(key);
    if(trp.hasContentEmbedded())
      fact.put(trp.getInputStream());
    fact.put(trp.createWriteVersion());
    InputStream inresp = fact.createStream();
    output = response.getOutputStream();
    IO.tc(inresp, output);
    valid = false;
  }


  @Override
  public Transport read() throws IOException {
    HttpEntityParser parser = HttpEntityParser.instance(serial);
    String enc = request.getHeader(HttpConsts.HD_CONT_ENCODING);
    gzip = HttpConsts.HD_VAL_GZIP_ENCODING.equals(enc);
    if(gzip) parser.enableGZipCoder();
    
    parser.parse(request.getInputStream());
    if(parser.getObject() == null) {
      throw new IOException("Invalid request. No object readed");
    }
    if(!Transport.class.isAssignableFrom(parser.getObject().getClass())) {
      String msg = "Invalid request. Can not handle object type: "+ parser.getObject().getClass();
      throw new IOException("Invalid request. Can not handle object type: "
          + parser.getObject().getClass());
    }
    Transport trp = (Transport) parser.getObject();
    if(parser.getInputStream() != null) {
      trp.setInputStream(parser.getInputStream());
    }
    return trp;
  }


  @Override
  public void close() {
    if(output == null) {
      try { output.close(); }
      catch(IOException e) {}
    }
  }


  @Override
  public boolean isValid() {
    return valid;
  }

}
