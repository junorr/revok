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
import us.pserver.cdr.StringByteConverter;
import us.pserver.revok.OpResult;
import us.pserver.revok.protocol.XmlSerializer;

/**
 *
 * @author Juno Roesler - juno.rr@gmail.com
 * @version 1.0 - 11/05/2015
 */
public class TestXStream {

  
  public static void main(String[] args) throws IOException {
    XmlSerializer xs = new XmlSerializer();
    StringByteConverter scv = new StringByteConverter();
    OpResult result = new OpResult();
    result.setReturn("Some String");
    result.setError(new IOException("Some Exception"));
    result.setSuccessOperation(false);
    System.out.println("* toBytes:");
    byte[] bs = xs.toBytes(result);
    System.out.println(scv.reverse(bs));
    
    System.out.println("\n");
    
    System.out.println("* fromBytes:");
    System.out.println(xs.fromBytes(bs));
  }
  
}
