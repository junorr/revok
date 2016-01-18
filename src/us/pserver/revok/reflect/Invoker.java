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

package us.pserver.revok.reflect;

import java.util.List;
import java.util.stream.Collectors;
import us.pserver.revok.MethodInvocationException;
import us.pserver.revok.RemoteMethod;
import us.pserver.revok.container.AuthenticationException;
import us.pserver.revok.container.Credentials;
import us.pserver.revok.container.ObjectContainer;
import static us.pserver.revok.reflect.Invoker.DEFAULT_INVOKE_TRIES;
import us.pserver.tools.Reflector;
import us.pserver.valid.Valid;


/**
 * Class for invoking object methods using reflection.
 * 
 * @author Juno Roesler - juno@pserver.com
 * @version 1.1 - 201506
 */
public class Invoker {
  
  /**
   * <code>
   *  DEFAULT_INVOKE_TRIES = 5
   * </code><br>
 Default number of tries to invoke a getMethod in case of error.
   * Allow greater quality results on multithreaded environments.
   */
  public static final int DEFAULT_INVOKE_TRIES = 5;
  
  /**
   * <code>
   *  VAR_SIGNAL = "$"
   * </code><br>
   * Character used to mark a variable name on the server.
   */
  public static final String VAR_SIGNAL = "$";
  
  
  private ObjectContainer container;
  
  private Credentials credentials;
  
  private Reflector ref;
  
  private Object target;
  
  private int tries;
  
  
  /**
   * Default constructor receives the <code>ObjectContainer</code>
   * with the stored object to invoke and a Credentials
   * object for authentication.
   * @param cont <code>ObjectContainer</code> with the 
   * stored object to invoke.
   * @param cred Credentials object for authentication.
   * @throws MethodInvocationException In case of error 
 invoking the remote getMethod.
   */
  public Invoker(ObjectContainer cont, Credentials cred) throws MethodInvocationException {
    if(cont == null) 
      throw new IllegalArgumentException("Invalid ObjectContainer ["+ cont+ "]");
    container = cont;
    if(container.isAuthEnabled()) {
      if(cred == null) throw new MethodInvocationException(
          "Invalid Credentials ["+ cred+ "]");
    }
    target = null;
    credentials = cred;
    ref = new Reflector();
    tries = DEFAULT_INVOKE_TRIES;
  }
  
  
  /**
   * Get the default number of invocation tries.
   * @return The default number of invocation tries.
   * @see us.pserver.revok.reflect.Invoker#DEFAULT_INVOKE_TRIES
   */
  public int tries() {
    return tries;
  }
  
  
  /**
   * Set the default number of invocation tries.
   * @param t The default number of invocation tries.
   * @return This modified <code>Invoker</code> instance.
   * @see us.pserver.revok.reflect.Invoker#DEFAULT_INVOKE_TRIES
   */
  public Invoker setTries(int t) {
    if(t > 1) tries = t;
    return this;
  }
  
  
  /**
   * Get the object whose getMethod will be invoked.
   * @param rm Method information.
   * @return The object whose getMethod will be invoked.
   * @throws MethodInvocationException In case of error invoking the getMethod.
   * @throws AuthenticationException In case of authentication error.
   */
  public Object getObject(RemoteMethod rm) 
      throws MethodInvocationException, AuthenticationException {
    Valid.off(rm).forNull().fail(RemoteMethod.class);
    if(!container.contains(rm.getObjectName())) {
      throw new MethodInvocationException("Object not found {"+ rm.getObjectName()+ "}");
    }
    return getObject(rm.getObjectName());
  }
  
  
  /**
   * Get the object whose getMethod will be invoked.
   * @param name Object name/namespace.
   * @return The object whose getMethod will be invoked.
   * @throws MethodInvocationException In case of error invoking the getMethod.
   * @throws AuthenticationException In case of authentication error.
   */
  private Object getObject(String name) throws MethodInvocationException, AuthenticationException {
    Object o = null;
    if(container.isAuthEnabled()) {
      o = container.get(credentials, name);
    }
    else {
      o = container.get(name);
    }
    return o;
  }
  
  
  /**
   * Invoke a getMethod on object and store the returned 
 value in a variable on the server (if defined).
   * @param mth Method to invoke.
   * @return The return value of the getMethod.
   * @throws MethodInvocationException In case an error occurs on invocation.
   * @throws AuthenticationException If authentication fails.
   */
  private Object invokeAndSave(RemoteMethod mth) throws MethodInvocationException, AuthenticationException {
    Object res = invoke(mth, 0);
    if(res != null) {
      container.put(mth.getReturnVariable().substring(1), res);
    }
    return res;
  }
    
    
  /**
   * Invoke a getMethod on object.
   * @param mth The Remote getMethod invocation request.
   * @return Objeto de retorno do método ou
   * <code>null</code> no caso <code>void</code>.
   * @throws MethodInvocationException In case an error occurs on invocation.
   * @throws AuthenticationException If authentication fails.
   */
  public Object invoke(RemoteMethod mth) throws MethodInvocationException, AuthenticationException {
    if(mth.getReturnVariable() != null) {
      return invokeAndSave(mth);
    }
    else {
      return invoke(mth, 0);
    }
  }
  
  
  /**
   * Verifies if exists any server variable defined 
   * as an argument or return value.
   * @param mth The getMethod to invoke.
   * @throws MethodInvocationException If the object does not exists on the server.
   * @throws AuthenticationException If authentication fails.
   */
  private void processArgs(RemoteMethod mth) throws MethodInvocationException, AuthenticationException {
    List args = (List) mth.getParameters().stream()
        .filter(o->o.toString().startsWith(VAR_SIGNAL))
        .collect(Collectors.toList());
    for(Object o : args) {
      Object x = getObject(o.toString().substring(1));
      int idx = mth.getParameters().indexOf(o);
      mth.getParameters().set(idx, x);
    }
  }
  
  
  /**
   * Invokes the getMethod in recursion mode, until the maximum 
 number of tries (in case of error).
   * @param currTry Current invocation try.
   * @return Returned value from the getMethod invocation or <code>null</code>.
   * @see us.pserver.remote.Invoker#DEFAULT_INVOKE_TRIES
   */
  private Object invoke(RemoteMethod mth, int currTry) throws MethodInvocationException, AuthenticationException {
    if(container == null || mth == null 
        || mth.getMethodName() == null 
        || tries < 1 || ref == null) 
      throw new IllegalStateException(
          "Invoker not properly configured");
    
    if(target == null && mth.getObjectName() == null) {
      throw new MethodInvocationException("Invalid Target Object Name {"+ mth.getObjectName()+ "}");
    }
    if(mth.getObjectName() != null) {
      target = getObject(mth);
    }
    
    processArgs(mth);
    Class[] cls = (mth.getTypes().isEmpty() ? null : mth.getTypesArray());
    ref.on(target).method(mth.getMethodName(), cls);
    
    if(!ref.isMethodPresent()) {
      if(currTry < tries)
        return invoke(mth, currTry+1);
      
      throw new MethodInvocationException("Method not found: "+ mth);
    }
    
    Object ret = ref.invoke((mth.getParameters().isEmpty() 
        ? null : mth.getParameters().toArray()));
      
    if(ref.hasError()) {
      if(currTry < tries) 
        return invoke(mth, currTry+1);
        
      throw new MethodInvocationException(
          "Invocation error ["
          + ref.getError().toString()+ "]", 
          ref.getError());
    }
      
    return ret;
  }
  
}
