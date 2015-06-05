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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.servlet.ServletConfig;

/**
 *
 * @author Juno Roesler - juno.rr@gmail.com
 * @version 1.0 - 03/06/2015
 */
public class ServletConfigUtil {

  private ServletConfig config;
  
  
  public ServletConfigUtil(ServletConfig sc) {
    if(sc == null) 
      throw new IllegalArgumentException("Invalid ServletConfig: "+ sc);
    config = sc;
  }
  
  
  public ServletConfig getServletConfig() {
    return config;
  }
  
  
  public boolean hasParam(String name) {
    return name != null 
        && this.getServletConfig()
            .getInitParameter(name) != null;
  }
  
  
  public List<String> getListParam(String name) {
    if(!hasParam(name)) return Collections.EMPTY_LIST;
    ArrayList<String> ls = new ArrayList<>();
    String param = this.getServletConfig().getInitParameter(name);
    if(param.contains(",")) {
      Arrays.asList(param.split(","))
          .forEach(s->ls.add(s.trim()));
    } else {
      ls.add(param);
    }
    return ls;
  }
  
  
  public List<ServletObjectParam> getObjectParamList(String name) {
    if(!hasParam(name)) return Collections.EMPTY_LIST;
    ArrayList<ServletObjectParam> ls = new ArrayList<>();
    String param = this.getServletConfig().getInitParameter(name);
    if(param.contains(",")) {
      Arrays.asList(param.split(","))
          .forEach(s->ls.add(ServletObjectParam.parse(s)));
    } else {
      ls.add(ServletObjectParam.parse(param));
    }
    return ls;
  }
  
  
  public String getParam(String name) {
    return this.getServletConfig().getInitParameter(name);
  }
  
}
