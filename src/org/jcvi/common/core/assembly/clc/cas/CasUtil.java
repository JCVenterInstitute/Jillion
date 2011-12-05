/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Oct 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.clc.cas;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.contig.cas.align.CasAlignment;
import org.jcvi.common.core.assembly.contig.cas.align.CasAlignmentRegion;
import org.jcvi.common.core.assembly.contig.cas.align.CasAlignmentRegionType;
import org.jcvi.common.core.assembly.contig.cas.read.AbstractCasFileNucleotideDataStore;
import org.jcvi.common.core.assembly.contig.cas.read.CasPlacedRead;
import org.jcvi.common.core.assembly.contig.cas.read.DefaultCasPlacedReadFromCasAlignmentBuilder;
import org.jcvi.common.core.assembly.contig.cas.read.FastaCasDataStoreFactory;
import org.jcvi.common.core.assembly.contig.cas.read.ReferenceCasFileNucleotideDataStore;
import org.jcvi.common.core.assembly.trim.SffTrimDataStoreBuilder;
import org.jcvi.common.core.assembly.util.trim.TrimDataStore;
import org.jcvi.common.core.datastore.MultipleDataStoreWrapper;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.io.IOUtil.ENDIAN;
import org.jcvi.common.core.seq.fastx.fastq.FastQQualityCodec;
import org.jcvi.common.core.seq.read.trace.pyro.sff.SffParser;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.util.Builder;
import org.jcvi.common.core.util.MultipleWrapper;
/**
 * {@code CasUtil} is a utility class for dealing with the binary
 * encodings inside a .cas file.
 * @author dkatzel
 *
 *
 */
public final class CasUtil {

    private CasUtil(){}
    /**
     * Get the number of bytes required to store the given number.
     * To save space, .cas files use a varible length field to 
     * store counters.  The length of the field depends on the max number
     * to be stored.
     * @param i the number to store.
     * @return the number of bytes needed to store the given 
     * input number as an int (which may be {@code 0}).
     * @throws IllegalArgumentException if {@code i<1}.
     */
    public static int numberOfBytesRequiredFor(long i){
        if(i < 1){
            throw new IllegalArgumentException("input number must be > 0 : " + i);
        }
       
        return (int)Math.ceil(Math.log(i)/Math.log(256));
    }
    /**
     * Parse a byte count from the given {@link InputStream}.
     * To save space, CAS files have a variable length field for byte counts
     * which range from 1 to 5 bytes long.
     * @param in the inputstream to read.
     * @return a byte count as a long; should always be >=0.
     * @throws IOException if there is a problem reading from the inputstream
     * @throws NullPointerException if {@code in == null}.
     */
    public static long parseByteCountFrom(InputStream in) throws IOException{
        
        int firstByte =in.read();
        if(firstByte<254){
            return firstByte;
        }
        
        if(firstByte ==254){
            //read next 2 bytes
           return readCasUnsignedShort(in);
        }
        return readCasUnsignedInt(in);
    }
    /**
     * parse a CAS encoded String from the given {@link InputStream}.
     * CAS files store strings in Pascal like format with 
     * the number of bytes in the string first, followed by the
     * characters in the string, there is no terminating character.
     * @param in the inputstream to parse.
     * @return the next String in the InputStream.
     * @throws IOException if there is a problem reading the String.
     * @throws NullPointerException if {@code in == null}.
     */
    public static String parseCasStringFrom(InputStream in) throws IOException{
        int length = (int)parseByteCountFrom(in);
       
        byte bytes[] = IOUtil.readByteArray(in, length);
        
        return new String(bytes);
        
    }
    /**
     * Read the next unsigned byte in the given inputStream.
     * CAS files are encoded in little endian.
     * @param in the inputstream to parse.
     * @return an unsigned byte as a short.
     * @throws IOException if there is a problem reading the inputStream.
     */
    public static short readCasUnsignedByte(InputStream in) throws IOException{
        return new BigInteger(1,
                 IOUtil.readByteArray(in, 1, ENDIAN.LITTLE)).shortValue();
     }
    /**
     * Read the next unsigned short in the given inputStream.
     * CAS files are encoded in little endian.
     * @param in the inputstream to parse.
     * @return an unsigned short as an int.
     * @throws IOException if there is a problem reading the inputStream.
     */
    public static int readCasUnsignedShort(InputStream in) throws IOException{
        return new BigInteger(1,
                 IOUtil.readByteArray(in, 2, ENDIAN.LITTLE)).intValue();
     }
    /**
     * Read the next unsigned int in the given inputStream.
     * this is the same as {@link #readCasUnsignedInt(InputStream, int)
     * readCasUnsignedInt(in,4)}
     * CAS files are encoded in little endian.
     * @param in the inputstream to parse.
     * @return an unsigned int as an long.
     * @throws IOException if there is a problem reading the inputStream.
     * @see #readCasUnsignedInt(InputStream, int)
     */
    public static long readCasUnsignedInt(InputStream in) throws IOException{
       return readCasUnsignedInt(in, 4);
    }
    /**
     * Read the next X bytes as an unsigned int in the given inputStream.
     * CAS files are encoded in little endian.
     * @param in the inputstream to parse.
     * @param numberOfBytesInNumber number of bytes to read from the inputStream.
     * @return an unsigned int as an long.
     * @throws IOException if there is a problem reading the inputStream.
     * @see #readCasUnsignedInt(InputStream, int)
     */
    public static long readCasUnsignedInt(InputStream in, int numberOfBytesInNumber) throws IOException{
        return new BigInteger(1,
                 IOUtil.readByteArray(in, numberOfBytesInNumber, ENDIAN.LITTLE)).longValue();
     }
    /**
     * Read the next unsigned long in the given inputStream.
     * CAS files are encoded in little endian.
     * @param in the inputstream to parse.
     * @return an unsigned long as an {@link BigInteger}; never null.
     * @throws IOException if there is a problem reading the inputStream.
     */
    public static BigInteger readCasUnsignedLong(InputStream in) throws IOException{
        return new BigInteger(1,
                 IOUtil.readByteArray(in, 8, ENDIAN.LITTLE));
     }
    
    
    public static CasPlacedRead createCasPlacedRead(CasMatch match,String readId,
            NucleotideSequence fullLengthReadBasecalls, Range traceTrimRange,
            NucleotideSequence gappedReference){
		CasAlignment alignment = match.getChosenAlignment();
        
        DefaultCasPlacedReadFromCasAlignmentBuilder builder;
        if(readId.equals("SOLEXA3_0041_FC:4:100:11098:11755#TAGCTT/2")){
            System.out.println("here");
        }
        long ungappedStartOffset = alignment.getStartOfMatch();
        long gappedStartOffset = gappedReference.getGappedOffsetFor((int)ungappedStartOffset);
        builder = new DefaultCasPlacedReadFromCasAlignmentBuilder(readId,
        		fullLengthReadBasecalls,
                alignment.readIsReversed(),
                gappedStartOffset,
                traceTrimRange
               );
        List<CasAlignmentRegion> regionsToConsider = new ArrayList<CasAlignmentRegion>(alignment.getAlignmentRegions());
        int lastIndex = regionsToConsider.size()-1;
        if(regionsToConsider.get(lastIndex).getType()==CasAlignmentRegionType.INSERT){
            regionsToConsider.remove(lastIndex);
        }
        
        try{
            builder.addAlignmentRegions(regionsToConsider,gappedReference);
        }catch(Throwable t){
            System.err.println("error computing alignment regions for "+ readId);
            throw new RuntimeException(t);
        }
        
        return builder.build();
	}

    /**
     * Get the java File object for a filepath in a cas file.
     * @param workingDir the working directory this cas file was
     * created in (usually the same location as the cas file itself).
     * @param filePath the path to the file which may or may not
     * be relative.
     * @return a new File object that represents the file.
     * @throws FileNotFoundException if the file does not exist.
     */
    public static File getFileFor(File workingDir,String filePath) throws FileNotFoundException {
        boolean isAbsolutePath = filePath.charAt(0) == File.separatorChar;
        final File dataStoreFile;
        if(isAbsolutePath){
            dataStoreFile = new File(filePath);
        }else{
            dataStoreFile = new File(workingDir, filePath);
        }            
         
        if(!dataStoreFile.exists()){
            throw new FileNotFoundException(dataStoreFile.getAbsolutePath());
        }
        return dataStoreFile;
    }
    public static CasInfoBuilder createCasInfoBuilder(File casFile){
        return new CasInfoBuilder(casFile);
    }
    
    public final static class CasInfoBuilder implements Builder<CasInfo>{
        private final File casFile;
        private FastQQualityCodec fastqQualityCodec = FastQQualityCodec.SANGER;
        private ExternalTrimInfo externalTrimInfo = ExternalTrimInfo.createEmptyInfo();
        private boolean hasEdits=false;
        private File chromatDir = null;
        
        
        private CasInfoBuilder(File casFile){
            if(casFile ==null){
                throw new NullPointerException("cas file can not be null");
            }
            this.casFile = casFile;
        }
        
        public CasInfoBuilder fastQQualityCodec(FastQQualityCodec fastqQualityCodec){
            if(fastqQualityCodec ==null){
                throw new NullPointerException("fastq quality codec can not be null");
            }
            this.fastqQualityCodec = fastqQualityCodec;
            return this;
        }
        public CasInfoBuilder externalTrimInfo(ExternalTrimInfo externalTrimInfo){
            if(externalTrimInfo ==null){
                throw new NullPointerException("externalTrimInfo can not be null");
            }
            this.externalTrimInfo = externalTrimInfo;
            return this;
        }
        public CasInfoBuilder chromatDir(File chromatDir){            
            this.chromatDir = chromatDir;
            return this;
        }
        public CasInfoBuilder hasEdits(boolean hasEdits){
            this.hasEdits = hasEdits;
            return this;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public CasInfo build() {
            try {
                return new CasInfoImpl(casFile, fastqQualityCodec, externalTrimInfo, chromatDir, hasEdits);
            } catch (IOException e) {
                throw new IllegalStateException("error building cas info",e);
            }
        }
        
    }
    
    private static class CasInfoImpl implements CasInfo{

        private final TrimDataStore multiTrimDataStore;
        private final TraceDetails traceDetails;
        private final File workingDirectory;
        private final List<NucleotideSequence> orderedGappedReferences;
        private final CasTrimMap casTrimMap;
        private final CasIdLookup referenceIdLookup;
        
        private CasInfoImpl(File casFile, FastQQualityCodec fastqQualityCodec, ExternalTrimInfo externalTrimInfo,
                File chromatDir, boolean hasEdits) throws IOException{
            final File casWorkingDirectory = casFile.getParentFile();
            final AbstractDefaultCasFileLookup referenceIdLookup = new DefaultReferenceCasFileLookup(casWorkingDirectory);
            
            
            AbstractCasFileNucleotideDataStore referenceNucleotideDataStore = new ReferenceCasFileNucleotideDataStore(
                    new FastaCasDataStoreFactory(casWorkingDirectory,externalTrimInfo.getCasTrimMap(),10));
            DefaultCasGappedReferenceMap gappedReferenceMap = new DefaultCasGappedReferenceMap(referenceNucleotideDataStore, referenceIdLookup);
            CasParser.parseCas(casFile, 
                    MultipleWrapper.createMultipleWrapper(CasFileVisitor.class,
                    referenceIdLookup,referenceNucleotideDataStore,gappedReferenceMap
                    ));
            final SffTrimDataStoreBuilder sffTrimDatastoreBuilder = new SffTrimDataStoreBuilder();
            CasFileVisitor sffTrimDataStoreVisitor  =new AbstractOnePassCasFileVisitor() {
               
               @Override
               protected void visitMatch(CasMatch match, long readCounter) {
                   //no-op
               }

               @Override
               public synchronized void visitReadFileInfo(CasFileInfo readFileInfo) {
                   super.visitReadFileInfo(readFileInfo);
                   for(String readFilename : readFileInfo.getFileNames()){
                           String extension =FilenameUtils.getExtension(readFilename);
                           if("sff".equals(extension)){
                               try {
                                   SffParser.parseSFF(new File(casWorkingDirectory,readFilename), sffTrimDatastoreBuilder);
                               } catch (Exception e) {
                                   throw new IllegalStateException("error trying to read sff file " + readFilename,e);
                               } 
                           }
                       }
                   }
               
           };
           CasParser.parseOnlyMetaData(casFile, sffTrimDataStoreVisitor);
           multiTrimDataStore =MultipleDataStoreWrapper.createMultipleDataStoreWrapper(
                   TrimDataStore.class, externalTrimInfo.getTrimDataStore(), sffTrimDatastoreBuilder.build());
           
           traceDetails = new TraceDetails.Builder(fastqQualityCodec)
                       .chromatDir(chromatDir)
                       .hasEdits(hasEdits)
                       .build();
           this.orderedGappedReferences = gappedReferenceMap.getOrderedList();
           this.workingDirectory = casWorkingDirectory;
           this.casTrimMap = externalTrimInfo.getCasTrimMap();
           this.referenceIdLookup = referenceIdLookup;
        }
       
        
        /**
         * @return the multiTrimDataStore
         */
        @Override
        public TrimDataStore getMultiTrimDataStore() {
            return multiTrimDataStore;
        }
        /**
         * @return the traceDetails
         */
        @Override
        public TraceDetails getTraceDetails() {
            return traceDetails;
        }


        /**
        * {@inheritDoc}
        */
        @Override
        public List<NucleotideSequence> getOrderedGappedReferenceList() {
            return orderedGappedReferences;
        }


        /**
        * {@inheritDoc}
        */
        @Override
        public File getCasWorkingDirectory() {
            return workingDirectory;
        }


        /**
        * {@inheritDoc}
        */
        @Override
        public CasTrimMap getCasTrimMap() {
            return casTrimMap;
        }


        /**
        * {@inheritDoc}
        */
        @Override
        public CasIdLookup getReferenceIdLookup() {
            return referenceIdLookup;
        }
    }
}
