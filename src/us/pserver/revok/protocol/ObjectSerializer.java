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

/**
 * Interface defining an object converter to and from byte array.
 * 
 * @author Juno Roesler - juno.rr@gmail.com
 * @version 1.0 - 11/05/2015
 */
public interface ObjectSerializer {

  /**
   * Serialize an object to byte array.
   * @param o Object to serilize.
   * @return Byte array from the serilized object.
   * @throws IOException In case of error serilizing the object.
   */
  public byte[] toBytes(Object o) throws IOException;
  
  /**
   * Deserialize an object from byte array.
   * @param bytes Byte array with a serilized object
   * @return An object deserilized from the byte array.
   * @throws IOException In case of error deserilizing the object.
   */
  public Object fromBytes(byte[] bytes) throws IOException;
  
}
