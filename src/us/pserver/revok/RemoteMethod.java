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
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import us.pserver.revok.http.HttpConsts;
import us.pserver.tools.Valid;

/**
 * Represents a remote getMethod to be invoked in a remote object.
 * Contains informations about getMethod name, object name, getArguments and getArgumentTypes.
 * 
 * @author Juno Roesler - juno@pserver.com
 * @version 1.1 - 201506
 */
public class RemoteMethod {
  
  private final String objname;
  
  private final String method;
  
  private final List arguments;
  
  private final List<Class> types;
  
  private final Credentials cred;
  
  private String returnVariable;
  
  
  /**
   * Default Constructor without getArguments.
   */
  protected RemoteMethod(String obj, String meth, List args, List<Class> tps, Credentials crd, String retvar) { 
    objname = Valid.off(obj).forNull()
        .getOrFail("Invalid object name: ");
    method = Valid.off(meth).forEmpty()
        .getOrFail("Invalid method name: ");
    arguments = Collections.unmodifiableList(
        Valid.off(args).forNull()
            .getOrFail("Invalid arguments list: ")
    );
    types = Collections.unmodifiableList(
        Valid.off(tps).forNull()
            .getOrFail("Invalid argument types list: ")
    );
    cred = Valid.off(crd).forNull().getOrFail(Credentials.class);
    returnVariable = retvar;
  }
  
  
  public static RemoteMethodBuilder builder() {
    return new RemoteMethodBuilder();
  }

  
  /**
   * Return the Credentials object to authentication with server.
   * @return Credentials object.
   */
  public Credentials getCredentials() {
    return cred;
  }
  
  
  /**
   * Get the return variable in the server.
   * @return Variable name <code>String</code>.
   */
  public String getServerReturnVariable() {
    return returnVariable;
  }
  
  
  /**
   * Return a list with the classes of the getMethod getArguments.
   * @return java.util.List
   */
  public List<Class> getArgumentTypes() {
    return types;
  }
  
  
  /**
   * Return a list with the getMethod getArguments.
   * @return java.util.List
   */
  public List getArguments() {
    return arguments;
  }
  
  
  /**
   * Return an array with the classes of the getMethod getArguments.
   * @return Array of Class
   */
  public Class[] typesArray() {
    Class[] cls = new Class[types.size()];
    if(types.isEmpty()) return cls;
    return types.toArray(cls);
  }
  
  
  /**
   * Get the getMethod name.
   * @return Method name.
   */
  public String getMethod() {
    return method;
  }


  /**
   * Get the object name.
   * @return Object name.
   */
  public String getObjectName() {
    return objname;
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
    if(returnVariable != null) {
      sb.append(returnVariable)
          .append(HttpConsts.SP)
          .append(HttpConsts.EQ)
          .append(HttpConsts.SP);
    }
    if(objname != null) {
      sb.append(objname);
    }
    sb.append(".").append(method).append("( ");
    if(!arguments.isEmpty()) {
      for(int i = 0; i < arguments.size(); i++) {
        sb.append(arguments.get(i));
        if(i < arguments.size() -1)
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
