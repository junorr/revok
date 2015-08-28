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

import us.pserver.revok.container.Credentials;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import us.pserver.revok.http.HttpConsts;
import us.pserver.revok.reflect.Invoker;

/**
 * Represents a remote getMethodName to be invoked on a remote object.
 * Contains informations about getMethodName name, object name, arguments and setTypes.
 * 
 * @author Juno Roesler - juno@pserver.com
 * @version 1.1 - 201506
 */
public class RemoteMethod {
  
  private String objname;
  
  private String method;
  
  private List parameters;
  
  private List<Class> types;
  
  private Credentials cred;
  
  private String retvar;
  
  
  /**
   * Default Constructor without arguments.
   */
  public RemoteMethod() {
    objname = null;
    method = null;
    parameters = new LinkedList();
    types = new LinkedList<>();
    cred = null;
    retvar = null;
  }

  
  /**
   * Constructor which receives object and getMethodName names.
   * @param ownerObject Owner object name.
   * @param name Method name.
   */
  public RemoteMethod(String ownerObject, String name) {
    method = name;
    objname = ownerObject;
    parameters = new LinkedList();
    types = new LinkedList<>();
    cred = null;
  }
  
  
  /**
   * Set the authentication <code>Credentials</code> object with server.
   * @param c Credentials object.
   * @return This modified <code>RemoteMethod</code> instance.
   */
  public RemoteMethod setCredentials(Credentials c) {
    cred = c;
    return this;
  }
  
  
  /**
   * Return the Credentials object to authentication with server.
   * @return Credentials object.
   */
  public Credentials getCredentials() {
    return cred;
  }
  
  
  /**
   * Adds a method parameter type and value.
   * @param type Method parameter type.
   * @param obj Method parameter value.
   * @return This modified <code>RemoteMethod</code> instance.
   */
  public RemoteMethod addParameter(Class type, Object obj) {
    if(type != null && obj != null) {
      types.add(type);
      parameters.add(obj);
    }
    return this;
  }
  

  /**
   * Adds a getMethodName argument.
   * @param obj Method argument.
   * @return This modified <code>RemoteMethod</code> instance.
   */
  public RemoteMethod addParameter(Object obj) {
    parameters.add(obj);
    return this;
  }
  

  /**
   * Adds a class type of a getMethodName argument.
   * @param cls Class type.
   * @return This modified <code>RemoteMethod</code> instance.
   */
  public RemoteMethod addType(Class cls) {
    if(cls != null)
      types.add(cls);
    return this;
  }
  
  
  /**
   * Set a variable in the server which will receive the 
 returned value of the getMethodName. The variable name must 
   * start with '$' and contains the namespace, dot separeted (i.e: '$global.myvar').
   * @param var Variable name.
   * @return This modified <code>RemoteMethod</code> instance.
   */
  public RemoteMethod setReturnVariable(String var) {
    if(var == null || !var.startsWith(Invoker.VAR_SIGNAL))
      throw new IllegalArgumentException(
          "Invalid var name {"+ var
              + "}. Variables must starts with '"
              + Invoker.VAR_SIGNAL+ "'"
      );
    retvar = var;
    return this;
  }
  
  
  /**
   * Get the return variable in the server.
   * @return Variable name <code>String</code>.
   */
  public String getReturnVariable() {
    return retvar;
  }
  
  
  /**
   * Clear the argument list.
   * @return This modified <code>RemoteMethod</code> instance.
   */
  public RemoteMethod clearParameters() {
    parameters.clear();
    return this;
  }
  
  
  /**
   * Clear the list of classes of the getMethodName arguments.
   * @return This modified <code>RemoteMethod</code> instance.
   */
  public RemoteMethod clearTypes() {
    types.clear();
    return this;
  }
  
  
  /**
   * Return a list with the classes of the getMethodName arguments.
   * @return java.util.List
   */
  public List<Class> getTypes() {
    return types;
  }
  
  
  /**
   * Return a list with the getMethodName arguments.
   * @return java.util.List
   */
  public List getParameters() {
    return parameters;
  }
  
  
  /**
   * Return an array with the classes of the getMethodName arguments.
   * @return Array of Class
   */
  public Class[] getTypesArray() {
    Class[] cls = new Class[types.size()];
    if(types.isEmpty()) return cls;
    return types.toArray(cls);
  }
  
  
  /**
   * Set the setTypes of the getMethodName arguments.
   * @param cls Classes dos tipos de argumentos.
   * @return This modified <code>RemoteMethod</code> instance.
   */
  public RemoteMethod setTypes(Class ... cls) {
    types.addAll(Arrays.asList(cls));
    return this;
  }
  
  
  /**
   * Extract the setTypes of getMethodName arguments 
 (this getMethodName is imprecise and may cause 
 invocation errors i.e: double/java.lang.Double).
   * @return This modified <code>RemoteMethod</code> instance.
   */
  public RemoteMethod extractTypesFromArgs() {
    if(parameters.isEmpty()) return null;
    types.clear();
    for(int i = 0; i < parameters.size(); i++) {
      types.add(parameters.get(i).getClass());
    }
    return this;
  }
  
  
  /**
   * Get the getMethodName name.
   * @return Method name.
   */
  public String getMethodName() {
    return method;
  }


  /**
   * Set the getMethodName name.
   * @param name Method name.
   * @return This modified <code>RemoteMethod</code> instance.
   */
  public RemoteMethod setMethodName(String name) {
    this.method = name;
    return this;
  }


  /**
   * Set the getMethodName arguments.
   * @param objs Method arguments.
   * @return This modified <code>RemoteMethod</code> instance.
   */
  public RemoteMethod setParameters(Object ... objs) {
    if(objs != null && objs.length > 0) {
      parameters.clear();
      parameters.addAll(Arrays.asList(objs));
    }
    return this;
  }


  /**
   * Get the object name.
   * @return Object name.
   */
  public String getObjectName() {
    return objname;
  }


  /**
   * Set the object name.
   * @param objName Object name.
   * @return This modified <code>RemoteMethod</code> instance.
   */
  public RemoteMethod setObjectName(String objName) {
    this.objname = objName;
    return this;
  }


  @Override
  public int hashCode() {
    int hash = 5;
    hash = 47 * hash + Objects.hashCode(this.method);
    hash = 47 * hash + Objects.hashCode(this.objname);
    if(this.types != null)
      hash = 47 * hash + Arrays.deepHashCode(this.types.toArray());
    else
      hash = 47 * hash + Objects.hashCode(this.types);
    return hash;
  }


  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final RemoteMethod other = (RemoteMethod) obj;
    if (!Objects.equals(this.objname, other.objname))
      return false;
    if (!Objects.equals(this.method, other.method))
      return false;
    if (this.types == null || other.types == null
        || !Arrays.deepEquals(this.types.toArray(), 
            other.types.toArray()))
      return false;
    return true;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    if(retvar != null) {
      sb.append(retvar)
          .append(HttpConsts.SP)
          .append(HttpConsts.EQ)
          .append(HttpConsts.SP);
    }
    if(objname != null) {
      sb.append(objname);
    }
    sb.append(".").append(method).append("( ");
    if(!parameters.isEmpty()) {
      for(int i = 0; i < parameters.size(); i++) {
        sb.append(parameters.get(i));
        if(i < parameters.size() -1)
          sb.append(", ");
      }
    }
    else if(types != null) {
      for(int i = 0; i < types.size(); i++) {
        sb.append(types.get(i).getSimpleName());
        if(i < types.size() -1)
          sb.append(", ");
      }
    }
    return sb.append(" )").toString();
  }
  
}
