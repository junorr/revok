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

import java.io.PrintStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import us.pserver.log.Log;
import us.pserver.log.LogFactory;
import us.pserver.revok.HttpConnector;
import us.pserver.revok.RemoteObject;
import us.pserver.streams.NullOutput;
import us.pserver.streams.Thing;

/**
 *
 * @author Juno Roesler - juno.rr@gmail.com
 * @version 1.0 - 22/06/2015
 */
public class TestRevokStress implements Runnable {
  
  private static final List<Double> times = 
      Collections.synchronizedList(new LinkedList<>());
  
  private static Thing<Integer> ERRORS = new Thing<>(0);
  
  
  public double random() {
    return Math.random() * 10000;
  }
  
  @Override
  public void run() {
    Log log = LogFactory.getSimpleLog(Thread.currentThread().getName());
    try {
      RemoteObject rob = new RemoteObject(new HttpConnector("http://localhost:8080/revokServletTest/revok"));
      //RemoteObject rob = new RemoteObject(new HttpConnector("localhost:9995"));
      ICalculator calc = rob.createRemoteObject("calc", ICalculator.class);
      double arg1 = random();
      double arg2 = random();
      long start = System.nanoTime();
      double r = calc.div(arg1, arg2);
      long end = System.nanoTime();
      double time = ((end - start)/1000000.0);
      times.add(time);
      //log.info("Call: calc.div( {}, {} ) = {} in {} ms", arg1, arg2, r, time);
      rob.close();
    } catch(Exception e) {
      ERRORS.increment();
    }
  }

  
  public static void main(String[] args) throws InterruptedException {
    Log log = LogFactory.getSimpleLog(TestRevokStress.class);
    PrintStream ps = new PrintStream(NullOutput.out);
    System.setErr(ps);
    
    int CALLS = 100;
    int count = 0;
    
    log.info("Running stress test with {} requests...", CALLS);
    
    ExecutorService exec = Executors.newCachedThreadPool();
    while(count++ < CALLS) {
      exec.submit(new TestRevokStress());
    }
    exec.shutdown();
    exec.awaitTermination(1000, TimeUnit.SECONDS);
    
    Thing<Double> med = new Thing(0.0);
    Thing<Integer> num = new Thing(0);
    times.forEach(t->{
      med.plus(t);
      num.increment();
    });
    
    log.info("Total time: {}; Average time for {} calls with {} errors: {} ms", med.get(), num.get(), ERRORS.get() + (CALLS - num.get()), (med.get() / num.get()));
  }
  
}
