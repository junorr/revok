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
import java.io.InputStream;

/**
 * A fake implementation of InputStream wich does nothing (what?).
 * This is used for transfering information about method arguments 
 * over the wire, but not the InputStream itself.
 * 
 * @author Juno Roesler - juno.rr@gmail.com
 * @version 1.1 - 20150422
 */
public class FakeInputStreamRef extends InputStream {

  /**
   * Default constructor without arguments.
   */
  public FakeInputStreamRef() {}

  
  @Override
  public int read() throws IOException {
    return -1;
  }


  @Override
  public String toString() {
    return "java.io.InputStream";
  }
  
}
