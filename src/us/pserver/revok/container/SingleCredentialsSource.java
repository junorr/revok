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

package us.pserver.revok.container;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static us.pserver.chk.Checker.nullarg;
import static us.pserver.chk.Checker.nullstr;

/**
 * A CredentialsSource based on a single Credentials object.
 * @author Juno Roesler - juno.rr@gmail.com
 * @version 1.1 - 20150422
 */
public class SingleCredentialsSource implements CredentialsSource {

  private Credentials cred;
  
  
  /**
   * Default constructor wich receives the single Credentials object.
   * @param c The Credentials object.
   */
  public SingleCredentialsSource(Credentials c) {
    nullarg(Credentials.class, c);
    nullstr(c.getUser());
    cred = c;
  }
  
  
  @Override
  public Credentials get(String username) {
    if(cred.getUser().equals(username))
      return cred;
    return null;
  }
  
  
  /**
   * Get the single Credentials object.
   * @return The single Credentials object.
   */
  public Credentials credentials() {
    return cred;
  }
  
  
  /**
   * Set the backend single Credentials object.
   * @param c The Credentials object.
   * @return This instance of SingleCredentialsSource.
   */
  public SingleCredentialsSource setCredentials(Credentials c) {
    nullarg(Credentials.class, c);
    cred = c;
    return this;
  }
  
  
  @Override
  public List<Credentials> getCredentials() {
    return Collections.unmodifiableList(
        Arrays.asList(cred));
  }
  
}
