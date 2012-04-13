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

import java.io.InputStream;

import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.fasta.FastaParser;
import org.jcvi.common.core.seq.fastx.fasta.nt.DefaultNucleotideSequenceFastaFileDataStore;
import org.jcvi.common.core.seq.fastx.fasta.nt.NucleotideFastaDataStoreBuilderVisitor;
import org.jcvi.common.core.seq.fastx.fasta.nt.NucleotideSequenceFastaDataStore;
import org.jcvi.common.core.seq.fastx.fasta.nt.NucleotideSequenceFastaRecord;
import org.jcvi.common.core.seq.fastx.fasta.pos.DefaultPositionFastaFileDataStore;
import org.jcvi.common.core.seq.fastx.fasta.pos.PositionFastaDataStore;
import org.jcvi.common.core.seq.fastx.fasta.pos.PositionSequenceFastaRecord;
import org.jcvi.common.core.seq.fastx.fasta.qual.DefaultQualityFastaFileDataStore;
import org.jcvi.common.core.seq.fastx.fasta.qual.QualitySequenceFastaDataStore;
import org.jcvi.common.core.seq.fastx.fasta.qual.QualitySequenceFastaRecord;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.ShortSymbol;
import org.jcvi.common.core.symbol.pos.SangerPeak;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.util.iter.CloseableIterator;

public class DefaultTraceArchiveTrace extends AbstractTraceArchiveTrace {
    
    public DefaultTraceArchiveTrace(TraceArchiveRecord record,String rootDirPath){
        super(record, rootDirPath);
    }
    @Override
    public SangerPeak getPeaks() {      
    	PositionFastaDataStore datastore = null;
    	CloseableIterator<PositionSequenceFastaRecord<Sequence<ShortSymbol>>> iterator =null;
        try{
            datastore =DefaultPositionFastaFileDataStore.create(getInputStreamFor(TraceInfoField.PEAK_FILE));
            iterator = datastore.iterator();
			return new SangerPeak(iterator.next().getSequence().asList());
        } catch (Exception e) {
            throw new IllegalArgumentException("peak file not valid",e);
        }
        finally{
            IOUtil.closeAndIgnoreErrors(iterator,datastore);
        }
    }

    @Override
    public NucleotideSequence getBasecalls() {
        InputStream in=null;
        NucleotideSequenceFastaDataStore datastore=null;
        NucleotideFastaDataStoreBuilderVisitor visitor= DefaultNucleotideSequenceFastaFileDataStore.createBuilder();
        CloseableIterator<NucleotideSequenceFastaRecord> iterator=null;
        try{
            in = getInputStreamFor(TraceInfoField.BASE_FILE);
            FastaParser.parseFasta(in, visitor);
            datastore = visitor.build();
            iterator = datastore.iterator();
			return iterator.next().getSequence();
        } catch (Exception e) {
            throw new IllegalArgumentException("basecall file not valid",e);
        }
        finally{
            IOUtil.closeAndIgnoreErrors(in, iterator,datastore);
        }
    }

    @Override
    public QualitySequence getQualities() {
        QualitySequenceFastaDataStore datastore =null;
        CloseableIterator<QualitySequenceFastaRecord> iterator =null;
        try{
        	datastore = DefaultQualityFastaFileDataStore.create(getInputStreamFor(TraceInfoField.QUAL_FILE));           
            iterator = datastore.iterator();
			return iterator.next().getSequence();
        } catch (Exception e) {
            throw new IllegalArgumentException("quality file not valid",e);
        }
        finally{
            IOUtil.closeAndIgnoreErrors(iterator, datastore);
        }
    }
}
