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
 * Created on Sep 8, 2009
 *
 * @author dkatzel
 */
package org.jcvi.auth;

import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.jcvi.cli.CommandLineOptionBuilder;
import org.jcvi.io.IOUtil;

public final class JCVIAuthorizerUtils {

   
    public static JCVIAuthorizer readPasswordFile(File passFile) throws FileNotFoundException{
        return readPasswordFile(new FileInputStream(passFile));
    }
    public static JCVIAuthorizer readPasswordFile(String pathToPassFile) throws FileNotFoundException{
        return readPasswordFile(new FileInputStream(pathToPassFile));
    }
    
    public static JCVIAuthorizer readPasswordFile(InputStream passFileInputStream){
        Scanner scanner=null;
        char[] password=null;
        try{
            scanner = new Scanner(passFileInputStream);
            String username =scanner.nextLine();            
            password = scanner.nextLine().toCharArray();
            return new DefaultJCVIAuthorizer(username, password);
        }
        finally{
            IOUtil.closeAndIgnoreErrors(scanner);
            clearPassword(password);
        }
    }
    static  void clearPassword(char[] password){
        Arrays.fill(password, ' ');
    }
    public static JCVIAuthorizer promptPassword(Console console, String username){
        char[] password = console.readPassword("%s's password: ", username);
        try{
            return new DefaultJCVIAuthorizer(username, password);
        }
        finally{
            clearPassword(password);
        }
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
        options.addOption(new CommandLineOptionBuilder("S","server","name of server")
                                .longName("Server")
                                .build());
        options.addOption(new CommandLineOptionBuilder("D","database","name of project database(s) to use comma separated if using more than one")
                                .isRequired(isDatabaseRequired)
                                .build());
       
        options.addOption(new CommandLineOptionBuilder("p","passfile","password file")
                    .longName("passfile")
                            .build());
        options.addOption(new CommandLineOptionBuilder("U","username","name of user")                            
                            .longName("username")                    
                            .build());
        options.addOption(new CommandLineOptionBuilder("P","password","password of user")                            
                            .longName("password")                    
                            .build());
    }
    
    /**
     * Parse a username and password from the commandline using the 
     * -U option for username and either -P for password or prompt
     * the console for user input.
     * @param commandLine the {@link CommandLine} instance to read.
     * @param console the {@link Console} instance to use if password prompting is needed.
     * @return an instance of {@link JCVIAuthorizer} using the login credentials.
     * @throws FileNotFoundException if the specified Password file is not found.
     */
    public static JCVIAuthorizer parseAuthorizerFrom(CommandLine commandLine,
            final Console console) {
        String username = commandLine.getOptionValue("U");
        JCVIAuthorizer auth;
        if(commandLine.hasOption("P")){
            auth = new DefaultJCVIAuthorizer(username, commandLine.getOptionValue("P").toCharArray());
        }
        else{
            auth = JCVIAuthorizerUtils.promptPassword(console, username);
        }
        return auth;
    }
    
}
