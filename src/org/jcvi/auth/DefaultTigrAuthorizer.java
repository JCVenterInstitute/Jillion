/*
 * Created on Oct 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.auth;

public class DefaultTigrAuthorizer implements TigrAuthorizer {
    public final static String DEFAULT_TIGR_SERVER = "SYBTIGR";
    private final JCVIAuthorizer authorizer;
    private final String project,  server;
    
    public DefaultTigrAuthorizer(JCVIAuthorizer authorizer, String project, String server){
        this.authorizer = authorizer;
        this.project = project;
        this.server = server;
    }
    @Override
    public String getProject() {
        return project;
    }

    @Override
    public String getServer() {
        return server;
    }

    @Override
    public void close() {
        authorizer.close();

    }
    
    @Override
    public boolean isClosed() {
        return authorizer.isClosed();
    }
    
    @Override
    public char[] getPassword() {
        return authorizer.getPassword();
    }

    @Override
    public String getUsername() {
        return authorizer.getUsername();
    }
    
    public static final  class Builder implements org.jcvi.Builder<DefaultTigrAuthorizer>{
        private JCVIAuthorizer authorizer = DefaultJCVIAuthorizer.DEFAULT_TIGR_USER ;
        private String project;
        private String server = DEFAULT_TIGR_SERVER;
        
        public Builder(){}
        public Builder(Builder copy){
            server(copy.server);
            authorizer(copy.authorizer);
            if(copy.project !=null){
                project(copy.project);
            }
        }
        public Builder(String project){
            project(project);
        }
        public Builder(JCVIAuthorizer authorizer){
            authorizer(authorizer);
        }
        public Builder(TigrAuthorizer authorizer){
            authorizer(authorizer);
        }
        public Builder project(String project){
            if(project == null){
                throw new NullPointerException("project can not be null");
            }
            this.project = project;
            return this;
        }
        public Builder server(String server){
            if(server == null){
                throw new NullPointerException("server can not be null");
            }
            this.server = server;
            return this;
        }
        public Builder authorizer(JCVIAuthorizer authorizer){
            if(authorizer == null){
                throw new NullPointerException("authorizer can not be null");
            }
            this.authorizer = authorizer;
            return this;
        }
        public Builder authorizer(TigrAuthorizer authorizer){
            authorizer((JCVIAuthorizer)authorizer);
            project(authorizer.getProject());
            server(authorizer.getServer());
            return this;
        }
        @Override
        public DefaultTigrAuthorizer build() {
            if(project ==null){
                throw new NullPointerException("project must be set");
            }
            return new DefaultTigrAuthorizer(authorizer, project, server);
        }
        
    }

    

}
