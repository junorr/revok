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

import us.pserver.cdr.StringByteConverter;
import us.pserver.cdr.b64.Base64ByteCoder;
import us.pserver.cdr.crypt.CryptAlgorithm;
import us.pserver.cdr.crypt.CryptByteCoder;
import us.pserver.cdr.crypt.CryptKey;
import us.pserver.cdr.gzip.GZipByteCoder;

/**
 *
 * @author Juno Roesler - juno.rr@gmail.com
 * @version 1.0 - 05/03/2015
 */
public class TestByteCoder {

  
  public static void main(String[] args) {
    StringByteConverter scv = new StringByteConverter();
    GZipByteCoder gz = new GZipByteCoder();
    Base64ByteCoder bbc = new Base64ByteCoder();
    CryptKey key = CryptKey.createRandomKey(CryptAlgorithm.AES_CBC_PKCS5);
    CryptByteCoder cbc = new CryptByteCoder(key);
    
    String str = "compute-2;longValue-0";
    System.out.println("* str="+ str);
    byte[] bs = scv.convert(str);
    bs = gz.encode(bs);
    bs = cbc.encode(bs);
    bs = bbc.encode(bs);
    System.out.println("* encoded="+ scv.reverse(bs));
    cbc = new CryptByteCoder(key);
    bs = bbc.decode(bs);
    bs = cbc.decode(bs);
    bs = gz.decode(bs);
    System.out.println("* decoded="+ scv.reverse(bs));
  }
  
}
