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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import us.pserver.cdr.StringByteConverter;
import us.pserver.cdr.crypt.CryptAlgorithm;
import us.pserver.cdr.crypt.CryptKey;
import us.pserver.revok.protocol.JsonSerializer;
import us.pserver.revok.protocol.ObjectSerializer;
import us.pserver.revok.protocol.XmlSerializer;
import us.pserver.streams.EncoderInputStream;
import us.pserver.streams.FunnelInputStream;
import us.pserver.streams.MixedWriteBuffer;
import us.pserver.streams.StreamCoderFactory;
import us.pserver.streams.StreamUtils;
import us.pserver.streams.StringBuilderInputStream;

/**
 * A factory for embed Http RPC info
 * in the Http message body.
 * 
 * @author Juno Roesler - juno@pserver.com
 * @version 1.1 - 201506
 */
public class HttpEntityFactory {

  
  /**
   * <code>
   * TYPE_X_JAVA_ROB = "application/x-java-rob"
   * </code><br>
   * Mime type for java remote object.
   */
  public static final ContentType 
      TYPE_X_JAVA_ROB = ContentType.create(
          "application/x-java-rob", Consts.UTF_8);
  
  
  private MixedWriteBuffer buffer;
  
  private final StreamCoderFactory streamCoder;
  
  private final FunnelInputStream funnelRaw;
  
  private final FunnelInputStream funnelEnc;
  
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
  public HttpEntityFactory(ContentType type) {
    if(type == null)
      type = TYPE_X_JAVA_ROB;
    this.type = type;
    buffer = new MixedWriteBuffer();
    scv = new StringByteConverter();
    key = null;
    obj = null;
    input = null;
    serial = new JsonSerializer();
    streamCoder = StreamCoderFactory.getNew();
    funnelRaw = new FunnelInputStream();
    funnelEnc = new FunnelInputStream();
  }
  
  
  /**
   * Constructor which receives the mime content 
   * type and the object serializator.
   * @param type Mime content type.
   * @param os <code>ObjectSerializer</code>.
   */
  public HttpEntityFactory(ContentType type, ObjectSerializer os) {
    this(type);
    if(os == null) os = new JsonSerializer();
    serial = os;
  }
  
  
  /**
   * Constructor which receives an <code>ObjectSerializer</code>.
   * @param os <code>ObjectSerializer</code>.
   */
  public HttpEntityFactory(ObjectSerializer os) {
    this(TYPE_X_JAVA_ROB);
    if(os == null) os = new JsonSerializer();
    serial = os;
  }
  
  
  /**
   * Default constructor without arguments.
   */
  public HttpEntityFactory() {
    this(TYPE_X_JAVA_ROB);
  }
  
  
  /**
   * Create an <code>HttpEntityFactory</code> 
   * with the specified content mime type.
   * @param type Content mime type.
   * @return <code>HttpEntityFactory</code> instance.
   */
  public static HttpEntityFactory instance(ContentType type) {
    return new HttpEntityFactory(type);
  }
  
  
  /**
   * Create an <code>HttpEntityFactory</code> 
   * with the specified content mime type and 
   * object serializator.
   * @param type Content mime type.
   * @param os <code>ObjectSerializer</code>.
   * @return <code>HttpEntityFactory</code> instance.
   */
  public static HttpEntityFactory instance(ContentType type, ObjectSerializer os) {
    return new HttpEntityFactory(type, os);
  }
  
  
  /**
   * Create an <code>HttpEntityFactory</code> 
   * with the specified object serializator.
   * @param os <code>ObjectSerializer</code>.
   * @return <code>HttpEntityFactory</code> instance.
   */
  public static HttpEntityFactory instance(ObjectSerializer os) {
    return new HttpEntityFactory(os);
  }
  
  
  /**
   * Create an <code>HttpEntityFactory</code>.
   * @return <code>HttpEntityFactory</code> instance.
   */
  public static HttpEntityFactory instance() {
    return new HttpEntityFactory();
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
  public HttpEntityFactory setObjectSerializer(ObjectSerializer serializer) {
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
  public HttpEntityFactory enableCryptCoder(CryptKey key) {
    if(key != null) {
      this.key = key;
      streamCoder.setCryptCoderEnabled(true, key);
    }
    return this;
  }
  
  
  /**
   * Disable all coders for this instance of 
   * <code>HttpEntityFactory</code>.
   * @return This modified <code>HttpEntityFactory</code> instance.
   */
  public HttpEntityFactory disableAllCoders() {
    if(streamCoder.isAnyCoderEnabled()) {
      streamCoder.setBase64CoderEnabled(false)
          .setCryptCoderEnabled(false, null)
          .setGZipCoderEnabled(false);
    }
    return this;
  }
  
  
  /**
   * Disable criptography for this instance of 
   * <code>HttpEntityFactory</code>.
   * @return This modified <code>HttpEntityFactory</code> instance.
   */
  public HttpEntityFactory disableCryptCoder() {
    streamCoder.setCryptCoderEnabled(false, null);
    return this;
  }
  
  
  /**
   * Enable GZip compression for this instance of 
   * <code>HttpEntityFactory</code>.
   * @return This modified <code>HttpEntityFactory</code> instance.
   */
  public HttpEntityFactory enableGZipCoder() {
    streamCoder.setGZipCoderEnabled(true);
    return this;
  }
  
  
  /**
   * Disable GZip compression for this instance of 
   * <code>HttpEntityFactory</code>.
   * @return This modified <code>HttpEntityFactory</code> instance.
   */
  public HttpEntityFactory disableGZipCoder() {
    streamCoder.setGZipCoderEnabled(false);
    return this;
  }
  
  
  /**
   * Enable Base64 encoding for this instance of 
   * <code>HttpEntityFactory</code>.
   * @return This modified <code>HttpEntityFactory</code> instance.
   */
  public HttpEntityFactory enableBase64Coder() {
    streamCoder.setBase64CoderEnabled(true);
    return this;
  }
  
  
  /**
   * Disable Base64 encoding for this instance of 
   * <code>HttpEntityFactory</code>.
   * @return This modified <code>HttpEntityFactory</code> instance.
   */
  public HttpEntityFactory disableBase64Coder() {
    streamCoder.setBase64CoderEnabled(false);
    return this;
  }
  
  
  /**
   * Put an object for embed in http content.
   * @param obj Object for embed in http content.
   * @return This modified <code>HttpEntityFactory</code> instance.
   */
  public HttpEntityFactory put(Object obj) {
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
  public HttpEntityFactory put(InputStream is) {
    if(is != null) {
      this.input = is;
    }
    return this;
  }
  
  
  /**
   * Write the criptography key in http content.
   * @throws IOException In case of error writing.
   */
  private void appendCryptKey() throws IOException {
    if(key != null) {
      StringBuilderInputStream sin = new StringBuilderInputStream()
          .append(XmlConsts.START_CRYPT_KEY)
          .append(key.toString())
          .append(XmlConsts.END_CRYPT_KEY);
      funnelRaw.append(sin);
    }
  }
  
  
  /**
   * Write the object in http content.
   * @param os OutputStream for write the content.
   * @throws IOException In case of error writing.
   */
  private void appendObject() throws IOException {
    if(obj != null) {
      funnelRaw.append(
          new StringBuilderInputStream(XmlConsts.START_CONTENT)
      );
      funnelEnc.append(
          new StringBuilderInputStream(XmlConsts.START_ROB)
      ).append(
          new ByteArrayInputStream(serial.toBytes(obj))
      ).append(
          new StringBuilderInputStream(XmlConsts.END_ROB)
      );
    }
  }
  
  
  /**
   * Write the input stream content.
   * @param os OutputStream for write the content.
   * @throws IOException In case of error writing.
   */
  private void appendInputStream() throws IOException {
    if(input != null) {
      if(obj == null) {
        funnelRaw.append(
            new StringBuilderInputStream(XmlConsts.START_CONTENT)
        );
      }
      funnelEnc.append(
          new StringBuilderInputStream(XmlConsts.START_STREAM)
      ).append(input
      ).append(
          new StringBuilderInputStream(XmlConsts.END_STREAM)
      );
    }
    if(obj != null || input != null) {
      funnelEnc.append(
          new StringBuilderInputStream(XmlConsts.END_CONTENT)
      );
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
    if(key == null && obj == null && input == null) {
      throw new IllegalStateException(String.format(
          "No content to encode {key=%s, obj=%s, input=%s}", key, obj, input)
      );
    }
    funnelRaw.listStream().clear();
    funnelEnc.listStream().clear();
    funnelRaw.append(
        new StringBuilderInputStream(XmlConsts.START_XML)
    );
    appendCryptKey();
    appendObject();
    appendInputStream();
    funnelEnc.append(
        new StringBuilderInputStream(XmlConsts.END_XML)
    );
    InputStream encoding = funnelEnc;
    if(streamCoder.isAnyCoderEnabled()) {
      encoding = new EncoderInputStream(funnelEnc, streamCoder);
    }
    return new FunnelInputStream()
        .append(funnelRaw)
        .append(encoding);
  }
  
  
  public static void main(String[] args) throws IOException {
    class MSG {
      String str;
      public MSG(String s) { str = s; }
      public String toString() { return "MSG{str="+ str+ "}"; }
    }
    CryptKey key = CryptKey.createRandomKey(CryptAlgorithm.AES_CBC_256_PKCS5);
    HttpEntityFactory fac = HttpEntityFactory.instance()
        .enableGZipCoder()
        .enableCryptCoder(key)
        .put(new MSG("Hello EntityFactory!"));
    
    InputStream in = fac.createStream();
    StreamUtils.transfer(in, System.out);
    System.out.println();
    
    fac = HttpEntityFactory.instance()
        .enableGZipCoder()
        .enableCryptCoder(key)
        .put(new MSG("Hello EntityFactory!"));
    in = fac.createStream();
    HttpEntityParser ep = HttpEntityParser.instance()
        .enableGZipCoder()
        .enableCryptCoder(key)
        ;
    ep.parse(in);
    System.out.println("* key: "+ ep.getCryptKey());
    System.out.println("* rob: "+ ep.getObject());
    //EntityUtils.consume(ent);
  }
  
}
