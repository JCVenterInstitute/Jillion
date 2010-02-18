/*
 * Created on Oct 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.auth;

public interface TigrAuthorizer extends JCVIAuthorizer{

    String getServer();
    String getProject();
}
