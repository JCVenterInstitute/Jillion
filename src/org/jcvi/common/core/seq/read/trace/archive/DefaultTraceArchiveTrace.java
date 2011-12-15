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
import org.jcvi.common.core.seq.fastx.fasta.nuc.NucleotideFastaDataStore;
import org.jcvi.common.core.seq.fastx.fasta.nuc.NucleotideFastaDataStoreBuilderVisitor;
import org.jcvi.common.core.seq.fastx.fasta.pos.DefaultPositionFastaFileDataStore;
import org.jcvi.common.core.seq.fastx.fasta.qual.DefaultQualityFastaFileDataStore;
import org.jcvi.common.core.seq.fastx.fasta.qual.QualityFastaDataStore;
import org.jcvi.common.core.symbol.pos.SangerPeak;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;

public class DefaultTraceArchiveTrace extends AbstractTraceArchiveTrace {
    
    public DefaultTraceArchiveTrace(TraceArchiveRecord record,String rootDirPath){
        super(record, rootDirPath);
    }
    @Override
    public SangerPeak getPeaks() {
        InputStream in=null;
        DefaultPositionFastaFileDataStore datastore =new DefaultPositionFastaFileDataStore();
        try{
            in = getInputStreamFor(TraceInfoField.PEAK_FILE);
            datastore =new DefaultPositionFastaFileDataStore();
            FastaParser.parseFasta(in, datastore);
            return new SangerPeak(datastore.iterator().next().getSequence().asList());
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
        NucleotideFastaDataStore datastore=null;
        NucleotideFastaDataStoreBuilderVisitor visitor= DefaultNucleotideFastaFileDataStore.createBuilder();
        try{
            in = getInputStreamFor(TraceInfoField.BASE_FILE);
            FastaParser.parseFasta(in, visitor);
            datastore = visitor.build();
            return datastore.iterator().next().getSequence();
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
        QualityFastaDataStore datastore =null;
        try{
        	datastore = DefaultQualityFastaFileDataStore.create(getInputStreamFor(TraceInfoField.QUAL_FILE));           
            return datastore.iterator().next().getSequence();
        } catch (IOException e) {
            throw new IllegalArgumentException("quality file not valid",e);
        }
        finally{
            IOUtil.closeAndIgnoreErrors(datastore);
        }
    }
}
