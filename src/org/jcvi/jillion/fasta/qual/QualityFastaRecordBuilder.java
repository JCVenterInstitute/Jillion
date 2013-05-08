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
package org.jcvi.jillion.fasta.qual;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.internal.fasta.AbstractFastaRecordBuilder;
/**
 * {@code QualityFastaRecordBuilder} is a factory class
 * that makes instances of {@link QualityFastaRecord}s.
 * Depending on the different parameters, the factory might
 * choose to return different implementations.
 * @author dkatzel
 *
 */
public final class QualityFastaRecordBuilder extends AbstractFastaRecordBuilder<PhredQuality, QualitySequence,QualityFastaRecord> {
	/**
	 * Pattern used to pull out individual quality
	 * values from the body of a string (or fasta file).
	 */
	private static final Pattern QUAL_BODY_PATTERN = Pattern.compile("\\d+");
	/**
	 * Create a new builder instance for the given id and 
	 * entire quality values as a human readable string.
	 * @param id the id of the quality fasta record; can not be null.
	 * @param entireRecordBody a string representation of the quality sequence,
     *  must contain whitespace to separate quality values; each quality value
     *  may have leading
     *  zeros but can not be null.
     *  @throws NullPointerException if either parameter is null.
	 */
	public QualityFastaRecordBuilder(String id, String entireRecordBody){
		this(id,parseQualitySequence(entireRecordBody));
	}
	/**
	 * Create a new builder instance for the given id and {@link QualitySequence}.
	 * @param id the id of the quality fasta record; can not be null.
	 * @param sequence the quality values of this 
	 *  @throws NullPointerException if either parameter is null.
	 */
	
	private static QualitySequence parseQualitySequence(String sequence) {
		QualitySequenceBuilder builder = new QualitySequenceBuilder(sequence.length());
		
		Matcher m = QUAL_BODY_PATTERN.matcher(sequence);
		while(m.find()){
			builder.append(Byte.parseByte(m.group()));
		}
		
    	return builder.build();
	}
	public QualityFastaRecordBuilder(String id, QualitySequence sequence) {
		super(id, sequence);
	}
	@Override
	protected QualityFastaRecord createNewInstance(String id, QualitySequence sequence,
			String comment) {
		if(comment ==null){
			return new UncommentedQualityFastaRecord(id, sequence);
		}
		return new CommentedQualityFastaRecord(id, sequence, comment);
	}
	
	
}
