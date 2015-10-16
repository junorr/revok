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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import us.pserver.cdr.crypt.CryptKey;
import us.pserver.revok.protocol.JsonSerializer;
import us.pserver.revok.protocol.ObjectSerializer;
import us.pserver.streams.BulkStoppableInputStream;
import us.pserver.streams.StreamCoderFactory;
import us.pserver.streams.StreamResult;
import us.pserver.streams.StreamUtils;
import us.pserver.tools.UTF8String;

/**
 * Parser for reading and converting Http message body in the RPC info.
 * 
 * @author Juno Roesler - juno@pserver.com
 * @version 1.1 - 201506
 */
public class HttpEntityParser {

  private final StreamCoderFactory streamCoder;
  
  private InputStream input;
  
  private InputStream decoder;
  
  private Object obj;
  
  private CryptKey key;
  
  private ObjectSerializer serial;
  
  
  /**
   * Default constructor without arguments.
   */
  public HttpEntityParser() {
    this(null);
  }
  
  
  /**
   * Constructor with <code>ObjectSerializer</code>
   * for objects serialization.
   * @param os <code>ObjectSerializer</code>
   * for objects serialization.
   */
  public HttpEntityParser(ObjectSerializer os) {
    input = null;
    obj = null;
    key = null;
    if(os == null) os = new JsonSerializer();
    serial = os;
    streamCoder = StreamCoderFactory.getNew();
  }
  
  
  /**
   * Create a new instance of <code>HttpEntityParser</code>.
   * @return New <code>HttpEntityParser</code> instance.
   */
  public static HttpEntityParser instance() {
    return new HttpEntityParser();
  }
  
  
  /**
   * Create a new instance of <code>HttpEntityParser</code>,
   * with the specified <code>ObjectSerializer</code>.
   * @param os The specified <code>ObjectSerializer</code>.
   * @return New <code>HttpEntityParser</code> instance.
   */
  public static HttpEntityParser instance(ObjectSerializer os) {
    return new HttpEntityParser(os);
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
  public HttpEntityParser setObjectSerializer(ObjectSerializer serializer) {
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
  public HttpEntityParser enableCryptCoder(CryptKey key) {
    if(key != null) {
      streamCoder.setCryptCoderEnabled(true, key);
    }
    return this;
  }
  
  
  /**
   * Disable all coders for this instance of 
   * <code>HttpEntityFactory</code>.
   * @return This modified <code>HttpEntityFactory</code> instance.
   */
  public HttpEntityParser disableAllCoders() {
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
  public HttpEntityParser disableCryptCoder() {
    streamCoder.setCryptCoderEnabled(false, null);
    return this;
  }
  
  
  /**
   * Enable GZip compression for this instance of 
   * <code>HttpEntityFactory</code>.
   * @return This modified <code>HttpEntityFactory</code> instance.
   */
  public HttpEntityParser enableGZipCoder() {
    streamCoder.setGZipCoderEnabled(true);
    return this;
  }
  
  
  /**
   * Disable GZip compression for this instance of 
   * <code>HttpEntityFactory</code>.
   * @return This modified <code>HttpEntityFactory</code> instance.
   */
  public HttpEntityParser disableGZipCoder() {
    streamCoder.setGZipCoderEnabled(false);
    return this;
  }
  
  
  /**
   * Enable Base64 encoding for this instance of 
   * <code>HttpEntityFactory</code>.
   * @return This modified <code>HttpEntityFactory</code> instance.
   */
  public HttpEntityParser enableBase64Coder() {
    streamCoder.setBase64CoderEnabled(true);
    return this;
  }
  
  
  /**
   * Disable Base64 encoding for this instance of 
   * <code>HttpEntityFactory</code>.
   * @return This modified <code>HttpEntityFactory</code> instance.
   */
  public HttpEntityParser disableBase64Coder() {
    streamCoder.setBase64CoderEnabled(false);
    return this;
  }
  

  /**
   * Get the readed object from <code>HttpEntity</code>.
   * @return The readed object from <code>HttpEntity</code>.
   */
  public Object getObject() {
    return obj;
  }
  
  
  /**
   * Get the readed criptography key from <code>HttpEntity</code>.
   * @return The readed criptography key from <code>HttpEntity</code>.
   */
  public CryptKey getCryptKey() {
    return key;
  }
  
  
  /**
   * Get the readed input stream from <code>HttpEntity</code>.
   * @return The readed input stream from <code>HttpEntity</code>.
   */
  public InputStream getInputStream() {
    return input;
  }
  
  
  /**
   * Read five chars from the input stream.
   * @param is The <code>InputStream</code> for read.
   * @return <code>String</code> with five chars readed.
   * @throws IOException In case of error reading.
   */
  private String readFive(InputStream is) throws IOException {
    if(is == null) return null;
    return StreamUtils.readString(is, 5);
  }
  
  
  /**
   * Verify if the informed string <code>five</code>
   * is not null and equal to the <code>expected</code> string.
   * @param expected The expected string.
   * @param five The string to verify.
   * @throws IOException Case the string to verify is null or not equal to the expected string.
   */
  private void checkExpectedToken(String expected, String five) throws IOException {
    if(expected == null) return;
    if(five == null || !expected.equals(five))
      throw new IOException("Invalid Content to Parse {"
          + "expected="+ expected+ ", read="+ five+ "}");
  }
  
  
  /**
   * Parse the specified <code>HttpEntity</code> content.
   * @param entity <code>HttpEntity</code> to parse.
   * @return This modified <code>HttpEntityParser</code> instance.
   * @throws IOException In case of error parsing.
   */
  public HttpEntityParser parse(HttpEntity entity) throws IOException {
    if(entity == null)
      throw new IllegalArgumentException("Invalid HttpEntity {"+ entity+ "}");
    
    this.parse(entity.getContent());
    return this;
  }
  
  
  /**
   * Parse the specified <code>InputStream</code> content.
   * @param content <code>InputStream</code> to parse.
   * @return This modified <code>HttpEntityParser</code> instance.
   * @throws IOException In case of error parsing.
   */
  public HttpEntityParser parse(InputStream content) throws IOException {
    if(content == null)
      throw new IllegalArgumentException("Invalid InputStream {"+ content+ "}");
    
    checkExpectedToken(XmlConsts.START_XML, readFive(content));
    
    String five = readFive(content);
    five = tryCryptKey(content, five);
    checkExpectedToken(XmlConsts.START_CONTENT, five);
    decoder = content;
    if(streamCoder.isAnyCoderEnabled())
      decoder = streamCoder.create(content);
    
    five = readFive(decoder);
    five = tryObject(decoder, five);
    tryStream(decoder, five);
    return this;
  }
  
  
  /**
   * Try to parse a criptography key from the <code>InputStream</code>.
   * @param is <code>InputStream</code> for read the content.
   * @param five <code>String</code> with the five chars readed from <code>InputStream</code>.
   * @return The next five chars readed from <code>InputStream</code>.
   * @throws IOException In case of error parsing.
   */
  private String tryCryptKey(InputStream is, String five) throws IOException {
    if(five == null || five.trim().isEmpty() || is == null)
      return five;
    if(XmlConsts.START_CRYPT_KEY.contains(five)) {
      StreamUtils.skipUntil(is, XmlConsts.GT);
      StreamResult sr = StreamUtils.readStringUntil(is, XmlConsts.END_CRYPT_KEY);
      key = CryptKey.fromString(sr.content());
      if(!streamCoder.isCryptCoderEnabled()) {
        this.enableCryptCoder(key);
      }
      five = readFive(is);
    }
    return five;
  }
  
  
  /**
   * Try to parse an object from the <code>InputStream</code>.
   * @param is <code>InputStream</code> for read the content.
   * @param five <code>String</code> with the five chars readed from <code>InputStream</code>.
   * @return The next five chars readed from <code>InputStream</code>.
   * @throws IOException In case of error parsing.
   */
  private String tryObject(InputStream is, String five) throws IOException {
    if(five == null || five.trim().isEmpty() || is == null)
      return five;
    if(XmlConsts.START_ROB.equals(five)) {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      StreamUtils.transferUntil(is, bos, XmlConsts.END_ROB);
      obj = serial.fromBytes(bos.toByteArray());
      five = readFive(is);
    }
    return five;
  }
  
  
  /**
   * Try to parse an embed input stream content from the <code>InputStream</code>.
   * @param is <code>InputStream</code> for read the content.
   * @param five <code>String</code> with the five chars readed from <code>InputStream</code>.
   * @throws IOException In case of error parsing.
   */
  private void tryStream(InputStream is, String five) throws IOException {
    if(five == null || five.trim().isEmpty() || is == null)
      return;
    if(XmlConsts.START_STREAM.contains(five)) {
      StreamUtils.skipUntil(is, XmlConsts.GT);
      input = new BulkStoppableInputStream(is, 
          new UTF8String(XmlConsts.END_STREAM).getBytes(),
          stream->{ try {
            StreamUtils.consume(stream.getSourceInputStream());
            stream.close();
          } catch(IOException e) {}}
      );
    }
  }
  
}
