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
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.impl.DefaultBHttpServerConnection;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author Juno Roesler - juno.rr@gmail.com
 * @version 1.0 - 16/06/2014
 */
public class TestHttpEntity {

  
  public static void main(String[] args) throws IOException, HttpException {
    //InetSocketAddress addr = new InetSocketAddress("172.24.75.2", 9011);
    //InetSocketAddress addr = new InetSocketAddress("10.100.0.104", 9011);
    InetSocketAddress addr = new InetSocketAddress("localhost", 9011);
    
    ServerSocket server = new ServerSocket();
    server.bind(addr);
    System.out.println("* Server listening on: "+ addr.toString());
    Socket sock = server.accept();
    System.out.println("* Connected: "+ sock.getRemoteSocketAddress());
    System.out.println("------------------------------------");
    
    DefaultBHttpServerConnection conn = new DefaultBHttpServerConnection(8*1024);
    conn.bind(sock);
    HttpRequest basereq = conn.receiveRequestHeader();
    System.out.println("* HttpRequest basereq="+ basereq);
    System.out.println("* HttpRequest basereq instanceof HttpEntityEnclosingRequest="+ (basereq instanceof HttpEntityEnclosingRequest));
    
    HttpEntityEnclosingRequest request = (HttpEntityEnclosingRequest) basereq;
    System.out.println("* HttpRequest request_line="+ request.getRequestLine());
    Iterator it = request.headerIterator();
    while(it.hasNext())
      System.out.println("  - header="+ it.next());
    
    conn.receiveRequestEntity(request);
    conn.receiveRequestEntity(request);
    HttpEntity entity = request.getEntity();
    System.out.println("* HttpEntity entity="+ entity);
    System.out.println("* entity.content_encoding="+ entity.getContentEncoding());
    System.out.println("* entity.content_type="+ entity.getContentType());
    System.out.println("* entity.content_length="+ entity.getContentLength());
    
    System.out.println("* entity.string_content="+ EntityUtils.toString(entity));
    
    conn.close();
  }
  
}
