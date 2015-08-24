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
import us.pserver.revok.protocol.ObjectSerializer;
import us.pserver.revok.proxy.RemoteInvocationHandler;
import us.pserver.revok.protocol.FakeInputStreamRef;
import us.pserver.tools.Valid;

/**
 * Represents a remote object for methods invocation.
 * 
 * @author Juno Roesler - juno@pserver.com
 * @version 1.1 - 201506
 */
public class RemoteObject {
  
  private final HttpConnector connector;
  
  private final ChannelFactory<HttpConnector> factory;
  
  private Channel channel;
  
  private final ObjectSerializer serial;
  
  
  /**
   * Default constructor without getArguments,
 uses a <code>XmlSerializer</code> for object serialization.
   */
  protected RemoteObject(HttpConnector hc, ChannelFactory<HttpConnector> cf, ObjectSerializer os) {
    connector = Valid.off(hc).forNull()
        .getOrFail(HttpConnector.class);
    factory = Valid.off(cf).forNull()
        .getOrFail(ChannelFactory.class);
    serial = Valid.off(os).forNull()
        .getOrFail(ObjectSerializer.class);
  }
  
  
  public static RemoteObjectBuilder builder() {
    return new RemoteObjectBuilder();
  }
  
  
  /**
   * Get the <code>ObjectSerializer</code> for objects serialization.
   * @return <code>ObjectSerializer</code> for objects serialization.
   */
  public ObjectSerializer getObjectSerializer() {
    return serial;
  }
  
  
  /**
   * Get the network informations <code>HttpConnector</code>.
   * @return Network informations <code>HttpConnector</code>.
   */
  public HttpConnector getHttpConnector() {
    return connector;
  }


  /**
   * Get the network channel factory.
   * @return Network channel factory <code>ChannelFactory</code>.
   */
  public ChannelFactory<HttpConnector> getChannelFactory() {
    return factory;
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
    
    if(connector == null) throw new IllegalStateException(
        "Cannot create Channel. Invalid NetConnector ["+ connector+ "]");
    if(factory == null) throw new IllegalStateException(
        "Invalid ChannelFactory ["+ factory+ "]");
    channel = factory.createChannel(connector, serial);
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
   * Create a Proxy instance of the remote object represented by the specified interface.
   * Any getMethod invocation in the returned proxy object, will be invoked remotly in the real object on server side.
   * @param <T> The type of the Proxy Object (same of the Class interface argument).
   * @param namespace The namespace on the server where is stored the remote instance, or the [namespace].[objectname].
   * @param interfac Class of the Interface.
   * @return The Proxy object created.
   */
  public <T> T createRemoteObject(String namespace, Class interfac) {
    if(namespace == null || namespace.trim().isEmpty())
      throw new IllegalArgumentException("Invalid Class {"+ interfac+ "}");
    if(interfac == null)
      throw new IllegalArgumentException("Invalid Class {"+ interfac+ "}");
    RemoteInvocationHandler handler = new RemoteInvocationHandler(this, namespace);
    T type = (T) Proxy.newProxyInstance(
        interfac.getClassLoader(), 
        new Class[]{interfac}, handler
    );
    handler.setInstance(type);
    return type;
  }
  
  
  /**
   * Invoke the remote getMethod.
   * @param rmt Remote getMethod information <code>RemoteMethod</code>.
   * @return Remote getMethod return value or <code>null</code>.
   * @throws MethodInvocationException In case of error invoking the getMethod.
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
   * Invoke the remote getMethod.
   * @param rmt Remote getMethod information <code>RemoteMethod</code>.
   * @throws MethodInvocationException In case of error invoking the getMethod.
   */
  public void invokeVoid(RemoteMethod rmt) throws MethodInvocationException {
    this.invoke(rmt);
  }
  
  
  /**
   * Invoke the remote getMethod.
   * @param rmt Remote getMethod information <code>RemoteMethod</code>.
   * @return Remote getMethod return value or <code>null</code>.
   */
  public OpResult invokeSafe(RemoteMethod rmt) {
    OpResult res = new OpResult();
    try {
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
   * Invoke the remote getMethod chain.
   * @param chain Remote getMethod chain information <code>MethodChain</code>.
   * @return Remote getMethod return value or <code>null</code>.
   * @throws MethodInvocationException In case of error invoking the getMethod.
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
   * Validates the getMethod chain.
   * @param chain <code>MethodChain</code>
   * @throws IllegalArgumentException If the getMethod chain is not valid.
   */
  private void validateChain(MethodChain chain) throws IllegalArgumentException {
    if(chain == null || chain.methods().isEmpty()) 
      throw new IllegalArgumentException(
        "Invalid MethodChain ["+ chain+ "]");
  }
  
  
  /**
   * 
   * Invoke the remote getMethod chain.
   * @param chain Remote getMethod chain information <code>MethodChain</code>.
   * @throws MethodInvocationException In case of error invoking the getMethod.
   */
  public void invokeVoid(MethodChain chain) throws MethodInvocationException {
    this.invoke(chain);
  }
  
  
  /**
   * Invoke the remote getMethod chain.
   * @param chain Remote getMethod chain information <code>MethodChain</code>.
   * @return Remote getMethod return value or <code>null</code>.
   */
  public OpResult invokeSafe(MethodChain chain) {
    this.validateChain(chain);
    OpResult res = new OpResult();
    try {
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
   * Check for <code>InputStream</code> reference in getMethod getArguments.
   * @param t <code>Transport</code> with remote getMethod object.
   * @param r Remote getMethod object.
   */
  private void checkInputStreamRef(Transport t, RemoteMethod r) {
    if(t == null || r == null) return;
    if(r.getArgumentTypes().isEmpty()) return;
    for(int i = 0; i < r.getArgumentTypes().size(); i++) {
      Class c = r.getArgumentTypes().get(i);
      if(InputStream.class.isAssignableFrom(c)) {
        Object o = r.getArguments().get(i);
        if(o != null && InputStream.class
            .isAssignableFrom(o.getClass())) {
          t.setInputStream((InputStream) o);
          r.getArguments().set(i, new FakeInputStreamRef());
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
    if(connector == null) throw new 
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
