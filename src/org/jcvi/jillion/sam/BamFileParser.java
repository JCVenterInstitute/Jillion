/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
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
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

import org.jcvi.jillion.core.Range;
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
import org.jcvi.jillion.sam.header.SamHeader;
import org.jcvi.jillion.sam.header.SamHeaderBuilder;
import org.jcvi.jillion.sam.header.SamReferenceSequenceBuilder;
/**
 * {@code BamFileParser} is a {@link SamParser}
 * that can parse BAM encoded files.
 * @author dkatzel
 *
 */
class BamFileParser extends AbstractSamFileParser {

	
	private static final VirtualFileOffset BEGINNING_OF_FILE = new VirtualFileOffset(0);
	protected final File bamFile;
	protected final SamAttributeValidator validator;
	protected final String[] refNames;
	protected final SamHeader header;
	
	
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
		
		try(BgzfInputStream in = new BgzfInputStream(bamFile)){
			
			verifyMagicNumber(in);
			
			SamHeaderBuilder headerBuilder = parseHeader(new TextLineParser(IOUtil.toInputStream(readPascalString(in))));
			refNames = parseReferenceNamesAndAddToHeader(in, headerBuilder);
			header = headerBuilder.build();
		}
	}
	
	
	
	@Override
	public SamHeader getHeader() throws IOException {
		return header;
	}
	@Override
	public boolean canParse() {
		return true;
	}
	
	
	private void verifyReferenceInHeader(String referenceName){
		Objects.requireNonNull(referenceName);
		if(header.getReferenceSequence(referenceName) == null){
			throw new IllegalArgumentException("no reference with name '"+ referenceName +"' contained in Bam file");
		}
	}
	
	@Override
	public void parse(String referenceName, SamVisitor visitor) throws IOException {
		verifyReferenceInHeader(referenceName);
		accept(visitor, SamUtil.alignsToReference(referenceName));		
	}
	@Override
	public void parse(String referenceName, Range alignmentRange, SamVisitor visitor) throws IOException {
		verifyReferenceInHeader(referenceName);
		accept(visitor, SamUtil.alignsToReference(referenceName, alignmentRange));		
	}
	
	@Override
	public void parse(SamVisitor visitor) throws IOException {
		accept(visitor, (record)->true);
	}
	
	@Override
	public void parse(SamVisitor visitor, SamVisitorMemento memento) throws IOException {
		Objects.requireNonNull(visitor);
		Objects.requireNonNull(memento);
		
		if( !(memento instanceof BamFileMemento)){
			throw new IllegalArgumentException("memento must be for bam file");
		}
		BamFileMemento bamMemento = (BamFileMemento)memento;
		if(this != bamMemento.parserInstance){
			throw new IllegalArgumentException("memento must be for this exact bam parser instance");
		}
		
		if(bamMemento.encodedFileOffset ==0){
			//start from beginning
			parse(visitor);
			return;
		}
		
		VirtualFileOffset vfs = new VirtualFileOffset(bamMemento.encodedFileOffset);
		
		
		
		try(BgzfInputStream in = BgzfInputStream.create(bamFile, vfs)){
			AtomicBoolean keepParsing = new AtomicBoolean(true);

			parseBamRecords(visitor, (record)->true, (v)->true, in, keepParsing);
		}
		
	}
	
	
	
	private void accept(SamVisitor visitor, Predicate<SamRecordI> filter) throws IOException {
		if(visitor ==null){
			throw new NullPointerException("visitor can not be null");
		}
		BgzfInputStream in=null;
		
		try{
			in = new BgzfInputStream(bamFile);			
			
			parseBamFromBeginning(visitor, filter, (vfs)->true, in);
		}finally{
			IOUtil.closeAndIgnoreErrors(in);
		}
	}
	
	protected void parseBamFromBeginning(SamVisitor visitor, Predicate<SamRecordI> filter, Predicate<VirtualFileOffset> keepParsingPredicate, BgzfInputStream in) throws IOException {
		verifyMagicNumber(in);
		//have to keep parsing header again for now
		//since it updates the file pointer in our bgzf stream
		//probably not worth seeking/skipping for now...
		SamHeaderBuilder headerBuilder = parseHeader(new TextLineParser(IOUtil.toInputStream(readPascalString(in))));
		
		parseReferenceNamesAndAddToHeader(in, headerBuilder);
		AtomicBoolean keepParsing = new AtomicBoolean(true);

		visitor.visitHeader(new BamCallback(keepParsing), header);
		
		parseBamRecords(visitor, filter, (vfs)->true, in, keepParsing);
	}
	
	protected void parseBamRecords(SamVisitor visitor, Predicate<SamRecordI> filter, Predicate<VirtualFileOffset> keepParsingPredicate, BgzfInputStream in, AtomicBoolean keepParsing) throws IOException {
		
		boolean canceledByPredicate=false;
		
		try{
			VirtualFileOffset start = in.getCurrentVirutalFileOffset();
			while(keepParsing.get() && in.hasMoreData()){	
				SamRecord record = parseNextSamRecord(in, refNames, header);
				
				VirtualFileOffset end = in.getCurrentVirutalFileOffset();
				if(keepParsingPredicate.test(start)){
					if(filter.test(record)){
						visitor.visitRecord(new BamCallback(keepParsing, start), 
										record, start,end);
					}
				}else{
					keepParsing.set(false);
					canceledByPredicate=true;
				}
				
				//update start to be old end
				start = end;
			}
		}catch(EOFException e){
			//ignore, we can't tell if we've hit
			//EOF until after we hit it otherwise
			//we will mess up the offset computations
		}
		if(canceledByPredicate || keepParsing.get()){
			visitor.visitEnd();
		}else{
			visitor.halted();
		}
	}
	
	private SamRecord parseNextSamRecord(InputStream in, String[] refNames, SamHeader header) throws IOException {
		//next alignment
		int blockSize = getSignedInt(in);
		SamRecordBuilder builder = new SamRecordBuilder(header, validator);
		
		int refId = getSignedInt(in);
		if(refId >=0){
			builder.setReferenceName(refNames[refId]);
		}
		//NOTE bam is 0-based while
		//SAM is 1-based
		int startPos = getSignedInt(in)+1;
		builder.setStartPosition(startPos);
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
			SamRecordBuilder builder, int blockSize, int bytesReadSoFar)
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
			SamHeaderBuilder headerBuilder) throws IOException {
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
				headerBuilder.addReferenceSequence(new SamReferenceSequenceBuilder(name,length)
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
			case 'I' : return new SamAttribute(key, SamAttributeType.UNSIGNED_INT, IOUtil.readUnsignedInt(in));

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
			//assume all values are negative
			/*
			//check all neg
			for(int i=1; i<bytes.length; i++){
				if(bytes[i] != -1){
					throw new IllegalStateException("invalid qualities some but not all values are set");
				}
			}
			*/
			//if we are here all are -1 (not set)
			return null;
		}
		return new QualitySequenceBuilder(bytes)
					//we turn off data compression since we
					//usually stream through millions of these records and
					//often throw the results away
					//so we don't care if temporarily we take up more memory
					.turnOffDataCompression(true)
					.build();
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
		if(lengthIncludingNull ==0){
			return "";
		}
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
			this(keepParsing,BEGINNING_OF_FILE);
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
