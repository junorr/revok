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

package us.pserver.revok.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.util.EntityUtils;
import us.pserver.cdr.StringByteConverter;
import us.pserver.cdr.crypt.CryptAlgorithm;
import us.pserver.cdr.crypt.CryptKey;
import us.pserver.revok.protocol.JsonSerializer;
import us.pserver.revok.protocol.ObjectSerializer;
import us.pserver.revok.protocol.XmlSerializer;
import us.pserver.streams.CoderType;
import us.pserver.streams.EncoderInputStream;
import us.pserver.streams.FunnelInputStream;
import us.pserver.streams.IO;

/**
 * A factory for embed Http RPC info
 * in the Http message body.
 * 
 * @author Juno Roesler - juno@pserver.com
 * @version 1.1 - 201506
 */
public class HttpContentFactory {

  
  /**
   * <code>
   * TYPE_X_JAVA_ROB = "application/x-java-rob"
   * </code><br>
   * Mime type for java remote object.
   */
  public static final ContentType 
      TYPE_X_JAVA_ROB = ContentType.create(
          "application/x-java-rob", Consts.UTF_8);
  
  
  private EncoderInputStream encoding;
  
  private FunnelInputStream funnel;
  
  private List<CoderType> coders;
  
  private final StringByteConverter scv;
  
  private ContentType type;
  
  private CryptKey key;
  
  private Object obj;
  
  private InputStream input;
  
  private ObjectSerializer serial;
  
  
  /**
   * Constructor receives the mime content type.
   * @param type mime content type.
   */
  public HttpContentFactory(ContentType type) {
    if(type == null)
      type = TYPE_X_JAVA_ROB;
    this.type = type;
    coders = new LinkedList<>();
    scv = new StringByteConverter();
    key = null;
    obj = null;
    input = null;
    serial = new JsonSerializer();
  }
  
  
  /**
   * Constructor which receives the mime content 
   * type and the object serializator.
   * @param type Mime content type.
   * @param os <code>ObjectSerializer</code>.
   */
  public HttpContentFactory(ContentType type, ObjectSerializer os) {
    this(type);
    if(os == null) os = new JsonSerializer();
    serial = os;
  }
  
  
  /**
   * Constructor which receives an <code>ObjectSerializer</code>.
   * @param os <code>ObjectSerializer</code>.
   */
  public HttpContentFactory(ObjectSerializer os) {
    this(TYPE_X_JAVA_ROB);
    if(os == null) os = new JsonSerializer();
    serial = os;
  }
  
  
  /**
   * Default constructor without arguments.
   */
  public HttpContentFactory() {
    this(TYPE_X_JAVA_ROB);
  }
  
  
  /**
   * Create an <code>HttpEntityFactory</code> 
   * with the specified content mime type.
   * @param type Content mime type.
   * @return <code>HttpEntityFactory</code> instance.
   */
  public static HttpContentFactory instance(ContentType type) {
    return new HttpContentFactory(type);
  }
  
  
  /**
   * Create an <code>HttpEntityFactory</code> 
   * with the specified content mime type and 
   * object serializator.
   * @param type Content mime type.
   * @param os <code>ObjectSerializer</code>.
   * @return <code>HttpEntityFactory</code> instance.
   */
  public static HttpContentFactory instance(ContentType type, ObjectSerializer os) {
    return new HttpContentFactory(type, os);
  }
  
  
  /**
   * Create an <code>HttpEntityFactory</code> 
   * with the specified object serializator.
   * @param os <code>ObjectSerializer</code>.
   * @return <code>HttpEntityFactory</code> instance.
   */
  public static HttpContentFactory instance(ObjectSerializer os) {
    return new HttpContentFactory(os);
  }
  
  
  /**
   * Create an <code>HttpEntityFactory</code>.
   * @return <code>HttpEntityFactory</code> instance.
   */
  public static HttpContentFactory instance() {
    return new HttpContentFactory();
  }
  
  
  /**
   * Get the object serializer used by this instance 
   * of <code>HttpEntityFactory</code>.
   * @return The object serializer used by this 
   * instance of <code>HttpEntityFactory</code>.
   */
  public ObjectSerializer getObjectSerializer() {
    return serial;
  }
  
  
  /**
   * Set the object serializer used by this instance 
   * of <code>HttpEntityFactory</code>.
   * @param serializer The object serializer used by this 
   * instance of <code>HttpEntityFactory</code>.
   * @return This modified <code>HttpEntityFactory</code> instance.
   */
  public HttpContentFactory setObjectSerializer(ObjectSerializer serializer) {
    if(serializer != null) {
      serial = serializer;
    }
    return this;
  }
  
  
  /**
   * Enable criptography for this instance of 
   * <code>HttpEntityFactory</code>.
   * @param key Criptography key.
   * @return This modified <code>HttpEntityFactory</code> instance.
   */
  public HttpContentFactory enableCryptCoder(CryptKey key) {
    if(key != null) {
      this.key = key;
      if(!coders.contains(CoderType.CRYPT))
        coders.add(CoderType.CRYPT);
    }
    return this;
  }
  
  
  /**
   * Disable all coders for this instance of 
   * <code>HttpEntityFactory</code>.
   * @return This modified <code>HttpEntityFactory</code> instance.
   */
  public HttpContentFactory disableAllCoders() {
    coders.clear();
    return this;
  }
  
  
  /**
   * Disable criptography for this instance of 
   * <code>HttpEntityFactory</code>.
   * @return This modified <code>HttpEntityFactory</code> instance.
   */
  public HttpContentFactory disableCryptCoder() {
    coders.remove(CoderType.CRYPT);
    return this;
  }
  
  
  /**
   * Enable GZip compression for this instance of 
   * <code>HttpEntityFactory</code>.
   * @return This modified <code>HttpEntityFactory</code> instance.
   */
  public HttpContentFactory enableGZipCoder() {
    if(!coders.contains(CoderType.GZIP))
      coders.add(CoderType.GZIP);
    return this;
  }
  
  
  /**
   * Disable GZip compression for this instance of 
   * <code>HttpEntityFactory</code>.
   * @return This modified <code>HttpEntityFactory</code> instance.
   */
  public HttpContentFactory disableGZipCoder() {
    coders.remove(CoderType.GZIP);
    return this;
  }
  
  
  /**
   * Enable Base64 encoding for this instance of 
   * <code>HttpEntityFactory</code>.
   * @return This modified <code>HttpEntityFactory</code> instance.
   */
  public HttpContentFactory enableBase64Coder() {
    if(!coders.contains(CoderType.BASE64))
      coders.add(CoderType.BASE64);
    return this;
  }
  
  
  /**
   * Disable Base64 encoding for this instance of 
   * <code>HttpEntityFactory</code>.
   * @return This modified <code>HttpEntityFactory</code> instance.
   */
  public HttpContentFactory disableBase64Coder() {
    coders.remove(CoderType.BASE64);
    return this;
  }
  
  
  /**
   * Put an object for embed in http content.
   * @param obj Object for embed in http content.
   * @return This modified <code>HttpEntityFactory</code> instance.
   */
  public HttpContentFactory put(Object obj) {
    if(obj != null) {
      this.obj = obj;
    }
    return this;
  }
  
  
  /**
   * Put an input stream for embed in http content.
   * @param is Input stream for embed in http content.
   * @return This modified <code>HttpEntityFactory</code> instance.
   */
  public HttpContentFactory put(InputStream is) {
    if(is != null) {
      this.input = is;
    }
    return this;
  }
  
  
  /**
   * Write the criptography key in http content.
   * @throws IOException In case of error writing.
   */
  private void writeCryptKey(OutputStream out) throws IOException {
    if(key != null) {
      // write plain data
      out.write(scv.convert(XmlConsts.START_CRYPT_KEY));
      out.write(scv.convert(key.toString()));
      out.write(scv.convert(XmlConsts.END_CRYPT_KEY));
    }
  }
  
  
  /**
   * Write the object in http content.
   * @param os OutputStream for write the content.
   * @throws IOException In case of error writing.
   */
  private void writeObject(OutputStream os) throws IOException {
    if(os == null) return;
    if(obj != null) {
      os.write(scv.convert(XmlConsts.START_ROB));
      os.write(serial.toBytes(obj));
      os.write(scv.convert(XmlConsts.END_ROB));
      os.flush();
    }
  }
  
  
  /**
   * Write the input stream content.
   * @param os OutputStream for write the content.
   * @throws IOException In case of error writing.
   */
  private void writeInputStream(OutputStream os) throws IOException {
    if(os == null) return;
    if(input != null) {
      if(obj == null) {
        buffer.write(scv.convert(XmlConsts.START_CONTENT));
      }
      os.write(scv.convert(XmlConsts.START_STREAM));
      IO.tr(input, os);
      os.write(scv.convert(XmlConsts.END_STREAM));
      os.flush();
    }
    if(obj != null || input != null) {
      os.write(scv.convert(XmlConsts.END_CONTENT));
      os.flush();
    }
  }
  
  
  /**
   * Create the <code>HttpEntity</code> with the content to be transmitted.
   * @return The <code>HttpEntity</code> with the content to be transmitted.
   * @throws IOException In case of error creating the <code>HttpEntity</code>.
   */
  public HttpEntity create() throws IOException {
    if(key == null && obj == null && input == null)
      return null;
    InputStream istream = createStream();
    return new InputStreamEntity(istream, istream.available(), type);
  }
  
  
  /**
   * Create an <code>InputStream</code> with the content to be transmitted.
   * @return <code>InputStream</code> with the content to be transmitted.
   * @throws IOException In case of error creating the <code>InputStream</code>.
   */
  public InputStream createStream() throws IOException {
    if(key == null && obj == null && input == null)
      return null;
    
    buffer.clear();
    buffer.write(scv.convert(XmlConsts.START_XML));
    // Encoded OutputStream
    OutputStream os = buffer.getOutputStream();
    
    writeCryptKey();
    writeObject(os);
    writeInputStream(os);
    
    os.write(scv.convert(XmlConsts.END_XML));
    os.flush();
    os.close();
    
    return buffer.getReadBuffer().getRawInputStream();
  }
  
  
  public static void main(String[] args) throws IOException {
    HttpContentFactory fac = HttpContentFactory.instance(new XmlSerializer())
        .enableGZipCoder()
        .enableCryptCoder(
            CryptKey.createRandomKey(CryptAlgorithm.AES_CBC_PKCS5));
    class MSG {
      String str;
      public MSG(String s) { str = s; }
      public String toString() { return "MSG{str="+ str+ "}"; }
    }
    fac.put(new MSG("Hello EntityFactory!"));
    HttpEntity ent = fac.create();
    ent.writeTo(System.out);
    System.out.println();
    
    ent = fac.create();
    HttpEntityParser ep = HttpEntityParser.instance(new XmlSerializer());//.enableGZipCoder();
    ep.parse(ent);
    System.out.println("* key: "+ ep.getCryptKey());
    System.out.println("* rob: "+ ep.getObject());
    EntityUtils.consume(ent);
  }
  
}
