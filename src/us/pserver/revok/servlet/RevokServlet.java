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

package us.pserver.revok.servlet;

import us.pserver.revok.channel.ServletChannel;
import java.util.List;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.pserver.revok.container.Authenticator;
import us.pserver.revok.container.Credentials;
import us.pserver.revok.container.CredentialsSource;
import us.pserver.revok.container.ObjectContainer;
import us.pserver.revok.protocol.RunnableConnectionHandler;
import us.pserver.revok.protocol.JsonSerializer;
import us.pserver.revok.protocol.ObjectSerializer;
import us.pserver.tools.rfl.Reflector;

/**
 * Servlet to be embeded in a servlet container, for handling 
 * Http RPC instead the standalone server <code>RevokServer</code>.
 * 
 * @author Juno Roesler - juno@pserver.com
 * @version 1.1 - 201506
 */
public class RevokServlet extends HttpServlet {
  
  private ObjectContainer container;
  
  private ObjectSerializer serial;
  
  private ServletConfigUtil util;
  
  private Logger log;
  
  
  /**
   * Read web.xml config file for custom ObjectSerializer class.
   * @throws ServletException In case of error initializing the ObjectSerializer.
   */
  private void initObjectSerializer() throws ServletException {
    log.debug("Init ObjectSerializer...");
    Reflector ref = new Reflector();
    String name = ObjectSerializer.class.getName();
    if(util.hasParam(name)) {
      serial = (ObjectSerializer) ref.onClass(
          util.getParam(name)).create();
      if(ref.hasError()) {
        String msg = "Error creating ObjectSerializer: "+ util.getParam(name);
				log.error(msg, ref.getError());
        throw new ServletException(msg, ref.getError());
      }
      log.debug("Using config custom serializer: "+ util.getParam(name));
    }
    else {
      serial = new JsonSerializer();
    }
  }
  
  
  /**
   * Read web.xml config file for Objects class to be exposed for Http RPC.
   * @throws ServletException In case of error initializing the Objects.
   */
  private void initObjects() {
    log.debug("Init Objects...");
    String name = ObjectContainer.class.getName();
    List<ServletObjectParam> lso = util.getObjectParamList(name);
    if(!util.hasParam(name) || lso.isEmpty()) {
      log.warn("No objects to initialize");
      return;
    }
    lso.forEach(p->{
      log.debug("Adding configured object: {} = {}", p.getName(), p.getClassName());
      container.put(p.getName(), p.createObject());
    });
  }
  
  
  /**
   * Read web.xml config file for Credentials information or CredentialsSource custom class.
   * Credentials can be writed under the key <code>us.pserver.revok.container.Credentials</code>
   * and with format <code>&lt;user&gt;:&lt;password&gt;@&lt;namespace&gt;</code>.
   * @throws ServletException In case of error initializing Credentials.
   */
  private void initCredentials() throws ServletException {
    log.debug("Init ObjectContainer...");
    if(util.hasParam(Credentials.class.getName())) {
      ServletCredentialsSource src = new ServletCredentialsSource(util);
      log.debug("Using configured credentials: {}", src);
      container = new ObjectContainer(new Authenticator(src));
    }
    else if(util.hasParam(CredentialsSource.class.getName())) {
      Reflector ref = new Reflector();
      String sclass = util.getParam(CredentialsSource.class.getName());
      CredentialsSource src = (CredentialsSource) 
          ref.onClass(sclass).create();
      if(ref.hasError()) {
        String msg = "Error creating CredentialsSource: "+ sclass;
        log.error(msg, ref.getError());
        throw new ServletException(msg, ref.getError());
      }
      container = new ObjectContainer(new Authenticator(src));
      log.debug("Using custom configured CredentialsSource: "+ sclass);
    }
    else {
      container = new ObjectContainer();
    }
  }
  
  
  @Override
  public void init(ServletConfig config) throws ServletException {
		log = LoggerFactory.getLogger(this.getClass());
		ch.qos.logback.classic.Logger lb = 
				(ch.qos.logback.classic.Logger) log;
		lb.addAppender(
				new ServletAppenderBase(
						config.getServletContext())
		);
    log.debug("Init Servlet...");
    util = new ServletConfigUtil(config);
    this.initObjectSerializer();
    this.initCredentials();
    this.initObjects();
  }
  
  
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
    try {
      ServletChannel channel = new ServletChannel(req, resp, serial);
      RunnableConnectionHandler handler = new RunnableConnectionHandler(channel, container);
      handler.run();
      handler.close();
    } catch(Exception e) {
      log.error("Error handling POST request", e);
      throw new ServletException(e.toString(), e);
    }
  }
  
}
