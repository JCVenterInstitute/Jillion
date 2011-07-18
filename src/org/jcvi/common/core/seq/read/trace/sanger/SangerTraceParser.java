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
package org.jcvi.common.core.seq.read.trace.sanger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import org.jcvi.common.core.seq.read.trace.TraceDecoderException;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.SCFCodecs;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ztr.ZTRChromatogramParser;
import org.jcvi.common.core.seq.read.trace.sanger.phd.SinglePhdFile;
import org.jcvi.io.IOUtil;
/**
 * {@code SangerTraceParser} is a SangerTraceCodec singleton
 * that can decode both ZTR and SCF trace files.
 * @author dkatzel
 *
 *
 */
public enum SangerTraceParser implements SangerTraceCodec{
    INSTANCE
    ;
    private static final int MARK_LIMIT = 1024;
    private static final ZTRChromatogramParser ZTR_PARSER = new ZTRChromatogramParser();

    
    private static final List<SangerTraceCodec> DECODER_ORDER = Arrays.asList(
            ZTR_PARSER, SCFCodecs.VERSION_3, SCFCodecs.VERSION_2);

    @Override
    public SangerTrace decode(File traceFile) throws TraceDecoderException, FileNotFoundException{

            for(SangerTraceCodec decoder: DECODER_ORDER){                   
                try{
                    return decoder.decode(traceFile);
                }
                catch(TraceDecoderException e){
                    //try next one...
                }
                
            }
            //try as phd            
            try{
            	return new SinglePhdFile(traceFile);
            }catch(Exception e){}
            throw new TraceDecoderException("unknown trace format");
        
    }
    @Override
    public SangerTrace decode(InputStream in) throws TraceDecoderException {
        BufferedInputStream bufferedIn = new BufferedInputStream(in);
        try{
            for(SangerTraceCodec decoder: DECODER_ORDER){
                bufferedIn.mark(MARK_LIMIT);
                try{
                    return decoder.decode(bufferedIn);
                }
                catch(TraceDecoderException e){
                    bufferedIn.reset();
                }
            }
          //try as phd   
            try{
            	return new SinglePhdFile(bufferedIn);
            }catch(Exception e){
            	e.printStackTrace();
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
