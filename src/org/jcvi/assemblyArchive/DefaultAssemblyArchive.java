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
 * Created on Sep 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assemblyArchive;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.jcvi.assembly.Contig;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.VirtualPlacedRead;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;

public class DefaultAssemblyArchive<T extends PlacedRead> implements AssemblyArchive<T> {
    

    private final String centerName;
    private final long taxonId;
    private final String description, structure,submitterReference;
    private final AssemblyArchiveType submissionType;
    private final Date submissionDate;
    
    private final Set<AssemblyArchiveContigRecord<T>> contigs;
    
    private final long numberOfTotalBasecalls;
    private final long numberOfConsensusBasecalls;
    private final long numberOfTraces;
    
    /**
     * @param centerName
     * @param taxonId
     * @param description
     * @param structure
     * @param submissionType
     * @param submissionDate
     * @param contigs
     */
    protected DefaultAssemblyArchive(Builder<T> builder) {
        this.centerName = builder.centerName;
        this.taxonId = builder.taxonId;
        this.description = builder.description;
        this.structure = builder.structure;
        this.submissionType = builder.submissionType;
        this.submissionDate = builder.submissionDate;
        this.contigs = builder.contigs;
        this.submitterReference = builder.submitterReference;
        long consensusBasecalls =0;
        long totalNumberOfBasecalls =0;
        long numberOfTraces = 0;
        for(AssemblyArchiveContigRecord contigRecord : contigs){
            Contig<PlacedRead> contig =contigRecord.getContig();
            consensusBasecalls += contig.getConsensus().getUngappedLength();
            numberOfTraces += contig.getNumberOfReads();
            for(VirtualPlacedRead<PlacedRead> virtualRead : contig.getVirtualPlacedReads()){
                final NucleotideEncodedGlyphs encodedGlyphs = virtualRead.getEncodedGlyphs();
                //only count non-gaps
                totalNumberOfBasecalls +=encodedGlyphs.getUngappedLength();
            }
        }
        this.numberOfTraces = numberOfTraces;
        this.numberOfConsensusBasecalls = consensusBasecalls;
        this.numberOfTotalBasecalls = totalNumberOfBasecalls;
    }

    @Override
    public String getCenterName() {
        return centerName;
    }

    @Override
    public Set<AssemblyArchiveContigRecord<T>> getContigRecords() {
        return contigs;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getStructure() {
        return structure;
    }

    @Override
    public Date getSubmissionDate() {
        //defensive copy
        return new Date(submissionDate.getTime());
    }

    @Override
    public String getSubmitterReference() {
        return submitterReference;
    }

    @Override
    public long getTaxonId() {
        return taxonId;
    }

    @Override
    public AssemblyArchiveType getType() {
        return submissionType;
    }
    
    @Override
    public long getNumberOfContigBases() {
        return numberOfConsensusBasecalls;
    }

    @Override
    public int getNumberOfContigs() {
        return contigs.size();
    }

    @Override
    public long getNumberOfTotalBasecalls() {
        return numberOfTotalBasecalls;
    }

    
    @Override
    public long getNumberOfTraces() {
        return numberOfTraces;
    }

    @Override
    public double getCoverageRatio() {
        return (double)(getNumberOfTotalBasecalls())/getNumberOfContigBases();
    }



    public static class Builder<T extends PlacedRead> implements org.jcvi.Builder<DefaultAssemblyArchive>{
        private String centerName;
        private long taxonId;
        private String description, structure, submitterReference;
        private AssemblyArchiveType submissionType;
        private Date submissionDate;
        
        private final Set<AssemblyArchiveContigRecord<T>> contigs = new HashSet<AssemblyArchiveContigRecord<T>>();
        
        public Builder(String centerName, long taxonId, 
                String description, String structure, AssemblyArchiveType submissionType){
            this.centerName = centerName;
            this.taxonId = taxonId;
            this.description = description;
            this.structure = structure;
            this.submissionType = submissionType;
        }
        
        public Builder addContigRecord(AssemblyArchiveContigRecord<T> contigRecord){
            contigs.add(contigRecord);
            return this;
        }
        
        public Builder submissionDate(Date date){
            //defensive copy
            this.submissionDate = new Date(date.getTime());
            return this;
        }
        public Builder submissionReference(String submitterReference){
            this.submitterReference = submitterReference;
            return this;
        }
        @Override
        public DefaultAssemblyArchive build() {
            return new DefaultAssemblyArchive(this);
        }

    }
}
