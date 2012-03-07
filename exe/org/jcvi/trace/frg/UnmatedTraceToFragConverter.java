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
 * Created on Nov 19, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.frg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.common.command.CommandLineOptionBuilder;
import org.jcvi.common.command.CommandLineUtils;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.read.trace.TraceDataStore;
import org.jcvi.common.core.seq.read.trace.TraceDecoderException;
import org.jcvi.common.core.seq.read.trace.frg.DefaultFragment;
import org.jcvi.common.core.seq.read.trace.frg.DefaultLibrary;
import org.jcvi.common.core.seq.read.trace.frg.Distance;
import org.jcvi.common.core.seq.read.trace.frg.Fragment;
import org.jcvi.common.core.seq.read.trace.frg.Frg2Writer;
import org.jcvi.common.core.seq.read.trace.frg.Library;
import org.jcvi.common.core.seq.read.trace.frg.MateOrientation;
import org.jcvi.common.core.seq.read.trace.sanger.SangerTrace;
import org.jcvi.common.core.seq.read.trace.sanger.SingleSangerTraceFileDataStore;

public class UnmatedTraceToFragConverter {

    /**
     * @param args
     * @throws DataStoreException 
     * @throws TraceDecoderException 
     * @throws IOException 
     * @throws ExecutionException 
     * @throws InterruptedException 
     */
    public static void main(String[] args) throws DataStoreException, TraceDecoderException, IOException, InterruptedException, ExecutionException {
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
            
            File traceDirectory =new File(commandLine.getOptionValue("dir"));
            OutputStream out = new FileOutputStream(commandLine.getOptionValue("out"));
            int distance = Integer.parseInt(commandLine.getOptionValue("distance"));
            
            TraceDataStore<SangerTrace> traceDataStore = new SingleSangerTraceFileDataStore(traceDirectory);
            Distance libraryDistance = Distance.buildDistance(distance, distance);
            Library library = new DefaultLibrary("0", 
            							libraryDistance, 
            							MateOrientation.UNORIENTED);
    
            Set<Fragment> unmatedFrags = new HashSet<Fragment>();
            Iterator<String> iter =traceDataStore.getIds();
            try{
            	while(iter.hasNext()){
            		String id =iter.next();                    
                    SangerTrace trace = traceDataStore.get(id);
                     DefaultFragment fragment = new DefaultFragment(id, trace, library);
					unmatedFrags.add(fragment); 
            	}
            }catch(DataStoreException e){
                throw new IllegalStateException("error getting traces",e);
            }
            new Frg2Writer().writeFrg2(unmatedFrags, out); 
            IOUtil.closeAndIgnoreErrors(out);
        }
        catch(ParseException e){
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "trace2Frg -dir -out [-distance]", options );
            System.exit(1);
        }
    }

}
