package org.basex.http.restxq;

import static org.basex.query.util.Err.*;
import static org.basex.util.Token.*;

import java.io.*;
import java.util.*;

import org.basex.http.*;
import org.basex.io.*;
import org.basex.query.*;
import org.basex.query.func.*;

/**
 * This class caches information on a single XQuery module with RESTXQ annotations.
 * 
 * @author BaseX Team 2005-12, BSD License
 * @author Christian Gruen
 */
final class RestXqModule {
  /** Supported methods. */
  final ArrayList<RestXqFunction> functions = new ArrayList<RestXqFunction>();
  /** File reference. */
  final IOFile file;
  /** Parsing timestamp. */
  long time;

  /**
   * Constructor.
   * @param in xquery module
   */
  RestXqModule(final IOFile in) {
    file = in;
    time = in.timeStamp();
  }

  /**
   * Checks the module for RESTFful annotations.
   * @param http http context
   * @return {@code true} if module contains relevant annotations
   * @throws QueryException query exception
   */
  boolean analyze(final HTTPContext http) throws QueryException {
    functions.clear();

    // loop through all functions
    final QueryContext qc = parse(http);
    try {
      for(final XQStaticFunction uf : qc.funcs.funcs()) {
        // consider only functions that are defined in this module
        if(!file.name().equals(new IOFile(uf.getInfo().file()).name())) continue;
        final RestXqFunction rxf = new RestXqFunction(uf, qc, this);
        if(rxf.analyze()) functions.add(rxf);
      }
    } finally {
      qc.close();
    }
    return !functions.isEmpty();
  }

  /**
   * Checks if the timestamp is still up-to-date.
   * @return result of check
   */
  boolean uptodate() {
    return time == file.timeStamp();
  }

  /**
   * Updates the timestamp.
   */
  void touch() {
    time = file.timeStamp();
  }

  /**
   * Adds functions that match the current request.
   * @param http http context
   * @param list list of functions
   */
  void add(final HTTPContext http, final ArrayList<RestXqFunction> list) {
    for(final RestXqFunction rxf : functions) {
      if(rxf.matches(http)) list.add(rxf);
    }
  }

  /**
   * Processes the HTTP request.
   * @param http HTTP context
   * @param func function to be processed
   * @throws Exception exception
   */
  void process(final HTTPContext http, final RestXqFunction func) throws Exception {
    // create new XQuery instance
    final QueryContext qc = parse(http);
    try {
      // loop through all functions
      for(final XQStaticFunction uf : qc.funcs.funcs()) {
        // compare input info
        if(func.function.getInfo().equals(uf.getInfo())) {
          // find and evaluate relevant function
          final RestXqFunction rxf = new RestXqFunction(uf, qc, this);
          rxf.analyze();
          new RestXqResponse(rxf, qc, http).create();
          break;
        }
      }
    } finally {
      qc.close();
    }
  }

  // PRIVATE METHODS ====================================================================

  /**
   * Parses the module and returns the query context.
   * @param http http context
   * @return query context
   * @throws QueryException query exception
   */
  private QueryContext parse(final HTTPContext http) throws QueryException {
    final QueryContext qc = new QueryContext(http.context());
    try {
      qc.module(string(file.read()), file.path());
      return qc;
    } catch(final IOException ex) {
      throw IOERR.thrw(null, ex);
    }
  }
}
