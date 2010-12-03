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
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jcvi.Range;
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.assembly.trim.DefaultPrimerTrimmer;
import org.jcvi.assembly.trim.PrimerTrimmer;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.SimpleDataStore;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.nuc.NucleotideDataStore;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.nuc.datastore.NucleotideDataStoreAdapter;
import org.jcvi.trace.fourFiveFour.flowgram.sff.AbstractSffFileVisitor;
import org.jcvi.trace.fourFiveFour.flowgram.sff.DefaultSFFCommonHeaderCodec;
import org.jcvi.trace.fourFiveFour.flowgram.sff.DefaultSFFReadDataCodec;
import org.jcvi.trace.fourFiveFour.flowgram.sff.LargeSffFileDataStore;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SFFCommonHeader;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SFFDecoderException;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SFFFlowgram;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SFFReadData;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SFFReadHeader;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SffDataStore;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SffFileVisitor;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SffInfoDataStore;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SffParser;

/**
 * @author dkatzel
 *
 *
 */
public class PrototypeTrimNexteraSff {

    /**
     * @param args
     * @throws DataStoreException 
     * @throws FileNotFoundException 
     * @throws SFFDecoderException 
     */
    public static void main(String[] args) throws DataStoreException, SFFDecoderException, FileNotFoundException {
        File sffFile = new File(
                "/usr/local/seq454/2010_10_19/R_2010_10_19_10_35_13_FLX02080319_Administrator_10"+
                "1910R1BBAY62R2FIBRBAC/D_2010_10_19_22_43_31_dell-2-0-"+
                "1_signalProcessing/sff/GPQK0ZM02.sff");
        
        
        PrimerTrimmer nexteraTransposonTrimmer = new DefaultPrimerTrimmer(13, .9f,false);
        Map<String, NucleotideEncodedGlyphs> forwardTransposon = new HashMap<String, NucleotideEncodedGlyphs>();
        forwardTransposon.put("5'", TransposonEndSequences.FORWARD);
        Map<String, NucleotideEncodedGlyphs> revesrseTransposon = new HashMap<String, NucleotideEncodedGlyphs>();
        
        revesrseTransposon.put("3'", TransposonEndSequences.REVERSE);
        
        NucleotideDataStore forwardTransposonDataStore = new NucleotideDataStoreAdapter(new SimpleDataStore<NucleotideEncodedGlyphs>(forwardTransposon));
        
        NucleotideDataStore reverseTransposonDataStore = new NucleotideDataStoreAdapter(new SimpleDataStore<NucleotideEncodedGlyphs>(revesrseTransposon));
        
        System.out.println(sffFile.getAbsolutePath());
        DefaultSFFCommonHeaderCodec headerCodec = new DefaultSFFCommonHeaderCodec();
        DefaultSFFReadDataCodec readCodec = new DefaultSFFReadDataCodec();
        
        SffFileVisitor visitor = new AbstractSffFileVisitor() {

            @Override
            public boolean visitCommonHeader(SFFCommonHeader commonHeader) {
                // TODO Auto-generated method stub
                return super.visitCommonHeader(commonHeader);
            }

            @Override
            public boolean visitReadData(SFFReadData readData) {
                // TODO Auto-generated method stub
                return super.visitReadData(readData);
            }

            @Override
            public boolean visitReadHeader(SFFReadHeader readHeader) {
                // TODO Auto-generated method stub
                return super.visitReadHeader(readHeader);
            }

            @Override
            public void visitEndOfFile() {
                // TODO Auto-generated method stub
                super.visitEndOfFile();
            }
            
        };
        SffDataStore dataStore = new SffInfoDataStore(sffFile);
        final Iterator<String> ids = dataStore.getIds();
        
        for(int i=0; i<10; i++){
            String id =ids.next();
            trim(nexteraTransposonTrimmer, forwardTransposonDataStore,reverseTransposonDataStore, dataStore, id);
        }
        
       // LargeSffFileDataStore dataStore = new LargeSffFileDataStore(sffFile,RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE);
       
        //System.out.println(dataStore.size());

    }

    protected static void trim(PrimerTrimmer nexteraTransposonTrimmer,
            NucleotideDataStore forwardTransposonDataStore,
            NucleotideDataStore reverseTransposonDataStore, 
            SffDataStore dataStore,
            String id) throws DataStoreException {
        SFFFlowgram flowgram =dataStore.get(id);
        System.out.println("readId ="+id);
        System.out.println("adapter clip =" +flowgram.getAdapterClip());
        System.out.println("qual clip =" +flowgram.getQualitiesClip());
        System.out.println("full sequence (including key seq):");
        System.out.println(NucleotideGlyph.convertToString(flowgram.getBasecalls().decode()));
        Range forwardClearRange =nexteraTransposonTrimmer.trim(flowgram.getBasecalls(), forwardTransposonDataStore);
        
        Range reverseClearRange =nexteraTransposonTrimmer.trim(flowgram.getBasecalls(), reverseTransposonDataStore);
        final Range clearRange;
        if(reverseClearRange.isSubRangeOf(forwardClearRange)){
            clearRange = Range.buildRange(CoordinateSystem.RESIDUE_BASED, 
                    forwardClearRange.getLocalStart(), reverseClearRange.getLocalEnd());
        }else{
            clearRange = forwardClearRange.intersection(reverseClearRange);
        }
        System.out.println("forward transposon clip =" +forwardClearRange);
        System.out.println("reverse transposon clip =" +reverseClearRange);
        
        System.out.println("nextera clip points =" + clearRange);
        System.out.println("left trimmed off= "+NucleotideGlyph.convertToString(flowgram.getBasecalls().decode(Range.buildRangeOfLength(0, clearRange.getStart()))));
        
        System.out.println("trimmed seq = "+NucleotideGlyph.convertToString(flowgram.getBasecalls().decode(clearRange)));
        
        System.out.println("right trimmed off= "+NucleotideGlyph.convertToString(flowgram.getBasecalls().decode(Range.buildRange(clearRange.getEnd()+1, flowgram.getBasecalls().getLength()-1))));
        
        System.out.println("intersection with quality clip= "+NucleotideGlyph.convertToString(flowgram.getBasecalls().decode(clearRange.intersection(flowgram.getQualitiesClip()))));
    
        System.out.println("====================\n\n");
        
    }

}
