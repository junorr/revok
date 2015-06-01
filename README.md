# Revok
<p>Revok is a Java RPC library, HTTP based, implementing client and server sides.<br>
The main goal of Revok RPC is to be simple to use and extensible, providing an API with a few classes and methods.<br> 
Here is some goods of this project:</p>

<ul>
  <li>Revok RPC is robust and fast, because uses Apache Http-Core (4.4.1) under the hoods.</li>
  <li>Revok is lightweight, with less then 265 KB without dependencies.</li>
  <li>Revok optimizes bandwidth consumption with GZIP compression.</li>
  <li>Revok is secure, because it uses AES-256 criptography by default.</li>
  <li>Revok is open source, distributed under GNU/LGPL v3 license.</li>
</ul>

<p>Let's see how Revok RPC is simple to use, benefiting of Proxy classes.
Suppose we have an interface with a method we want to expose on server:<br></p>

<pre>public interface <b>ISum</b> {
  public double sum( double a, double b );
}
</pre>

<pre>public class <b>Sum</b> implements <i>ISum</i> {
  public double sum( double a, double b ) {
    return a + b;
  }
}
</pre>

<p>Creating and starting the server:</p>

<pre>public class <b>MyRPC</b> {
  public static void main( String[] args ) {
  
    <i>// Create the object container to hold our exposed object</i>
    ObjectContainer objs = new ObjectContainer();
    
    <i>// Put objects with &lt;namespace&gt;.&lt;name&gt; notation</i>
    objs.put( "calc.Sum", new Sum() );
    
    <i>// Set the network information</i>
    HttpConnector conn = new HttpConnector( "localhost", 10001 );
    
    <i>// Create and start the server itself.</i>
    RevokServer server = new RevokServer( objs, conn );
    server.start();
    
  }
}
</pre>

<p>That is it for the server side. Now, lets ivoke our <code>sum</code> method:</p>

<pre>public class <b>MyInvoker</b> {
  public static void main( String[] args ) {
  
    <i>// Create a RemoteObject instance</i>
    RemoteObject rob = new RemoteObject( 
        new HttpConnector( "localhost", 10001 ) 
    );
    
    <i>// Get a remote ISum instance</i>
    ISum rsum = rob.createRemoteObject( "calc.Sum", ISum.class );
    
    <i>// And finally invoke the method remotly</i>
    double res = rsum.sum( 512.25, 1024.12 );
    
  }
}
</pre>

<p>That is pretty simple to me   ; )<br></p>

<p>Revok also implements a simple server access control, based on namespaces permissions:</p>

<pre><i>// Omitting the repeated parts for brevity,</i>
<i>// the access controll consists on 3 classes:</i>
<i>// Credentials, CredentialsSource and an Authenticator</i>
Credentials crd = new Credentials( "jack", "mypassword".getBytes() );
<i>// Set the namespace access</i>
crd.addAccess( "calc" );

<i>// CredentialsSource interface represents a source of Credentials objects.</i>
<i>// The SingleCredentialsSource is an implementation based</i>
<i>// on a single Credentials object.</i>
CredentialsSource src = new SingleCredentialsSource( crd );

<i>// Last, the Authenticator receives a CredentialsSource to authenticate users.</i>
Authenticator auth = new Authenticator( src );

<i>// And the modified object container, now receives</i>
<i>// the Authenticator as a constructor argument.</i>
ObjectContainer objs = new ObjectContainer( auth );
</pre>

<p>To set up the Credentials on client side:</p>

<pre>RemoteObject rob = ...
rob.setCredentials( 
    new Credentials( "jack", "mypassword".getBytes() ) 
);
</pre>

<p>If you have any suggestions, please let me know: <code>juno &lt;at&gt; pserver &lt;dot&gt; us</code><br><br></p>

<h4><a id="user-content-dependencies" class="anchor" href="#dependencies" aria-hidden="true"><span class="octicon octicon-link"></span></a>Dependencies</h4>

<p>Revok depends on 4 third part libraries:</p>

<ul>
  <li><a href="https://hc.apache.org/httpcomponents-core-ga/">Apache Http Core</a></li>
  <li><a href="https://commons.apache.org/proper/commons-codec/">Apache Commons Codec</a></li>
  <li><a href="https://github.com/jdereg/json-io">jdereg / json-io</a></li>
  <li><a href="http://xstream.codehaus.org/">XStream</a></li>
</ul>

<h4><a id="user-content-logging-notes" class="anchor" href="#logging-notes" aria-hidden="true"><span class="octicon octicon-link"></span></a>Logging notes:</h4>

<p>Revok server uses an own logging library, which by default is setted for standard and error output only (no file logging). This behavior can be changed on some ways:</p>

<ul>
  <li>Completly disabling log: <code>RevokServer.disableLogging()</code></li>
  <li>(re)Enabling log: <code>RevokServer.enableLogging()</code></li>
  <li>Enabling file log (disabled by default): <code>RevokServer.enableFileLogging( String path )</code></li>
  <li>Disabling file log: <code>RevokServer.disableFileLogging()</code></li>
</ul>

<p>Some not critical Exceptions are logged only (not thrown) on server side, like method signature not found, wrong method arguments, authentication exception, etc. On client side, Exceptions are allways throwed and there are no logging. <br>
Other logging custom changes can be done using the very simple log library SLogV2 (soon on github: junorr/slogv2).</p>

