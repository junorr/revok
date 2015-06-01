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

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;
import java.io.IOException;
import us.pserver.cdr.StringByteConverter;

/**
 * An object serializer for (de)serialize objects to/from JSON format.
 * 
 * @author Juno Roesler - juno.rr@gmail.com
 * @version 1.0 - 11/05/2015
 */
public class JsonSerializer implements ObjectSerializer {

  private StringByteConverter scv;
  
  
  /**
   * Default contructor without arguments.
   */
  public JsonSerializer() {
    scv = new StringByteConverter();
  }
  
  
  @Override
  public byte[] toBytes(Object o) throws IOException {
    if(o == null)
      throw new IOException("JsonSerializer.toBytes( Object ): Invalid Object {"+ o+ "}");
    return scv.convert(JsonWriter.objectToJson(o));
  }


  @Override
  public Object fromBytes(byte[] bytes) throws IOException {
    if(bytes == null || bytes.length < 1)
      throw new IOException(
          "JsonSerializer.fromBytes( byte[] ): "
              + "Invalid Byte Array {"
              + (bytes != null ? "bytes.length="+ bytes.length : bytes)+ "}");
    return JsonReader.jsonToJava(scv.reverse(bytes));
  }
  
}
