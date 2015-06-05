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
import com.jpower.rfl.Reflector;
import java.util.List;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import us.pserver.revok.container.Authenticator;
import us.pserver.revok.container.Credentials;
import us.pserver.revok.container.CredentialsSource;
import us.pserver.revok.container.ObjectContainer;
import us.pserver.revok.protocol.RunnableConnectionHandler;
import us.pserver.revok.protocol.JsonSerializer;
import us.pserver.revok.protocol.ObjectSerializer;

/**
 *
 * @author Juno Roesler - juno.rr@gmail.com
 * @version 1.0 - 28/05/2015
 */
public class RevokServlet extends HttpServlet {
  
  private ObjectContainer container;
  
  private ObjectSerializer serial;
  
  private ServletConfigUtil util;
  
  private ServletLog log;
  
  
  public Class stringToClass(String str) {
    try { 
      return Class.forName(str); 
    }
    catch(ClassNotFoundException e) { 
      return null; 
    }
  }
  
  
  private void initObjectSerializer() throws ServletException {
    Reflector ref = new Reflector();
    String name = ObjectSerializer.class.getName();
    if(util.hasParam(name)) {
      serial = (ObjectSerializer) ref.onClass(
          util.getParam(name)).create();
      if(ref.hasError()) {
        String msg = "Error creating ObjectSerializer: "+ util.getParam(name);
        log.fatal(msg).fatal(ref.getError(), true);
        throw new ServletException(msg, ref.getError());
      }
      log.debug("Using config custom serializer: "+ util.getParam(name));
    }
    else {
      serial = new JsonSerializer();
    }
  }
  
  
  private void initObjects() {
    String name = ObjectContainer.class.getName();
    List<ServletObjectParam> lso = util.getObjectParamList(name);
    if(!util.hasParam(name) || lso.isEmpty()) {
      log.warning("No objects to initialize");
      return;
    }
    lso.forEach(p->{
      log.debug("Adding configured object: "+ p.getName()+ "="+ p.getClassName());
      container.put(p.getName(), p.createObject());
    });
  }
  
  
  private void initObjectContainer() throws ServletException {
    if(util.hasParam(Credentials.class.getName())) {
      log.debug("Using configured credentials: "
          + util.getParam(Credentials.class.getName()));
      CredentialsSource src = new ServletCredentialsSource(util);
      container = new ObjectContainer(new Authenticator(src));
    }
    else if(util.hasParam(CredentialsSource.class.getName())) {
      Reflector ref = new Reflector();
      String sclass = util.getParam(CredentialsSource.class.getName());
      CredentialsSource src = (CredentialsSource) 
          ref.onClass(sclass).create();
      if(ref.hasError()) {
        String msg = "Error creating CredentialsSource: "+ sclass;
        log.fatal(msg).fatal(ref.getError(), true);
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
    log = new ServletLog(this);
    util = new ServletConfigUtil(config);
    this.initObjectSerializer();
    this.initObjectContainer();
    this.initObjects();
  }
  
  
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
    try {
      ServletChannel channel = new ServletChannel(req, resp, serial);
      RunnableConnectionHandler handler = new RunnableConnectionHandler(channel, container, log);
      handler.run();
      handler.close();
    } catch(Exception e) {
      log.fatal(e, true);
      throw new ServletException(e.toString(), e);
    }
  }
  
}
