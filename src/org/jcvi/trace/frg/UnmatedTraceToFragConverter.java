/*
 * Created on Nov 19, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.frg;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.Distance;
import org.jcvi.Range;
import org.jcvi.cli.CommandLineOptionBuilder;
import org.jcvi.cli.CommandLineUtils;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.io.IOUtil;
import org.jcvi.io.fileServer.DirectoryFileServer;
import org.jcvi.sequence.DefaultLibrary;
import org.jcvi.sequence.Library;
import org.jcvi.sequence.MateOrientation;
import org.jcvi.trace.TraceDataStore;
import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.sanger.SangerTrace;
import org.jcvi.trace.sanger.SangerTraceParser;
import org.jcvi.trace.sanger.TraceFileServerDataStore;

public class UnmatedTraceToFragConverter {

    /**
     * @param args
     * @throws DataStoreException 
     * @throws TraceDecoderException 
     * @throws IOException 
     */
    public static void main(String[] args) throws DataStoreException, TraceDecoderException, IOException {
        Options options = new Options();
        
        options.addOption(new CommandLineOptionBuilder("dir", "trace directory as input")
                        .isRequired(true)
                        .build());
        options.addOption(new CommandLineOptionBuilder("out", "path to output File")
                        .isRequired(true)
                        .build());
        options.addOption(new CommandLineOptionBuilder("distance", "mean distance for library")
                                        .build());
        try{
            CommandLine commandLine = CommandLineUtils.parseCommandLine(options, args);
            
            String traceDirectory =commandLine.getOptionValue("dir");
            OutputStream out = new FileOutputStream(commandLine.getOptionValue("out"));
            int distance = Integer.parseInt(commandLine.getOptionValue("distance"));
            
            TraceDataStore<SangerTrace> traceDataStore = 
                new TraceFileServerDataStore<SangerTrace>(DirectoryFileServer.createReadOnlyDirectoryFileServer(traceDirectory),SangerTraceParser.getInstance());
            
                
            
            
            Distance libraryDistance = Distance.buildDistance(distance, distance);
            Integer idCounter=0;
            Library library = new DefaultLibrary(idCounter.toString(), libraryDistance, MateOrientation.UNORIENTED);
    
            Map<String, Fragment> map = new HashMap<String, Fragment>();
            Iterator<String> iter =traceDataStore.getIds();
            while(iter.hasNext()){
                String id =iter.next();
                    try{
                    SangerTrace trace = traceDataStore.get(id);
                    Range range = Range.buildRangeOfLength(0, trace.getBasecalls().getLength());
                    map.put(id,new DefaultFragment(id, trace, 
                            range, range, library, null));   
                }catch(DataStoreException e){
                    e.printStackTrace();
                }
            }
            new Frg2Writer().writeFrg2(Collections.EMPTY_LIST, 
                    new ArrayList<Fragment>(map.values()), out);
            
            
            IOUtil.closeAndIgnoreErrors(out);
        }
        catch(ParseException e){
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "trace2Frg -dir -out [-distance]", options );
            System.exit(1);
        }
    }

}
