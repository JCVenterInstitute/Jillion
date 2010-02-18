/*
 * Created on Oct 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CommandLineUtils {

    public static CommandLine parseCommandLine(Options options, String[] args) throws ParseException{
        CommandLineParser parser = new GnuParser();
        return parser.parse(options, args);
        
    }
    
    public static Option createHelpOption(){
        return new CommandLineOptionBuilder("h", "show this message")
            .longName("help")
            .build();
    }
}
