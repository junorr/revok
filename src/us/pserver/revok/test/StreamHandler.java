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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import us.pserver.streams.IO;

/**
 *
 * @author Juno Roesler - juno.rr@gmail.com
 * @version 1.0 - 08/05/2015
 */
public class StreamHandler implements IStreamHandler {


  @Override
  public long write(String path, InputStream input) throws IOException {
    if(path == null)
      throw new IOException("StreamHandler.write( Path, InputStream ): Invalid Path {"+ path+ "}");
    if(input == null)
      throw new IOException("StreamHandler.write( Path, InputStream ): Invalid InputStream {"+ input+ "}");
    return IO.tr(input, IO.os(IO.p(path)));
  }


  @Override
  public InputStream read(String path) throws IOException {
    if(path == null)
      throw new IOException("StreamHandler.read( Path ): Invalid Path {"+ path+ "}");
    return IO.is(IO.p(path));
  }


  @Override
  public long size(String path) throws IOException {
    if(path == null)
      throw new IOException("StreamHandler.size( Path ): Invalid Path {"+ path+ "}");
    return Files.size(IO.p(path));
  }

}
