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

import org.jcvi.jillion.core.io.FileUtil;
import org.jcvi.jillion.internal.sam.index.BamIndexer;
import org.jcvi.jillion.sam.attribute.ReservedAttributeValidator;
import org.jcvi.jillion.sam.attribute.SamAttributeValidator;
import org.jcvi.jillion.sam.header.SamHeader;
import org.jcvi.jillion.sam.header.SamHeaderBuilder;
/**
 * {@code SamFileWriterBuilder}
 * is a Builder object that will create
 * a {@link SamWriter} implementation
 * to write out either SAM or BAM formatted files
 * and optionally re-sort them.
 * If resorting the {@link SamRecord}s, temp data
 * will be written to a temp directory.
 * By default, the system's temp directory is used.
 * @author dkatzel
 *
 */
public final class SamFileWriterBuilder {

	
	private static final int DEFAULT_RECORDS_IN_MEMORY = 2000000; //2 million
	
	private final File outputFile;
	private final SamHeaderBuilder headerBuilder;
	private SamAttributeValidator attributeValidator = ReservedAttributeValidator.INSTANCE;
	
	private File tmpDirRoot = null; //default to systemp temp
	
	private SortOrder reSortOrder = null;
	
	private int maxRecordsToKeepInMemory = DEFAULT_RECORDS_IN_MEMORY;
	
	private boolean makeBamIndex=false;
	
	/**
	 * Get the max number of {@link SamRecord}s
	 * to keep in memory at any one time if
	 * re-sorting on the fly. 
	 * @return an int will always be >=0.
	 */
	public static int getDefaultRecordsToKeepInMemory() {
		return DEFAULT_RECORDS_IN_MEMORY;
	}
	/**
	 * Create a new {@link SamFileWriterBuilder} instance
	 * that will write out {@link SamRecord}s
	 * to the given output file.  The file encoding
	 * to use is determined by the file extension of the output file.
	 * @param outputFile the output file to write to;
	 * can not be null.
	 * the File extension must be either ".sam" or ".bam".
	 * @param header the {@link SamHeader}; can not be null.
	 * The sort order in the header 
	 * may be overridden by {@link #reSortBy(SortOrder)}
	 * or {@link #reSortBy(SortOrder, int)} or {@link #forceHeaderSortOrder(SortOrder)}.
	 * @throws NullPointerException if either parameter is null.
	 */
	public SamFileWriterBuilder(File outputFile, SamHeader header){
		if(outputFile == null){
			throw new NullPointerException("output file can not be null");
		}
		if(header ==null){
			throw new NullPointerException("headerBuilder can not be null");
		}
		
		this.outputFile = outputFile;
		this.headerBuilder = new SamHeaderBuilder(header);
	}
	/**
	 * Change the temp directory to write
	 * re-sorted temp data to when writing out the 
	 * SAM or BAM file.
	 * @param tmpDir the temp directory to use;
	 * if null, then use the default system temp directory.
	 * If this temp directory does not exist, it will be created
	 * if used.
	 * @return this.
	 */
	public SamFileWriterBuilder setTempRootDir(File tmpDir){
		this.tmpDirRoot = tmpDir;
		return this;
	}
	/**
	 * Set a {@link SamAttributeValidator} to use
	 * to confirm that the {@link SamRecord}s given to the writer
	 * are valid for this {@link SamHeader}.
	 * If not provided, then a default validator
	 * that only knows about ReservedSamAttributeKeys
	 * will be used.
	 * @param validator the validator to use; can not be null.
	 * @return this
	 * @throws NullPointerException if validator is null.
	 */
	public SamFileWriterBuilder setSamAttributeValidator(SamAttributeValidator validator){
		if(validator ==null){
			throw new NullPointerException("validator can not be null");
		}
		this.attributeValidator = validator;
		return this;
	}
	
	public SamFileWriterBuilder createBamIndex(boolean createBamIndex){
		this.makeBamIndex = createBamIndex;
		return this;
	}
	/**
	 * Convenience method to resort 
	 * using the default number of records to get in memory
	 * specified by {@link #getDefaultRecordsToKeepInMemory()}.
	 * Same as {@link #reSortBy(SortOrder, int) reSortBy(sortOrder, getDefaultRecordsToKeepInMemory()}
	 * 
	 * @param sortOrder the {@link SortOrder} to use;
	 * can not be null.
	 * @return this.
	 * @see #reSortBy(SortOrder, int).
	 */
	public SamFileWriterBuilder reSortBy(SortOrder sortOrder){
		return reSortBy(sortOrder, DEFAULT_RECORDS_IN_MEMORY);
	}
	/**
	 * Resort the {@link SamRecord}s written by this
	 * writer on the fly.  This will also modify
	 * the SAM/BAM header written out
	 * to specify the correct sort order.
	 * NOTE: Setting {@link SortOrder#UNKNOWN}
	 * or {@link SortOrder#UNSORTED}
	 * will not cause a re-sort on the fly
	 * but the header written will still be modified.
	 * @param sortOrder the {@link SortOrder} to use;
	 * can not be null.
	 * @param maxRecordsToKeepInMemory the number of SamRecords
	 * to keep in memory; must be >0.
	 * @return this.
	 * @throws NullPointerException if sortOrder is not specified.
	 * @throws IllegalArgumentException if maxRecordsToKeepInMemory < 1.
	 */
	public SamFileWriterBuilder reSortBy(SortOrder sortOrder, int maxRecordsToKeepInMemory){
		if(sortOrder ==null){
			throw new NullPointerException("sort order can not be null");
		}
		if(maxRecordsToKeepInMemory < 1){
			throw new IllegalArgumentException("max records to keep in memory must be positive");
		}
		
		forceHeaderSortOrder(sortOrder);
		this.reSortOrder = sortOrder;
		this.maxRecordsToKeepInMemory = maxRecordsToKeepInMemory;
		return this;
	}
	/**
	 * Change the sort order specified in the written
	 * out header but do not actually re-sort the records on the fly.
	 * This method should be used with care and should only be used
	 * if the records are already known to be sorted
	 * but for some reason the provided header has the wrong
	 * sort order.
	 * @param sortOrder the sort order to use; if null,
	 * then the sort order will be set to {@link SortOrder#UNKNOWN}.
	 * @return this.
	 */
	public SamFileWriterBuilder forceHeaderSortOrder(SortOrder sortOrder){
		headerBuilder.setSortOrder(sortOrder);
		return this;
	}
	/**
	 * Create a new {@link SamWriter} instance
	 * using the provided configuration.
	 * @return a new {@link SamWriter} will never be null.
	 * @throws IOException if there is a problem creating
	 * the output directories or temp areas.
	 */
	public SamWriter build() throws IOException{
		SamHeader header = headerBuilder.build();
		Encoding encoding = Encoding.parseEncoding(FileUtil.getExtension(outputFile));
		BamIndexer indexer;
		if(makeBamIndex && encoding== Encoding.BAM){
			//check sort order in new header
			//which will be the sort order we use
			//regardless if we are re-sorting or not.
			//
			//BAM indexes must be applied to 
			//coordinate sorted bam files
			if(header.getSortOrder() != SortOrder.COORDINATE){
				throw new IllegalStateException("can not make bam index when bam file to be written is not coordinate sorted");
			}
			indexer = new BamIndexer(header);
		}else{
			indexer =null;
		}
		if(writeUnSortedRecords()){
			return encoding.createPreSortedNoValidationOutputWriter(outputFile, header, indexer);
		}
		return encoding.createReSortedOutputWriter(outputFile, tmpDirRoot, header, maxRecordsToKeepInMemory, attributeValidator, indexer);
		
	}

	private boolean writeUnSortedRecords() {
		return reSortOrder ==null || reSortOrder == SortOrder.UNKNOWN || reSortOrder == SortOrder.UNSORTED;
	}

	
	
	
}
