/*
 * Created on Aug 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.auth;


import org.jcvi.io.Base64;
/**
 * {@code BasicEncodedJCVIAuthorizer} is a Base 64 Encoded
 * version of JCVIEncodedAuthorizer that is commonly used
 * for some JCVI web authentications.
 * @author dkatzel
 *
 *
 */
public class BasicEncodedJCVIAuthorizer implements JCVIEncodedAuthorizer {

    
    
    private final String encodedAuthorization;
    private final JCVIAuthorizer auth;
    private boolean isClosed = false;
    public BasicEncodedJCVIAuthorizer(String username, char[] password){
          this(new DefaultJCVIAuthorizer(username, password));           
    }
    
    public BasicEncodedJCVIAuthorizer(JCVIAuthorizer authenticator){
       this.auth = authenticator;
       StringBuilder stringToEncode = new StringBuilder(authenticator.getUsername())
       .append(":");
       
        final char[] password = authenticator.getPassword();
        for(int i=0; i< password.length; i++){
            stringToEncode.append(password[i]);
        }
        
        encodedAuthorization ="Basic "+ Base64.encode(stringToEncode.toString().getBytes());  
    }
    @Override
    public synchronized String getEncodedAuthorization() {
        if(isClosed){
            throw new IllegalStateException("Authorizer is closed");
        }
        return encodedAuthorization;
    }

    @Override
    public synchronized void close() {
        auth.close();
        isClosed=true;
    }

    @Override
    public char[] getPassword() {
        return auth.getPassword();
    }

    @Override
    public String getUsername() {
        return auth.getUsername();
    }

    @Override
    public synchronized boolean isClosed() {
        return isClosed;
    }

}
