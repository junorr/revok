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

import com.jpower.rfl.Reflector;
import us.pserver.revok.server.RevokServer;
import us.pserver.revok.container.ObjectContainer;

/**
 *
 * @author Juno Roesler - juno.rr@gmail.com
 * @version 1.0 - 24/07/2014
 */
public class TestReflector {

  
  public static void main(String[] args) {
    Reflector ref = new Reflector();
    String str = "Hello!";
    System.out.println("* str="+ str);
    str = (String) ref.on(str)
        .method("replace", 
            CharSequence.class, 
            CharSequence.class)
        .invoke("ll", "-");
    System.out.println("* str="+ str);
    
    System.out.println("* ref.method(\"toString\").isMethodPresent()="
        +ref.method("toString", null).isMethodPresent());
    
    RevokServer sv = new RevokServer(new ObjectContainer());
    ref.on(sv).method("toString").isMethodPresent();
    System.out.println("* ref.on(sv).method(\"toString\").isMethodPresent()="
        +ref.on(sv).method("toString").isMethodPresent());
  }
  
}
