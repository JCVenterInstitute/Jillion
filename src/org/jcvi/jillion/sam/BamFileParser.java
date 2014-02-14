package org.jcvi.jillion.sam;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.jcvi.jillion.core.io.FileUtil;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.io.IOUtil.Endian;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.internal.core.io.OpenAwareInputStream;
import org.jcvi.jillion.internal.core.io.TextLineParser;
import org.jcvi.jillion.sam.attribute.ReservedAttributeValidator;
import org.jcvi.jillion.sam.attribute.SamAttributeValidator;
import org.jcvi.jillion.sam.cigar.Cigar;
import org.jcvi.jillion.sam.cigar.CigarOperation;
import org.jcvi.jillion.sam.header.ReferenceSequence;
import org.jcvi.jillion.sam.header.SamHeader;

public class BamFileParser extends AbstractSamFileParser {

	
	private static final Nucleotide[] ENCODED_BASES;
	
	private final File bamFile;
	private final SamAttributeValidator validator;
	
	
	static{
		//`=ACMGRSVTWYHKDBN'
		ENCODED_BASES = new Nucleotide[16];
		//TODO: note [0]set to null to force NPE
		ENCODED_BASES[0] = null;
		
		ENCODED_BASES[1] = Nucleotide.Adenine;
		ENCODED_BASES[2] = Nucleotide.Cytosine;
		ENCODED_BASES[3] = Nucleotide.Amino;
		ENCODED_BASES[4] = Nucleotide.Guanine;
		ENCODED_BASES[5] = Nucleotide.Purine;
		ENCODED_BASES[6] = Nucleotide.Strong;
		ENCODED_BASES[7] = Nucleotide.NotThymine;
		ENCODED_BASES[8] = Nucleotide.Thymine;
		ENCODED_BASES[9] = Nucleotide.Weak;
		ENCODED_BASES[10] = Nucleotide.Pyrimidine;
		ENCODED_BASES[11] = Nucleotide.NotGuanine;
		ENCODED_BASES[12] = Nucleotide.Keto;
		ENCODED_BASES[13] = Nucleotide.NotCytosine;
		ENCODED_BASES[14] = Nucleotide.NotAdenine;
		ENCODED_BASES[15] = Nucleotide.Unknown;
	}
	public BamFileParser(File bamFile) throws IOException {
		if(bamFile ==null){
			throw new NullPointerException("bam file can not be null");
		}
		if(!FileUtil.getExtension(bamFile).equals("bam")){
			throw new IllegalArgumentException("must be .bam file" + bamFile.getAbsolutePath());
		}
		if(!bamFile.exists()){
			throw new FileNotFoundException(bamFile.getAbsolutePath());
		}
		if(!bamFile.canRead()){
			throw new IllegalArgumentException("bam file not readable " + bamFile.getAbsolutePath());
		}
		this.bamFile = bamFile;
		validator = ReservedAttributeValidator.INSTANCE;
	}
	@Override
	public boolean canAccept() {
		return true;
	}

	@Override
	public void accept(SamVisitor visitor) throws IOException {
		if(visitor ==null){
			throw new NullPointerException("visitor can not be null");
		}
		OpenAwareInputStream in=null;
		
		try{
			//in= new OpenAwareInputStream(new BgzfInputStream(bamFile));
			//TODO write BgzfInputStream implementation for 
			//java 6 support?
			//GZIPInputStream bug that prevented reading
			//concatenated GZIP blocks was fixed in an early
			//Java 7 release.
			//in = new OpenAwareInputStream(new GZIPInputStream(new FileInputStream(bamFile)));
			in = new OpenAwareInputStream(new ConcatenatedGZipInputStream(new FileInputStream(bamFile)));
			
			verifyMagicNumber(in);
			
			SamHeader.Builder headerBuilder = parseHeader(new TextLineParser(IOUtil.toInputStream(readPascalString(in))));
			int referenceCount = getSignedInt(in);
			//The reference names
			//are only given by 
			//index in the SAM records
			//below, so we will need to
			//keep an array of the names
			//in the correct order so we know
			//what everything is named.
			String[] refNames = new String[referenceCount];
			
			int numReadsSoFar = 0;
			
			for(int i=0; i<referenceCount; i++){
				String name = readPascalString(in);
				refNames[i] = name;
				int length = getSignedInt(in);
				//add ref to header if not yet present
				if(!headerBuilder.hasReferenceSequence(name)){
					headerBuilder.addReferenceSequence(new ReferenceSequence.Builder(name,length)
														.build());
				}
			
				
			}
			SamHeader header = headerBuilder.build();
			visitor.visitHeader(header);
			
			while(in.isOpen()){	
				//next alignment
				int blockSize = getSignedInt(in);
				byte[] buf = new byte[blockSize];
				IOUtil.blockingRead(in, buf);
				InputStream tmp = new ByteArrayInputStream(buf);
				SamRecord.Builder builder = new SamRecord.Builder(header, validator);
				numReadsSoFar++;
				System.out.println(numReadsSoFar);
				
				int refId = getSignedInt(tmp);
				if(refId >=0){
					builder.setReferenceName(refNames[refId]);
				}
				//NOTE bam is 0-based while
				//SAM is 1-based
				builder.setStartPosition(getSignedInt(tmp)+1);
				long binMqReadLength = getUnsignedInt(tmp);
				int bin = (int)((binMqReadLength>>16) & 0xFFFF);
				byte mapQuality = (byte)((binMqReadLength>>8) & 0xFF);
				builder.setMappingQuality(mapQuality);
				int readNameLength = (int)(binMqReadLength & 0xFF);
				
				long flagsNumCigarOps = getUnsignedInt(tmp);
				int bitFlags = (int)((flagsNumCigarOps>>16) & 0xFFFF);
				
				Set<SamRecordFlags> flags = SamRecordFlags.parseFlags(bitFlags);
				builder.setFlags(flags);
				int numCigarOps = (int)(flagsNumCigarOps & 0xFFFF);
				int seqLength = getSignedInt(tmp);
				
				
				int nextRefId = getSignedInt(tmp);
				if(nextRefId >=0){
					builder.setNextReferenceName(refNames[nextRefId]);
				}
				//NOTE bam is 0-based while
				//SAM is 1-based
				builder.setNextPosition(getSignedInt(tmp)+1);
				builder.setObservedTemplateLength(getSignedInt(tmp));
				
				String readId = readNullTerminatedString(tmp, readNameLength);
				builder.setQueryName(readId);
				
				Cigar.Builder cigarBuilder = new Cigar.Builder(numCigarOps);
				if(numCigarOps >0){
					for(int i=0; i<numCigarOps; i++){
						long bits = getUnsignedInt(tmp);
						int opCode = (int)(bits &0xF);
						int length = (int)(bits>>4);
						if(length==0){
							System.out.println("here");
						}
						cigarBuilder.addElement(CigarOperation.parseBinary(opCode), length);
					}
					builder.setCigar(cigarBuilder.build());
				}
				if(seqLength >0){
					
					NucleotideSequence seq = readSequence(tmp,seqLength);
					builder.setSequence(seq);
					builder.setQualities(readQualities(tmp, seqLength));
					
				}
				//bytes read so far
				//8*int32s + char[readNameLength) + int32[numCigarOps] +uint8[(l_seq+1)/2] +char[l_seq])
				int bytesReadSoFar = 32+ 4*numCigarOps + readNameLength+ (seqLength+1)/2+ seqLength;
				//TODO actually parse attributes
				long attributeBytes = blockSize - bytesReadSoFar;
				IOUtil.blockingSkip(tmp, attributeBytes);
				/*
				while(bytesReadSoFar < blockSize){
					//read optional attribute
					char key1 = (char) in.read();
					char key2 = (char) in.read();
					//TODO we need to parse the
					//value based on the stream
					//AND set the value.
					//currently we can only create a type
					//after we have parsed the value.
					//also need to know how many bytes have been parsed
					//in the value to updateBytesReadSoFar
					//
					//char type = ;
					
				//	SamAttributeType type = SamAttributeType.parseType((char) in.read(), value)
				}
				*/
				
				visitor.visitRecord(builder.build());
			}
			visitor.visitEnd();
		}finally{
			IOUtil.closeAndIgnoreErrors(in);
		}
	}
	private NucleotideSequence readSequence(InputStream in, int seqLength) throws IOException {
		byte[] seqBytes = new byte[(seqLength+1)/2];
		IOUtil.blockingRead(in, seqBytes);
		NucleotideSequenceBuilder builder = new NucleotideSequenceBuilder(seqLength);
		//first fully populate all but last byte
		for(int i=0; i<seqBytes.length-1; i++){
			byte value = seqBytes[i];
			try{
			builder.append(ENCODED_BASES[(value>>4) & 0x0F]);
			builder.append(ENCODED_BASES[value & 0x0F]);
			}catch(RuntimeException t){
				System.out.println( " i = " + i + " value = " + value);
				throw t;
			}
		}
		byte lastByte = seqBytes[seqBytes.length-1];
		//for last byte we should always include high nibble
		builder.append(ENCODED_BASES[(lastByte>>4) & 0x0F]);
		//only include lower nibble if we are even
		if(seqLength %2 ==0){
			builder.append(ENCODED_BASES[lastByte & 0x0F]);
		}
		//TODO '=' char not support
		//which is used to mean "same as reference"
		//we would need to link to the reference seq
		//to get those.
		
		return builder.build();
	}
	
	private QualitySequence readQualities(InputStream in, int seqLength) throws IOException{
		byte[] bytes = new byte[seqLength];
		IOUtil.blockingRead(in, bytes);
		if(bytes[0] == -1){
			//check all neg
			for(int i=1; i<bytes.length; i++){
				if(bytes[i] != -1){
					throw new IllegalStateException("invalid qualities some but not all values are set");
				}
			}
			//if we are here all are -1 (not set)
			return null;
		}
		return new QualitySequenceBuilder(bytes).build();
	}
	private long getUnsignedInt(InputStream in) throws IOException {
		return IOUtil.readUnsignedInt(in, Endian.LITTLE);
	}
	private int getSignedInt(InputStream in) throws IOException {
		return (int) IOUtil.readUnsignedInt(in, Endian.LITTLE);
	}
	private void verifyMagicNumber(InputStream in) throws IOException {
		byte[] header = new byte[4];
		IOUtil.blockingRead(in, header);
		byte[] BAM_HEADER = new byte[]{'B','A','M',1};
		
		if(!Arrays.equals(BAM_HEADER, header)){
			throw new IOException("invalid bam magic number header : " + Arrays.toString(header));
		}
	}

	private String readPascalString(InputStream in ) throws IOException{
		int length =getSignedInt(in);
		return readNullTerminatedString(in, length);
	}
	private String readNullTerminatedString(InputStream in, int lengthIncludingNull) throws IOException {
		//TODO spec says char[] does
		//that mean 2 bytes per element?
		byte[] data = new byte[lengthIncludingNull];
		IOUtil.blockingRead(in, data);
		//don't include \0 at end of string
		return new String(data, 0, lengthIncludingNull-1,IOUtil.UTF_8);
		
	}
	
	private String readString(InputStream in, int length) throws IOException {
		//TODO spec says char[] does
		//that mean 2 bytes per element?
		byte[] data = new byte[length];
		IOUtil.blockingRead(in, data);
		return new String(data, IOUtil.UTF_8);
		
	}
	
	
}
