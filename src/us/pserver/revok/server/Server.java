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

package us.pserver.revok.server;

import us.pserver.revok.container.ObjectContainer;
import java.io.IOException;


/**
 * Interface defining the main structure of an object server.
 * 
 * @author Juno Roesler - juno@pserver.com
 * @version 1.1 - 201506
 */
public interface Server extends Runnable {
  
  /**
   * <code>
   *  DEFAULT_AVAILABLE_THREADS = 6
   * </code><br>
   * Default number of worker <code>Threads</code> for client requests attending.
   */
  public static final int DEFAULT_AVAILABLE_THREADS = 6;
  
  
  /**
   * Set the <code>ObjectContainer</code> with the stored objects
   * whose methods will be invoked.
   * @param cont The <code>ObjectContainer</code> with the stored 
   * objects whose methods will be invoked.
   */
  public void setContainer(ObjectContainer cont);
  
  /**
   * Get the <code>ObjectContainer</code> with the stored objects
   * whose methods will be invoked.
   * @return The <code>ObjectContainer</code> with the stored 
   * objects whose methods will be invoked.
   */
  public ObjectContainer container();
  
  /**
   * Set the number of available worker threads.
   * @param threads Number of <code>Threads</code>
   * which will attend client requests.
   * @see us.pserver.revok.server.Server#DEFAULT_AVAILABLE_THREADS
   */
  public void setAvailableThreads(int threads);
  
  /**
   * Get the number of available worker threads.
   * @return The number of <code>Threads</code>
   * which will attend client requests.
   * @see us.pserver.revok.server.Server#DEFAULT_AVAILABLE_THREADS
   */
  public int getAvailableThreads();
  
  /**
   * Starts the server execution.
   * @throws IOException In case of error executing server.
   */
  public void start() throws IOException;
  
  /**
   * Stops the server execution.
   */
  public void stop();
  
  /**
   * Check if the server is running.
   * @return <code>true</code> if the server is running,
   * <code>false</code> otherwise.
   */
  public boolean isRunning();
  
}
