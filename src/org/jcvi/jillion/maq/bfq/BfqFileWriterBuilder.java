/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.maq.bfq;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;
import java.util.zip.GZIPOutputStream;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.trace.fastq.FastqRecord;
import org.jcvi.jillion.trace.fastq.FastqWriter;

/**
 * {@code BfqFileWriterBuilder}
 * is a builder class that will create
 * a new {@link FastqWriter} instance
 * that will write MAQ binary encoded fastq
 * files ({@literal .bfq} files).
 * 
 * It is important to note that BFQ
 * files only encode ACGT and N bases.
 * Any bases that are not ACG or T are
 * converted into N's and their quality values
 * are set to 0.
 * Furthermore, any quality values >63 will be capped
 * at 63.  This means that it is possible 
 * that writing a {@link FastqRecord} to the built writer
 * which either has quality values > 63 or bases that are not
 * ACG and T will not be written out the same. 
 * @author dkatzel
 *
 */
public class BfqFileWriterBuilder {
	/**
	 * Output file to write.
	 */
	private final File outputBfqFile;
	/**
	 * The ByteOrder to use, default to system
	 * order if not set by user.
	 */
	private ByteOrder endian = ByteOrder.nativeOrder();
	
	/**
	 * Create a new {@link BfqFileWriterBuilder} instance
	 * which will create a {@link FastqWriter}
	 * that will write to the given output {@link File}.
	 * @param outputBfqFile the output bfq file to write to; can not be null.
	 * If this file already exists, then it will be overwritten.  If the file
	 * or any parent directories do not yet exist,
	 * then they will be created when {@link #build()}
	 * is called.
	 * @throws NullPointerException if outputBfqFile is null.
	 */
	public BfqFileWriterBuilder(File outputBfqFile) {
		if(outputBfqFile == null){
			throw new NullPointerException("output bfq file can not be null");
		}
		
		this.outputBfqFile = outputBfqFile;
	}
	/**
	 * Force the writer to use the given {@link ByteOrder}.
	 * If this method is not used, then the system default
	 * endian will be used.  MAQ uses the default
	 * endian of the machine it is run on so if you wish
	 * to use bfq files produced by this writer in MAQ
	 * then you will need to make sure the endian
	 * matches the endian the target machine MAQ will run on.
	 * @param endian the endian to use; can not be null.
	 * @return this
	 * @throws NullPointerException if endian is null.
	 */
	public BfqFileWriterBuilder endian(ByteOrder endian){
		if(endian ==null){
			throw new NullPointerException("endian can not be null");
		}
		this.endian = endian;
		return this;
	}
	/**
	 * Create a new {@link FastqWriter}
	 * instance that will write Binary Fastq encoded data
	 * to the given output file.
	 * @return a new {@link FastqWriter}
	 * instance will never be null.
	 * @throws IOException if there is a problem creating the 
	 * file or any parent directory or opening the file for writing.
	 */
	public FastqWriter build() throws IOException{
		IOUtil.mkdirs(outputBfqFile.getParentFile());
		
		return new BinaryFastqFileWriter(outputBfqFile, endian);
	}
	
	
	private static class BinaryFastqFileWriter implements FastqWriter {
		private final OutputStream out;
		private final ByteOrder byteOrder;
		
		public BinaryFastqFileWriter(File bqf, ByteOrder endian) throws IOException{
			this.byteOrder = endian;
			this.out = new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(bqf)));
		}
		
		@Override
		public void close() throws IOException {
			out.close();
		}

		@Override
		public void write(FastqRecord record) throws IOException {
			write(record.getId(), record.getNucleotideSequence(), record.getQualitySequence());

		}

		

		@Override
		public void write(String id, NucleotideSequence sequence,
				QualitySequence qualities, String optionalComment)
				throws IOException {
			//ignore comment
			write(id, sequence, qualities);

		}
		
		@Override
		public void write(String id, NucleotideSequence nucleotides,
				QualitySequence qualities) throws IOException {
			int nameLength = id.length() +1;
			int numBases = (int) nucleotides.getLength();
			int bufferSize = 8 + nameLength + numBases;
			
			ByteBuffer buf = ByteBuffer.allocate(bufferSize);
			buf.order(byteOrder);
			buf.putInt(nameLength);
			buf.put(asNullTerminatedBytes(id, nameLength));
			buf.putInt(numBases);
			buf.put(encodeBasesAndQualities(nucleotides,qualities, numBases));
			
			buf.flip();
			byte[] b = new byte[bufferSize];
			buf.get(b);
			out.write(b);
		}

		private byte[] encodeBasesAndQualities(
				NucleotideSequence nucleotides, QualitySequence qualities,
				int numBases) {
			
			Iterator<Nucleotide> basesIter = nucleotides.iterator();
			byte[] buffer = qualities.toArray();
			for(int i=0; i< buffer.length; i++){
				Nucleotide n = basesIter.next();
				int v = encode(n);
				if(v >3){
					//follow Maq and make non ACGT
					//0
					//I guess we can tell by having 
					//a quality value of 0?
					buffer[i] = 0;
				}else{
					//the byte value is the ACG or T
					//and the qual value bit masked together
					//plus the 
					buffer[i] = (byte)Math.min(buffer[i], 63);
					buffer[i] |= v<<6;
				}
			}
			return buffer;
		}

		private int encode(Nucleotide n) {
			switch(n){
			case Adenine : return 0;
			case Cytosine : return 1;
			case Guanine : return 2;
			case Thymine : return 3;
			default : 
				return 4;
			}
		}

		private byte[] asNullTerminatedBytes(String id, int nameLength) {
			byte[] b = new byte[nameLength];
			char[] cs = id.toCharArray();
			for(int i=0; i<cs.length; i++){
				b[i] = (byte)cs[i];
			}
			//last byte should be initialized to 0
			return b;
		}

	}
	
	
}
