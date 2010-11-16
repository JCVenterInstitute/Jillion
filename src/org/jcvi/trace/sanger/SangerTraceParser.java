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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import org.jcvi.io.IOUtil;
import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.sanger.chromatogram.scf.SCFCodec;
import org.jcvi.trace.sanger.chromatogram.scf.Version2SCFCodec;
import org.jcvi.trace.sanger.chromatogram.scf.Version3SCFCodec;
import org.jcvi.trace.sanger.chromatogram.ztr.ZTRChromatogramParser;
import org.jcvi.trace.sanger.phd.PhdCodec;

public class SangerTraceParser implements SangerTraceCodec{
    
    private static final int MARK_LIMIT = 1024;
    private static final ZTRChromatogramParser ZTR_PARSER = new ZTRChromatogramParser();
    private static final PhdCodec PHD_CODEC = new PhdCodec();
    private static final SCFCodec SCF_VERSION_2_CODEC = Version2SCFCodec.INSTANCE;
    private static final SCFCodec SCF_VERSION_3_CODEC = Version3SCFCodec.INSTANCE;
    
    private static final List<SangerTraceCodec> decoderOrder = Arrays.asList(
            ZTR_PARSER, SCF_VERSION_3_CODEC, SCF_VERSION_2_CODEC, PHD_CODEC);
    
    private static final SangerTraceParser instance = new SangerTraceParser();
    
    public static SangerTraceParser getInstance(){
        return instance;
    }
    private SangerTraceParser(){}
    @Override
    public SangerTrace decode(File traceFile) throws TraceDecoderException, FileNotFoundException{

            for(SangerTraceCodec decoder: decoderOrder){                   
                try{
                    return decoder.decode(traceFile);
                }
                catch(TraceDecoderException e){
                    //try next one...
                }
                
            }
            throw new TraceDecoderException("unknown trace format");
        
    }
    @Override
    public SangerTrace decode(InputStream in) throws TraceDecoderException {
        BufferedInputStream bufferedIn = new BufferedInputStream(in);
        try{
            for(SangerTraceCodec decoder: decoderOrder){
                bufferedIn.mark(MARK_LIMIT);
                try{
                    return decoder.decode(bufferedIn);
                }
                catch(TraceDecoderException e){
                    bufferedIn.reset();
                }
                
            }
        }catch(IOException ioException){
            ioException.printStackTrace();
            throw new TraceDecoderException("error resetting inputstream", ioException);
        }finally{
            IOUtil.closeAndIgnoreErrors(bufferedIn);
        }
        throw new TraceDecoderException("unknown trace format");
    }
    @Override
    public void encode(SangerTrace trace, OutputStream out) throws IOException {
        throw new UnsupportedOperationException("SangerTraceParser can not encode");
        
    }


}
