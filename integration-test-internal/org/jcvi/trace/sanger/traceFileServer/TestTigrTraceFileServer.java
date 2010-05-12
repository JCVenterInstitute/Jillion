/*
 * Created on Sep 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.traceFileServer;

import org.jcvi.auth.DefaultJCVIAuthorizer;
import org.jcvi.auth.JCVIAuthorizer;

public class TestTigrTraceFileServer extends AbstractTestActualJcviTraceFileServer{

    @Override
    protected JCVIAuthorizer getAuthorizer() {
        return new DefaultJCVIAuthorizer("access", "access".toCharArray());
    }

    @Override
    protected String getURL() {
        return "https://tracefileserver-val/TraceFileServer-tigr/TraceFileServer";
    }

}
