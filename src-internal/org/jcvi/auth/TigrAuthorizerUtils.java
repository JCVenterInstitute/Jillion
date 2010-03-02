/*
 * Created on Feb 19, 2010
 *
 * @author dkatzel
 */
package org.jcvi.auth;

import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.jcvi.io.IOUtil;

public final class TigrAuthorizerUtils {
    public static TigrAuthorizer readTigrPasswordFile(File passFile) throws FileNotFoundException{
        return readTigrPasswordFile(new FileInputStream(passFile));
    }
    public static TigrAuthorizer readTigrPasswordFile(String pathToPassFile) throws FileNotFoundException{
        return readTigrPasswordFile(new FileInputStream(pathToPassFile));
    }
    public static TigrAuthorizer readTigrPasswordFile(InputStream passFileInputStream){
        Scanner scanner=null;
        char[] password=null;
        try{
            scanner = new Scanner(passFileInputStream);
            String username =scanner.nextLine();
            String project = scanner.nextLine();
            password = scanner.nextLine().toCharArray();
            String server = scanner.nextLine();
            return new DefaultTigrAuthorizer(
                    new DefaultJCVIAuthorizer(username, password),
                    project,server);
        }
        finally{
            IOUtil.closeAndIgnoreErrors(scanner);
            JCVIAuthorizerUtils.clearPassword(password);
        }
    }
    /**
     * Reads Project DB Login Options created by {@link #addProjectDbLoginOptionsTo(Options, boolean)}
     * @param commandLine the {@link CommandLine} instance to read.
     * @param console the {@link Console} instance to use if password prompting is needed.
     * @return an instance of {@link TigrAuthorizer} using the Project Db login credentials.
     * @throws FileNotFoundException if the specified Password file is not found.
     */
    public static List<TigrAuthorizer> getMultipleProjectDbAuthorizersFrom(
            CommandLine commandLine, final Console console)
            throws FileNotFoundException {
        List<TigrAuthorizer> authorizers = new ArrayList<TigrAuthorizer>();
        if(!commandLine.hasOption("D")){
            throw new IllegalArgumentException("Database(s) must be set!");
        }
        DefaultTigrAuthorizer.Builder masterAuthorizer = new DefaultTigrAuthorizer.Builder();
        if(commandLine.hasOption("p")){
            masterAuthorizer.authorizer(readTigrPasswordFile(commandLine.getOptionValue("p")));
        }
        if(commandLine.hasOption("U")){
            JCVIAuthorizer auth = JCVIAuthorizerUtils.parseAuthorizerFrom(commandLine, console);
            masterAuthorizer.authorizer(auth);
        }
        if(commandLine.hasOption("S")){
            masterAuthorizer.server(commandLine.getOptionValue("S"));
        }
        String dbs = commandLine.getOptionValue("D");
        for(String db : dbs.split(",")){
            DefaultTigrAuthorizer.Builder builder = new DefaultTigrAuthorizer.Builder(masterAuthorizer);
            builder.project(db); 
            authorizers.add(builder.build());
        }
       
        return authorizers;
    }
    /**
     * Reads Project DB Login Options created by {@link #addProjectDbLoginOptionsTo(Options, boolean)}
     * @param commandLine the {@link CommandLine} instance to read.
     * @param console the {@link Console} instance to use if password prompting is needed.
     * @return an instance of {@link TigrAuthorizer} using the Project Db login credentials.
     * @throws FileNotFoundException if the specified Password file is not found.
     */
    public static TigrAuthorizer getProjectDbAuthorizerFrom(
            CommandLine commandLine, final Console console)
            throws FileNotFoundException {
        DefaultTigrAuthorizer.Builder tigrAuthBuilder = new DefaultTigrAuthorizer.Builder();
        if(commandLine.hasOption("p")){
            tigrAuthBuilder.authorizer(readTigrPasswordFile(commandLine.getOptionValue("p")));
        }
        if(commandLine.hasOption("U")){
            JCVIAuthorizer auth = JCVIAuthorizerUtils.parseAuthorizerFrom(commandLine, console);
            tigrAuthBuilder.authorizer(auth);
        }
        if(commandLine.hasOption("S")){
            tigrAuthBuilder.server(commandLine.getOptionValue("S"));
        }
        if(commandLine.hasOption("D")){
            tigrAuthBuilder.project(commandLine.getOptionValue("D"));
        }
   
        TigrAuthorizer authorizer = tigrAuthBuilder.build();
        return authorizer;
    }
}
