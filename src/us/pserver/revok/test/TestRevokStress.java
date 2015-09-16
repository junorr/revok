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
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import us.pserver.log.LogHelper;
import us.pserver.revok.HttpConnector;
import us.pserver.revok.RemoteObject;
import us.pserver.streams.NullOutput;
import us.pserver.streams.Instance;
import us.pserver.tools.timer.Timer;

/**
 *
 * @author Juno Roesler - juno.rr@gmail.com
 * @version 1.0 - 22/06/2015
 */
public class TestRevokStress implements Runnable {
  
  private static final List<Timer.Nanos> times = 
      Collections.synchronizedList(new LinkedList<>());
  
  private static Instance<Integer> ERRORS = new Instance<>(0);
  
  
  public double random() {
    return Math.random() * 10000;
  }
  
  @Override
  public void run() {
    try {
      /*RemoteObject rob = new RemoteObject(
          new HttpConnector("http://localhost:8080/revokServletTest/revok")
      );*/
      RemoteObject rob = new RemoteObject(
          new HttpConnector("localhost:9995")
      );
      ICalculator calc = rob.createRemoteObject("calc", ICalculator.class);
      double arg1 = random();
      double arg2 = random();
      Timer.Nanos tm = new Timer.Nanos().start();
      double r = calc.div(arg1, arg2);
      times.add(tm.stop());
      //System.out.printf("Call: calc.div( %f, %f ) = %f in %s%n", arg1, arg2, r, tm);
      rob.close();
    } catch(Exception e) {
      ERRORS.increment();
    }
  }

  
  public static void main(String[] args) throws InterruptedException {
    PrintStream ps = new PrintStream(NullOutput.out);
    System.setErr(ps);
    
    int CALLS = 2;
    int count = 0;
    
    System.out.printf("Running stress test with %d requests...%n", CALLS);
    
    /*ThreadPoolExecutor*/ ForkJoinPool exec = 
        (ForkJoinPool) Executors.newWorkStealingPool();
    System.out.printf("ExecutorService.class = %s%n", exec.getClass());
    Timer tm = new Timer.Nanos().start();
    while(count++ < CALLS) {
      exec.submit(new TestRevokStress());
      //System.out.printf("-  exec.getPoolSize() = %d%n", exec.getPoolSize());
    }
    exec.shutdown();
    exec.awaitTermination(1000, TimeUnit.SECONDS);
    tm.stop();
    
    Instance<Double> avg = new Instance(0.0);
    Instance<Double> min = new Instance((double)Integer.MAX_VALUE);
    Instance<Double> max = new Instance(0.0);
    Instance<Integer> num = new Instance(0);
    times.forEach(t->{
      System.out.println("t.lapsAverage(): "+ t.lapsAverage());
      avg.plus(t.lapsAverage());
      min.set(Math.min(min.get(), t.lapsAverage()));
      max.set(Math.max(max.get(), t.lapsAverage()));
      num.increment();
    });
    
    DecimalFormat df = new DecimalFormat("#,##0.00");
    System.out.println("Done!");
    System.out.println("-------------");
    System.out.printf("Time........: %s%n", tm);
    System.out.printf("Average.....: %f%n", avg.get()/num.get());
    System.out.printf("Min.........: %f%n", min.get());
    System.out.printf("Max.........: %f%n", max.get());
    System.out.printf("Calls.......: %d%n", num.get());
    System.out.printf("Errors......: %d%n", ERRORS.get() + (CALLS - num.get()));
  }
  
}
