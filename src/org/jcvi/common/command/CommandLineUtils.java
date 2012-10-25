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
 * Created on Oct 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.command;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.datastore.DataStoreFilters;
import org.jcvi.common.io.idReader.DefaultFileIdReader;
import org.jcvi.common.io.idReader.IdReader;
import org.jcvi.common.io.idReader.IdReaderException;
import org.jcvi.common.io.idReader.StringIdParser;
/**
 * Utility class for commandline parsing.
 * @author dkatzel
 *
 *
 */
public final class CommandLineUtils {
	/**
	 * {@value}.
	 */
	private static final String INCLUDE_FLAG = "i";
	/**
	 * {@value}.
	 */
	private static final String EXCLUDE_FLAG = "e";
	private CommandLineUtils(){
		//private constructor
	}
    /**
     * Parse the a command line using the given options and the given
     * arguments.
     * @param options the options to use during parsing.
     * @param args the arguments to parse.
     * @return a new CommandLine object (not null).
     * @throws ParseException if the argument to parse are not valid for
     * the given Options.
     */
    public static CommandLine parseCommandLine(Options options, String[] args) throws ParseException{
        CommandLineParser parser = new GnuParser();
        //flag says stop parsing if non-option is encountered
        return parser.parse(options, args);
        
    }
    public static void addIncludeAndExcludeDataStoreFilterOptionsTo(Options options){
        options.addOption(new CommandLineOptionBuilder(INCLUDE_FLAG, "optional file of contig ids to include")
            .build());
        options.addOption(new CommandLineOptionBuilder(EXCLUDE_FLAG, "optional file of contig ids to exclude")
            .build());

    }
    public static DataStoreFilter createDataStoreFilter(
            CommandLine commandLine) throws IdReaderException {
        final DataStoreFilter filter;
        if(commandLine.hasOption(INCLUDE_FLAG)){
            Set<String> includeList=parseIdsFrom(new File(commandLine.getOptionValue(INCLUDE_FLAG)));
            if(commandLine.hasOption(EXCLUDE_FLAG)){
                Set<String> excludeList=parseIdsFrom(new File(commandLine.getOptionValue(EXCLUDE_FLAG)));
                includeList.removeAll(excludeList);
            }
            filter = DataStoreFilters.newIncludeFilter(includeList);
            
        }else if(commandLine.hasOption(EXCLUDE_FLAG)){
            filter = DataStoreFilters.newExcludeFilter(parseIdsFrom(new File(commandLine.getOptionValue(EXCLUDE_FLAG))));
        }else{
            filter = DataStoreFilters.alwaysAccept();
        }
        return filter;
    }
    private static Set<String> parseIdsFrom(final File idFile)   throws IdReaderException {
        IdReader<String> idReader = new DefaultFileIdReader<String>(idFile,new StringIdParser());
        Set<String> ids = new HashSet<String>();
        Iterator<String> iter =idReader.getIds();
        while(iter.hasNext()){
            ids.add(iter.next());
        }
        return ids;
    }
    /**
     * Create a new {@link Option}
     * that will "show this message" if {@code -h} or {@code --h}
     * or {@code -help} or {@code --help} are provided 
     * to the CommandLine.
     * @return a new Option to show help.
     * @see #helpRequested(String[])
     */
    public static Option createHelpOption(){
        return new CommandLineOptionBuilder("h", "show this message")
            .longName("help")
            .build();
    }
    /**
     * Does the given argument list ask for help.  This
     * check should be done before the commandline
     * is parsed since  not all of the required
     * parameters may exist.
     * @param args the command line arguments to inspect.
     * @return {@true} if {@code -h} or {@code --h}
     * or {@code -help} or {@code --help} are anywhere
     * in the given argument list.
     */
    public static boolean helpRequested(String[] args){
        for(int i=0; i< args.length; i++){
            String arg = args[i];
            if("-h".equals(arg) || "--h".equals(arg) 
            		||   "-help".equals(arg) || "--help".equals(arg)){
                return true;
            }
        }
        return false;
    }
   
}
