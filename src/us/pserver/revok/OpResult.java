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

import java.util.Objects;

/**
 * Encapsulates method invocation results like
 * success, exceptions and method returned object.
 * 
 * @author Juno Roesler - juno.rr@gmail.com
 * @version 1.0 - 11/11/2013
 */
public class OpResult {

  private boolean success;
  
  private Object ret;
  
  private MethodInvocationException error;
  
  
  /**
   * Default constructor without arguments.
   */
  public OpResult() {
    success = true;
    ret = null;
    error = null;
  }


  /**
   * Verifies if the operation is successful.
   * @return <code>true</code> if the operation was successful,
   * <code>false</code> otherwise.
   */
  public boolean isSuccessOperation() {
    return success;
  }


  /**
   * Set the operation success.
   * @param success <code>true</code> if the operation was successful,
   * <code>false</code> otherwise.
   */
  public void setSuccessOperation(boolean success) {
    this.success = success;
  }
  
  
  /**
   * Verifies if it has a return value.
   * @return <code>true</code> if it has a return value,
   * <code>false</code> otherwise.
   */
  public boolean hasReturn() {
    return ret != null;
  }
  
  
  /**
   * Verifies if it has an exception error.
   * @return <code>true</code> if it has an exception error,
   * <code>false</code> otherwise.
   */
  public boolean hasError() {
    return error != null;
  }


  /**
   * Get the return value.
   * @return Return value or <code>null</code>.
   */
  public Object getReturn() {
    return ret;
  }


  /**
   * Set the return value.
   * @param ret Return value.
   */
  public void setReturn(Object ret) {
    this.ret = ret;
  }


  /**
   * Get the exception thrown.
   * @return exception thrown or <code>null</code>.
   */
  public MethodInvocationException getError() {
    return error;
  }


  /**
   * Set the exception thrown.
   * @param error Exception thrown.
   */
  public void setError(Exception error) {
    if(error != null) {
      if(MethodInvocationException.class
          .isAssignableFrom(error.getClass()))
        this.error = (MethodInvocationException) error;
      else
        this.error = new MethodInvocationException(
            error.getMessage(), error);
    }
  }


  @Override
  public int hashCode() {
    int hash = 7;
    hash = 37 * hash + (this.success ? 1 : 0);
    hash = 37 * hash + Objects.hashCode(this.ret);
    hash = 37 * hash + Objects.hashCode(this.error);
    return hash;
  }


  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final OpResult other = (OpResult) obj;
    if (this.success != other.success)
      return false;
    if (!Objects.equals(this.ret, other.ret))
      return false;
    if (!Objects.equals(this.error, other.error))
      return false;
    return true;
  }


  @Override
  public String toString() {
    return "OpResult{ " + "success = " + success 
        + ", return = " + (ret == null ? "null" : (ret.toString().length() > 87 
            ? ret.toString().substring(0, 87).concat("...")
            : ret.toString())) + " }";
  }
  
}
