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

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.CompactWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import us.pserver.cdr.StringByteConverter;

/**
 * An object serializer for (de)serialize objects to/from XML format.
 * 
 * @author Juno Roesler - juno.rr@gmail.com
 * @version 1.0 - 11/05/2015
 */
public class XmlSerializer implements ObjectSerializer {

  private StringByteConverter scv;
  
  private XStream xst;
  
  
  /**
   * Default constructor without arguments.
   */
  public XmlSerializer() {
    scv = new StringByteConverter();
    xst = new XStream();
  }
  

  @Override
  public byte[] toBytes(Object o) throws IOException {
    if(o == null)
      throw new IOException("XmlSerializer.toBytes( Object ): Invalid Object {"+ o+ "}");
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    xst.marshal(o, new CompactWriter(new OutputStreamWriter(bos)));
    return bos.toByteArray();
  }


  @Override
  public Object fromBytes(byte[] bytes) throws IOException {
    if(bytes == null || bytes.length < 1)
      throw new IOException(
          "XmlSerializer.fromBytes( byte[] ): "
              + "Invalid Byte Array {"
              + (bytes != null ? "bytes.length="+ bytes.length : bytes)+ "}");
    return xst.fromXML(new ByteArrayInputStream(bytes));
  }

}
