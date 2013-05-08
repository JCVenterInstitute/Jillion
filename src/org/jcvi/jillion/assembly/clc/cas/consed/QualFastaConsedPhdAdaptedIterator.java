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
package org.jcvi.jillion.assembly.clc.cas.consed;

import java.io.File;
import java.util.Date;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.FileUtil;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecord;
import org.jcvi.jillion.fasta.qual.QualityFastaDataStore;
import org.jcvi.jillion.fasta.qual.QualityFastaFileDataStoreBuilder;
import org.jcvi.jillion.fasta.qual.QualityFastaRecord;
/**
 * {@code QualFastaConsedPhdAdaptedIterator} is a {@link FastaConsedPhdAdaptedIterator}
 * that will try to find a corresponding qual file
 * and look up the quality scores.
 * @author dkatzel
 *
 */
public class QualFastaConsedPhdAdaptedIterator extends FastaConsedPhdAdaptedIterator{

	private final QualityFastaDataStore qualIter;
	
	public QualFastaConsedPhdAdaptedIterator(
			StreamingIterator<NucleotideFastaRecord> fastaIterator,
			File fastaFile, Date phdDate, PhredQuality defaultQualityValue) {
		super(fastaIterator, fastaFile, phdDate, defaultQualityValue);
		File qualFile = new File(fastaFile.getParentFile(), FileUtil.getBaseName(fastaFile)+".qual");
		if(qualFile.exists()){
			try {
				qualIter = new QualityFastaFileDataStoreBuilder(qualFile)
									.build();
			} catch (Exception e) {
				throw new IllegalStateException("error parsing corresponding qual file : " + qualFile.getAbsolutePath(), e);
			}
		}else{
			qualIter = null;
		}
	}

	@Override
	protected QualitySequence getQualitiesFor(
			NucleotideFastaRecord nextFasta) {
		QualityFastaRecord qualRecord=null;
		
		if(qualIter !=null){
			try {
				qualRecord =qualIter.get(nextFasta.getId());
			} catch (DataStoreException e) {
				throw new IllegalStateException("error getting quality fasta record "+ nextFasta.getId(), e);
			}
		}
		
		if(qualRecord ==null){
			return super.getQualitiesFor(nextFasta);
		}
		return qualRecord.getSequence();
	}

	
}
