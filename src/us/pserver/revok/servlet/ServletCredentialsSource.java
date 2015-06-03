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

import java.util.List;
import javax.servlet.ServletConfig;
import us.pserver.revok.container.Credentials;
import us.pserver.revok.container.ListCredentialsSource;

/**
 *
 * @author Juno Roesler - juno.rr@gmail.com
 * @version 1.0 - 03/06/2015
 */
public class ServletCredentialsSource extends ListCredentialsSource {

  private ServletConfigUtil util;
  
  
  public ServletCredentialsSource(ServletConfig sc) {
    super();
    util = new ServletConfigUtil(sc);
    init();
  }
  
  
  private void init() {
    String crdname = Credentials.class.getName();
    List<String> ls = util.getListParam(crdname);
    if(!util.hasParam(crdname) || ls.isEmpty()) {
      throw new IllegalStateException("No Credentials on ServletConfig");
    }
    Credentials last = null;
    for(String s : ls) {
      last = parseCreds(s, last);
      if(last != null) this.add(last);
    }
  }
  
  
  private Credentials parseCreds(String str, Credentials last) {
    if(str == null 
        || (!str.contains(":") && last == null) 
        || !str.contains("@")) 
      return null;
    String[] scrd = str.trim().split("@");
    if(scrd[0] == null || !scrd[0].contains(":")) {
      last.addAccess(scrd[1].trim());
      return last;
    }
    String[] usrpwd = scrd[0].trim().split(":");
    Credentials cred = new Credentials(usrpwd[0].trim(), usrpwd[1].trim().getBytes());
    cred.addAccess(scrd[1].trim());
    return cred;
  }
  
}
