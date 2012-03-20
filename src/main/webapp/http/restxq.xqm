(:~
 : This module contains some basic examples for RESTXQ annotations
 : @author BaseX Team
 :)
module namespace page = 'http://basex.org/modules/web-page';

declare namespace rest = 'http://exquery.org/ns/restxq';

declare variable $page:style :=
  <style type="text/css"><!--
  body { font-family:Ubuntu,'Trebuchet MS',SansSerif; }
  h2 { color:#0040A0; }
  hr { height:1px; border:0px; background-color:black; }
  a { color:#0040A0; text-decoration: none; }
  --></style>;

declare %rest:path("hello/{$world}")
        %rest:header-param("User-Agent", "{$agent}")
        function page:hello($world as xs:string, $agent as xs:string) {
  <response>
    <title>Hello { $world }!</title>
    <info>You requested this page with { $agent }.</info>
  </response>
};

declare %rest:path("")
        %output:method("xhtml")
        %output:doctype-public("-//W3C//DTD HTML 4.01//EN")
        %output:doctype-system("http://www.w3.org/TR/html4/strict.dtd")
  function page:start() {

  let $title := 'Welcome to RESTXQ!' return
  <html xmlns="http://www.w3.org/1999/xhtml">
    <head>
      <title>{ $title }</title>
      { $page:style }
    </head>
    <body>
      <h2>{ $title }</h2>
      <p><a href="http://docs.basex.org/wiki/RESTXQ">RESTXQ</a> is a new API
      that facilitates the use of XQuery as a Server Side processing language
      for the Web.<br/>
      This page has been generated by a RESTXQ module located in the web
      server's root directory.</p>
      <p>The following links return a page with dynamic contents:</p>
      <ul>
        <li><a href="/restxq/hello/World">http://localhost:8984/restxq/hello/World</a></li>
        <li><a href="/restxq/hello/Universe">http://localhost:8984/restxq/hello/Universe</a></li>
      </ul>
      <p>The source of this file is shown below:</p>
      <hr/>
      <pre>{ unparsed-text(static-base-uri()) }</pre>
      <hr/>
      <p style='text-align:right;'><a href='..'>...back to main page</a></p>
    </body>
  </html>
};