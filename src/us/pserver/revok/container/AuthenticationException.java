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

package us.pserver.revok.container;

/**
 * Represents an error in the authentication process on the server.
 * @author Juno Roesler - juno.rr@gmail.com
 * @version 1.1 - 20150422
 */
public class AuthenticationException extends Exception {

  /**
   * Default constructor without arguments.
   */
  public AuthenticationException() {}
  
  
  /**
   * Constructor with the message of the exception.
   * @param msg Message of exception.
   */
  public AuthenticationException(String msg) {
    super(msg);
  }
  
  
  /**
   * Constructor with message and cause of the exception.
   * @param msg Message of exception.
   * @param cause Cause of exception.
   */
  public AuthenticationException(String msg, Throwable cause) {
    super(msg, cause);
  }


  @Override
  public String toString() {
    return "AuthenticationException: " + this.getMessage();
  }
  
}
