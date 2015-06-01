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

package us.pserver.revok.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import us.pserver.revok.RemoteMethod;
import us.pserver.revok.RemoteObject;

/**
 * Invocation handler used by the Proxy remote object.
 * 
 * @author Juno Roesler - juno@pserver.us
 * @version 1.1 - 20150422
 * @see us.pserver.revok.RemoteObject#createRemoteObject(java.lang.String, java.lang.Class) 
 */
public class RemoteInvocationHandler implements InvocationHandler {

  private RemoteObject rob;
  
  private String objname;
  
  
  /**
   * Default constructor which receives the RemoteObject used in 
   * method invocations and the object name on the server.
   * @param rob RemoteObject.
   * @param objname Object name on the server.
   */
  public RemoteInvocationHandler(RemoteObject rob, String objname) {
    if(rob == null)
      throw new IllegalArgumentException(
          "[RemoteInvocationHandler( RemoteObject )] "
              + "Invalid RemoteObject {"+ rob+ "}");
    if(objname == null || objname.trim().isEmpty())
      throw new IllegalArgumentException(
          "[RemoteInvocationHandler( RemoteObject )] "
              + "Invalid namespace {"+ rob+ "}");
    this.rob = rob;
    this.objname = objname;
  }
  
  
  /**
   * Return the RemoteObject used in methods invocations.
   * @return The RemoteObject used in methods invocations.
   */
  public RemoteObject getRemoteObject() {
    return rob;
  }
  
  
  /**
   * Return the object name on server.
   * @return The object name on server.
   */
  public String getObjectName() {
    return objname;
  }
  
  
  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    Class[] ints = proxy.getClass().getInterfaces();
    if(ints == null || ints.length == 0)
      throw new IllegalArgumentException("Invalid Proxy object. No implemented interfaces");
    RemoteMethod rm = new RemoteMethod()
        .forObject((!objname.contains(".") ? objname.concat(".")
            .concat(ints[0].getSimpleName()) : objname))
        .method(method.getName());
    if(args != null && args.length > 0) {
      rm.types(method.getParameterTypes()).args(args);
    }
    return rob.invoke(rm);
  }
  
}
