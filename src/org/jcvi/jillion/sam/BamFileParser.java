package org.jcvi.jillion.sam;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Set;

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
import org.jcvi.jillion.sam.attribute.InvalidAttributeException;
import org.jcvi.jillion.sam.attribute.ReservedAttributeValidator;
import org.jcvi.jillion.sam.attribute.SamAttribute;
import org.jcvi.jillion.sam.attribute.SamAttributeKey;
import org.jcvi.jillion.sam.attribute.SamAttributeKeyFactory;
import org.jcvi.jillion.sam.attribute.SamAttributeType;
import org.jcvi.jillion.sam.attribute.SamAttributeValidator;
import org.jcvi.jillion.sam.cigar.Cigar;
import org.jcvi.jillion.sam.cigar.CigarOperation;
import org.jcvi.jillion.sam.header.ReferenceSequence;
import org.jcvi.jillion.sam.header.SamHeader;
/**
 * {@code BamFileParser} is a {@link SamParser}
 * that can parse BAM encoded files.
 * @author dkatzel
 *
 */
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
			in = new OpenAwareInputStream(new ConcatenatedGZipInputStream(new BufferedInputStream(new FileInputStream(bamFile))));
			
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

				int attributeByteLength = (int)( blockSize - bytesReadSoFar);
				if(attributeByteLength >0){
					byte[] attributeBytes = new byte[attributeByteLength];
					IOUtil.blockingRead(in, attributeBytes);
					//IOUtil.blockingSkip(tmp, attributeBytes);
					OpenAwareInputStream attributeStream = new OpenAwareInputStream(new ByteArrayInputStream(attributeBytes));
					while(attributeStream.isOpen()){
						
						SamAttribute attribute = parseAttribute(attributeStream);
						try {
							builder.addAttribute(attribute);
						} catch (InvalidAttributeException e) {
							throw new IOException("invalid attribute " + attribute, e);
						}
					}
				}
				
				
				visitor.visitRecord(builder.build());
			}
			visitor.visitEnd();
		}finally{
			IOUtil.closeAndIgnoreErrors(in);
		}
	}
	private SamAttribute parseAttribute(OpenAwareInputStream in) throws IOException {
		SamAttributeKey key = SamAttributeKeyFactory.getKey((char) in.read(), (char) in.read());
		
		char type = (char) in.read();
		switch(type){
		//all single integer types are actually just SIGNED_INT in SAM
			case 'i' :  return new SamAttribute(key, SamAttributeType.SIGNED_INT,  IOUtil.readSignedInt(in));
			case 'I' : return new SamAttribute(key, SamAttributeType.SIGNED_INT, IOUtil.readUnsignedInt(in));

			case 'Z' : return new SamAttribute(key, SamAttributeType.STRING, readNullTerminatedStringAttribute(in));
			case 'B' : return handleArray(key,in);
			case 'A':
				return new SamAttribute(key, SamAttributeType.PRINTABLE_CHARACTER,  Character.valueOf((char)in.read()));
			case 'c':
				return new SamAttribute(key, SamAttributeType.SIGNED_INT,  in.read());
			case 'C' : return new SamAttribute(key, SamAttributeType.SIGNED_INT,  IOUtil.readUnsignedByte(in));
			case 's' : return new SamAttribute(key, SamAttributeType.SIGNED_INT,  IOUtil.readSignedShort(in));
			case 'S' : return new SamAttribute(key, SamAttributeType.SIGNED_INT,  IOUtil.readUnsignedShort(in));
			
			case 'f' : return new SamAttribute(key, SamAttributeType.FLOAT,  IOUtil.readFloat(in));
			
			
			case 'H' : return new SamAttribute(key, SamAttributeType.BYTE_ARRAY_IN_HEX,  toByteArray(readNullTerminatedStringAttribute(in))); 
			default : throw new IOException("unknown type : " + type);
		}
		
	}
	
	
	private SamAttribute handleArray(SamAttributeKey key, OpenAwareInputStream in) throws IOException {
		char arrayType = (char) in.read();
		int length = IOUtil.readSignedInt(in);
		//for memory packing, we read everything as
		//signed primitives. The SamAttributeType
		//class will handle converting the signed to unsigned
		//values for us without having to take up 2x the memory.
		switch(arrayType){
			case 'i' : return new SamAttribute(key, SamAttributeType.SIGNED_INT_ARRAY, IOUtil.readIntArray(in, length));
			case 'I' : return new SamAttribute(key, SamAttributeType.UNSIGNED_INT_ARRAY, IOUtil.readIntArray(in, length));
			
			case 'c':
				return new SamAttribute(key, SamAttributeType.SIGNED_BYTE_ARRAY, IOUtil.readByteArray(in, length));
			case 'C' : return new SamAttribute(key, SamAttributeType.UNSIGNED_BYTE_ARRAY, IOUtil.readByteArray(in, length));
			case 's' : return new SamAttribute(key, SamAttributeType.SIGNED_SHORT_ARRAY, IOUtil.readShortArray(in, length));
			case 'S' : return new SamAttribute(key, SamAttributeType.UNSIGNED_SHORT_ARRAY, IOUtil.readShortArray(in, length));
			
			case 'f' : return new SamAttribute(key, SamAttributeType.FLOAT_ARRAY, IOUtil.readFloatArray(in, length));
			
			default : throw new IOException("unknown array type : " + arrayType);
		}

	}

	private byte[] toByteArray(String hex) {
		//2 chars per byte
		byte[] array = new byte[hex.length()/2];
		char[] chars = hex.toCharArray();
		for(int i=0; i<chars.length; i+=2){
			array[i] = Byte.parseByte(new String(chars, i, 2),16);
		}
		return array;
	}
	
	private String readNullTerminatedStringAttribute(OpenAwareInputStream in) throws IOException {
		//it looks like Strings are just null terminated
		//the length is not encoded
		//so just keep reading till we get to '\0'
		boolean done = false;
		StringBuilder builder = new StringBuilder();
		do{
			int value = in.read();
			if(value == -1 || value ==0){
				done =true;
			}else{
				builder.append((char)value);
			}
		}while(!done && in.isOpen());
		return builder.toString();
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
	
	
	
}
