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

package org.jcvi.trace.nextera;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.jcvi.Range;
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.assembly.trim.DefaultPrimerTrimmer;
import org.jcvi.assembly.trim.PrimerTrimmer;
import org.jcvi.datastore.SimpleDataStore;
import org.jcvi.glyph.nuc.NucleotideDataStore;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.datastore.NucleotideDataStoreAdapter;
import org.jcvi.io.IOUtil;
import org.jcvi.trace.fourFiveFour.flowgram.sff.AbstractSffFileVisitor;
import org.jcvi.trace.fourFiveFour.flowgram.sff.DefaultSFFCommonHeader;
import org.jcvi.trace.fourFiveFour.flowgram.sff.DefaultSFFReadHeader;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SFFCommonHeader;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SFFReadData;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SFFReadHeader;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SffParser;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SffWriter;

/**
 * @author dkatzel
 *
 *
 */
public class TrimNexteraSff {

    
    private static final PrimerTrimmer NEXTERA_TRIMMER = new DefaultPrimerTrimmer(13, .9f,false);
    
    private static final NucleotideDataStore FORWARD_DATASTORE ;
    
    private static final NucleotideDataStore REVERSE_DATASTORE;
    static{
        Map<String, NucleotideEncodedGlyphs> forwardTransposon = new HashMap<String, NucleotideEncodedGlyphs>();
        
        Map<String, NucleotideEncodedGlyphs> revesrseTransposon = new HashMap<String, NucleotideEncodedGlyphs>();
       
        forwardTransposon.put("5'", TransposonEndSequences.FORWARD);
        revesrseTransposon.put("3'", TransposonEndSequences.REVERSE);     
        FORWARD_DATASTORE = new NucleotideDataStoreAdapter(new SimpleDataStore<NucleotideEncodedGlyphs>(forwardTransposon));
        
       REVERSE_DATASTORE = new NucleotideDataStoreAdapter(new SimpleDataStore<NucleotideEncodedGlyphs>(revesrseTransposon));
        
    }
    
    
    private DefaultSFFCommonHeader.Builder headerBuilder;
    private long numberOfTrimmedReads=0;
    private final OutputStream tempOut;
    private final File untrimmedSffFile;
    private final File tempReadDataFile;
    public TrimNexteraSff(File untrimmedSffFile) throws IOException{
        this(untrimmedSffFile,  File.createTempFile("nexteraTrimmed", "reads.sff"));
    }
    public TrimNexteraSff(File untrimmedSffFile, File tempReadDataFile) throws IOException{
        tempOut = new FileOutputStream(tempReadDataFile);
        this.untrimmedSffFile = untrimmedSffFile;
        this.tempReadDataFile = tempReadDataFile;
    }
    
    public void trimAndWriteNewSff(OutputStream out) throws IOException{
       
        TrimParser trimmer = new TrimParser();
        SffParser.parseSFF(untrimmedSffFile, trimmer);
        tempOut.close();
        headerBuilder.withNoIndex()
                    .numberOfReads(numberOfTrimmedReads);
        
        SffWriter.writeCommonHeader(headerBuilder.build(), out);
        
        InputStream in = null;
        try{
            in= new FileInputStream(tempReadDataFile);
            IOUtils.copyLarge(in, out);
        }finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
    }
    
    private class TrimParser extends AbstractSffFileVisitor{
        private SFFReadHeader currentReadHeader;
        @Override
        public boolean visitCommonHeader(SFFCommonHeader commonHeader) {
            headerBuilder = new DefaultSFFCommonHeader.Builder(commonHeader);
            return true;
        }

        @Override
        public boolean visitReadData(SFFReadData readData) {
            Range forwardClearRange =NEXTERA_TRIMMER.trim(readData.getBasecalls(), FORWARD_DATASTORE);
            
            Range reverseClearRange =NEXTERA_TRIMMER.trim(readData.getBasecalls(), REVERSE_DATASTORE);
            final Range clearRange;
            if(reverseClearRange.isSubRangeOf(forwardClearRange)){
                clearRange = Range.buildRange(CoordinateSystem.RESIDUE_BASED, 
                        forwardClearRange.getLocalStart(), reverseClearRange.getLocalEnd());
            }else{
                clearRange = forwardClearRange.intersection(reverseClearRange);
            }
            //throw out any reads without any adapter in the front
            if(forwardClearRange.getStart() !=0){
                numberOfTrimmedReads++;
                try {
                    DefaultSFFReadHeader.Builder builder = new DefaultSFFReadHeader.Builder(currentReadHeader);
                    builder.qualityClip(clearRange);
                    SffWriter.writeReadHeader(builder.build(), tempOut);
                    SffWriter.writeReadData(readData, tempOut);
                } catch (IOException e) {
                    throw new IllegalStateException("error writing read data to temp", e);
                }
                
               
            }else{
                System.out.println("skipping "+ currentReadHeader.getName());
            }
            return true;
        }

        @Override
        public boolean visitReadHeader(SFFReadHeader readHeader) {
            currentReadHeader = readHeader;
            return true;
            
        }
        
    }
  
}
