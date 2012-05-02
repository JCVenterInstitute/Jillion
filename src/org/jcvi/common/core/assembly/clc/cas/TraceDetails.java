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

package org.jcvi.common.core.assembly.clc.cas;

import java.io.File;
import java.util.Date;

import org.jcvi.common.core.seq.fastx.fastq.FastqQualityCodec;
import org.jcvi.common.core.util.DateUtil;

/**
 * @author dkatzel
 *
 *
 */
public final class TraceDetails {
    private final boolean hasFastaEdits;
    private final File chromatDir;
    private final Date phdDate;
    private final FastqQualityCodec fastqQualityCodec;
    
    public static class Builder implements org.jcvi.common.core.util.Builder<TraceDetails>{
        private boolean hasFastaEdits=false;
        private File chromatDir;
        private Date phdDate =null;
        private final FastqQualityCodec fastqQualityCodec;
        public Builder(FastqQualityCodec fastqQualityCodec){
            if(fastqQualityCodec==null){
                throw new NullPointerException("can not be null");
            }
            this.fastqQualityCodec = fastqQualityCodec;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public TraceDetails build() {
            if(phdDate ==null){
                phdDate = DateUtil.getCurrentDate();
            }
            return new TraceDetails(chromatDir, phdDate, fastqQualityCodec, hasFastaEdits);
        }
        public Builder hasEdits(boolean hasEdits){
            this.hasFastaEdits = hasEdits;
            return this;
        }
        public Builder phdDate(Date phdDate){
            this.phdDate = phdDate;
            return this;
        }
        public Builder chromatDir(File chromatDir){
            this.chromatDir = chromatDir;
            return this;
        }
    }
    private TraceDetails(File chromatDir, Date phdDate,
            FastqQualityCodec fastqQualityCodec, boolean hasFastaEdits) {
        this.chromatDir = chromatDir;
        this.phdDate = phdDate;
        this.fastqQualityCodec = fastqQualityCodec;
        this.hasFastaEdits = hasFastaEdits;
    }
    /**
     * @return the hasFastaEdits
     */
    public boolean hasFastaEdits() {
        return hasFastaEdits;
    }
    /**
     * @return the chromatDir
     */
    public File getChromatDir() {
        return chromatDir;
    }
    /**
     * @return the phdDate
     */
    public Date getPhdDate() {
        return phdDate;
    }
    /**
     * @return the fastqQualityCodec
     */
    public FastqQualityCodec getFastqQualityCodec() {
        return fastqQualityCodec;
    }
    
    public boolean hasChromatDir(){
        return chromatDir !=null;
    }

}
