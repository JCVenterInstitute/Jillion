/*
 * Created on Aug 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.auth;

public interface JCVIEncodedAuthorizer extends JCVIAuthorizer{

    String getEncodedAuthorization();
}
