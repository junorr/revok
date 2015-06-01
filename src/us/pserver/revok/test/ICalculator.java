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

/**
 *
 * @author Juno Roesler - juno@pserver.us
 * @version 0.0 - 17/04/2015
 */
public interface ICalculator {

  public ICalculator setX(double d);
  
  public ICalculator setY(double d);
  
  public ICalculator setZ(double d);
  
  public ICalculator xyz(double x, double y, double z);
  
  public ICalculator printX();
  
  public ICalculator printY();
  
  public ICalculator printZ();
  
  public ICalculator print();
  
  public double x();
  
  public double y();
  
  public double z();
  
  public ICalculator moveXY();
  
  public ICalculator moveXZ();
  
  public ICalculator moveYX();
  
  public ICalculator moveYZ();
  
  public ICalculator moveZX();
  
  public ICalculator moveZY();
  
  public double sum(double d1, double d2);
  
  public double sub(double d1, double d2);
  
  public double mult(double d1, double d2);
  
  public double div(double d1, double d2);
  
  public double pow(double d1, double d2);
  
  public double sqrt(double d1);
  
  public double round(double n, int dec);
  
  public ICalculator sum();
  
  public ICalculator sub();
  
  public ICalculator mult();
  
  public ICalculator div();
  
  public ICalculator pow();
  
  public ICalculator sqrt();
  
  public ICalculator round(int dec);
  
  public double random(int limit);
  
}
