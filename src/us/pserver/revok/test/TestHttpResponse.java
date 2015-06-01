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
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import org.apache.http.impl.DefaultBHttpServerConnection;
import us.pserver.revok.channel.HttpResponseChannel;
import us.pserver.revok.protocol.Transport;
import us.pserver.streams.StreamUtils;

/**
 *
 * @author Juno Roesler - juno.rr@gmail.com
 * @version 1.0 - 16/06/2014
 */
public class TestHttpResponse {

  
  public static void main(String[] args) throws IOException {
    //InetSocketAddress addr = new InetSocketAddress("172.24.75.2", 9011);
    //InetSocketAddress addr = new InetSocketAddress("10.100.0.104", 9011);
    InetSocketAddress addr = new InetSocketAddress("0.0.0.0", 9011);
    
    ServerSocket server = new ServerSocket();
    server.bind(addr);
    System.out.println("* Server listening on: "+ addr.toString());
    Socket sock = server.accept();
    System.out.println("* Connected: "+ sock.getRemoteSocketAddress());
    System.out.println("------------------------------------");
    
    DefaultBHttpServerConnection conn = new DefaultBHttpServerConnection(8*1024);
    conn.bind(sock);
    HttpResponseChannel channel = new HttpResponseChannel(conn);
    Transport trp = channel.read();
    System.out.println("* received: "+ trp);
    /*
    if(trp.hasContentEmbedded()) {
      System.out.println("* content embedded received!");
      Path to = Paths.get("c:/.local/inputstream.png");
      System.out.println("* writing to: "+ to.toString());
      OutputStream out = Files.newOutputStream(to,
          StandardOpenOption.WRITE, 
          StandardOpenOption.CREATE);
      StreamUtils.transfer(trp.getInputStream(), out);
      out.close();
    }
    */
    channel.write(trp.setInputStream(null));
    System.out.println("* echo response writed!");
    sock.close();
    server.close();
  }
  
}
