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
 * @author Juno Roesler - juno.rr@gmail.com
 * @version 1.0 - 17/04/2015
 */
public class Calculator implements ICalculator {
  
  private double x, y, z;
  
  public Calculator() { 
    x = y = z = 0.0; 
  }
  
  public Calculator setX(double d) { 
    x = d; return this; 
  }
  
  public Calculator setY(double d) {
    y = d; return this; 
  }
  
  public Calculator setZ(double d) {
    z = d; return this; 
  }
  
  public Calculator xyz(double x, double y, double z) { 
    this.x = x; this.y = y; this.z = z; 
    return this;
  }
  
  public Calculator printX() { 
    System.out.println("* Calculator{ x="+ x+ " }"); 
    return this;
  }
  
  public Calculator printY() { 
    System.out.println("* Calculator{ y="+ y+ " }"); 
    return this;
  }
  
  public Calculator printZ() { 
    System.out.println("* Calculator{ z="+ z+ " }"); 
    return this;
  }
  
  public Calculator print() {
    System.out.println("--> "+ toString());
    return this;
  }
  
  public double x() { return x; }
  
  public double y() { return y; }
  
  public double z() { return z; }
  
  public Calculator moveXY() {
    y = x; 
    return this; 
  }
  
  public Calculator moveXZ() { 
    z = x; 
    return this; 
  }
  
  public Calculator moveYX() {
    x = y; 
    return this;
  }
  
  public Calculator moveYZ() {
    z = y;
    return this;
  }
  
  public Calculator moveZX() {
    x = z;
    return this;
  }
  
  public Calculator moveZY() {
    y = z; 
    return this;
  }
  
  public double sum(double d1, double d2) { 
    return d1 + d2; 
  }
  
  public double sub(double d1, double d2) { 
    return d1 - d2; 
  }
  
  public double mult(double d1, double d2) {
    return d1 * d2;
  }
  
  public double div(double d1, double d2) {
    return d1 / d2;
  }
  
  public double pow(double d1, double d2) {
    return Math.pow(d1, d2);
  }
  
  public double sqrt(double d1) {
    return Math.sqrt(d1);
  }
  
  public double round(double n, int dec) {
    long i = (long) n;
    long d = Math.round((n - i) * pow(10, dec));
    return i + d / pow(10, dec);
  }
  
  public Calculator sum() { 
    z = x + y; 
    return this; 
  }
  
  public Calculator sub() { 
    z = x - y; 
    return this; 
  }
  
  public Calculator mult() {
    z = x * y;
    return this;
  }
  
  public Calculator div() {
    z = x / y;
    return this;
  }
  
  public Calculator pow() {
    z = Math.pow(x, y);
    return this;
  }
  
  public Calculator sqrt() {
    z = Math.sqrt(x);
    return this;
  }
  
  public Calculator round(int dec) {
    z = round(x, dec);
    return this;
  }
  
  public double random(int limit) {
    return Math.random() * limit;
  }
  
  @Override
  public String toString() {
    return "Calculator{ x="+ x+ ", y="+ y+ ", z="+ z+ " }";
  }
  
}