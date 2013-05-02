/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Jul 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion_experimental.trace.archive;

import java.io.File;
import java.io.InputStream;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.nt.NucleotideSequenceFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideSequenceFastaFileDataStoreBuilder;
import org.jcvi.jillion.fasta.nt.NucleotideSequenceFastaRecord;
import org.jcvi.jillion.fasta.pos.DefaultPositionFastaFileDataStore;
import org.jcvi.jillion.fasta.pos.PositionSequenceFastaDataStore;
import org.jcvi.jillion.fasta.pos.PositionSequenceFastaRecord;
import org.jcvi.jillion.fasta.qual.QualitySequenceFastaDataStore;
import org.jcvi.jillion.fasta.qual.QualitySequenceFastaFileDataStoreBuilder;
import org.jcvi.jillion.fasta.qual.QualitySequenceFastaRecord;

public class DefaultTraceArchiveTrace extends AbstractTraceArchiveTrace {
    
    public DefaultTraceArchiveTrace(TraceArchiveRecord record,String rootDirPath){
        super(record, rootDirPath);
    }
    
    
    @Override
	public PositionSequence getPositionSequence() {
    	PositionSequenceFastaDataStore datastore = null;
    	InputStream in=null;
        try{
            datastore =DefaultPositionFastaFileDataStore.create(getFile(TraceInfoField.PEAK_FILE));
            StreamingIterator<PositionSequenceFastaRecord> iter=null;
            try{
            	iter = datastore.iterator();
            	return iter.next().getSequence();
            }finally{
            	 IOUtil.closeAndIgnoreErrors(iter);
            }
           
        } catch (Exception e) {
            throw new IllegalArgumentException("peak file not valid",e);
        }
        finally{
            IOUtil.closeAndIgnoreErrors(in,datastore);
        }
	}



    @Override
    public NucleotideSequence getNucleotideSequence() {
    	StreamingIterator<NucleotideSequenceFastaRecord> iterator=null;
    	NucleotideSequenceFastaDataStore datastore=null;
        try{
        	 datastore= new NucleotideSequenceFastaFileDataStoreBuilder(getFile(TraceInfoField.BASE_FILE)).build();
             
            iterator = datastore.iterator();
			return iterator.next().getSequence();
        } catch (Exception e) {
            throw new IllegalArgumentException("basecall file not valid",e);
        }
        finally{
            IOUtil.closeAndIgnoreErrors(iterator,datastore);
        }
    }

    @Override
    public QualitySequence getQualitySequence() {
        QualitySequenceFastaDataStore datastore =null;
        StreamingIterator<QualitySequenceFastaRecord> iterator =null;
        try{
        	File qualFile = getFile(TraceInfoField.QUAL_FILE);
			datastore = new QualitySequenceFastaFileDataStoreBuilder(qualFile).build();         
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
