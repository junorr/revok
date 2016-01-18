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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Proxy;
import us.pserver.revok.channel.Channel;
import us.pserver.revok.protocol.Transport;
import us.pserver.revok.container.Credentials;
import us.pserver.revok.factory.ChannelFactory;
import us.pserver.revok.factory.ChannelFactoryBuilder;
import us.pserver.revok.protocol.JsonSerializer;
import us.pserver.revok.protocol.ObjectSerializer;
import us.pserver.revok.proxy.RemoteInvocationHandler;
import us.pserver.revok.protocol.FakeInputStreamRef;
import us.pserver.valid.Valid;

/**
 * Represents a remote object for methods invocation.
 * 
 * @author Juno Roesler - juno.rr@gmail.com
 * @version 1.0 - 11/11/2013
 */
public class RemoteObject {
  
  private HttpConnector net;
  
  private ChannelFactory<HttpConnector> factory;
  
  private Channel channel;
  
  private Credentials cred;
  
  private ObjectSerializer serial;
  
  
  /**
   * Default constructor without arguments,
   * uses a <code>XmlSerializer</code> for object serialization.
   */
  public RemoteObject() {
    net = new HttpConnector();
    factory = ChannelFactoryBuilder.builder()
        .createHttpRequestChannelFactory();
    channel = null;
    cred = null;
    serial = new JsonSerializer();
  }
  
  
  /**
   * Constructor which receives a <code>HttpConnector</code> 
   * for network information.
   * @param con <code>HttpConnector</code>.
   */
  public RemoteObject(HttpConnector con) {
    this();
    net = Valid.off(con).getOrFail(HttpConnector.class);
    factory = ChannelFactoryBuilder.builder()
        .createHttpRequestChannelFactory();
    serial = new JsonSerializer();
  }
  
  
  /**
   * Constructor which receives <code>HttpConnector</code>
   * and <code>ObjectSerializer</code> for objects serialization.
   * @param con <code>HttpConnector</code>.
   * @param serial <code>ObjectSerializer</code> for object serialization.
   */
  public RemoteObject(HttpConnector con, ObjectSerializer serial) {
    this(con);
    if(serial != null)
      this.serial = serial;
  }
  
  
  /**
   * Get the <code>ObjectSerializer</code> for objects serialization.
   * @return <code>ObjectSerializer</code> for objects serialization.
   */
  public ObjectSerializer getObjectSerializer() {
    return serial;
  }
  
  
  /**
   * Set the <code>ObjectSerializer</code> for objects serialization.
   * @param serializer <code>ObjectSerializer</code> for objects serialization.
   * @return This modified <code>RemoteObject</code> instance.
   */
  public RemoteObject setObjectSerializer(ObjectSerializer serializer) {
    if(serializer != null) {
      serial = serializer;
    }
    return this;
  }
  
  
  /**
   * Return the Credentials object to authentication with server.
   * @return Credentials object.
   */
  public Credentials getCredentials() {
    return cred;
  }
  
  
  /**
   * Set the Credentials object to authentication with server.
   * @param crd The <code>Credentials</code> object.
   * @return This modified <code>RemoteObject</code> instance.
   */
  public RemoteObject setCredentials(Credentials crd) {
    cred = crd;
    return this;
  }
  
  
  /**
   * Get the network informations <code>HttpConnector</code>.
   * @return Network informations <code>HttpConnector</code>.
   */
  public HttpConnector getHttpConnector() {
    return net;
  }


  /**
   * Set the network informations <code>HttpConnector</code>.
   * @param net Network informations <code>HttpConnector</code>.
   * @return This modified <code>RemoteObject</code> instance.
   */
  public RemoteObject setHttpConnector(HttpConnector net) {
    this.net = net;
    return this;
  }


  /**
   * Get the network channel factory.
   * @return Network channel factory <code>ChannelFactory</code>.
   */
  public ChannelFactory<HttpConnector> getChannelFactory() {
    return factory;
  }


  /**
   * Set the network channel factory.
   * @param fact Network channel factory <code>ChannelFactory</code>.
   * @return This modified <code>RemoteObject</code> instance.
   */
  public RemoteObject setChannelFactory(ChannelFactory<HttpConnector> fact) {
    this.factory = fact;
    return this;
  }
  
  
  /**
   * Get the current network channel in use.
   * @return <code>Channel</code>
   */
  public Channel getChannel() {
    return channel;
  }
  
  
  /**
   * Create a network channel.
   * @return <code>Channel</code>.
   */
  private Channel channel() {
    if(channel != null && channel.isValid())
      return channel;
    
    if(net == null) throw new IllegalStateException(
        "Cannot create Channel. Invalid NetConnector ["+ net+ "]");
    if(factory == null) throw new IllegalStateException(
        "Invalid ChannelFactory ["+ factory+ "]");
    channel = factory.createChannel(net, serial);
    return channel;
  }
  
  
  /**
   * Create a new Channel instance.
   * @return The created Channel.
   */
  private Channel newChannel() {
    channel.close();
    channel = null;
    return channel();
  }
  
  
  /**
   * Close any current open connections.
   * @return This instance of RemoteObject
   */
  public RemoteObject close() {
    if(channel != null)
      channel.close();
    return this;
  }
  
  
  /**
   * Create a Proxy instance of the remote object represented by the interface Class passed.
   * Any method invocation in the returned proxy object, will be invoked remotly in the real object on server side.
   * @param <T> The type of the Proxy Object (same of the Class interface argument).
   * @param namespace The namespace on the server where is stored the remote instance, or the [namespace].[objectname].
   * @param interf Class of Interface representation
   * @return The Proxy object created.
   */
  public <T> T createRemoteObject(String namespace, Class interf) {
    if(namespace == null || namespace.trim().isEmpty())
      throw new IllegalArgumentException(
          "RemoteObject.createRemoteObject( Class, String )] "
              + "Invalid Class {"+ interf+ "}");
    if(interf == null)
      throw new IllegalArgumentException(
          "RemoteObject.createRemoteObject( Class, String )] "
              + "Invalid Class {"+ interf+ "}");
    return (T) Proxy.newProxyInstance(
        interf.getClassLoader(), new Class[]{interf}, 
        new RemoteInvocationHandler(this, namespace));
  }
  
  
  /**
   * Invoke the remote method.
   * @param rmt Remote method information <code>RemoteMethod</code>.
   * @return Remote method return value or <code>null</code>.
   * @throws MethodInvocationException In case of error invoking the method.
   */
  public Object invoke(RemoteMethod rmt) throws MethodInvocationException {
    if(rmt == null) throw new 
        IllegalArgumentException(
        "Invalid Null RemoteObject");
    
    OpResult res = this.invokeSafe(rmt);
    if(res != null && res.isSuccessOperation()) {
      return res.getReturn();
    }
    else if(res != null && res.hasError()) {
      throw res.getError();
    }
    else return null;
  }
  
  
  /**
   * Invoke the remote method.
   * @param rmt Remote method information <code>RemoteMethod</code>.
   * @throws MethodInvocationException In case of error invoking the method.
   */
  public void invokeVoid(RemoteMethod rmt) throws MethodInvocationException {
    this.invoke(rmt);
  }
  
  
  /**
   * Invoke the remote method.
   * @param rmt Remote method information <code>RemoteMethod</code>.
   * @return Remote method return value or <code>null</code>.
   */
  public OpResult invokeSafe(RemoteMethod rmt) {
    OpResult res = new OpResult();
    try {
      if(cred != null) rmt.setCredentials(cred);
      Transport trp = new Transport();
      this.checkInputStreamRef(trp, rmt);
      trp.setObject(rmt);
      trp = this.sendTransport(trp).read();
      if(trp == null || trp.getObject() == null) {
        res.setSuccessOperation(false);
        res.setError(new IllegalStateException(
            "Cannot read object from channel"));
      }
      else {
        res = trp.castObject();
        if(trp.hasContentEmbedded())
          res.setReturn(trp.getInputStream());
      }
    } 
    catch(IOException ex) {
      ex.printStackTrace();
      res.setError(ex);
      res.setSuccessOperation(false);
    }
    
    if(channel != null && !channel.isValid())
        channel.close();
    
    return res;
  }
  
  
  /**
   * Invoke the remote method chain.
   * @param chain Remote method chain information <code>MethodChain</code>.
   * @return Remote method return value or <code>null</code>.
   * @throws MethodInvocationException In case of error invoking the method.
   */
  public Object invoke(MethodChain chain) throws MethodInvocationException {
    this.validateChain(chain);
    OpResult res = this.invokeSafe(chain);
    if(res != null && res.isSuccessOperation()) {
      return res.getReturn();
    }
    else if(res != null && res.hasError()) {
      throw res.getError();
    }
    else return null;
  }
  
  
  /**
   * Validates the method chain.
   * @param chain <code>MethodChain</code>
   * @throws IllegalArgumentException If the method chain is not valid.
   */
  private void validateChain(MethodChain chain) throws IllegalArgumentException {
    if(chain == null || chain.methods().isEmpty()) 
      throw new IllegalArgumentException(
        "Invalid MethodChain ["+ chain+ "]");
  }
  
  
  /**
   * 
   * Invoke the remote method chain.
   * @param chain Remote method chain information <code>MethodChain</code>.
   * @throws MethodInvocationException In case of error invoking the method.
   */
  public void invokeVoid(MethodChain chain) throws MethodInvocationException {
    this.invoke(chain);
  }
  
  
  /**
   * Invoke the remote method chain.
   * @param chain Remote method chain information <code>MethodChain</code>.
   * @return Remote method return value or <code>null</code>.
   */
  public OpResult invokeSafe(MethodChain chain) {
    this.validateChain(chain);
    OpResult res = new OpResult();
    try {
      if(cred != null) chain.current().setCredentials(cred);
      Transport trp = new Transport();
      this.checkInputStreamRef(trp, chain.current());
      trp.setObject(chain.rewind());
      trp = this.sendTransport(trp).read();
      if(trp == null || trp.castObject() == null) {
        res.setSuccessOperation(false);
        res.setError(new IllegalStateException(
            "Cannot read object from channel"));
      }
      else {
        res = trp.castObject();
        if(trp.hasContentEmbedded())
          res.setReturn(trp.getInputStream());
      }
    } 
    catch(IOException ex) {
      res.setError(ex);
      res.setSuccessOperation(false);
    }
    
    if(channel != null && !channel.isValid())
        channel.close();
    
    return res;
  }
  
  
  /**
   * Check for <code>InputStream</code> reference in method arguments.
   * @param t <code>Transport</code> with remote method object.
   * @param r Remote method object.
   */
  private void checkInputStreamRef(Transport t, RemoteMethod r) {
    if(t == null || r == null) return;
    if(r.getTypes().isEmpty()) r.extractTypesFromArgs();
    if(r.getTypes().isEmpty()) return;
    for(int i = 0; i < r.getTypes().size(); i++) {
      Class c = r.getTypes().get(i);
      if(InputStream.class.isAssignableFrom(c)) {
        Object o = r.getParameters().get(i);
        if(o != null && InputStream.class
            .isAssignableFrom(o.getClass())) {
          t.setInputStream((InputStream) o);
          r.getParameters().set(i, new FakeInputStreamRef());
        }
      }
    }
  }
  
  
  /**
   * Sends a <code>Transport</code> object over the wire (Channel).
   * @param trp <code>Transport</code> object to send.
   * @return <code>Channel</code> for network communication.
   * @throws IOException In case of error sending the object.
   */
  public Channel sendTransport(Transport trp) throws IOException {
    if(trp == null) throw new 
        IllegalArgumentException(
        "Invalid Null RemoteMethod");
    if(net == null) throw new 
        IllegalStateException(
        "Invalid Null NetConnector");
    
    try {
      this.channel();
    } catch(RuntimeException e) {
      throw new IOException(e.toString(), e);
    }
    
    try {
      channel.write(trp);
    } catch(IOException e) {
      newChannel().write(trp);
    }
    return channel;
  }
  
}
