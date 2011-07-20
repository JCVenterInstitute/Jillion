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
 * Created on Jul 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.archive;

import java.io.IOException;
import java.io.InputStream;

import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.fasta.FastaParser;
import org.jcvi.common.core.seq.fastx.fasta.nuc.DefaultNucleotideFastaFileDataStore;
import org.jcvi.common.core.seq.fastx.fasta.pos.DefaultPositionFastaFileDataStore;
import org.jcvi.common.core.seq.fastx.fasta.qual.DefaultQualityFastaFileDataStore;
import org.jcvi.common.core.symbol.pos.Peaks;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;

public class DefaultTraceArchiveTrace extends AbstractTraceArchiveTrace {
    
    public DefaultTraceArchiveTrace(TraceArchiveRecord record,String rootDirPath){
        super(record, rootDirPath);
    }
    @Override
    public Peaks getPeaks() {
        InputStream in=null;
        DefaultPositionFastaFileDataStore datastore =new DefaultPositionFastaFileDataStore();
        try{
            in = getInputStreamFor(TraceInfoField.PEAK_FILE);
            datastore =new DefaultPositionFastaFileDataStore();
            FastaParser.parseFasta(in, datastore);
            return new Peaks(datastore.iterator().next().getValue().decode());
        } catch (IOException e) {
            throw new IllegalArgumentException("peak file not valid",e);
        }
        finally{
            IOUtil.closeAndIgnoreErrors(in);
            IOUtil.closeAndIgnoreErrors(datastore);
        }
    }

    @Override
    public NucleotideSequence getBasecalls() {
        InputStream in=null;
        DefaultNucleotideFastaFileDataStore datastore = new DefaultNucleotideFastaFileDataStore();
        try{
            in = getInputStreamFor(TraceInfoField.BASE_FILE);
            FastaParser.parseFasta(in, datastore);
            return datastore.iterator().next().getValue();
        } catch (IOException e) {
            throw new IllegalArgumentException("basecall file not valid",e);
        }
        finally{
            IOUtil.closeAndIgnoreErrors(in);
            IOUtil.closeAndIgnoreErrors(datastore);
        }
    }

    @Override
    public QualitySequence getQualities() {
        InputStream in=null;
        DefaultQualityFastaFileDataStore datastore = new DefaultQualityFastaFileDataStore();
        try{
            in = getInputStreamFor(TraceInfoField.QUAL_FILE);
            FastaParser.parseFasta(in, datastore);
            return datastore.iterator().next().getValue();
        } catch (IOException e) {
            throw new IllegalArgumentException("quality file not valid",e);
        }
        finally{
            IOUtil.closeAndIgnoreErrors(in);
            IOUtil.closeAndIgnoreErrors(datastore);
        }
    }
}
