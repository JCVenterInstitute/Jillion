/*
 * Created on Sep 8, 2009
 *
 * @author dkatzel
 */
package org.jcvi.auth;

import java.io.Closeable;

public interface JCVIAuthorizer extends Closeable{

    String getUsername();
    char[] getPassword();
    /**
     * Cleans up any resources created.  
     * It is recommended that the password
     * is cleared out for security.
     */
    void close();
    /**
     * Checks to see if this Authorizer is closed.
     * @return {@code true} if closed; {@code false} otherwise.
     */
    boolean isClosed();
}
