/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
    public TigrAuthorizer switchProject(String newProject) {
        return new DefaultTigrAuthorizer(authorizer, newProject, server);
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
    
    public static final  class Builder implements org.jcvi.common.core.util.Builder<DefaultTigrAuthorizer>{
        private JCVIAuthorizer authorizer = DefaultJCVIAuthorizer.DEFAULT_TIGR_USER ;
        private String project=null;
        private String server = DEFAULT_TIGR_SERVER;
        private boolean projectRequired=true;
        
        public Builder(){}
        public Builder(Builder copy){
            server(copy.server);
            authorizer(copy.authorizer);
            if(copy.project !=null){
                project(copy.project);
            }
        }
        
        public Builder projectRequired(boolean projectRequired){
            this.projectRequired = projectRequired;
            return this;
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
            if(projectRequired && project == null){
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
            if(projectRequired && project ==null){
                throw new NullPointerException("project must be set");
            }
            return new DefaultTigrAuthorizer(authorizer, project, server);
        }
        
    }

    

}
