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

/**
 * Represents an object to be exposed for RPC calls on the <code>RevokServlet</code>,
 * where the object namespace and class are readed from the web.xml config file.
 * The objects configured in the web.xml file, must follow this pattern: 
 * <code>&lt;name/namespace&gt;.&lt;obj-name&gt;=&lt;full-class-name&gt;</code>.
 * For example: <code>global.ObjectContainer=us.pserver.revok.container.ObjectContainer</code>.
 * 
 * @author Juno Roesler - juno@pserver.com
 * @version 1.1 - 201506
 */
public class ServletObjectParam {
  
  public static final String EQ = "=";

  private String name;
  
  private String cname;
  
  
  /**
   * Constructor without arguments.
   */
  public ServletObjectParam() {
    name = null;
    cname = null;
  }
  
  
  /**
   * Constructor which receives the name/namespace and the full class name.
   * @param name Name/namespace.
   * @param className Full class name.
   */
  public ServletObjectParam(String name, String className) {
    this.name = name;
    this.cname = className;
  }
  
  
  /**
   * Parse a string in pattern <code>&lt;name/namespace&gt;.&lt;obj-name&gt;=&lt;full-class-name&gt;</code>,
   * into a <code>ServletObjectParam</code> object.
   * @param str A string in pattern <code>&lt;name/namespace&gt;.&lt;obj-name&gt;=&lt;full-class-name&gt;</code>.
   * @return The parsed <code>ServletObjectParam</code> object or <code>null</code>.
   */
  public static ServletObjectParam parse(String str) {
    if(str == null) return null;
    str = str.replace("\n", "").replace("\r", "").replace("\t", "");
    String[] pair;
    if(str.contains(EQ)) pair = str.split(EQ);
    else pair = new String[] {str.trim(), str.trim()};
    return new ServletObjectParam(pair[0].trim(), pair[1].trim());
  }
  
  
  /**
   * Get the name/namespace of the object.
   * @return The name/namespace of the object.
   */
  public String getName() {
    return name;
  }
  
  
  /**
   * Set the name/namespace of the object.
   * @param str The name/namespace of the object.
   */
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
