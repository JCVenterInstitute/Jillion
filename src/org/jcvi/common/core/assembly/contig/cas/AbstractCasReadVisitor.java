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

package org.jcvi.common.core.assembly.contig.cas;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.jcvi.common.core.assembly.contig.cas.align.score.CasScoringScheme;
import org.jcvi.common.core.assembly.contig.cas.read.CasPlacedRead;
import org.jcvi.common.core.assembly.trim.TrimDataStore;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.util.ChainedCloseableIterator;
import org.jcvi.common.core.util.iter.CloseableIterator;


/**
 * @author dkatzel
 *
 *
 */
public abstract class AbstractCasReadVisitor<R extends ReadRecord> extends AbstractOnePassCasFileVisitor {

    private final File workingDir;
    private final CasTrimMap trimMap;
    private CloseableIterator<R> phdIterator;
    private final List<NucleotideSequence> orderedGappedReferences;
    private final TrimDataStore validRangeDataStore;
    private final List<CloseableIterator<R>> iterators = new ArrayList<CloseableIterator<R>>();
    private final TraceDetails traceDetails;
    private AbstractCasReadVisitor(File workingDir, CasTrimMap trimMap,
            List<NucleotideSequence> orderedGappedReferences,
            TrimDataStore validRangeDataStore,
            TraceDetails traceDetails) {
        this.traceDetails = traceDetails;
        this.workingDir = workingDir;
        this.trimMap = trimMap;
        this.orderedGappedReferences = orderedGappedReferences;
        this.validRangeDataStore = validRangeDataStore;
    }
    public AbstractCasReadVisitor(CasInfo casInfo) {
        this(casInfo.getCasWorkingDirectory(),
                casInfo.getCasTrimMap(),
                casInfo.getOrderedGappedReferenceList(),
                casInfo.getMultiTrimDataStore(),
                casInfo.getTraceDetails());
    }
    protected final NucleotideSequence getGappedReference(int index){
        return orderedGappedReferences.get(index);
    }
    
    /**
     * @return the validRangeDataStore
     */
    public TrimDataStore getValidRangeDataStore() {
        return validRangeDataStore;
    }
    public abstract CloseableIterator<R>  createIlluminaIterator(File illuminaFile, TraceDetails traceDetails);
    
    public abstract CloseableIterator<R>  createSffIterator(File sffFile, TraceDetails traceDetails);
    
    public abstract CloseableIterator<R>  createFastaIterator(File fastaFile, TraceDetails traceDetails);
    
    public abstract CloseableIterator<R>  createChromatogramIterator(File chromatogramFile, TraceDetails traceDetails);
    
    @Override
    public final synchronized void visitReadFileInfo(CasFileInfo readFileInfo) {
        super.visitReadFileInfo(readFileInfo);
        for(String filename :readFileInfo.getFileNames()){
            
            File file;
            try {
                file = getTrimmedFileFor(filename);
            } catch (FileNotFoundException e) {
                throw new IllegalStateException(e);
            }
            ReadFileType readType = ReadFileType.getTypeFromFile(filename);
            switch(readType){
                case ILLUMINA:
                        iterators.add(createIlluminaIterator(file, traceDetails));
                        break;
                case SFF:
                    iterators.add(createSffIterator(file, traceDetails));
                    break;
                case FASTA:
                    final CloseableIterator<R> iter;
                    if(!traceDetails.hasChromatDir()){
                        iter= createFastaIterator(file, traceDetails);
                    }else{
                        iter = createChromatogramIterator(file, traceDetails);
                    }
                            
                    iterators.add(iter);
                    break;
                default: throw new IllegalArgumentException("unsupported type "+ file.getName());
                    
            }           
        }
        
    }
    
      @Override
    public final synchronized void visitScoringScheme(CasScoringScheme scheme) {
        super.visitScoringScheme(scheme);
        phdIterator = new ChainedCloseableIterator<R>(iterators);
    }

    
    private File getTrimmedFileFor(String pathToDataStore) throws FileNotFoundException {
            final File dataStoreFile = CasUtil.getFileFor(workingDir, pathToDataStore);
            File trimmedDataStore = trimMap.getUntrimmedFileFor(dataStoreFile);
            return trimmedDataStore;
        }
    

    @Override
    protected final synchronized void visitMatch(CasMatch match, long readCounter) {
        R phdReadRecord =phdIterator.next();
        try {
            if(match.matchReported()){
                String recordId = phdReadRecord.getId();
                int casReferenceId = (int)match.getChosenAlignment().contigSequenceId();
                NucleotideSequence gappedReference =orderedGappedReferences.get(casReferenceId);
                CasPlacedRead placedRead = CasUtil.createCasPlacedRead(match, recordId, 
                        phdReadRecord.getBasecalls(), 
                        validRangeDataStore.get(recordId), gappedReference);
                visitMatch(match, phdReadRecord, placedRead);              
            }else{
                visitUnMatched(phdReadRecord);
            }
        } catch (Exception e) {
            IOUtil.closeAndIgnoreErrors(phdIterator);
            throw new IllegalStateException("error getting trim range for " + phdReadRecord, e);
        }
    }
    protected abstract void visitUnMatched(R readRecord) throws Exception;
    protected abstract void visitMatch(CasMatch match, R readRecord, CasPlacedRead placedRead)
            throws Exception;
    

    
}
