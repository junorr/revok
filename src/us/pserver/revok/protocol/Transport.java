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

package us.pserver.revok.protocol;

import java.io.InputStream;


/**
 * <code>Transport</code> is an object for
 * transmitting content over a communication channel.
 * 
 * @author Juno Roesler - juno@pserver.com
 * @version 1.1 - 201506
 */
public class Transport {
  
  private Object object;
  
  private InputStream input;
  
  private boolean hasContentEmbedded;
  
  
  /**
   * Default constructor without arguments.
   */
  public Transport() {
    object = null;
    input = null;
    hasContentEmbedded = false;
  }
  
  
  /**
   * Constructor with an embed object.
   * @param obj Embedded object.
   */
  public Transport(Object obj) {
    object = obj;
    input = null;
    hasContentEmbedded = false;
  }
  
  
  /**
   * Constructor with an embed object and stream content.
   * @param obj Embed object.
   * @param input Embed <code>InputStream</code>.
   */
  public Transport(Object obj, InputStream input) {
    object = obj;
    setInputStream(input);
  }
  
  
  /**
   * Get a cloned <code>Transport</code> object,
   * properly formatted for channel communication.
   * @return A cloned <code>Transport</code> object,
   * properly formatted for channel communication.
   */
  public Transport createWriteVersion() {
    Transport t = new Transport();
    t.object = object;
    t.hasContentEmbedded = hasContentEmbedded;
    return t;
  }


  /**
   * Get the embedded object.
   * @return The embedded object.
   */
  public Object getObject() {
    return object;
  }


  /**
   * Set the embedded object.
   * Define o objeto a ser embarcado.
   * @param object The embedded object.
   * @return This midified <code>Transport</code> instance.
   */
  public Transport setObject(Object object) {
    this.object = object;
    return this;
  }


  /**
   * Get the embedded <code>InputStream</code>.
   * @return The embedded <code>InputStream</code>.
   */
  public InputStream getInputStream() {
    return input;
  }
  
  
  /**
   * Check if exists an embedded <code>InputStream</code> 
   * content in this <code>Transport</code> instance.
   * @return <code>true</code> if exists an embedded 
   * <code>InputStream</code> content in this instance, 
   * <code>false</code> otherwise.
   */
  public boolean hasContentEmbedded() {
    return hasContentEmbedded;
  }


  /**
   * Set the embedded <code>InputStream</code>.
   * @param in The embedded <code>InputStream</code>.
   * @return This midified <code>Transport</code> instance.
   */
  public Transport setInputStream(InputStream in) {
    this.input = in;
    hasContentEmbedded = (in != null);
    return this;
  }
  
  
  /**
   * Check if the embedded object is from the specified class type.
   * @param cls Class for comparing the object type.
   * @return <code>true</code> if the embedded object is 
   * from the specified class type, <code>false</code> otherwise.
   */
  public boolean isObjectFromType(Class cls) {
    if(object == null || cls == null)
      return false;
    return cls.isAssignableFrom(object.getClass());
  }
  
  
  /**
   * Cast the embedded object to the specified type.
   * @param <T> Type to the embedded object will be casted.
   * @return The embedded object casted to the <code>T</code> 
   * type, or <code>null</code> in case of <code>ClassCastException</code>.
   */
  public <T> T castObject() {
    try {
      return (T) object;
    } catch(Exception e) {
      return null;
    }
  }


  @Override
  public String toString() {
    return "Transport{" + "object=" + object + ", hasContentEmbedded=" + hasContentEmbedded + '}';
  }
  
}
