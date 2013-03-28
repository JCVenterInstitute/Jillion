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
package org.jcvi.jillion.trace.sff;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.internal.core.util.GrowableByteArray;
/**
 * {@code SffWriterBuilder} is an Builder 
 * that will build an {@link SffWriter} implementation.
 * 
 * Currently the {@link SffWriter} built is not able to
 * create a manifest for the new sff file.  The writer
 * will perform identical behavior to the 454 program
 * sfffile using the '-nmft' option.
 * @author dkatzel
 *
 */
public class SffWriterBuilder {

	private final NucleotideSequence keySequence, flowSequence;
	private final File outputFile;

	private boolean includeIndex;
	
	/**
	 * Create a new {@link SffWriterBuilder} that will write
	 * out the given outputFile using the given
	 * {@link SffCommonHeader}.  All flowgrams
	 * written to this writer must all use the same key and flow sequences.
	 * @param outputFile the output file to write; can not be null.
	 * If the outputFile already exists, it will be overwritten.
	 * If the path to the outputFile does not exist, it will 
	 * be created. Actual file creation is delayed until 
	 * {@link #build()} is called.
	 * @param sffHeader an {@link SffCommonHeader} instance;
	 * can not be null.
	 * @throws NullPointerException if either sffCommonHeader  or output file are null.
	 * @throws IllegalArgumentException if the header's key sequence or flow sequence contain gaps.
	 */
	public SffWriterBuilder(File outputFile, SffCommonHeader sffHeader){
		this(outputFile, sffHeader.getKeySequence(), sffHeader.getFlowSequence());
	}
	/**
	 * Create a new {@link SffWriterBuilder} that will write
	 * out the given outputFile using the given
	 * key sequence and flow sequence.  All flowgrams
	 * written to this writer must all use the same key and flow sequences.
	 * @param outputFile the output file to write; can not be null.
	 * If the outputFile already exists, it will be overwritten.
	 * If the path to the outputFile does not exist, it will 
	 * be created. Actual file creation is delayed until 
	 * {@link #build()} is called.
	 * @param keySequence a {@link NucleotideSequence} that represents the key sequence;
	 * can not be null.
	 * @param flowSequence a {@link NucleotideSequence} that represents the flow sequence;
	 * can not be null.
	 * @throws NullPointerException if either output file, keySequence or flowSequence are null.
	 * @throws IllegalArgumentException if the keySequence or flowSequence contain gaps.
	 */
	public SffWriterBuilder(File outputFile, NucleotideSequence keySequence, NucleotideSequence flowSequence){
		if(keySequence ==null){
			throw new NullPointerException("key sequence can not be null");
		}
		if(flowSequence ==null){
			throw new NullPointerException("flow sequence can not be null");
		}
		if(keySequence.getNumberOfGaps() >0){
			throw new IllegalArgumentException("key sequence can not contain any gaps");
		}
		if(flowSequence.getNumberOfGaps() >0){
			throw new IllegalArgumentException("flow sequence can not contain any gaps");
		}
		if(outputFile ==null){
			throw new NullPointerException("outputFile can not be null");
		}
		this.keySequence = keySequence;
		this.flowSequence = flowSequence;
		this.outputFile = outputFile;
	}
	/**
	 * Flag to turn on or off the encoded read index.  454 Sff files include an encoded index containing
	 * the file offsets for each read in the file.  This
	 * index can be used by 454 programs (and Jillion)
	 * to improve performance when performing random access
	 * tasks in the file (such as parsing a specific read  from the file).
	 * by default, this flag is set to {@code false}.
	 * @param includeIndex {@code true} to include an index;
	 * {@code false} otherwise.
	 * @return this
	 */
	public SffWriterBuilder includeIndex(boolean includeIndex){
		this.includeIndex = includeIndex;
		return this;
	}
	/**
	 * Create a new {@link SffWriter} instance using the parameters 
	 * collected so far.
	 * @return a new {@link SffWriter}; will never be null.
	 * @throws IOException if there is a problem creating the output file
	 * to write to.
	 */
	public SffWriter build() throws IOException{
		return new SffWriterImpl(outputFile, includeIndex, keySequence, flowSequence);
	}
	
	private static final class SffWriterImpl implements SffWriter{
		private static final byte NULL_TERMINATOR = (byte)0;
		private static final byte READ_SEPARATOR = (byte)0xFF;
		private final File outputFile;
		private final OutputStream out;
		private final boolean includeIndex;
		
		private long numberOfReads=0L;
		private boolean closed=false;
		private long currentOffset=0L;
		/**
		 * Sff indexes are stored in alphabetical order
		 * so we need to collect the name-offset pairs
		 * in a sorted map and write them out to the 
		 * file later when {@link #close()} is called.
		 */
		private final SortedMap<String, Long> indexMap = new TreeMap<String, Long>();
		
		public SffWriterImpl(File outputFile, boolean includeIndex,NucleotideSequence keySequence,NucleotideSequence flowSequence) throws IOException {
			this.outputFile = outputFile;
			this.includeIndex = includeIndex;
			
			IOUtil.mkdirs(outputFile.getParentFile());
			this.out = new BufferedOutputStream(new FileOutputStream(outputFile));
			writePartialHeader(keySequence, flowSequence);			
		}

		private void writePartialHeader(NucleotideSequence keySequence,NucleotideSequence flowSequence) throws IOException{
			int numberOfFlows = (int)flowSequence.getLength();
			//partial header will put temp values for manifest and number of reads
			//these values will be updated in the close() call.
			SffCommonHeader paritalHeader = new DefaultSffCommonHeader(BigInteger.valueOf(0), 0L, 
					0L, numberOfFlows, flowSequence, keySequence);
			currentOffset+=SffWriterUtil.writeCommonHeader(paritalHeader, out);
		}
		
		private synchronized void checkNotClosed() throws IOException{
			if(closed){
				throw new IOException("writer is closed");
			}
		}
		@Override
		public synchronized void close() throws IOException {
			if(closed){
				return;
			}
			final byte[] index;
			if(includeIndex){
				index = conmputeBinaryIndex();
				out.write(index);
			}else{
				index=null;
			}
			out.close();
			if(numberOfReads >0){
				RandomAccessFile f = new RandomAccessFile(outputFile, "rw");
				//magic number = 8bytes
				if(includeIndex){
					//manifest info starts at offset 8
					f.seek(8);
					f.writeLong(currentOffset);
					f.writeInt(index.length);
				}
				
				//num of reads starts at offset 20
				f.seek(20);
				f.write(IOUtil.convertUnsignedIntToByteArray(numberOfReads));
			}
			closed=true;
		}

		@Override
		public synchronized void write(SffFlowgram flowgram) throws IOException {
			checkNotClosed();
			NucleotideSequence seq = flowgram.getNucleotideSequence();
			SffReadHeader header = new DefaultSffReadHeader((int)seq.getLength(), flowgram.getQualityClip(), flowgram.getAdapterClip(), flowgram.getId());
			
			SffReadData data = new DefaultSffReadData(seq, flowgram.getRawIndexes()
					, flowgram.getRawEncodedFlowValues(), flowgram.getQualitySequence());
			
			write(header, data);
			
		}
		@Override
		public synchronized void write(SffReadHeader header, SffReadData data) throws IOException {
			checkNotClosed();
			if(includeIndex){
				indexMap.put(header.getId(), currentOffset);
			}
			currentOffset+= SffWriterUtil.writeReadHeader(header, out);
			currentOffset+= SffWriterUtil.writeReadData(data, out);			
			numberOfReads++;
		}
		
		private byte[] conmputeBinaryIndex(){
			GrowableByteArray indexBytes = new GrowableByteArray(19 * indexMap.size() + 8);
			//this tells 454 sff parsers that
			//the index does not contain an XML manifest
			//and is encoded version 1.00 of the index encoding.
			indexBytes.append(".srt1.00".getBytes(IOUtil.UTF_8));
			
			for(Entry<String, Long> entry : indexMap.entrySet()){
				byte[] b =entry.getKey().getBytes(IOUtil.UTF_8);
				indexBytes.append(b);
				indexBytes.append(NULL_TERMINATOR);
				indexBytes.append(SffUtil.toSffIndexOffsetValue(entry.getValue().longValue()));
				indexBytes.append(READ_SEPARATOR);
			}
			
			return indexBytes.toArray();
		}
	}
}
