/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.sam;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.internal.sam.index.BamIndexer;
import org.jcvi.jillion.sam.attribute.SamAttributeValidator;
import org.jcvi.jillion.sam.header.SamHeader;

/**
 * {@code Encoding}
 * handles writing out 
 * the different SAM or BAM encoded files.
 * 
 * @author dkatzel
 *
 */
enum Encoding{
	/**
	 * Encode data as SAM files.
	 */
	SAM(".sam"){
		@Override
		SamWriter createPreSortedOutputWriter(File out, SamHeader header, SamAttributeValidator validator, BamIndexer indexer)
				throws IOException {
			//indexer parameter ignored
			//since it is only used in BAM files.
			return new PresortedSamFileWriter(out, header, validator);
		}
		@Override
		SamParser createNewNoValidationSamParser(File f) throws IOException {
			return new SamFileParser(f, NullSamAttributeValidator.INSTANCE);
		}
	},
	/**
	 * Encode data as BAM files.
	 */
	BAM(".bam"){
		@Override
		SamWriter createPreSortedOutputWriter(File out, SamHeader header, SamAttributeValidator validator, BamIndexer indexer)
				throws IOException {
			return new PresortedBamFileWriter(header, out, indexer, validator);
		}

		@Override
		SamParser createNewNoValidationSamParser(File f) throws IOException {
			return new BamFileParser(f, NullSamAttributeValidator.INSTANCE);
		}
		
		
	};
	
	private final String suffix;
	
	private Encoding(String suffix){
		this.suffix = suffix;
	}
	/**
	 * Get the suffix for this encoding
	 * for example BAM files suffix is {@literal ".bam"}.
	 * @return a String will never be null.
	 */
	public String getSuffix() {
		return suffix;
	}
	/**
	 * Create a new {@link SamWriter}
	 * that will write out {@link SamRecord}s to the given file
	 * without any validation performed by a 
	 * {@link SamAttributeValidator}.
	 * This should only be used when the {@link SamRecord}s to write
	 * have been previously
	 * validated and presorted.
	 * @param out the output file to write to if the file
	 * already exists, it will be overwritten.
	 * @param header the {@link SamHeader} to use.
	 * @return a new {@link SamWriter} will never be null.
	 * @throws IOException if there is a problem creating the new output file.
	 */
	SamWriter createPreSortedNoValidationOutputWriter(File out, SamHeader header, BamIndexer indexer) throws IOException{
		//no validation since we have already validated
		//the reads when we added them to our in memcheck
		return createPreSortedOutputWriter(out, header, NullSamAttributeValidator.INSTANCE, indexer);
	}
	
	
	abstract SamParser createNewNoValidationSamParser(File f) throws IOException;
	/**
	 * Create a new {@link SamWriter}
	 * that will write out {@link SamRecord}s to the given file
	 * and validate those {@link SamRecord}s using the given
	 * {@link SamAttributeValidator}.
	 * This should only be used when the {@link SamRecord}s to write
	 * have been previously
	 * validated and presorted.
	 * @param out the output file to write to if the file
	 * already exists, it will be overwritten.
	 * @param header the {@link SamHeader} to use; can not be null.
	 * @param validator the {@link SamAttributeValidator} to use;
	 * can not be null.
	 * @return a new {@link SamWriter} will never be null.
	 * @throws IOException if there is a problem creating the new output file.
	 */
	abstract SamWriter createPreSortedOutputWriter(File out, SamHeader header, SamAttributeValidator validator, BamIndexer indexer)throws IOException;

	/**
	 * Create a new {@link SamWriter} implementation
	 * that can take {@link SamRecord}s given to it via
	 * the {@link #writeRecord(SamRecord)}
	 * in ANY ORDER and write out the SAM or BAM file
	 * sorted by the manner specified in the given {@link SamHeader#getSortOrder()}.
	 * @param out the output file to write.
	 * @param tmpDirRoot the temp directory root to write temp files underneath.
	 * If this value is null, then the system default temp dir is used.
	 * @param header the {@link SamHeader} to write and to validate against.
	 * @param maxRecordsToKeepInMemory the maximum number of {@link SamRecord}s
	 * to save in memory at any one time, once this threshold has been reached,
	 * the records are persisted to a temp file under the given tempDirRoot.
	 * @param validator  the {@link SamAttributeValidator} to use;
	 * can not be null. 
	 * @return a new {@link SamWriter} will never be null.
	 * @throws IOException if there is a problem creating the new output file.
	 */
	SamWriter createReSortedOutputWriter(File out, File tmpDirRoot,
			SamHeader header, int maxRecordsToKeepInMemory, SamAttributeValidator validator, BamIndexer indexer)
			throws IOException {
		return new ReSortSamFileWriter(out, tmpDirRoot,header, maxRecordsToKeepInMemory, validator, this, indexer);
	}
	
	
	/**
	 * Parse the given file extension (no {@literal "."})
	 * and return the correct Encoding implementation.
	 * @param extension the extension; may be null
	 * or empty.
	 * @return {@link Encoding#BAM}
	 * if the extension is "bam" ignoring case,
	 * "sam" otherwise.
	 */
	public static Encoding parseEncoding(String extension){
		if("bam".equalsIgnoreCase(extension)){
			return BAM;
		}
		//assume sam for all else ?
		return SAM;
	}
}
