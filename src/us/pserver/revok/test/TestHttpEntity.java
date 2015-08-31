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
import us.pserver.cdr.crypt.CryptAlgorithm;
import us.pserver.cdr.crypt.CryptKey;
import us.pserver.revok.http.HttpEntityFactory;
import us.pserver.revok.http.XmlConsts;
import us.pserver.streams.SequenceInputStream;
import us.pserver.streams.StreamUtils;
import us.pserver.streams.StringBuilderInputStream;

/**
 *
 * @author Juno Roesler - juno@pserver.us
 * @version 0.0 - 29/08/2015
 */
public class TestHttpEntity {

  
  public static void main(String[] args) throws IOException {
    CryptKey key = CryptKey.createRandomKey(CryptAlgorithm.AES_CBC_256_PKCS5);
    StringByteConverter scv = new StringByteConverter();
    System.out.write(scv.convert(key.toString()));
    System.out.println("\n-----------------------------------");
    
    StringBuilderInputStream sin = new StringBuilderInputStream()
        .append(scv.convert(XmlConsts.START_CRYPT_KEY))
        .append(scv.convert(key.toString()))
        .append(scv.convert(XmlConsts.END_CRYPT_KEY));
    StreamUtils.transfer(sin, System.out);
    System.out.println("\n-----------------------------------");
      
    HttpEntityFactory fac = HttpEntityFactory.instance()
        .put("Hello!")
        .put(new SequenceInputStream(50, 60));
    System.out.println("-> Clean content:");
    StreamUtils.transfer(fac.createStream(), System.out);
    System.out.println();
    
    fac = HttpEntityFactory.instance()
        .put("Hello!")
        .put(new SequenceInputStream(50, 60))
        .enableBase64Coder();
    System.out.println("-> Base64 content:");
    StreamUtils.transfer(fac.createStream(), System.out);
    System.out.println();
    
    fac = HttpEntityFactory.instance()
        .put("Hello!")
        .put(new SequenceInputStream(50, 60))
        .enableGZipCoder()
        .enableBase64Coder();
    System.out.println("-> GZip+Base64 content:");
    StreamUtils.transfer(fac.createStream(), System.out);
    System.out.println();
    
    fac = HttpEntityFactory.instance()
        .put("Hello!")
        .put(new SequenceInputStream(50, 60))
        .enableGZipCoder()
        .enableCryptCoder(key);
        //.enableBase64Coder();
    System.out.println("-> GZip+Crypt+Base64 content:");
    StreamUtils.transfer(fac.createStream(), System.out);
    System.out.println();
  }
  
}
