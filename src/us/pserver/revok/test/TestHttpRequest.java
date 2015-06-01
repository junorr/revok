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
import us.pserver.revok.channel.HttpRequestChannel;
import us.pserver.revok.HttpConnector;
import us.pserver.revok.protocol.Transport;

/**
 *
 * @author Juno Roesler - juno.rr@gmail.com
 * @version 1.0 - 13/06/2014
 */
public class TestHttpRequest {
  
  public static void main(String[] args) throws IOException {
    //NetConnector nc = newHttpConnectorr("172.24.77.6", 9099);
  HttpConnector hc = new HttpConnector("10.100.0.102", 9011);
    /* set proxy */
    hc.setProxyAddress("127.0.0.1")
        .setProxyPort(6060)
        .setProxyAuthorization("f6036477:00000000");
    /**/
    HttpRequestChannel channel = new HttpRequestChannel(hc);
    
    Transport trp = new Transport();
    trp.setObject("Hello Apache HttpCore!!");

    /*
    InputStream input = Files.newInputStream(
        Paths.get("c:/.local/splash.png"), 
        StandardOpenOption.READ);
    //trp.setInputStream(input);
    */
    channel.write(trp);
    
    System.out.println("* request sent!");
    System.out.println("* "+ channel.getLastResponse().getStatusLine());
    trp = channel.read();
    
    System.out.println("* received: "+ trp);
    System.out.println("--------------------------------");
    channel.close();
  }
  
}
