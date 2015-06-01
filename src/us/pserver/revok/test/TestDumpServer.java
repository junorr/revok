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
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import us.pserver.streams.IO;
import us.pserver.streams.StreamUtils;

/**
 *
 * @author Juno Roesler - juno.rr@gmail.com
 * @version 1.0 - 03/03/2015
 */
public class TestDumpServer {

  
  static void sendUI(OutputStream out) throws IOException {
    System.out.println("* Sending UI...");
    InputStream in = TestDumpServer.class
        .getResourceAsStream("/us/pserver/revok/http/revok.html");
    PrintWriter pw = new PrintWriter(out);
    pw.print("HTTP/1.1 200 OK\r\n");
    pw.print("Content-Type: 'text/html'\r\n");
    pw.flush();
    IO.tr(in, out);
    StreamUtils.write("\r\n\r\n\r\n\r\n", out);
    in.close();
    out.flush();
    System.out.println("* Done UI!");
  }
  
  
  public static void main(String[] args) throws IOException, InterruptedException {
    //http://localhost:36000/?obj=a&mth=compute&types=int%3Bint&args=5%3B3
    System.out.println("* Listening on 0.0.0.0:9995");
    ServerSocket srv = new ServerSocket();
    srv.bind(new InetSocketAddress("0.0.0.0", 9995));
    
    while(true) {
      Socket sock = srv.accept();
      Thread.sleep(500);
      System.out.println("* Connection Received: "+ sock);
      System.out.println("---------------------------------------");
      String hds = StreamUtils.readString(sock.getInputStream(), 30);
      System.out.print(hds);
      StreamUtils.transferUntil(sock.getInputStream(), System.out, "\r\n\r\n");
      System.out.println();
      System.out.println("---------------------------------------");
      System.out.println();
      if(hds.contains("/ui/")) {
        sendUI(sock.getOutputStream());
      }
      sock.shutdownOutput();
      sock.close();
    }
  }
  
}
