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

import java.util.Collections;
import java.util.List;
import javax.servlet.GenericServlet;
import javax.servlet.Servlet;
import us.pserver.log.Log;
import us.pserver.log.LogLevel;
import us.pserver.log.BasicLogOutput;
import us.pserver.log.BasicOutputFormatter;

/**
 *
 * @author Juno Roesler - juno.rr@gmail.com
 * @version 1.0 - 05/06/2015
 */
public class ServletLog implements Log {
  
  private static final String
      SDEBUG = "[DEBUG] ",
      SINFO =  "[INFO ] ",
      SWARN =  "[WARN ] ",
      SERROR = "[ERROR] ",
      SFATAL = "[FATAL] ";

  private GenericServlet servlet;
  
  private boolean debug, info, warning, error, fatal;
  
  
  public ServletLog(GenericServlet svt) {
    if(svt == null)
      throw new IllegalArgumentException("Invalid Servlet for logging: "+ svt);
    servlet = svt;
    debug = info = warning = error = fatal = true;
  }
  

  @Override
  public Log reset() {
    debug = info = warning = error = fatal = true;
    return this;
  }


  @Override
  public Log add(BasicLogOutput out) {
    return this;
  }


  @Override
  public Log clearOutputs() {
    return this;
  }


  @Override
  public List<BasicLogOutput> outputs() {
    return Collections.EMPTY_LIST;
  }


  @Override
  public Log formatter(BasicOutputFormatter fmt) {
    return this;
  }


  @Override
  public Log debug(boolean bool) {
    debug = bool;
    return this;
  }


  @Override
  public Log debug(String msg) {
    if(debug) log(msg, LogLevel.DEBUG);
    return this;
  }


  @Override
  public Log debug(Throwable th, boolean logStackTrace) {
    if(debug) servlet.log(SDEBUG+ th.toString(), th);
    return this;
  }


  @Override
  public Log info(boolean bool) {
    info = bool;
    return this;
  }


  @Override
  public Log info(String msg) {
    if(info) log(msg, LogLevel.INFO);
    return this;
  }


  @Override
  public Log info(Throwable th, boolean logStackTrace) {
    if(info) servlet.log(SINFO+ th.toString(), th);
    return this;
  }


  @Override
  public Log warning(boolean bool) {
    warning = bool;
    return this;
  }


  @Override
  public Log warning(String msg) {
    if(warning) log(msg, LogLevel.WARN);
    return this;
  }


  @Override
  public Log warning(Throwable th, boolean logStackTrace) {
    if(warning) servlet.log(SWARN+ th.toString(), th);
    return this;
  }


  @Override
  public Log error(boolean bool) {
    error = bool;
    return this;
  }


  @Override
  public Log error(String msg) {
    if(error) log(msg, LogLevel.ERROR);
    return this;
  }


  @Override
  public Log error(Throwable th, boolean logStackTrace) {
    if(error) servlet.log(SERROR+ th.toString(), th);
    return this;
  }


  @Override
  public Log fatal(boolean bool) {
    fatal = bool;
    return this;
  }


  @Override
  public Log fatal(String msg) {
    if(fatal) log(msg, LogLevel.FATAL);
    return this;
  }


  @Override
  public Log fatal(Throwable th, boolean logStackTrace) {
    if(fatal) servlet.log(SFATAL+ th.toString(), th);
    return this;
  }


  @Override
  public Log log(String msg, LogLevel lvl) {
    String pre;
    switch(lvl) {
      case DEBUG:
        pre = SDEBUG;
        break;
      case INFO:
        pre = SINFO;
        break;
      case WARN:
        pre = SWARN;
        break;
      case ERROR:
        pre = SERROR;
        break;
      case FATAL:
        pre = SFATAL;
        break;
      default:
        pre = SINFO;
    }
    servlet.log(pre+ msg);
    return this;
  }


  @Override
  public Log close() {
    return this;
  }

}
