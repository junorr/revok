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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import us.pserver.revok.HttpConnector;
import us.pserver.revok.MethodChain;
import us.pserver.revok.MethodInvocationException;
import us.pserver.revok.RemoteMethod;
import us.pserver.revok.RemoteObject;
import us.pserver.revok.container.Credentials;
import us.pserver.revok.protocol.JsonSerializer;
import us.pserver.revok.server.Server;
import us.pserver.streams.IO;

/**
 * Test class for revok client making remote getMethod calls.
 * 
 * @author Juno Roesler - juno@pserver.com
 * @version 1.1 - 201506
 */
public class TestRevokClient {

  
  public static void main(String[] args) throws MethodInvocationException, IOException {
    MethodChain chain = new MethodChain();
    //HttpConnector hc = new HttpConnector("http://localhost:8080/revokServletTest/revok");
    HttpConnector hc = new HttpConnector("localhost:9995");
    Credentials cred = new Credentials("juno", "1234".getBytes());
    RemoteObject rob = RemoteObject.builder()
        .setConnector(hc)
        .setSerial(new JsonSerializer())
        .create();
    RemoteMethod rm = null;
    List<String> mts = null;
    
    
    System.out.println("----------------------------------");
    rm = RemoteMethod.builder()
        .setObjectName("global.ObjectContainer")
        .setMethodName("listMethods")
        .addArgument(String.class, "calc.ICalculator")
        .create();
    System.out.println("* Invoke      --> "+ rm);
    mts = (List<String>) rob.invoke(rm);
    System.out.println("* mts.size="+ mts.size());
    mts.forEach(System.out::println);
    
    
    System.out.println("----------------------------------");
    rm = RemoteMethod.builder()
        .setObjectName("global.ObjectContainer")
        .setMethodName("objects")
        .addArgument(String.class, "global")
        .create();
    System.out.println("* Invoke      --> "+ rm);
    mts = (List<String>) rob.invoke(rm);
    System.out.println("* objs.size="+ mts.size());
    mts.forEach(System.out::println);
    
    
    System.out.println("----------------------------------");
    chain.add(RemoteMethod.builder()
        .setObjectName("calc.ICalculator")
        .setMethodName("xyz")
        .setArgumentTypes(double.class, double.class, double.class)
        .setArguments(113.0, 7.0, 0.0)
        .create()
    );
    chain.add("div");
    chain.add("print");
    chain.add("moveZX");
    chain.add(RemoteMethod.builder()
        .setMethodName("round")
        .addArgument(int.class, 4)
        .create()
    );
    chain.add("z");
    System.out.println("* Invoke      --> "+ chain);
    Double z = (Double) rob.invoke(chain);
    System.out.println(">> "+ z);
    
    
    System.out.println("----------------------------------");
    rm = RemoteMethod.builder()
        .setObjectName("calc.ICalculator")
        .setMethodName("z")
        .setServerReturnVariable("$calc.temp")
        .create();
    System.out.println("* Invoke      --> "+ rm);
    System.out.println(">> "+ rob.invoke(rm));
    
    
    System.out.println("----------------------------------");
    rm = RemoteMethod.builder()
        .setObjectName("calc.ICalculator")
        .setMethodName("sum")
        .setArgumentTypes(double.class, double.class)
        .setArguments("$calc.temp", 30.0)
        .create();
    System.out.println("* Invoke      --> "+ rm);
    System.out.println(">> "+ rob.invoke(rm));
    
    
    System.out.println("----- ProxyClass: ICalculator -----");
    ICalculator calc = rob.createRemoteObject("calc", ICalculator.class);
    System.out.println("* Invoking calc.sum( 30, 27 ) = "+ calc.sum(30, 27));
    System.out.println("* Invoking calc.printZ()");
    calc.printZ();
    
    System.out.println("----------------------------------");
    IStreamHandler handler = rob.createRemoteObject("io", IStreamHandler.class);
    String p = "/storage/pic.jpg";
    System.out.println("* Invoking IStreamHandler.toString() = "+ handler);
    System.out.println("* Invoking IStreamHandler.size( "+ p+ " ) = "+ handler.size(p));
    System.out.print("* Invoking IStreamHandler.read( "+ p+ " ) = ");
    InputStream is = handler.read(p);
    System.out.println(is+ ", available="+ is.available());
    System.out.println("* Bytes Transferred="+ IO.tr(is, IO.os(IO.p("/storage/pic-2.jpg"))));
    
    System.out.println("----- ProxyClass: Server -----");
    Server srv = rob.createRemoteObject("global.RevokServer", Server.class);
    System.out.println("* Server: srv="+ srv.toString());
    System.out.println("* Invoking srv.isRunning() = "+ srv.isRunning());
    System.out.println("* Invoking srv.getAvailableThreads() = "+ srv.getAvailableThreads());
    System.out.println("* Invoking srv.stop()");
    srv.stop();
    
    rob.close();
  }
  
}
