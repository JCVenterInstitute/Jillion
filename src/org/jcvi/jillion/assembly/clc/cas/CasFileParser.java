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
/*
 * Created on Oct 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.clc.cas;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jcvi.jillion.assembly.clc.cas.CasFileVisitor.CasVisitorCallback;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.internal.core.io.RandomAccessFileInputStream;
/**
 * {@code CasFileParser} is a {@link CasParser}
 * that can parse CLC .cas formated files
 * that were created using either the 
 * legacy clc_ref_assemble_X programs or the newer
 * clc_mapper program.  Both programs make slightly different
 * versions of cas files that are not backwards compatible.
 * 
 * @author dkatzel
 *
 */
public final class CasFileParser implements CasParser{
	
	/**
	 * The first 7 bytes of the cas file magic number
	 * should be the same for all the different versions
	 * of cas.
	 */
	private static final byte[] CAS_MAGIC_NUMBER_PREFIX = new byte[]{
        (byte)0x43,
        (byte)0x4c,
        (byte)0x43,
        (byte)0x80,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
    };
	
    private  int numberOfBytesForContigPosition,numberOfBytesForContigNumber;
    private  long numberOfReads;
    private CasScoringScheme scoringScheme;
    private final File casFile;
    
    /**
     * Create a new {@link CasParser} instance to parse the given
     * cas formatted file.  Please note, that the file won't
     * actually be parsed until {@link CasParser#parse(CasFileVisitor)}
     * is called.
     * @param casFile the cas formatted file to parse;
     * can not be null and must exist.
     * @return a new {@link CasParser} instance; 
     * will never be null.
     * @throws IOException if the casFile does not exist.
     * @throws NullPointerException if casFile is null.
     */
    public static CasParser create(File casFile) throws IOException{
    	return new CasFileParser(casFile);
    }
    
    private CasFileParser(File file) throws IOException{
    	if(file ==null){
    		throw new NullPointerException("cas file can not be null");
    	}
    	if(!file.exists()){
    		throw new FileNotFoundException(file.getAbsolutePath());
    	}
    	this.casFile = file;
    }
    @Override
    public void parse(CasFileVisitor visitor) throws IOException{
    	parseMetaData(visitor);
    	CasVisitorCallbackImpl callback = new CasVisitorCallbackImpl();
    	CasMatchVisitor matchVisitor = visitor.visitMatches(callback);
    	if(matchVisitor!=null){
    		//need to visit matches
        	visitMatches(callback, matchVisitor);

    	}
    	//done the file
		if(callback.keepParsing()){
			visitor.visitEnd();
		}else{
			visitor.halted();
		}
	}
    	
    @Override
	public boolean canParse() {
		return true;
	}

	private void visitMatches(CasVisitorCallbackImpl callback,
    		CasMatchVisitor visitor) throws IOException {
        DataInputStream dataIn = new DataInputStream(new BufferedInputStream(new FileInputStream(casFile)));
        try{
        IOUtil.blockingSkip(dataIn, 16);
        for(int i=0; callback.keepParsing() && i<numberOfReads; i++){
            byte info = dataIn.readByte();
            boolean hasMatch= (info & 0x01)!=0;
            boolean hasMultipleMatches= (info & 0x02)!=0;
            boolean hasMultipleAlignments= (info & 0x04)!=0;
            boolean isPartOfPair= (info & 0x08)!=0;
            long totalNumberOfMatches=hasMatch?1:0, numberOfReportedAlignments=hasMatch?1:0;
            if(hasMultipleMatches){                
                totalNumberOfMatches = CasUtil.parseByteCountFrom(dataIn) +2;
            }
            if(hasMultipleAlignments){
                numberOfReportedAlignments = CasUtil.parseByteCountFrom(dataIn) +2;
            }
            
            int score=0;
            CasAlignment chosenAlignment=null;
            if(hasMatch){
           
                long numberOfBytesInForThisMatch =CasUtil.parseByteCountFrom(dataIn);
                long contigSequenceId = CasUtil.readCasUnsignedInt(dataIn, this.numberOfBytesForContigNumber);
                long startPosition = CasUtil.readCasUnsignedInt(dataIn, this.numberOfBytesForContigPosition);
                boolean isreverse = dataIn.readBoolean();
                DefaultCasAlignment.Builder builder = new DefaultCasAlignment.Builder(
                                                    contigSequenceId, startPosition, 
                                                    isreverse);
                long count=0;
                
                while(count <numberOfBytesInForThisMatch){
                    short matchValue = CasUtil.readCasUnsignedByte(dataIn);
                    if(matchValue == 255){
                        builder.addPhaseChange(dataIn.readByte());                        
                        count++;
                    }
                    else if(matchValue<128){
                        builder.addRegion(CasAlignmentRegionType.MATCH_MISMATCH, matchValue +1);                        
                    }
                    else if(matchValue<192){
                        builder.addRegion(CasAlignmentRegionType.INSERT, matchValue -127);
                    }
                    else{
                        builder.addRegion(CasAlignmentRegionType.DELETION, matchValue -191);
                    }
                    count++;
                }
                chosenAlignment =builder.build();
            }
            visitor.visitMatch(new DefaultCasMatch(hasMatch, totalNumberOfMatches, numberOfReportedAlignments,
                    isPartOfPair, chosenAlignment,score));
        }
        if(callback.keepParsing()){
        	visitor.visitEnd();
        }else{
        	visitor.halted();
        }
        }finally{
            IOUtil.closeAndIgnoreErrors(dataIn);
        }
        
    }
   
    private void parseMetaData(CasFileVisitor visitor) throws IOException {
    	RandomAccessFile randomAccessFile = new RandomAccessFile(casFile,"r");
    	RandomAccessFileInputStream in=null;
    	try{
			in = new RandomAccessFileInputStream(randomAccessFile);
			byte[] magicNumber = IOUtil.toByteArray(in, 8);
			CasNumberParserStrategy strategy = CasNumberParserStrategy.valueOf(magicNumber);
			// the cas file puts the header at the end of the file
			// perhaps to make it easier to modify later?
			// so we have to skip over all the matches (possibly gigabytes of
			// data)
			BigInteger headerOffset = CasUtil.readCasUnsignedLong(in);
			randomAccessFile.seek(headerOffset.longValue());

			long numberOfContigSequences = parseMetadata(visitor, in, strategy);
			parseProgramInfo(visitor, in);

			parseReferenceFiles(visitor, in, strategy);

			parseReadFiles(visitor, in, strategy);

			parseScore(visitor, in, numberOfContigSequences);
    	 
    	}finally{
    		IOUtil.closeAndIgnoreErrors(in, randomAccessFile);
    	}
        
        
    }

	private void parseScore(CasFileVisitor visitor,
			RandomAccessFileInputStream in, long numberOfContigSequences)
			throws IOException {
		CasScoreType scoreType = CasScoreType.valueOf((byte) in.read());
		if (scoreType != CasScoreType.NO_SCORE) {
			CasAlignmentScoreBuilder alignmentScoreBuilder = new CasAlignmentScoreBuilder()
					.firstInsertion(CasUtil.readCasUnsignedShort(in))
					.insertionExtension(CasUtil.readCasUnsignedShort(in))
					.firstDeletion(CasUtil.readCasUnsignedShort(in))
					.deletionExtension(CasUtil.readCasUnsignedShort(in))
					.match(CasUtil.readCasUnsignedShort(in))
					.transition(CasUtil.readCasUnsignedShort(in))
					.transversion(CasUtil.readCasUnsignedShort(in))
					.unknown(CasUtil.readCasUnsignedShort(in));
			if (scoreType == CasScoreType.COLOR_SPACE_SCORE) {
				alignmentScoreBuilder.colorSpaceError(IOUtil
						.readUnsignedShort(in));
			}
			CasAlignmentScore score = alignmentScoreBuilder.build();
			CasAlignmentType alignmentType = CasAlignmentType
					.valueOf((byte) in.read());
			scoringScheme = new DefaultCasScoringScheme(scoreType, score,
					alignmentType);
			visitor.visitScoringScheme(scoringScheme);
			long maxContigLength = 0;
			for (long i = 0; i < numberOfContigSequences; i++) {
				long contigLength = CasUtil.readCasUnsignedInt(in);
				boolean isCircular = (IOUtil.readUnsignedShort(in) & 0x01) == 1;
				visitor.visitReferenceDescription(new DefaultCasReferenceDescription(
						contigLength, isCircular));
				maxContigLength = Math.max(maxContigLength, contigLength);
			}
			numberOfBytesForContigNumber = CasUtil
					.numberOfBytesRequiredFor(numberOfContigSequences);

			numberOfBytesForContigPosition = CasUtil
					.numberOfBytesRequiredFor(maxContigLength);
			// contig pairs not currently used so ignore them

		}
	}

	private void parseReadFiles(CasFileVisitor visitor,
			RandomAccessFileInputStream in, CasNumberParserStrategy strategy) throws IOException {
		long numberOfReadFiles = CasUtil.parseByteCountFrom(in);
		visitor.visitNumberOfReadFiles(numberOfReadFiles);
		for (long i = 0; i < numberOfReadFiles; i++) {
			boolean twoFiles = (in.read() & 0x01) == 1;
			long numberOfSequencesInFile = strategy.parseNumber(in);
			
			BigInteger residuesInFile = CasUtil.readCasUnsignedLong(in);
			List<String> names = new ArrayList<String>();
			names.add(CasUtil.parseCasStringFrom(in));
			if (twoFiles) {
				names.add(CasUtil.parseCasStringFrom(in));
			}
			visitor.visitReadFileInfo(new DefaultCasFileInfo(names,
					numberOfSequencesInFile, residuesInFile));
		}
	}

	private void parseReferenceFiles(CasFileVisitor visitor,
			RandomAccessFileInputStream in, CasNumberParserStrategy strategy) throws IOException {
		long numberOfContigFiles = CasUtil.parseByteCountFrom(in);
		visitor.visitNumberOfReferenceFiles(numberOfContigFiles);
		for (long i = 0; i < numberOfContigFiles; i++) {
			boolean twoFiles = (in.read() & 0x01) == 1;
			long numberOfSequencesInFile = strategy.parseNumber(in);
			BigInteger residuesInFile = CasUtil.readCasUnsignedLong(in);
			List<String> names = new ArrayList<String>();
			names.add(CasUtil.parseCasStringFrom(in));
			if (twoFiles) {
				names.add(CasUtil.parseCasStringFrom(in));
			}
			visitor.visitReferenceFileInfo(new DefaultCasFileInfo(names,
					numberOfSequencesInFile, residuesInFile));
		}
	}

	private long parseMetadata(CasFileVisitor visitor,
			RandomAccessFileInputStream in, CasNumberParserStrategy strategy) throws IOException {
		long numberOfContigSequences = CasUtil.readCasUnsignedInt(in);
		numberOfReads = strategy.parseNumber(in);
		
		visitor.visitMetaData(numberOfContigSequences, numberOfReads);
		return numberOfContigSequences;
	}

	private void parseProgramInfo(CasFileVisitor visitor,
			RandomAccessFileInputStream in) throws IOException {
		String nameOfAssemblyProgram = CasUtil.parseCasStringFrom(in);
		String version = CasUtil.parseCasStringFrom(in);
		String parameters = CasUtil.parseCasStringFrom(in);
		visitor.visitAssemblyProgramInfo(nameOfAssemblyProgram, version,
				parameters);
	}

    
   
    
    
    private static final class CasVisitorCallbackImpl implements CasVisitorCallback{
    	private final AtomicBoolean keepParsing = new AtomicBoolean(true);
		@Override
		public boolean canCreateMemento() {
			return false;
		}

		@Override
		public CasVisitorMemento createMemento() {
			throw new UnsupportedOperationException("mementos not supported");
		}

		@Override
		public void haltParsing() {
			keepParsing.set(false);			
		}
		
		public boolean keepParsing(){
			return keepParsing.get();
		}
    	
    }
    /**
     * {@code CasNumberParserStrategy} is a Strategy
     * object to handle how different versions of cas files
     * need to parse numbers differently.  Over the years,
     * the different CLC programs have created different
     * versions of cas files which store numbers
     * differently.
     * 
     * @author dkatzel
     *
     */
    private enum CasNumberParserStrategy{
    	/**
    	 * The original version of cas files
    	 * created by the clc_ref_assemble_X programs
    	 * used unsigned ints
    	 * to store numbers (max number is 2^32-1).
    	 */
    	REF_ASSEMBLE{

			@Override
			public long parseNumber(RandomAccessFileInputStream in)
					throws IOException {
				return CasUtil.readCasUnsignedInt(in);
			}
    		
    	},
    	/**
    	 * The version of cas files
    	 * created by the clc_mapper program
    	 * uses unsigned longs
    	 * to store numbers (max number is 2^64-1).
    	 */
    	MAPPER{
    		@Override
			public long parseNumber(RandomAccessFileInputStream in)
					throws IOException {
    			return CasUtil.readCasUnsignedLong(in).longValue();
			}
    	};
    	
    	public abstract long parseNumber(RandomAccessFileInputStream in) throws IOException;
    	
    	/**
    	 * Get the {@link CasNumberParserStrategy} instance to use
    	 * based on the cas file's magic number.
    	 * @param magicNumber the first 8 bytes in a cas file.
    	 * @return a {@link CasNumberParserStrategy} instance;
    	 * will never be null.
    	 */
    	public static CasNumberParserStrategy valueOf(byte[] magicNumber){
        	
        	for(int i=0; i<CAS_MAGIC_NUMBER_PREFIX.length; i++){
        		if(CAS_MAGIC_NUMBER_PREFIX[i] != magicNumber[i]){
        			 throw new IllegalArgumentException("input stream not a valid cas file wrong magic number expected : " + Arrays.toString(CAS_MAGIC_NUMBER_PREFIX) + " but was "+Arrays.toString(magicNumber));
        		      
        		}
        	}
        	switch(magicNumber[7]){
	        	case 1: return REF_ASSEMBLE;
	        	case 3 : return MAPPER;
	        	default :
	        		throw new IllegalArgumentException("unknown cas file format version magic number = "+ Arrays.toString(magicNumber));
        	}
        	
        }
    }
}
