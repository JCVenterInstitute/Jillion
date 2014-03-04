package org.jcvi.jillion.maq;

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
import org.jcvi.jillion.core.io.IOUtil.Endian;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.trace.fastq.FastqRecord;
import org.jcvi.jillion.trace.fastq.FastqRecordWriter;

public class BinaryFastqFileWriterBuilder {

	

	private final File outputBfqFile;
	private Endian endian = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN? Endian.BIG : Endian.LITTLE;
	
	public BinaryFastqFileWriterBuilder(File outputBfqFile) {
		if(outputBfqFile == null){
			throw new NullPointerException("output bfq file can not be null");
		}
		
		this.outputBfqFile = outputBfqFile;
	}
	
	public BinaryFastqFileWriterBuilder endian(Endian endian){
		if(endian ==null){
			throw new NullPointerException("endian can not be null");
		}
		this.endian = endian;
		return this;
	}
	
	public FastqRecordWriter build() throws IOException{
		IOUtil.mkdirs(outputBfqFile.getParentFile());
		
		return new BinaryFastqFileWriter(outputBfqFile, endian);
	}
	
	
	private static class BinaryFastqFileWriter implements FastqRecordWriter {
		private final OutputStream out;
		private ByteOrder byteOrder;
		
		public BinaryFastqFileWriter(File bqf, Endian endian) throws IOException{
			this.byteOrder = endian.toByteOrder();
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
				//we can just or the values as is
				buffer[i] = (byte)Math.min(buffer[i], 63);
				buffer[i] |= v<<6;
			}
			return buffer;
		}

		private int encode(Nucleotide n) {
			switch(n){
			case Adenine : return 0;
			case Cytosine : return 1;
			case Guanine : return 2;
			case Thymine : return 3;
			default : throw new IllegalArgumentException("bfq files only support A,C,G, and T : " + n.getCharacter());
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
