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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

/**
 * A CredentialsSource based on a list of Credentials objects.
 * @author Juno Roesler - juno.rr@gmail.com
 * @version 1.1 - 20150422
 */
public class ListCredentialsSource implements CredentialsSource, List<Credentials> {

  private List<Credentials> creds;
  
  
  /**
   * Default constructor without arguments.
   */
  public ListCredentialsSource() {
    creds = new LinkedList<>();
  }
  

  @Override
  public List<Credentials> getCredentials() {
    return Collections.unmodifiableList(creds);
  }
  
  
  @Override
  public Credentials get(String username) {
    if(username == null || username.trim().isEmpty())
      return null;
    Optional<Credentials> opt = creds.stream()
        .filter(c->c.getUser() != null 
            && c.getUser().equals(username)).findFirst();
    return (opt.isPresent() ? opt.get() : null);
  }


  @Override
  public int size() {
    return creds.size();
  }


  @Override
  public boolean isEmpty() {
    return creds.isEmpty();
  }


  @Override
  public boolean contains(Object o) {
    return creds.contains(o);
  }


  @Override
  public Iterator<Credentials> iterator() {
    return creds.iterator();
  }


  @Override
  public Object[] toArray() {
    return creds.toArray();
  }


  @Override
  public <T> T[] toArray(T[] a) {
    return creds.toArray(a);
  }


  @Override
  public boolean add(Credentials e) {
    return creds.add(e);
  }


  @Override
  public boolean remove(Object o) {
    return creds.remove(o);
  }


  @Override
  public boolean containsAll(Collection<?> c) {
    return creds.containsAll(c);
  }


  @Override
  public boolean addAll(Collection<? extends Credentials> c) {
    return creds.addAll(c);
  }


  @Override
  public boolean addAll(int index, Collection<? extends Credentials> c) {
    return creds.addAll(index, c);
  }


  @Override
  public boolean removeAll(Collection<?> c) {
    return creds.removeAll(c);
  }


  @Override
  public boolean retainAll(Collection<?> c) {
    return creds.retainAll(c);
  }


  @Override
  public void clear() {
    creds.clear();
  }


  @Override
  public Credentials get(int index) {
    return creds.get(index);
  }


  @Override
  public Credentials set(int index, Credentials element) {
    return creds.set(index, element);
  }


  @Override
  public void add(int index, Credentials element) {
    creds.add(index, element);
  }


  @Override
  public Credentials remove(int index) {
    return creds.remove(index);
  }


  @Override
  public int indexOf(Object o) {
    return creds.indexOf(o);
  }


  @Override
  public int lastIndexOf(Object o) {
    return creds.lastIndexOf(o);
  }


  @Override
  public ListIterator<Credentials> listIterator() {
    return creds.listIterator();
  }


  @Override
  public ListIterator<Credentials> listIterator(int index) {
    return creds.listIterator(index);
  }


  @Override
  public List<Credentials> subList(int fromIndex, int toIndex) {
    return creds.subList(fromIndex, toIndex);
  }

}
