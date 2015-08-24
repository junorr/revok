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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import us.pserver.revok.container.Credentials;

/**
 *
 * @author Juno Roesler - juno@pserver.us
 * @version 0.0 - 23/08/2015
 */
public class RemoteMethodBuilder {

  private String objname;
  
  private String method;
  
  private final List arguments;
  
  private final List<Class> types;
  
  private Credentials credentials;
  
  private String returnVar;
  
  
  public RemoteMethodBuilder() {
    objname = "";
    method = null;
    arguments = new LinkedList();
    types = new LinkedList<>();
    credentials = null;
    returnVar = null;
  }
  
  
  public RemoteMethodBuilder addArgument(Class type, Object obj) {
    if(type != null && obj != null) {
      arguments.add(obj);
      types.add(type);
    }
    return this;
  }
  
  
  public RemoteMethodBuilder addArgument(Object obj) {
    if(obj != null) {
      arguments.add(obj);
    }
    return this;
  }
  
  
  public RemoteMethodBuilder addArgumentType(Class type) {
    if(type != null) {
      types.add(type);
    }
    return this;
  }
  
  
  public RemoteMethodBuilder setArguments(Object ... args) {
    if(args != null && args.length > 0) {
      arguments.addAll(Arrays.asList(args));
    }
    return this;
  }
  
  
  public RemoteMethodBuilder setArgumentTypes(Class ... tps) {
    if(tps != null && tps.length > 0) {
      types.addAll(Arrays.asList(tps));
    }
    return this;
  }


  public String getObjectName() {
    return objname;
  }


  public RemoteMethodBuilder setObjectName(String objname) {
    this.objname = objname;
    return this;
  }


  public String getMethodName() {
    return method;
  }


  public RemoteMethodBuilder setMethodName(String method) {
    this.method = method;
    return this;
  }


  public List getArguments() {
    return arguments;
  }


  public List<Class> getArgumentTypes() {
    return types;
  }


  public Credentials getCredentials() {
    return credentials;
  }


  public RemoteMethodBuilder setCredentials(Credentials credentials) {
    this.credentials = credentials;
    return this;
  }


  public String getReturnVar() {
    return returnVar;
  }


  public RemoteMethodBuilder setServerReturnVariable(String returnVar) {
    this.returnVar = returnVar;
    return this;
  }
  
  
  public RemoteMethodBuilder extractTypesFromArgs() {
    if(arguments.isEmpty()) return null;
    types.clear();
    for(int i = 0; i < arguments.size(); i++) {
      types.add(arguments.get(i).getClass());
    }
    return this;
  }
  
  
  public RemoteMethodBuilder clear() {
    objname = null;
    method = null;
    arguments.clear();
    types.clear();
    credentials = null;
    returnVar = null;
    return this;
  }
  
  
  public RemoteMethod create() {
    return new RemoteMethod(objname, method, arguments, types, credentials, returnVar);
  }
  
}
