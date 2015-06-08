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

package us.pserver.revok.servlet;

import com.jpower.rfl.Reflector;
import us.pserver.log.Log;
import us.pserver.log.LogFactory;
import us.pserver.revok.server.RevokServer;

/**
 *
 * @author Juno Roesler - juno.rr@gmail.com
 * @version 1.0 - 03/06/2015
 */
public class ServletObjectParam {
  
  public static final String EQ = "=";

  private String name;
  
  private String cname;
  
  
  public ServletObjectParam() {
    name = null;
    cname = null;
  }
  
  
  public ServletObjectParam(String name, String className) {
    this.name = name;
    this.cname = className;
  }
  
  
  public static ServletObjectParam parse(String str) {
    if(str == null) return null;
    str = str.replace("\n", "").replace("\r", "").replace("\t", "");
    String[] pair;
    if(str.contains(EQ)) pair = str.split(EQ);
    else pair = new String[] {str.trim(), str.trim()};
    return new ServletObjectParam(pair[0].trim(), pair[1].trim());
  }
  
  
  public String getName() {
    return name;
  }
  
  
  public void setName(String str) {
    name = str;
  }
  
  
  public String getClassName() {
    return cname;
  }
  
  
  public void setClassName(String str) {
    cname = str;
  }
  
  
  public Class getObjectClass() {
    try {
      return Class.forName(cname);
    } catch(ClassNotFoundException e) {
      return null;
    }
  }
  
  
  public Object createObject() {
    return new Reflector().onClass(cname).create();
  }
  
}
