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

package us.pserver.revok;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a list of methods wich will be invoked in sequence 
 * (i.e.: someobj.toString().length() ).
 * 
 * @author Juno Roesler - juno@pserver.com
 * @version 1.1 - 201506
 */
public class MethodChain {

  private List<RemoteMethod> meths;
  
  private Iterator<RemoteMethod> iter;
  
  private RemoteMethod curr, lastadd;
  
  
  /**
   * Default Constructor without arguments.
   */
  public MethodChain() {
    meths = new LinkedList<>();
    iter = null;
    curr = null;
    lastadd = null;
  }
  
  
  /**
   * Add a RemoteMethod to the sequence.
   * @param rm RemoteMethod to be added.
   * @return This instance of MethodChain.
   */
  public MethodChain add(RemoteMethod rm) {
    if(rm != null) {
      meths.add(rm);
      lastadd = rm;
    }
    return this;
  }
  
  
  /**
   * Add a RemoteMethod constructed with passed arguments.
   * @param objname Object name.
   * @param method Method name.
   * @return The created RemoteMethod object.
   */
  public RemoteMethod add(String objname, String method) {
    if(objname != null && method != null) {
      RemoteMethod rm = new RemoteMethod(objname, method);
      meths.add(rm);
      lastadd = rm;
      return rm;
    }
    return null;
  }
  
  
  /**
   * Add a RemoteMethod constructed with passed argument.
   * @param method Method name.
   * @return The created RemoteMethod object.
   */
  public RemoteMethod add(String method) {
    if(method != null) {
      RemoteMethod rm = new RemoteMethod()
          .setMethodName(method);
      meths.add(rm);
      lastadd = rm;
      return rm;
    }
    return null;
  }
  
  
  /**
   * Return the last RemoteMethod added.
   * @return The last RemoteMethod added.
   */
  public RemoteMethod lastAdded() {
    return lastadd;
  }
  
  
  /**
   * Return a list with the RemoteMethod's.
   * @return java.util.List.
   */
  public List<RemoteMethod> methods() {
    return meths;
  }
  
  
  /**
   * Reset the internal iterator.
   * @return This instance of MethodChain.
   */
  public MethodChain rewind() {
    iter = null;
    curr = null;
    return this;
  }
  
  
  /**
   * Return the current RemoteMethod of the internal iterator.
   * @return The current RemoteMethod of the internal iterator.
   */
  public RemoteMethod current() {
    if(curr == null) return next();
    return curr;
  }
  
  
  /**
   * Return the next element of the internal iterator.
   * @return The next element of the internal iterator.
   */
  public RemoteMethod next() {
    if(iter == null)
      iter = meths.iterator();
    if(iter.hasNext())
      curr = iter.next();
    else
      curr = null;
    return curr;
  }
  
  
  /**
   * Verifies if the internal iterator has next element.
   * @return <code>true</code> if the internal iterator has a next element, <code>false</code> in other case.
   */
  public boolean hasNext() {
    if(iter == null)
      iter = meths.iterator();
    return iter.hasNext();
  }


  @Override
  public String toString() {
    return stringChain();
    //return "MethodChain{ methods = " + meths.size() + " }";
  }
  
  
  /**
   * Return the String representation of this MethodChain (Same as toString()).
   * @return The String representation of this MethodChain (Same as toString()).
   */
  public String stringChain() {
    StringBuffer sb = new StringBuffer();
    meths.forEach(rm->sb.append(rm.toString()));
    return sb.toString();
  }
  
}
