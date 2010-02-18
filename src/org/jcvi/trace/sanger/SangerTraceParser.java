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
 * Created on Mar 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.sanger.chromatogram.scf.SCFChromatogram;
import org.jcvi.trace.sanger.chromatogram.scf.SCFCodec;
import org.jcvi.trace.sanger.chromatogram.scf.Version2SCFCodec;
import org.jcvi.trace.sanger.chromatogram.scf.Version3SCFCodec;
import org.jcvi.trace.sanger.chromatogram.ztr.ZTRChromatogramParser;
import org.jcvi.trace.sanger.phd.PhdCodec;

public class SangerTraceParser implements SangerTraceCodec{
    
    private static final int MARK_LIMIT = 1024;
    private static final ZTRChromatogramParser ZTR_PARSER = new ZTRChromatogramParser();
    private static final PhdCodec PHD_CODEC = new PhdCodec();
    private static final SCFCodec SCF_VERSION_2_CODEC = new Version2SCFCodec();
    private static final SCFCodec SCF_VERSION_3_CODEC = new Version3SCFCodec();
    
    private static final List<SangerTraceCodec> decoderOrder = Arrays.asList(
            ZTR_PARSER, SCF_VERSION_3_CODEC, SCF_VERSION_2_CODEC, PHD_CODEC);
    
    private static final SangerTraceParser instance = new SangerTraceParser();
    
    public static SangerTraceParser getInstance(){
        return instance;
    }
    private SangerTraceParser(){}
    
    @Override
    public SangerTrace decode(InputStream in) throws TraceDecoderException {
        BufferedInputStream bufferedIn = new BufferedInputStream(in);
        try{
            for(SangerTraceCodec decoder: decoderOrder){
                bufferedIn.mark(MARK_LIMIT);
                try{
                    return decode(bufferedIn,decoder);
                }
                catch(TraceDecoderException e){
                    bufferedIn.reset();
                }
                
            }
        }catch(IOException ioException){
            ioException.printStackTrace();
            throw new TraceDecoderException("error resetting inputstream", ioException);
        }
        throw new TraceDecoderException("unknown trace format");
    }

    private SangerTrace decode(InputStream in, SangerTraceCodec decoder) throws TraceDecoderException{
        return decoder.decode(in);
    }
    @Override
    public void encode(SangerTrace trace, OutputStream out) throws IOException {
        throw new UnsupportedOperationException("SangerTraceParser can not encode");
        
    }
    
    public static void main(String[] args) throws TraceDecoderException, IOException{
        SangerTraceParser parser = new SangerTraceParser();
       String folder = "/usr/local/projects/GEO/JGI_reads/scf_dir/abi_20090216_GAXP0316_RV_ABI253_094315_0_2305792_1_11376";
       
       String scf = folder+"/GAXP30336_test.g1";
       SCFChromatogram chromo = (SCFChromatogram) parser.decode(new FileInputStream(scf));
       System.out.println(NucleotideGlyph.convertToString(chromo.getBasecalls().decode()));
       System.out.println(chromo.getPeaks().getData().decode());
       System.out.println(chromo.getQualities().decode());
     //  System.out.println(Arrays.toString(chromo.getChannelGroup().getAChannel().getPositions().array()));
    }

}
