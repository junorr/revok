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


/**
 * Exception thrown in case of method invocation error.
 * 
 * @author Juno Roesler - juno.rr@gmail.com
 * @version 1.0 - 2014-01-21
 */
public class MethodInvocationException extends Exception {
  
  
  /**
   * Default constructor.
   */
  public MethodInvocationException() {}
  
  
  /**
   * Receives the error message.
   * @param msg Error message <code>String</code>.
   */
  public MethodInvocationException(String msg) {
    super(msg);
  }
  
  
  /**
   * Receives the error message and cause.
   * @param msg Error message <code>String</code>.
   * @param cause Cause error <code>Throwable</code>.
   */
  public MethodInvocationException(String msg, Throwable cause) {
    super(msg, cause);
  }
  
  
  /**
   * Receives the cause error.
   * @param cause Cause error <code>Throwable</code>.
   */
  public MethodInvocationException(Throwable cause) {
    super(cause);
  }


  @Override
  public String toString() {
    return "MethodInvocationException: " + this.getMessage();
  }
  
}
