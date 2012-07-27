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

package org.jcvi.common.core.assembly.asm;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jcvi.common.core.DirectedRange;
import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.asm.AsmVisitor.NestedContigMessageTypes;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.read.trace.frg.FragmentDataStore;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.core.util.DefaultIndexedFileRange;
import org.jcvi.common.core.util.IndexedFileRange;
import org.jcvi.common.core.util.MapUtil;
import org.jcvi.common.core.util.iter.StreamingIterator;

/**
 * {@code IndexedAsmContigDataStore} is an {@link AsmContigDataStore}
 * implementation that only stores indexes and range offsets
 * of contigs in the input asm file.  This allows large files to provide random 
 * access without taking up much memory.  The downside is each contig
 * must be re-parsed each time and the asm file must exist and not
 * get altered during the entire lifetime of this object.
 * @author dkatzel
 */
public final class IndexedAsmContigDataStore implements AsmContigDataStore{
    private final File asmFile;
    private final IndexedFileRange fileRange;
    private final FragmentDataStore frgDataStore;
    private final Range afgRange;
    
    public static AsmContigDataStoreBuilder createBuilder(File asmFile, FragmentDataStore frgDataStore){
        return new IndexedAsmContigDataStoreBuilder(asmFile,frgDataStore,IncludeType.ALL, new DefaultIndexedFileRange());
    }
    public static AsmContigDataStoreBuilder createDegenerateBuilder(File asmFile,FragmentDataStore frgDataStore){
        return new IndexedAsmContigDataStoreBuilder(asmFile,frgDataStore,IncludeType.DEGENERATE_ONLY,new DefaultIndexedFileRange());
    }
    public static AsmContigDataStoreBuilder createPlacedBuilder(File asmFile,FragmentDataStore frgDataStore){
        return new IndexedAsmContigDataStoreBuilder(asmFile,frgDataStore,IncludeType.PLACED,new DefaultIndexedFileRange());
    }
    /**
     * Create an {@link AsmContigDataStore} of all the contigs for the given
     * asm file which assembled the given fragments in the fragmentDataStore.
     * @param asmFile the asm file to parse.
     * @param frgDataStore the {@link FragmentDataStore} containing the frgs
     * used in the assembly (includes basecalls and qualities of all input reads).
     * @return a new {@link AsmContigDataStore}, never null.
     * @throws IOException if there was a problem parsing the asm file.
     */
    public static AsmContigDataStore createDataStore(File asmFile, FragmentDataStore frgDataStore) throws IOException{
        AsmContigDataStoreBuilder builder =  new IndexedAsmContigDataStoreBuilder(asmFile,frgDataStore,IncludeType.ALL,new DefaultIndexedFileRange());
        AsmParser.parseAsm(asmFile, builder);
        return builder.build();
    }
    /**
     * Create an {@link AsmContigDataStore} of only the degenerate contigs 
     * (contigs not placed in any scaffold) for the given
     * asm file which assembled the given fragments in the fragmentDataStore.
     * @param asmFile the asm file to parse.
     * @param frgDataStore the {@link FragmentDataStore} containing the frgs
     * used in the assembly (includes basecalls and qualities of all input reads).
     * @return a new {@link AsmContigDataStore}, never null.
     * @throws IOException if there was a problem parsing the asm file.
     */
    public static AsmContigDataStore createDegenerateDataStore(File asmFile, FragmentDataStore frgDataStore) throws IOException{
        AsmContigDataStoreBuilder builder =  new IndexedAsmContigDataStoreBuilder(asmFile,frgDataStore,IncludeType.DEGENERATE_ONLY,new DefaultIndexedFileRange());
        AsmParser.parseAsm(asmFile, builder);
        return builder.build();
    }
    /**
     * Create an {@link AsmContigDataStore} of only the placed contigs 
     * (contigs that have been placed in any scaffold) for the given
     * asm file which assembled the given fragments in the fragmentDataStore.
     * @param asmFile the asm file to parse.
     * @param frgDataStore the {@link FragmentDataStore} containing the frgs
     * used in the assembly (includes basecalls and qualities of all input reads).
     * @return a new {@link AsmContigDataStore}, never null.
     * @throws IOException if there was a problem parsing the asm file.
     */
    public static AsmContigDataStore createPlacedDataStore(File asmFile, FragmentDataStore frgDataStore) throws IOException{
        AsmContigDataStoreBuilder builder =  new IndexedAsmContigDataStoreBuilder(asmFile,frgDataStore,IncludeType.PLACED,new DefaultIndexedFileRange());
        AsmParser.parseAsm(asmFile, builder);
        return builder.build();
    }
    
    
    private enum IncludeType{
        ALL{
            @Override
            boolean include(boolean isDegenerate) {
                return true;
            }            
        },
        DEGENERATE_ONLY{
            @Override
            boolean include(boolean isDegenerate) {
                return isDegenerate;
            }            
        },
        PLACED{
            @Override
            boolean include(boolean isDegenerate) {
                return !isDegenerate;
            }            
        }
        ;
        
        abstract boolean include(boolean isDegenerate);
    }
    private IndexedAsmContigDataStore(File asmFile, IndexedFileRange fileRange,  Range afgRange, FragmentDataStore frgDataStore) {
        this.asmFile = asmFile;
        this.fileRange = fileRange;
        this.frgDataStore = frgDataStore;
        this.afgRange = afgRange;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public StreamingIterator<String> idIterator() throws DataStoreException {
        return fileRange.getIds();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public AsmContig get(String contigId) throws DataStoreException {
        Range contigRange = fileRange.getRangeFor(contigId);
        try{
            //To save memory we only want to store clear ranges 
            //of only the reads in the contig we are asked to get.
            //We can do this by parsing the contig message twice.
            //1. First pass - we get the read ids in the contig
            //2. parse AFG block to get clear ranges of all the reads
            //   and store only the ones we need for this contig.
            //3. Second Pass - reparse the contig and consult 
            //   frg datastore and clear ranges to actually build contig
            InputStream afgStream = IOUtil.createInputStreamFromFile(asmFile,afgRange);        
            InputStream firstPassContigStream = IOUtil.createInputStreamFromFile(asmFile,contigRange);
            
            ReadMapVisitor readMapVisitor = new ReadMapVisitor(contigId);
            AsmParser.parseAsm(firstPassContigStream, readMapVisitor);
            //read map has dummy values, we only care about keys
            //actual values will be set later, we just build the map
            //here to save memory so we don't have a map AND a set
            Map<String,Range> readMap =readMapVisitor.getReadMap();
            SingleContigVisitor contigVisitor = new SingleContigVisitor(contigId, readMap, frgDataStore);
            AsmParser.parseAsm(afgStream, contigVisitor);
            InputStream secondPassContigStream =IOUtil.createInputStreamFromFile(asmFile,contigRange);
            AsmParser.parseAsm(secondPassContigStream, contigVisitor);
            return contigVisitor.getContig();
        }catch(IOException e){
            throw new DataStoreException("error getting contig data for contig id "+ contigId , e);
        }
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean contains(String id) throws DataStoreException {
        return fileRange.contains(id);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public long getNumberOfRecords() throws DataStoreException {
        return fileRange.size();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isClosed() throws DataStoreException {
        return fileRange.isClosed();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void close() throws IOException {
        fileRange.close();
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public StreamingIterator<AsmContig> iterator() {

        try {
            return new AsmContigIterator(idIterator());
        } catch (DataStoreException e) {
            throw new IllegalStateException("could not create iterator",e);
        }
    }
    
    private class AsmContigIterator implements StreamingIterator<AsmContig>{
        private final StreamingIterator<String> idIterator;
        
        public AsmContigIterator(StreamingIterator<String> idIterator){
            this.idIterator = idIterator;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove not supported");
            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public boolean hasNext() {
            return idIterator.hasNext();
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void close() throws IOException {
            idIterator.close();
            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public AsmContig next() {
            String next = idIterator.next();
            try {
                return get(next);
            } catch (DataStoreException e) {
               throw new IllegalStateException("error getting next contig: " + next,e);
            }
        }
        
        
    }
    
    private static class IndexedAsmContigDataStoreBuilder extends AbstractAsmVisitor implements AsmContigDataStoreBuilder{
        private final FragmentDataStore frgDataStore;
        private final IncludeType includeType;
        private final IndexedFileRange fileRange;
        private long currentFileOffset;
        private String currentContigId;
        private final File asmFile;
        private Long startAFG=null;
        private Long endAFG=null;
        private Long startCurrentContigOffset=null;
        public IndexedAsmContigDataStoreBuilder(File asmFile,FragmentDataStore frgDataStore, IncludeType includeType, IndexedFileRange fileRange) {
            this.includeType = includeType;
            this.frgDataStore = frgDataStore;
            this.fileRange = fileRange;
            this.asmFile = asmFile;
        }
        @Override
        public synchronized void visitLine(String line) {        
            final int length = line.length();
             
            if(line.startsWith("{AFG")){
                if(startAFG==null){
                    startAFG = currentFileOffset;
                }
            }else if(endAFG==null && line.startsWith("{UTG")){
                endAFG = currentFileOffset-1;
            }else if(line.startsWith("{CCO")){
                startCurrentContigOffset = currentFileOffset;
            }
            currentFileOffset+=length; 
            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitRead(String externalId, long internalId,
                MateStatus mateStatus, boolean isSingleton, Range clearRange) {
            
            super.visitRead(externalId, internalId, mateStatus, isSingleton, clearRange);
        }
        /**
        * {@inheritDoc}
        * Returns an empty set if this contig is a degenerate
        * and we don't want degenerates in this datastore;
        * returns a set containing just {@link NestedContigMessageTypes#READ_MAPPING}
        * otherwise.
        */
        @Override
        public synchronized Set<NestedContigMessageTypes> visitContig(String externalId, long internalId,
                boolean isDegenerate, NucleotideSequence consensusSequence,
                QualitySequence consensusQualities, int numberOfReads,
                int numberOfUnitigs, int numberOfVariants) {
            if(includeType.include(isDegenerate)){
                
                currentContigId = externalId;     
               
                return EnumSet.of(NestedContigMessageTypes.READ_MAPPING);
            }
            return EnumSet.noneOf(NestedContigMessageTypes.class);
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public boolean visitUnitig(String externalId, long internalId,
                float aStat, float measureOfPolymorphism, UnitigStatus status,
                NucleotideSequence consensusSequence,
                QualitySequence consensusQualities, int numberOfReads) {
            
            
            return false;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized void visitEndOfContig() {
            if(currentContigId !=null){
               this.fileRange.put(currentContigId, Range.create(startCurrentContigOffset, currentFileOffset));
               currentContigId=null;
            }
        }

        
        @Override
        public synchronized AsmContigDataStore build(){
            
            return new IndexedAsmContigDataStore(asmFile, fileRange,Range.create(startAFG, endAFG),frgDataStore);
        }

    }
    /**
     * {@code SingleContigVisitor} parses (parts of) an asm file
     * but only extracts information for the particular given contig. 
     * @author dkatzel
     *
     *
     */
    private static class SingleContigVisitor extends AbstractAsmVisitor{
        private final Map<String,Range> readMap;
        private final String contigId;
        
        private AsmContigBuilder currentBuilder=null;
        private final FragmentDataStore frgDataStore;
        private AsmContig contig=null;
        /**
         * Constructor.  
         * @param contigId the contig id to parse from asm file.
         * @param readMap the readMap of clear ranges for only the reads
         * we care about.  We only really care about the keys at first,
         * this instance will be modified by this program to correctly
         * set their values.
         * @param frgDataStore the {@link FragmentDataStore}
         * containing all the full length basecalls for this assembly.
         * (needed to create read records)
         */
        public SingleContigVisitor(String contigId, Map<String,Range> readMap,FragmentDataStore frgDataStore){
            this.contigId = contigId;
            this.readMap = readMap;
            this.frgDataStore = frgDataStore;
        }
        
        /**
        * {@inheritDoc}
        */
        @Override
        public void visitRead(String externalId, long internalId,
                MateStatus mateStatus, boolean isSingleton, Range clearRange) {
            if(readMap.containsKey(externalId)){
                //replace fake range with actual range
                readMap.put(externalId, clearRange);
            }
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public void visitReadLayout(char readType, String externalReadId,
                DirectedRange readRange, List<Integer> gapOffsets) {
            if(currentBuilder!=null){
              //in contig
                try {
                    NucleotideSequence fullLengthSequence = frgDataStore.get(externalReadId).getNucleotideSequence();
                    Range clearRange = readMap.get(externalReadId);
                    if(clearRange==null){
                        throw new IllegalStateException("do not have clear range information for read "+ externalReadId);
                    }
                   
                    NucleotideSequenceBuilder validBases = new NucleotideSequenceBuilder(fullLengthSequence.asList(clearRange));
                    if(readRange.getDirection() == Direction.REVERSE){
                        validBases.reverseComplement();
                    }
                    String gappedValidBases = AsmUtil.computeGappedSequence(
                            validBases.asList(), gapOffsets);
                    currentBuilder.addRead(externalReadId, gappedValidBases,
                            (int)readRange.asRange().getBegin(),readRange.getDirection(),
                            clearRange, 
                            (int)fullLengthSequence.getLength(),
                            false);
                } catch (DataStoreException e) {
                    throw new IllegalStateException("error getting read id "+ externalReadId +
                            " from frg file",e);
                }
            }
           
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public Set<NestedContigMessageTypes> visitContig(String externalId,
                long internalId, boolean isDegenerate,
                NucleotideSequence consensusSequence,
                QualitySequence consensusQualities, int numberOfReads,
                int numberOfUnitigs, int numberOfVariants) {
            if(externalId.equals(contigId)){
                currentBuilder =  DefaultAsmContig.createBuilder(externalId, consensusSequence,isDegenerate);
                return EnumSet.of(NestedContigMessageTypes.READ_MAPPING);
            }
            currentBuilder=null;
            return EnumSet.noneOf(NestedContigMessageTypes.class);
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public void visitEndOfContig() {
            if(currentBuilder !=null){
                contig = currentBuilder.build();
                currentBuilder=null;
            }
        }

        /**
         * @return the contig
         */
        public AsmContig getContig() {
            return contig;
        }
        
    }
    private static class ReadMapVisitor extends AbstractAsmVisitor{
        private Map<String,Range> map;
        private final String contigId;
        private boolean inContigWeCareAbout=false;
        public ReadMapVisitor(String contigId){
            this.contigId = contigId;
        }
      
        /**
        * {@inheritDoc}
        */
        @Override
        public void visitReadLayout(char readType, String externalReadId,
                DirectedRange readRange, List<Integer> gapOffsets) {
            if(inContigWeCareAbout){
                map.put(externalReadId,null);
            }
            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public Set<NestedContigMessageTypes> visitContig(String externalId,
                long internalId, boolean isDegenerate,
                NucleotideSequence consensusSequence,
                QualitySequence consensusQualities, int numberOfReads,
                int numberOfUnitigs, int numberOfVariants) {
            if(externalId.equals(contigId)){
            	int mapSize = MapUtil.computeMinHashMapSizeWithoutRehashing(numberOfReads);
                map = new HashMap<String, Range>(mapSize);
                inContigWeCareAbout=true;
                return EnumSet.of(NestedContigMessageTypes.READ_MAPPING);
            }
            inContigWeCareAbout=false;
            return EnumSet.noneOf(NestedContigMessageTypes.class);
        }

        /**
         * @return the map
         */
        public Map<String, Range> getReadMap() {
            return map;
        }

        
        
    }
}

