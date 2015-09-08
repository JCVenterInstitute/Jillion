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
package org.jcvi.jillion.maq.bfa;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.LongBuffer;
import java.util.Iterator;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecord;
import org.jcvi.jillion.fasta.nt.NucleotideFastaWriter;

/**
 * {@code BfaWriterBuilder}
 * is a builder class that will create
 * a new {@link NucleotideFastaWriter} instance
 * that will write MAQ binary encoded fasta
 * files ({@literal .bfa} files).
 * 
 * It is important to note that BFA
 * files only encode ACGT and N bases.
 * Any bases that are not ACG or T are
 * converted into N's.
 * This means that it is possible 
 * that writing a {@link NucleotideFastaRecord} to the built writer
 * which either has bases that are not
 * ACG and T will not be written out the same. 
 * @author dkatzel
 *
 */
public class BfaWriterBuilder {
	/**
	 * Output file to write.
	 */
	private final File outputBfaFile;
	private final OutputStream out;
	
	/**
	 * The ByteOrder to use, default to system
	 * order if not set by user.
	 */
	private ByteOrder endian = ByteOrder.nativeOrder();
	
	/**
	 * Create a new {@link BfaWriterBuilder} instance
	 * which will create a {@link NucleotideFastaWriter}
	 * that will write to the given output {@link File}.
	 * @param outputBfaFile the output bfa file to write to; can not be null.
	 * If this file already exists, then it will be overwritten.  If the file
	 * or any parent directories do not yet exist,
	 * then they will be created when {@link #build()}
	 * is called.
	 * @throws NullPointerException if outputBfaFile is null.
	 */
	public BfaWriterBuilder(File outputBfaFile) {
		if(outputBfaFile == null){
			throw new NullPointerException("output bfa file can not be null");
		}
		this.out = null;
		this.outputBfaFile = outputBfaFile;
	}
	/**
	 * Create a new {@link BfaWriterBuilder} instance
	 * which will create a {@link NucleotideFastaWriter}
	 * that will write to the given {@link OutputStream}.
	 * @param out the {@link OutputStream} to encode bfq data to; can not be null.
	 * @throws NullPointerException if out is null.
	 */
	public BfaWriterBuilder(OutputStream out) {
		if(out == null){
			throw new NullPointerException("output bfq stream can not be null");
		}
		this.out = out;
		this.outputBfaFile = null;
	}
	/**
	 * Force the writer to use the given {@link ByteOrder}.
	 * If this method is not used, then the system default
	 * endian will be used.  MAQ uses the default
	 * endian of the machine it is run on so if you wish
	 * to use bfa files produced by this writer in MAQ
	 * then you will need to make sure the endian
	 * matches the endian the target machine MAQ will run on.
	 * @param endian the endian to use; can not be null.
	 * @return this
	 * @throws NullPointerException if endian is null.
	 */
	public BfaWriterBuilder endian(ByteOrder endian){
		if(endian ==null){
			throw new NullPointerException("endian can not be null");
		}
		this.endian = endian;
		return this;
	}
	/**
	 * Create a new {@link NucleotideFastaWriter}
	 * instance that will write Binary Fasta encoded data
	 * to the given output file.
	 * @return a new {@link NucleotideFastaWriter}
	 * instance will never be null.
	 * @throws IOException if there is a problem creating the 
	 * file or any parent directory or opening the file for writing.
	 */
	public NucleotideFastaWriter build() throws IOException{
		if(outputBfaFile !=null){
			IOUtil.mkdirs(outputBfaFile.getParentFile());
			
			return new BinaryFastaFileWriter(new BufferedOutputStream(new FileOutputStream(outputBfaFile)),
											endian);
		}
		return new BinaryFastaFileWriter(out, endian);
	}
	
	
	private static class BinaryFastaFileWriter implements NucleotideFastaWriter {
		private final OutputStream out;
		private final ByteOrder byteOrder;
		
		public BinaryFastaFileWriter(OutputStream out, ByteOrder endian) throws IOException{
			this.byteOrder = endian;
			this.out = out;
		}
		
		@Override
		public void close() throws IOException {
			out.close();
		}
		
		@Override
		public void write(NucleotideFastaRecord record) throws IOException {
			write(record.getId(), record.getSequence());
			
		}

		@Override
		public void write(String id, NucleotideSequence sequence)
				throws IOException {
			int nameLength = id.length() +1;
			int numBases = (int) sequence.getLength();
			int arrayLength =numberOfElementsInEncodedArray(numBases);
			int bufferSize = 12 + nameLength + arrayLength*16;
			
			ByteBuffer buf = ByteBuffer.allocate(bufferSize);
			buf.order(byteOrder);
			buf.putInt(nameLength);
			buf.put(asNullTerminatedBytes(id, nameLength));
			buf.putInt(numBases);
			buf.putInt(arrayLength);
			
			long[] encodedBases = new long[arrayLength];
			long[] mask = new long[arrayLength];
			Iterator<Nucleotide> iter = sequence.iterator();
			for(int i=0; i<arrayLength-1; i++){
				//our encoded bases ACGT
				long v=0;
				//our mask which
				//i think is how we tell an A from an N
				//if the mask is set to 0, then it's an N
				//otherwise those 2 bits are set to 3
				long m=0;
				for(int j = 0; j<32; j++){
					Nucleotide n =iter.next();
					//shift values over 2 bits
					//to make room for next base.
					//We shift first since
					//shifting 0 is still 0
					//and we don't have to worry
					//about shifting extra when we
					//are done looping.
					v <<=2;
					m <<=2;
					switch (n) {
						case Adenine:
										m |= 3;
										break;
						case Cytosine:
										v |= 1;
										m |= 3;
										break;
						case Guanine:
										v |= 2;
										m |= 3;
										break;
						case Thymine:
										v |= 3;
										m |= 3;
										break;
						// anything else gets mask set to 0
						default:
					}
					
				}
				encodedBases[i] = v;
				mask[i] =m;
			}
			//unrolled loop so we don't have to 
			//do extra boundary checking
			//when we don't need to
			//our encoded bases ACGT
			long v=0;
			//our mask which
			//i think is how we tell an A from an N
			//if the mask is set to 0, then it's an N
			//otherwise those 2 bits are set to 3
			long m=0;
			for(int j = 0; j<32; j++){
				
				//shift values over 2 bits
				//to make room for next base.
				//We shift first since
				//shifting 0 is still 0
				//and we don't have to worry
				//about shifting extra when we
				//are done looping.
				//need to shift every time
				//even if we are out of bases
				//to keep the bits in the correct location
				v <<=2;
				m <<=2;
				if(!iter.hasNext()){
					continue;
				}
				Nucleotide n =iter.next();
				switch(n){
					case Adenine :
									m |=3;
									break;
					case Cytosine : v |= 1;
									m |=3;
									break;
					case Guanine : v |= 2;
									m |=3;
									break;
					case Thymine : v |= 3;
									m |=3;
									break;
					//anything else gets mask set to 0
					default:								
				}
				
			}
			encodedBases[arrayLength-1] = v;
			mask[arrayLength-1] =m;
			//now the arrays are fully populated
			LongBuffer longBuffer = buf.asLongBuffer();
			longBuffer.put(encodedBases);
			longBuffer.put(mask);
			
			buf.rewind();
			//can't use flip()
			//because LongBuffer uses independent
			//position so previous
			//puts of long[]s did not 
			//update buf position
		
			byte[] b = new byte[bufferSize];
			buf.get(b);
			out.write(b);
		}

		private static int numberOfElementsInEncodedArray(int numBases){
			return (numBases/32)+ (numBases%32 ==0?0:1);
		}
		@Override
		public void write(String id, NucleotideSequence sequence,
				String optionalComment) throws IOException {
			//comment is ignored
			write(id, sequence);
			
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
