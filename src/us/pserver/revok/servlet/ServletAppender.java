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
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;
import us.pserver.tools.Valid;

/**
 *
 * @author Juno Roesler - juno@pserver.us
 * @version 0.0 - 23/08/2015
 */
public class ServletAppender extends AppenderSkeleton {
  
  private final String LOG_PATTERN = "%d{yyyy-MM-dd HH:mm:ss.SSS}  [%-5p]  %c{4}:%L - %m%n";
  
  private final ServletContext sctx;
  
  private final Layout layout;
  
  
  public ServletAppender(ServletContext sctx) {
    this.sctx = Valid.off(sctx).forNull()
        .getOrFail(ServletContext.class);
    layout = new PatternLayout(LOG_PATTERN);
    this.setLayout(layout);
  }
  
  
  public ServletContext getServletContext() {
    return sctx;
  }


  @Override
  protected void append(LoggingEvent le) {
    sctx.log("\n"+ layout.format(le));
  }


  @Override
  public boolean requiresLayout() {
    return true;
  }


  @Override
  public void close() {}

}
