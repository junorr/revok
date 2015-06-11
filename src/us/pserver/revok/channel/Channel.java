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


/**
 * Channel interface for network communication.
 * 
 * @author Juno Roesler - juno@pserver.com
 * @version 1.1 - 201506
 */
public interface Channel {
  
  
  /**
   * Writes a <code>Transport</code> object on this transmission channel.
   * @param trp <code>Transport</code> object to write.
   * @throws IOException In case of error writing the object.
   * @see us.pserver.revok.protocol.Transport
   */
  public void write(Transport trp) throws IOException;
  
  
  /**
   * Reads a <code>Transport</code> object from this transmission channel.
   * @return <code>Transport</code> object readed.
   * @throws IOException In case of error reading the object.
   * @see us.pserver.revok.protocol.Transport
   */
  public Transport read() throws IOException;
  
  
  /**
   * Close the transmission channel.
   */
  public void close();
  
  
  /**
   * Verifies if this channel is still valid for transmissions.
   * @return <code>true</code> if this channel is still valid for transmissions, 
   * <code>false</code> otherwise.
   */
  public boolean isValid();
  
}
