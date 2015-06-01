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
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import static us.pserver.chk.Checker.nullarray;
import static us.pserver.chk.Checker.nullstr;

/**
 * The Credentials object encapsulates information 
 * about username and password for authentication 
 * with the server.
 * @author Juno Roesler - juno.rr@gmail.com
 * @version 1.1 - 20150422
 */
public class Credentials {

  private String user;
  
  private byte[] pswd;
  
  private List<String> access;
  
  
  /**
   * Constructor without arguments.
   */
  public Credentials() {
    user = null;
    pswd = null;
    access = new LinkedList<>();
  }
  
  
  /**
   * Constructor wich receives the user name and byte array password.
   * @param user User name
   * @param pswd Byte array password.
   */
  public Credentials(String user, byte[] pswd) {
    nullstr(user);
    nullarray(pswd);
    this.user = user;
    this.pswd = pswd;
    access = new LinkedList<>();
  }
  
  
  /**
   * Add access to the specified server namespace.
   * @param label Server namespace.
   * @return This modified <code>Credentials</code> instance.
   */
  public Credentials addAccess(String label) {
    if(label != null) {
      access.add(label);
    }
    return this;
  }
  
  
  /**
   * Add access to all the specified server namespaces.
   * @param labels Server namespaces.
   * @return This modified <code>Credentials</code> instance.
   */
  public Credentials addAccess(String ... labels) {
    if(labels != null && labels.length > 0) {
      access.addAll(Arrays.asList(labels));
    }
    return this;
  }
  
  
  /**
   * Get the list of access namespaces.
   * @return list of access namespaces.
   */
  public List<String> accessList() {
    return access;
  }
  
  
  /**
   * Set the user name.
   * @param u User name
   * @return This modified <code>Credentials</code> instance.
   */
  public Credentials setUser(String u) {
    if(u != null) user = u;
    return this;
  }
  
  
  /**
   * Set the password.
   * @param p Byte array password.
   * @return This modified <code>Credentials</code> instance.
   */
  public Credentials setPassword(byte[] p) {
    if(p != null && p.length > 0)
      pswd = p;
    return this;
  }
  
  
  /**
   * Return the user name.
   * @return User name.
   */
  public String getUser() {
    return user;
  }
  
  
  /**
   * Authenticate this Credentials against other 
   * <code>Credentials</code> instance. 
   * The equals method could be used instead.
   * @param c Another credentials object.
   * @return <code>true</code> if the user name and 
   * password of the another instance, is exactly the 
   * same of this instance of Credentials.
   */
  public boolean authenticate(Credentials c) {
    return c != null && c.getUser() != null
        && user.equals(c.user)
        && equals(pswd, c.pswd);
  }
  
  
  /**
   * Utility method for comparing the content of two byte arrays.
   * @param c1 First byte array.
   * @param c2 Second byte array.
   * @return <code>true</code> if all elements of the two 
   * byte arrays are equals, <code>false</code> otherwise.
   */
  public static boolean equals(byte[] c1, byte[] c2) {
    if(c1 == null || c2 == null)
      return false;
    if(c1.length != c2.length)
      return false;
    boolean eq = true;
    for(int i = 0; i < c1.length; i++) {
      eq = eq && c1[i] == c2[i];
    }
    return eq;
  }


  @Override
  public int hashCode() {
    int hash = 7;
    hash = 79 * hash + Objects.hashCode(this.user);
    hash = 79 * hash + Arrays.hashCode(this.pswd);
    return hash;
  }


  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Credentials other = (Credentials) obj;
    if (!Objects.equals(this.user, other.user)) {
      return false;
    }
    if (!equals(this.pswd, other.pswd)) {
      return false;
    }
    return true;
  }


  @Override
  public String toString() {
    return "Credentials{ user = " + user + " }";
  }
  
}
