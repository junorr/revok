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

import us.pserver.revok.HttpConnector;

/**
 *
 * @author Juno Roesler - juno@pserver.us
 * @version 0.0 - 23/08/2015
 */
public class TestHttpConnector {

  
  public static void main(String[] args) {
    HttpConnector con = new HttpConnector();
    String addr = "https://127.0.0.1:9090/special";
    con.setAddress(addr);
    System.out.println("** addr: "+ addr);
    System.out.println(" * proto: "+ con.getProtocol());
    System.out.println(" * addr : "+ con.getAddress());
    System.out.println(" * port : "+ con.getPort());
    System.out.println(" * path : "+ con.getPath());
    System.out.println(" * full : "+ con.getFullAddress());
    System.out.println(" * uri  : "+ con.getURI());
    System.out.println(" * s_uri: "+ con.getURIString());
    System.out.println();

    addr = "https://127.0.0.1:9090";
    con = new HttpConnector();
    con.setAddress(addr);
    System.out.println("** addr: "+ addr);
    System.out.println(" * proto: "+ con.getProtocol());
    System.out.println(" * addr : "+ con.getAddress());
    System.out.println(" * port : "+ con.getPort());
    System.out.println(" * path : "+ con.getPath());
    System.out.println(" * full : "+ con.getFullAddress());
    System.out.println(" * uri  : "+ con.getURI());
    System.out.println(" * s_uri: "+ con.getURIString());
    System.out.println();

    addr = "127.0.0.1:9090";
    con = new HttpConnector();
    con.setAddress(addr);
    System.out.println("** addr: "+ addr);
    System.out.println(" * proto: "+ con.getProtocol());
    System.out.println(" * addr : "+ con.getAddress());
    System.out.println(" * port : "+ con.getPort());
    System.out.println(" * path : "+ con.getPath());
    System.out.println(" * full : "+ con.getFullAddress());
    System.out.println(" * uri  : "+ con.getURI());
    System.out.println(" * s_uri: "+ con.getURIString());
    System.out.println();
  }
  
}
