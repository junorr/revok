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

package us.pserver.revok;

import us.pserver.revok.factory.ChannelFactory;
import us.pserver.revok.factory.ChannelFactoryBuilder;
import us.pserver.revok.protocol.JsonSerializer;
import us.pserver.revok.protocol.ObjectSerializer;

/**
 *
 * @author Juno Roesler - juno@pserver.us
 * @version 0.0 - 23/08/2015
 */
public class RemoteObjectBuilder {

  private HttpConnector connector;
  
  private ChannelFactory<HttpConnector> channelFactory;
  
  private ObjectSerializer serial;
  
  
  public RemoteObjectBuilder() {
    connector = null;
    channelFactory = ChannelFactoryBuilder.builder()
        .createHttpRequestChannelFactory();
    serial = new JsonSerializer();
  }


  public HttpConnector getConnector() {
    return connector;
  }


  public RemoteObjectBuilder setConnector(HttpConnector connector) {
    this.connector = connector;
    return this;
  }


  public ChannelFactory<HttpConnector> getChannelFactory() {
    return channelFactory;
  }


  public RemoteObjectBuilder setChannelFactory(ChannelFactory<HttpConnector> channelFactory) {
    this.channelFactory = channelFactory;
    return this;
  }


  public ObjectSerializer getSerial() {
    return serial;
  }


  public RemoteObjectBuilder setSerial(ObjectSerializer serial) {
    this.serial = serial;
    return this;
  }
  
  
  public RemoteObject create() {
    return new RemoteObject(connector, channelFactory, serial);
  }
  
}
