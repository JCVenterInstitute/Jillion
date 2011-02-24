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
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.jcvi.command.CommandLineOptionBuilder;
import org.jcvi.io.IOUtil;

public final class TigrAuthorizerUtils {
    public static TigrAuthorizer readTigrPasswordFile(File passFile) throws FileNotFoundException{
        return readTigrPasswordFile(new FileInputStream(passFile));
    }
    public static TigrAuthorizer readTigrPasswordFile(String pathToPassFile) throws FileNotFoundException{
        return readTigrPasswordFile(new FileInputStream(pathToPassFile));
    }
    public static boolean hasProjectDbAuthorizer(CommandLine commandLine){
        return commandLine.hasOption("-D") || commandLine.hasOption("-p");
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
    public static TigrAuthorizer getProjectDbAuthorizerFrom(
            CommandLine commandLine) throws FileNotFoundException{
        return getProjectDbAuthorizerFrom(commandLine, System.console());
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
        return getProjectDbAuthorizerFrom(commandLine, console, true);
    }
    
    /**
     * Reads Project DB Login Options created by {@link #addProjectDbLoginOptionsTo(Options, boolean)}
     * except that the -D option is not required.  This is useful if you need
     * to only use the server, and user credentials.
     * @param commandLine the {@link CommandLine} instance to read.
     * @param console the {@link Console} instance to use if password prompting is needed.
     * @return an instance of {@link TigrAuthorizer} using the Project Db login credentials.
     * @throws FileNotFoundException if the specified Password file is not found.
     */
    public static TigrAuthorizer getProjectDbNotRequiredAuthorizerFrom(
            CommandLine commandLine, final Console console)
            throws FileNotFoundException {
        return getProjectDbAuthorizerFrom(commandLine, console, false);
    }
    private static TigrAuthorizer getProjectDbAuthorizerFrom(
            CommandLine commandLine, final Console console, boolean projectRequired)
            throws FileNotFoundException {
        DefaultTigrAuthorizer.Builder tigrAuthBuilder = new DefaultTigrAuthorizer.Builder();
        tigrAuthBuilder.projectRequired(projectRequired);
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
   
    
    /**
     * Add common Project DB login options which allow multiple databases including:
     * <ul>
     * <li> {@code -S} to specify the Project Server</li>
     * <li> {@code -D} to specify which Project database(s) to log into; comma separated if multiple</li>
     * <li> {@code -U} to specify which user name to log in as</li>
     * <li> {@code -P} to specify username's password (not recommended to use)</li>
     * <li> {@code -p} to specify login credentals using a Project DB password file</li>
     * </ul>
     * If a user uses the -U option without providing a password, the console
     * will prompt for a password. (recommended)
     * @param options the {@link Options} instance to add the login options to.
     * @param isDatabaseRequired forces the {@code -D} option to be required.
     */
    public static void addMultipleProjectDbLoginOptionsTo(Options options, boolean isDatabaseRequired) {
        _addProjectDbLoginOptionsTo(options,isDatabaseRequired, "(s) to use comma separated if using more than one");
    }
    
    /**
     * Add common Project DB login options including:
     * <ul>
     * <li> {@code -S} to specify the Project Server</li>
     * <li> {@code -D} to specify which Project database to log into</li>
     * <li> {@code -U} to specify which user name to log in as</li>
     * <li> {@code -P} to specify username's password (not recommended to use)</li>
     * <li> {@code -p} to specify login credentals using a Project DB password file</li>
     * </ul>
     * If a user uses the -U option without providing a password, the console
     * will prompt for a password. (recommended)
     * @param options the {@link Options} instance to add the login options to.
     * @param isDatabaseRequired forces the {@code -D} option to be required.
     */
    public static void addProjectDbLoginOptionsTo(Options options, boolean isDatabaseRequired) {
        _addProjectDbLoginOptionsTo(options,isDatabaseRequired, "");
    }
    
    /**
     * Add common Project DB login options including:
     * <ul>
     * <li> {@code -S} to specify the Project Server</li>
     * <li> {@code -D} to specify which Project database to log into</li>
     * <li> {@code -U} to specify which user name to log in as</li>
     * <li> {@code -P} to specify username's password (not recommended to use)</li>
     * <li> {@code -p} to specify login credentals using a Project DB password file</li>
     * </ul>
     * If a user uses the -U option without providing a password, the console
     * will prompt for a password. (recommended)
     * @param options the {@link Options} instance to add the login options to.
     * @param isDatabaseRequired forces the {@code -D} option to be required.
     */
    private static void _addProjectDbLoginOptionsTo(Options options, boolean isDatabaseRequired, String databaseUsageSuffix) {
        options.addOption(new CommandLineOptionBuilder("S","server","name of server")
                                .longName("Server")
                                .build());
        OptionGroup group = new OptionGroup();
        
        group.addOption(new CommandLineOptionBuilder("D","database","name of project database"+databaseUsageSuffix)                                
                                .build());
       
        group.addOption(new CommandLineOptionBuilder("p","passfile","password file")
                    .longName("passfile")
                            .build());
        group.setRequired(isDatabaseRequired);
        options.addOptionGroup(group);
        options.addOption(new CommandLineOptionBuilder("U","username","name of user")                            
                            .longName("username")                    
                            .build());
        options.addOption(new CommandLineOptionBuilder("P","password","password of user")                            
                            .longName("password")                    
                            .build());
    }
}
