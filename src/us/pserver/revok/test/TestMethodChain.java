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

import us.pserver.revok.MethodChain;
import us.pserver.revok.MethodInvocationException;
import us.pserver.revok.HttpConnector;
import us.pserver.revok.RemoteObject;
import us.pserver.revok.container.Credentials;

/**
 *
 * @author Juno Roesler - juno.rr@gmail.com
 * @version 1.0 - 13/08/2014
 */
public class TestMethodChain {

  
  public static void main(String[] args) throws MethodInvocationException {
    HttpConnector hc = new HttpConnector()
        .setAddress("172.24.77.60")
        .setProxyAddress("172.24.75.19")
        .setProxyPort(6060)
        .setPort(HttpConnector.DEFAULT_PORT);
    
    RemoteObject rob = new RemoteObject(hc);
    
    MethodChain chain = new MethodChain();
    chain.add("NetworkServer", "container")
        .setCredentials(new Credentials("juno", "32132155".getBytes()));
    chain.add("contains")
        .types(String.class)
        .args("StreamHandler");
    
    System.out.println("* invoking...");
    System.out.println(chain.stringChain());
    System.out.println("* return = "+ rob.invoke(chain));
  }
  
}
