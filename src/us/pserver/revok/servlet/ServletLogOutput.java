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

import javax.servlet.ServletContext;
import us.pserver.log.LogLevel;
import us.pserver.log.internal.LogLevelManager;
import us.pserver.log.output.LogOutput;

/**
 *
 * @author Juno Roesler - juno.rr@gmail.com
 * @version 1.0 - 05/06/2015
 */
public class ServletLogOutput implements LogOutput {
  
  private ServletContext scontext;
  
  private LogLevelManager levels;
  
  
  public ServletLogOutput(ServletContext svt) {
    if(svt == null)
      throw new IllegalArgumentException("Invalid null GenericServlet");
    scontext = svt;
    levels = new LogLevelManager();
  }


  @Override
  public LogOutput setLevelEnabled(LogLevel lvl, boolean enabled) {
    levels.setLevelEnabled(lvl, enabled);
    return this;
  }


  @Override
  public boolean isLevelEnabled(LogLevel lvl) {
    return levels.isLevelEnabled(lvl);
  }


  @Override
  public LogOutput log(LogLevel lvl, String msg) {
    if(lvl!= null && msg != null && levels.isLevelEnabled(lvl)) {
      scontext.log("\n"+ msg);
    }
    return this;
  }

  @Override public void close() {}

}
