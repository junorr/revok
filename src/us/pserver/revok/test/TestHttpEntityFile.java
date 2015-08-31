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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import us.pserver.cdr.crypt.CryptAlgorithm;
import us.pserver.cdr.crypt.CryptKey;
import us.pserver.revok.http.HttpEntityFactory;
import us.pserver.revok.http.HttpEntityParser;
import us.pserver.streams.StreamUtils;
import us.pserver.tools.timer.Timer;

/**
 *
 * @author Juno Roesler - juno@pserver.us
 * @version 0.0 - 30/08/2015
 */
public class TestHttpEntityFile {

  
  public static void main(String[] args) throws FileNotFoundException, IOException {
    HttpEntityFactory fac = HttpEntityFactory.instance()
        .enableGZipCoder()
        .enableCryptCoder(CryptKey.createRandomKey(CryptAlgorithm.AES_CBC_256_PKCS5))
        .put("Hello!")
        .put(new FileInputStream("/storage/pic.jpg"));
    
    Timer tm = new Timer.Nanos().start();
    HttpEntityParser par = HttpEntityParser.instance()
        .enableGZipCoder()
        .parse(fac.createStream());
    System.out.println("* time to parse: "+ tm.stop());
    
    System.out.println("par.getCryptKey()="+par.getCryptKey());
    System.out.println("par.getObject()="+par.getObject());
    System.out.println("par.getInputStream()="+par.getInputStream());
    
    tm = new Timer.Nanos().start();
    StreamUtils.transfer(par.getInputStream(), new FileOutputStream("/storage/pic.dec.jpg"));
    System.out.println("* time decoding: "+ tm.lapAndStop());
  }
  
}
