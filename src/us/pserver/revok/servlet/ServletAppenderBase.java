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

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import javax.servlet.ServletContext;
import us.pserver.valid.Valid;

/**
 *
 * @author Juno Roesler - juno@pserver.us
 * @version 0.0 - 23/08/2015
 */
public class ServletAppenderBase extends AppenderBase<ILoggingEvent> {
  
  private final String LOG_PATTERN = "%date{yyyy-MM-dd HH:mm:ss,SSS} [%-5level] %logger{4}:%line - %msg%n";
  
  private final ServletContext sctx;
	
	private PatternLayoutEncoder encoder;
  
  
  public ServletAppenderBase(ServletContext sctx) {
    this.sctx = Valid.off(sctx).forNull()
        .getOrFail(ServletContext.class);
		encoder = new PatternLayoutEncoder();
		encoder.setPattern(LOG_PATTERN);
  }
  
  
  public ServletContext getServletContext() {
    return sctx;
  }


	@Override
	protected void append(ILoggingEvent e) {
		if(encoder == null || e == null) 
			return;
		sctx.log(
				encoder.getLayout().doLayout(e)
		);
	}
	
	
	public PatternLayoutEncoder getEncoder() {
    return encoder;
  }

	
  public void setEncoder(PatternLayoutEncoder encoder) {
    this.encoder = encoder;
  }

}
