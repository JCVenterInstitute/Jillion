/*
 * Created on Sep 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.traceFileServer;

import org.jcvi.auth.DefaultJCVIAuthorizer;
import org.jcvi.auth.JCVIAuthorizer;

public class TestTraceFileServerSecure extends AbstractTestActualJcviTraceFileServer{

    @Override
    protected JCVIAuthorizer getAuthorizer() {
        return DefaultJCVIAuthorizer.DEFAULT_USER;
    }

    @Override
    protected String getURL() {
        return "https://tracefileserver-val/TraceFileServer/TraceFileServer-secure";
    }

}
