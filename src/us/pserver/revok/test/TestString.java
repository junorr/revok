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
 * @author Juno Roesler - juno.rr@gmail.com
 * @version 1.0 - 03/06/2015
 */
public class TestString {

  
  public static void main(String[] args) {
    String s1 = " string ";
    String s2 = "   string2 ";
    String s3 = "  string  string   ";
    System.out.println("* string1 = '"+ s1+ "'");
    System.out.println("* string1.trim() = '"+ s1.trim()+ "'");
    System.out.println("* string2 = '"+ s2+ "'");
    System.out.println("* string2 = '"+ s2.trim()+ "'");
    System.out.println("* string3 = '"+ s3+ "'");
    System.out.println("* string3 = '"+ s3.trim()+ "'");
    
    HttpConnector con = new HttpConnector();
    con.setAddress("http://localhost:8080/revok");
    System.out.println("con.getURIString(): "+ con.getURIString());
    System.out.println("con.getFullAddress(): "+ con.getFullAddress());
    System.out.println("con.getProtocol(): "+ con.getProtocol());
    System.out.println("con.getAddress(): "+ con.getAddress());
    System.out.println("con.getPort(): "+ con.getPort());
    System.out.println("con.getPath(): "+ con.getPath());
  }
  
}
