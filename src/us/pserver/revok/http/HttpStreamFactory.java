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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import us.pserver.cdr.StringByteConverter;
import us.pserver.cdr.crypt.CryptAlgorithm;
import us.pserver.cdr.crypt.CryptKey;
import us.pserver.revok.protocol.JsonSerializer;
import us.pserver.revok.protocol.ObjectSerializer;
import us.pserver.revok.protocol.XmlSerializer;
import us.pserver.streams.IO;
import us.pserver.streams.MixedWriteBuffer;
import us.pserver.streams.StreamCoderFactory;

/**
 * A factory for Http entities embedded
 * in the Http message body.
 * 
 * @author Juno Roesler - juno.rr@gmail.com
 * @version 1.0 - 08/04/2015
 */
public class HttpStreamFactory {

  
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
  
  private final StringByteConverter scv;
  
  private ContentType type;
  
  private CryptKey key;
  
  private Object obj;
  
  private InputStream input;
  
  private ObjectSerializer serial;
  
  private StreamCoderFactory streamfact;
  
  
  /**
   * Constructor receives the mime content type.
   * @param type mime content type.
   */
  public HttpStreamFactory(ContentType type) {
    if(type == null)
      type = TYPE_X_JAVA_ROB;
    this.type = type;
    buffer = new MixedWriteBuffer();
    scv = new StringByteConverter();
    key = null;
    obj = null;
    input = null;
    serial = new JsonSerializer();
    streamfact = StreamCoderFactory.getNew();
  }
  
  
  /**
   * Constructor which receives the mime content 
   * type and the object serializator.
   * @param type Mime content type.
   * @param os <code>ObjectSerializer</code>.
   */
  public HttpStreamFactory(ContentType type, ObjectSerializer os) {
    this(type);
    if(os == null) os = new JsonSerializer();
    serial = os;
  }
  
  
  /**
   * Constructor which receives an <code>ObjectSerializer</code>.
   * @param os <code>ObjectSerializer</code>.
   */
  public HttpStreamFactory(ObjectSerializer os) {
    this(TYPE_X_JAVA_ROB);
    if(os == null) os = new JsonSerializer();
    serial = os;
  }
  
  
  /**
   * Default constructor without arguments.
   */
  public HttpStreamFactory() {
    this(TYPE_X_JAVA_ROB);
  }
  
  
  /**
   * Create an <code>HttpEntityFactory</code> 
   * with the specified content mime type.
   * @param type Content mime type.
   * @return <code>HttpEntityFactory</code> instance.
   */
  public static HttpStreamFactory instance(ContentType type) {
    return new HttpStreamFactory(type);
  }
  
  
  /**
   * Create an <code>HttpEntityFactory</code> 
   * with the specified content mime type and 
   * object serializator.
   * @param type Content mime type.
   * @param os <code>ObjectSerializer</code>.
   * @return <code>HttpEntityFactory</code> instance.
   */
  public static HttpStreamFactory instance(ContentType type, ObjectSerializer os) {
    return new HttpStreamFactory(type, os);
  }
  
  
  /**
   * Create an <code>HttpEntityFactory</code> 
   * with the specified object serializator.
   * @param os <code>ObjectSerializer</code>.
   * @return <code>HttpEntityFactory</code> instance.
   */
  public static HttpStreamFactory instance(ObjectSerializer os) {
    return new HttpStreamFactory(os);
  }
  
  
  /**
   * Create an <code>HttpEntityFactory</code>.
   * @return <code>HttpEntityFactory</code> instance.
   */
  public static HttpStreamFactory instance() {
    return new HttpStreamFactory();
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
  public HttpStreamFactory setObjectSerializer(ObjectSerializer serializer) {
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
  public HttpStreamFactory enableCryptCoder(CryptKey key) {
    if(key != null) {
      streamfact.setCryptCoderEnabled(true, key);
      this.key = key;
    }
    return this;
  }
  
  
  /**
   * Disable all coders for this instance of 
   * <code>HttpEntityFactory</code>.
   * @return This modified <code>HttpEntityFactory</code> instance.
   */
  public HttpStreamFactory disableAllCoders() {
    streamfact.clearCoders();
    return this;
  }
  
  
  /**
   * Disable criptography for this instance of 
   * <code>HttpEntityFactory</code>.
   * @return This modified <code>HttpEntityFactory</code> instance.
   */
  public HttpStreamFactory disableCryptCoder() {
    streamfact.setCryptCoderEnabled(false, null);
    return this;
  }
  
  
  /**
   * Enable GZip compression for this instance of 
   * <code>HttpEntityFactory</code>.
   * @return This modified <code>HttpEntityFactory</code> instance.
   */
  public HttpStreamFactory enableGZipCoder() {
    streamfact.setGZipCoderEnabled(true);
    return this;
  }
  
  
  /**
   * Disable GZip compression for this instance of 
   * <code>HttpEntityFactory</code>.
   * @return This modified <code>HttpEntityFactory</code> instance.
   */
  public HttpStreamFactory disableGZipCoder() {
    streamfact.setGZipCoderEnabled(false);
    return this;
  }
  
  
  /**
   * Enable Base64 encoding for this instance of 
   * <code>HttpEntityFactory</code>.
   * @return This modified <code>HttpEntityFactory</code> instance.
   */
  public HttpStreamFactory enableBase64Coder() {
    streamfact.setBase64CoderEnabled(true);
    return this;
  }
  
  
  /**
   * Disable Base64 encoding for this instance of 
   * <code>HttpEntityFactory</code>.
   * @return This modified <code>HttpEntityFactory</code> instance.
   */
  public HttpStreamFactory disableBase64Coder() {
    streamfact.setBase64CoderEnabled(false);
    return this;
  }
  
  
  /**
   * Put an object for embed in http content.
   * @param obj Object for embed in http content.
   * @return This modified <code>HttpEntityFactory</code> instance.
   */
  public HttpStreamFactory put(Object obj) {
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
  public HttpStreamFactory put(InputStream is) {
    if(is != null) {
      this.input = is;
    }
    return this;
  }
  
  
  /**
   * Write the criptography key in http content.
   * @throws IOException In case of error writing.
   */
  private void writeCryptKey(ContentStream cont) throws IOException {
    if(cont != null && key != null) {
      // write plain data
      cont.writebuf.write(scv.convert(XmlConsts.START_CRYPT_KEY));
      cont.writebuf.write(scv.convert(key.toString()));
      cont.writebuf.write(scv.convert(XmlConsts.END_CRYPT_KEY));
    }
  }
  
  
  /**
   * Write the object in http content.
   * @param os OutputStream for write the content.
   * @throws IOException In case of error writing.
   */
  private void writeObject(ContentStream cont) throws IOException {
    if(cont == null || obj == null) return;
    cont.encoded.write(scv.convert(XmlConsts.START_ROB));
    cont.encoded.write(serial.toBytes(obj));
    cont.encoded.write(scv.convert(XmlConsts.END_ROB));
    cont.encoded.flush();
  }
  
  
  /**
   * Write the input stream content.
   * @param os OutputStream for write the content.
   * @throws IOException In case of error writing.
   */
  private void writeInputStream(ContentStream cont) throws IOException {
    if(cont == null || input == null) return;
    
    if(input != null) {
      if(obj == null) {
        buffer.write(scv.convert(XmlConsts.START_CONTENT));
      }
      cont.encoded.write(scv.convert(XmlConsts.START_STREAM));
      cont
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
   * Create an <code>InputStream</code> with the content to be transmitted.
   * @return <code>InputStream</code> with the content to be transmitted.
   * @throws IOException In case of error creating the <code>InputStream</code>.
   */
  public InputStream createStream() throws IOException {
    if(obj == null && input == null)
      return null;
    
    ContentStream content = new ContentStream();
    content.writebuf.write(scv.convert(XmlConsts.START_XML));
    
    writeCryptKey(content);
    if(obj != null || input != null)
      content.writebuf.write(scv.convert(XmlConsts.START_CONTENT));
    
    if(streamfact.isAnyCoderEnabled()) {
      content.encoded = streamfact.create(content.writebuf);
    }
    else {
      content.encoded = content.writebuf;
    }
    
    writeObject(content);
    writeInputStream(content);
    
    os.write(scv.convert(XmlConsts.END_XML));
    os.flush();
    os.close();
    
    return buffer.getReadBuffer().getRawInputStream();
  }
  
  
  
  class ContentStream extends InputStream {

    ByteArrayOutputStream writebuf;
    
    ByteArrayOutputStream endbuf;
    
    OutputStream encoded;
    
    ByteArrayInputStream readbuf;
    
    InputStream src;
    
    
    public ContentStream() {
      src = null;
      writebuf = new ByteArrayOutputStream();
      endbuf = new ByteArrayOutputStream();
      readbuf = null;
    }

    @Override
    public int read() throws IOException {
      if(readbuf != null && readbuf.available() > 0) {
        return readbuf.read();
      }
      else {
        fillBuffer();
        if(readbuf != null && readbuf.available() > 0) {
          return readbuf.read();
        }
        else {
          return -1;
        }
      }
    }
    
    @Override
    public int read(byte[] bs, int off, int len) throws IOException {
      if(readbuf != null && readbuf.available() > 0) {
        return readbuf.read(bs, off, len);
      }
      else {
        fillBuffer();
        if(readbuf != null && readbuf.available() > 0) {
          return readbuf.read(bs, off, len);
        }
        else {
          if(encoded != null) {
            encoded.flush();
            encoded.close();
            encoded = null;
            if(writebuf.size() > 0 || endbuf.size() > 0)
              return read(bs, off, len);
          }
          return -1;
        }
      }
    }
    
    @Override
    public int read(byte[] bs) throws IOException {
      return read(bs, 0, bs.length);
    }
    
    public void fillBuffer() throws IOException {
      if(writebuf.size() > 0) {
        readbuf = new ByteArrayInputStream(writebuf.toByteArray());
        writebuf.reset();
      }
      else {
        byte[] buf = new byte[4096];
        int read = src.read(buf);
        if(read < 1) {
          if(endbuf.size() > 0) {
            
          }
          else return;
        }
        if(encoded != null) 
          encoded.write(buf, 0, read);
        else 
          writebuf.write(buf, 0, read);
        fillBuffer();
      }
    }
    
  }
  
  
  public static void main(String[] args) throws IOException {
    HttpStreamFactory fac = HttpStreamFactory.instance(new XmlSerializer())
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
