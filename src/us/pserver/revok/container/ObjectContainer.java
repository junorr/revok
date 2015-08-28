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

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import us.pserver.tools.Reflector;
import us.pserver.tools.Valid;


/**
 * Objects container on server, biding 
 * them to a recovery <code>String</code> key.
 * <code>ObjectContainer</code> is secure for 
 * multithreaded environments.
 * 
 * @author Juno Roesler - juno@pserver.com
 * @version 1.1 - 201506
 */
public class ObjectContainer {
  
  /**
   * <code>
   *  NAMESPACE_GLOBAL = "global"
   * </code><br>
   * Glogal namespace container.
   */
  public static final String NAMESPACE_GLOBAL = "global";
  
  /**
   * <code>
   *  CONTAINER_KEY = ObjectContainer.class.getSimpleName()
   * </code><br>
   * Key for recovering the ObjectContainer instance.
   */
  public static final String CONTAINER_KEY = ObjectContainer.class.getSimpleName();
  
  
  private final Map<String, Map<String, Object>> space;
  
  private Authenticator auth;
  
  
  /**
   * Default constructor without arguments.
   */
  public ObjectContainer() {
    space = new ConcurrentHashMap<>();
    space.put(NAMESPACE_GLOBAL, new ConcurrentHashMap<>());
    space.get(NAMESPACE_GLOBAL).put(CONTAINER_KEY, this);
  }
  
  
  /**
   * Constructor which receives an <code>Authenticator</code> object.
   * @param a <code>Authenticator</code>
   */
  public ObjectContainer(Authenticator a) {
    this();
    auth = Valid.off(a).forNull()
        .getOrFail(Authenticator.class);
  }
  
  
  /**
   * Set the <code>Authenticator</code> object.
   * @param a <code>Authenticator</code> object.
   * @return This modified <code>ObjectContainer</code> instance.
   */
  public ObjectContainer setAuthenticator(Authenticator a) {
    auth = a;
    return this;
  }
  

  /**
   * Get the <code>Authenticator</code> object.
   * @return <code>Authenticator</code> object.
   */
  public Authenticator getAuthenticator() {
    return auth;
  }
  
  
  /**
   * Verify if the authentication is enabled 
   * on this container.
   * @return <code>true</code> if the authentication
   * is enabled on this container.
   */
  public boolean isAuthEnabled() {
    return auth != null;
  }
  
  
  /**
   * Insert an object with the specified key
   * on this object container.
   * @param name Recovery key <code>String</code>.
   * @param obj Stored object.
   * @return This modified <code>ObjectContainer</code> instance.
   */
  public ObjectContainer put(String name, Object obj) {
    if(name != null && obj != null) {
      if(!name.contains("."))
        throw new IllegalArgumentException(
            "[ObjectContainer.put( String, Object )] "
                + "Namespace missing. Name argument must be provided like: <namespace>.<object_name>");
      String[] names = split(name);
      put(names[0], names[1], obj);
    }
    return this;
  }
  
  
  private String[] split(String str) {
    if(!str.contains(".")) return new String[0];
    int ip = str.indexOf(".");
    String[] ss = new String[2];
    ss[0] = str.substring(0, ip);
    ss[1] = str.substring(ip+1);
    return ss;
  }
  
  
  /**
   * Insert an object with the specified namespace and key
   * on this object container.
   * @param namespace The namespace of the stored object.
   * @param name Recovery key <code>String</code>.
   * @param obj Stored object.
   * @return This modified <code>ObjectContainer</code> instance.
   */
  public ObjectContainer put(String namespace, String name, Object obj) {
    if(namespace == null)
      throw new IllegalArgumentException(
          "[ObjectContainer.put( String, String, Object )] "
              + "Invalid Namespace {"+ namespace+ "}");
    if(name == null)
      throw new IllegalArgumentException(
          "[ObjectContainer.put( String, String, Object )] "
              + "Invalid name {"+ name+ "}");
    if(obj == null)
      throw new IllegalArgumentException(
          "[ObjectContainer.put( String, String, Object )] "
              + "Invalid Object {"+ obj+ "}");
    if(!space.containsKey(namespace)) {
      space.put(namespace, new ConcurrentHashMap<>());
    }
    space.get(namespace).put(name, obj);
    return this;
  }
  
  
  /**
   * Removes an object with the specified key
   * from this object container.
   * @param name Recovery key <code>String</code>.
   * @return The removed object.
   * @throws AuthenticationException In case of container authentication error.
   */
  public Object remove(String name) throws AuthenticationException {
    if(isAuthEnabled())
      throw new AuthenticationException("[ObjectContainer.remove( String )] Authentication needed");
    if(!name.contains("."))
      throw new IllegalArgumentException(
          "[ObjectContainer.remove( String )] "
              + "Namespace missing. Name argument must be provided like: <namespace>.<object_name>");
    String[] names = split(name);
    if(space.containsKey(names[0])) {
      return space.get(names[0]).remove(names[1]);
    }
    return null;
  }
  
  
  /**
   * Removes an object with the specified key
   * from this object container.
   * @param c <code>Credentials</code> object for authentication.
   * @param name Recovery key <code>String</code>.
   * @return The removed object.
   * @throws AuthenticationException In case the authentication fails for the <code>Credentials</code> object.
   */
  public Object remove(Credentials c, String name) throws AuthenticationException {
    if(isAuthEnabled()) {
      auth.authenticate(c);
    }
    if(!name.contains("."))
      throw new IllegalArgumentException(
          "[ObjectContainer.remove( Credentials, String )] "
              + "Namespace missing. Name argument must be provided like: <namespace>.<object_name>");
    String[] names = split(name);
    if(space.containsKey(names[0])) {
      return space.get(names[0]).remove(names[1]);
    }
    return null;
  }
  
  
  /**
   * Verify if this object container contains
   * an stored object with the specified name/namespace.
   * Verifica se existe um objeto armazenado,
   * identificado pela chave fornecida.
   * @param name Recovery key/namespace <code>String</code>.
   * @return <code>true</code> if exists an stored object
   * with the specified key/namespace in this object container,
   * <code>false</code> otherwise.
   */
  public boolean contains(String name) {
    if(!name.contains("."))
      throw new IllegalArgumentException(
          "[ObjectContainer.contains( String )] "
              + "Namespace missing. Name argument must be provided like: <namespace>.<object_name>");
    String[] names = split(name);
    if(space.containsKey(names[0])) {
      return space.get(names[0]).containsKey(names[1]);
    }
    return false;
  }
  
  
  /**
   * Get an stored object with the specified key
   * from this object container.
   * @param name Recovery key <code>String</code>.
   * @return The removed object.
   * @throws AuthenticationException In caso of container authentication error.
   */
  public Object get(String name) throws AuthenticationException {
    if(isAuthEnabled())
      throw new AuthenticationException("[ObjectContainer.get( String )] Authentication needed");
    if(!name.contains("."))
      throw new IllegalArgumentException(
          "[ObjectContainer.get( String )] "
              + "Namespace missing. Name argument must be provided like: <namespace>.<object_name>");
    String[] names = split(name);
    if(space.containsKey(names[0])) {
      return space.get(names[0]).get(names[1]);
    }
    return null;
  }
  
  
  /**
   * Get an stored object with the specified key
   * from this object container.
   * @param c <code>Credentials</code> object for authentication.
   * @param name Recovery key <code>String</code>.
   * @return The removed object.
   * @throws AuthenticationException In case the authentication fails for the <code>Credentials</code> object.
   */
  public Object get(Credentials c, String name) throws AuthenticationException {
    if(!name.contains("."))
      throw new IllegalArgumentException(
          "[ObjectContainer.get( Credentials, String )] "
              + "Namespace missing. Name argument must be provided like: <namespace>.<object_name>");
    String[] names = split(name);
    if(isAuthEnabled()) {
      Credentials serverCreds = auth.authenticate(c);
      // Verify access to namespace
      // '*' represents all namespaces
      if(!serverCreds.accessList().contains(names[0])
          && !serverCreds.accessList().contains("*")) {
        throw new AuthenticationException(serverCreds+ " do not have access to namespace{ "+ names[0]+ " }");
      }
    }
    if(space.containsKey(names[0])) {
      return space.get(names[0]).get(names[1]);
    }
    return null;
  }
  
  
  /**
   * Get the number of namespaces.
   * @return number of namespaces <code>int</code>.
   */
  public int namespaceSize() {
    return space.size();
  }
  
  
  /**
   * Get the number of stored objects for specified namespace.
   * @param namespace The namespace.
   * @return number of stored objects for specified namespace.
   */
  public int sizeOf(String namespace) {
    if(namespace == null || !space.containsKey(namespace))
      return -1;
    return space.get(namespace).size();
  }
  
  
  /**
   * Get a list of all namespaces on this object container.
   * @return list of all namespaces on this object container.
   */
  public List<String> namespaces() {
    List<String> nsp = new LinkedList<>();
    space.keySet().stream().forEach(m->nsp.add(m));
    return nsp;
  }
  
  
  /**
   * Get a list of all objects for the specified namespace.
   * @param namespace The namespace.
   * @return list of all objects for the specified namespace.
   */
  public List<String> objects(String namespace) {
    List<String> nsp = new LinkedList<>();
    if(namespace == null || !space.containsKey(namespace))
      return nsp;
    space.get(namespace).keySet().stream().forEach(no->nsp.add(no));
    return nsp;
  }
  
  
  /**
   * Get a list of all methods for the specified object 
   * stored in this object container.
   * @param name The object name/namespace.
   * @return list of all methods for the specified object.
   */
  public List<String> listMethods(String name) {
    if(!name.contains("."))
      throw new IllegalArgumentException(
          "[ObjectContainer.get( Credentials, String )] "
              + "Namespace missing. Name argument must be provided like: <namespace>.<object_name>");
    List<String> mts = new LinkedList<>();
    String[] names = split(name);
    if(space.containsKey(names[0])) {
      Object o = space.get(names[0]).get(names[1]);
      if(o != null) {
        Reflector ref = new Reflector();
        Method[] ms = ref.on(o).methods();
        Arrays.asList(ms).forEach(m->mts.add(m.toString()));
      }
    }
    return mts;
  }

}
