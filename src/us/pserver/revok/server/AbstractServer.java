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


/**
 * Abstract class implementing basic object server functions.
 * 
 * @author Juno Roesler - juno@pserver.com
 * @version 1.1 - 201506
 * @see us.pserver.revok.server.Server
 */
public abstract class AbstractServer implements Server {
  
  private static final Class ABSTRACT_SERVER = AbstractServer.class;
  
  
  int availableThreads;
  
  boolean running;
  
  ObjectContainer container;
  
  
  /**
   * Default constructor without arguments.
   */
  AbstractServer() {
    availableThreads = DEFAULT_AVAILABLE_THREADS;
    running = false;
    container = null;
  }
  
  
  /**
   * Constructor which receives the <code>ObjectContainer</code>
   * with the stored objects whose methods will be invoked.
   * @param container <code>ObjectContainer</code>.
   */
  AbstractServer(ObjectContainer container) {
    if(container == null)
      throw new IllegalArgumentException(
          "Invalid ObjectContainer ["+ container+ "]");
    availableThreads = DEFAULT_AVAILABLE_THREADS;
    running = false;
    this.container = container;
  }


  @Override
  public void setContainer(ObjectContainer cont) {
    this.container = cont;
  }


  @Override
  public ObjectContainer container() {
    return container;
  }


  @Override
  public void setAvailableThreads(int threads) {
    if(threads < 1) throw new IllegalArgumentException(
        "Invalid available Threads ["+ threads+ "]");
    availableThreads = threads;
  }


  @Override
  public int getAvailableThreads() {
    return availableThreads;
  }
  
  
  /**
   * Set if the server is running.
   * @param run <code>true</code> if the server is running,
   * <code>false</code> otherwise.
   */
  protected void setRunning(boolean run) {
    synchronized(ABSTRACT_SERVER) {
      running = run;
    }
  }


  @Override
  public void stop() {
    setRunning(false);
  }


  @Override
  public boolean isRunning() {
    synchronized(ABSTRACT_SERVER) {
      return running;
    }
  }

}
