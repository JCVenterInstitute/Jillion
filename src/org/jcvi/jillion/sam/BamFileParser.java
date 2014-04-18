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

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jcvi.jillion.core.io.FileUtil;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.internal.core.io.OpenAwareInputStream;
import org.jcvi.jillion.internal.core.io.TextLineParser;
import org.jcvi.jillion.internal.sam.SamUtil;
import org.jcvi.jillion.sam.SamVisitor.SamVisitorCallback.SamVisitorMemento;
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
final class BamFileParser extends AbstractSamFileParser {

	
	
	private final File bamFile;
	private final SamAttributeValidator validator;
	
	
	
	public BamFileParser(File bamFile) throws IOException {
		this(bamFile, ReservedAttributeValidator.INSTANCE);
	}
	public BamFileParser(File bamFile, SamAttributeValidator validator) throws IOException {
		if(bamFile ==null){
			throw new NullPointerException("bam file can not be null");
		}
		if(!"bam".equals(FileUtil.getExtension(bamFile))){
			throw new IllegalArgumentException("must be .bam file" + bamFile.getAbsolutePath());
		}
		if(!bamFile.exists()){
			throw new FileNotFoundException(bamFile.getAbsolutePath());
		}
		if(!bamFile.canRead()){
			throw new IllegalArgumentException("bam file not readable " + bamFile.getAbsolutePath());
		}
		if(validator ==null){
			throw new NullPointerException("validator can not be null");
		}
		this.bamFile = bamFile;
		this.validator = validator;
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
		BgzfInputStream in=null;
		
		try{
			in = new BgzfInputStream(bamFile);
			
			verifyMagicNumber(in);
			
			SamHeader.Builder headerBuilder = parseHeader(new TextLineParser(IOUtil.toInputStream(readPascalString(in))));
			String[] refNames = parseReferenceNamesAndAddToHeader(in, headerBuilder);
			SamHeader header = headerBuilder.build();
			AtomicBoolean keepParsing = new AtomicBoolean(true);

			visitor.visitHeader(new BamCallback(keepParsing), header);
			try{
				while(keepParsing.get() && in.hasMoreData()){	
					VirtualFileOffset start = in.getVirutalFileOffset();
					SamRecord record = parseNextSamRecord(in, refNames, header);
					
					VirtualFileOffset end = in.getVirutalFileOffset();
					visitor.visitRecord(new BamCallback(keepParsing, start), 
										record, start,end);
				}
			}catch(EOFException e){
				//ignore, we can't tell if we've hit
				//EOF until after we hit it otherwise
				//we will mess up the offset computations
			}
			if(keepParsing.get()){
				visitor.visitEnd();
			}else{
				visitor.halted();
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(in);
		}
	}
	
	private SamRecord parseNextSamRecord(InputStream in, String[] refNames, SamHeader header) throws IOException {
		//next alignment
		int blockSize = getSignedInt(in);
		SamRecord.Builder builder = new SamRecord.Builder(header, validator);
		
		int refId = getSignedInt(in);
		if(refId >=0){
			builder.setReferenceName(refNames[refId]);
		}
		//NOTE bam is 0-based while
		//SAM is 1-based
		builder.setStartPosition(getSignedInt(in)+1);
		
		long binMqReadLength = getUnsignedInt(in);
		//don't care about bin we can recompute it 
		//if we need it
		//int bin = (int)((binMqReadLength>>16) & 0xFFFF);
		byte mapQuality = (byte)((binMqReadLength>>8) & 0xFF);
		builder.setMappingQuality(mapQuality);
		int readNameLength = (int)(binMqReadLength & 0xFF);
		
		long flagsNumCigarOps = getUnsignedInt(in);
		int bitFlags = (int)((flagsNumCigarOps>>16) & 0xFFFF);
		
		Set<SamRecordFlags> flags = SamRecordFlags.parseFlags(bitFlags);
		builder.setFlags(flags);
		int numCigarOps = (int)(flagsNumCigarOps & 0xFFFF);
		int seqLength = getSignedInt(in);
		
		
		int nextRefId = getSignedInt(in);
		if(nextRefId >=0){
			builder.setNextReferenceName(refNames[nextRefId]);
		}
		//NOTE bam is 0-based while
		//SAM is 1-based
		builder.setNextPosition(getSignedInt(in)+1);
		builder.setObservedTemplateLength(getSignedInt(in));
		
		String readId = readNullTerminatedString(in, readNameLength);
		builder.setQueryName(readId);
		
		
		if(numCigarOps >0){
			Cigar cigar = parseCigar(in, numCigarOps);
			builder.setCigar(cigar);
		}
		if(seqLength >0){			
			NucleotideSequence seq = SamUtil.readBamEncodedSequence(in,seqLength);
			builder.setSequence(seq);
			builder.setQualities(readQualities(in, seqLength));			
		}
		//bytes read so far
		//8*int32s + char[readNameLength) + int32[numCigarOps] +uint8[(l_seq+1)/2] +char[l_seq])
		int bytesReadSoFar = 32+ 4*numCigarOps + readNameLength+ (seqLength+1)/2+ seqLength;

		parseAttributesIfAnyAndAddToBuilder(in, builder, blockSize,	bytesReadSoFar);
		
		
		return builder.build();
	}
	private Cigar parseCigar(InputStream in, int numCigarOps)
			throws IOException {
		Cigar.Builder cigarBuilder = new Cigar.Builder(numCigarOps);
		for(int i=0; i<numCigarOps; i++){
			long bits = getUnsignedInt(in);
			int opCode = (int)(bits &0xF);
			int length = (int)(bits>>4);
			cigarBuilder.addElement(CigarOperation.parseBinary(opCode), length);
		}
		return cigarBuilder.build();
	}
	private void parseAttributesIfAnyAndAddToBuilder(InputStream in,
			SamRecord.Builder builder, int blockSize, int bytesReadSoFar)
			throws IOException {
		int attributeByteLength =  blockSize - bytesReadSoFar;
		if(attributeByteLength >0){
			//to simplify parsing
			//to slurp up the all the bytes for the attributes
			//and read through them as a new InputStream
			byte[] attributeBytes = new byte[attributeByteLength];
			IOUtil.blockingRead(in, attributeBytes);
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
	}
	private String[] parseReferenceNamesAndAddToHeader(InputStream in,
			SamHeader.Builder headerBuilder) throws IOException {
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
		return refNames;
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
		return IOUtil.readUnsignedInt(in, ByteOrder.LITTLE_ENDIAN);
	}
	private int getSignedInt(InputStream in) throws IOException {
		return (int) IOUtil.readUnsignedInt(in, ByteOrder.LITTLE_ENDIAN);
	}
	private void verifyMagicNumber(InputStream in) throws IOException {
		byte[] header = new byte[4];
		IOUtil.blockingRead(in, header);
		
		if(!SamUtil.matchesBamMagicNumber(header)){
			throw new IOException("invalid bam magic number header : " + Arrays.toString(header));
		}
	}

	private String readPascalString(InputStream in ) throws IOException{
		int length =getSignedInt(in);
		return readNullTerminatedString(in, length);
	}
	private String readNullTerminatedString(InputStream in, int lengthIncludingNull) throws IOException {
		//TODO spec says char[] does
		//but looks like ASCII bytes so 1 byte per char?
		byte[] data = new byte[lengthIncludingNull];
		IOUtil.blockingRead(in, data);
		//don't include \0 at end of string
		return new String(data, 0, lengthIncludingNull-1,IOUtil.UTF_8);
		
	}
	
	
	private final class BamCallback extends AbstractCallback{
		private final long encodedFileOffset;
		
		public BamCallback(AtomicBoolean keepParsing){
			this(keepParsing, VirtualFileOffset.create(0, 0));
		}
		public BamCallback(AtomicBoolean keepParsing, VirtualFileOffset currentPosition) {
			super(keepParsing);
			this.encodedFileOffset = currentPosition.getEncodedValue();
		}
		
		

		@Override
		public boolean canCreateMemento() {
			return true;
		}

		@Override
		public SamVisitorMemento createMemento() {
			return new BamFileMemento(BamFileParser.this, encodedFileOffset);
		}
		
	}
	
	private static final class BamFileMemento implements SamVisitorMemento{
		private final BamFileParser parserInstance;
		private final long encodedFileOffset;
		
		public BamFileMemento(BamFileParser parserInstance, long position) {
			this.parserInstance = parserInstance;
			this.encodedFileOffset = position;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime
					* result
					+ ((parserInstance == null) ? 0 : parserInstance.hashCode());
			result = prime * result + (int) (encodedFileOffset ^ (encodedFileOffset >>> 32));
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof BamFileMemento)) {
				return false;
			}
			BamFileMemento other = (BamFileMemento) obj;
			//has to be EXACT same instance
			if (parserInstance != other.parserInstance) {
				return false;
			}
			if (encodedFileOffset != other.encodedFileOffset) {
				return false;
			}
			return true;
		}
		
	}
	
	
}
