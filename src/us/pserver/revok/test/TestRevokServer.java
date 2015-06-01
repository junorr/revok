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
import us.pserver.revok.container.Authenticator;
import us.pserver.revok.container.Credentials;
import us.pserver.revok.container.ObjectContainer;
import us.pserver.revok.container.SingleCredentialsSource;
import us.pserver.revok.protocol.XmlSerializer;
import us.pserver.revok.server.RevokServer;

/**
 *
 * @author Juno Roesler - juno.rr@gmail.com
 * @version 1.0 - 17/04/2015
 */
public class TestRevokServer {

  public static void main(String[] args) {
    HttpConnector hc = new HttpConnector("0.0.0.0", 9995);
    ObjectContainer cont = new ObjectContainer(
        new Authenticator( new SingleCredentialsSource(
            new Credentials("juno", "1234".getBytes())
                .addAccess("*"))));
    RevokServer revok = new RevokServer(cont, hc, new XmlSerializer());
    Calculator calc = new Calculator();
    cont.put("calc.ICalculator", calc);
    cont.put("io.IStreamHandler", new StreamHandler());
    revok.start();
  }
  
}
