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

package us.pserver.revok.test;

/**
 *
 * @author Juno Roesler - juno@pserver.us
 * @version 0.0 - 24/06/2015
 */
public class TestNanoTime {

  
  public static void main(String[] args) throws InterruptedException {
    long ms = System.currentTimeMillis();
    long ns = System.nanoTime();
    Thread.sleep(300);
    long millis = System.currentTimeMillis() - ms;
    long nanos = System.nanoTime() - ns;
    System.out.println("MILLIS.. = "+ millis);
    System.out.println("NANOS... = "+ nanos);
    System.out.println("NANOS/1M = "+ (nanos/1000000.0));
    System.out.println("AVAILABLE PROCESSORS = "+ Runtime.getRuntime().availableProcessors());
  }
  
}
