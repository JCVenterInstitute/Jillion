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
 * Created on Jan 6, 2010
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.ace;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
/**
 * {@code DefaultAceTagsFromAceFile} is a class
 * that can create {@link AceTags} instances
 * by parsing an Ace file.
 * @author dkatzel
 *
 *
 */
public final class DefaultAceTagsFromAceFile {
    /**
     * {@code AceTagsFromFileBuilder} is an {@link AceFileVisitor}
     * that will populate an {@link AceTags} object
     * as the ace file is parsed.  As with all
     * {@link org.jcvi.common.core.util.Builder}s, the
     * {@link #build()} method must be called
     * to actually create the immutable instance.
     * @author dkatzel
     *
     *
     */
    public interface AceTagsFromFileBuilder extends AceFileVisitor, org.jcvi.common.core.util.Builder<AceTags>{
        
    }

    /**
     * Create a new {@link AceTagsFromFileBuilder} instance
     * that needs to get visited by {@link AceFileParser}
     * in order to create a new {@link AceTags} instance.
     * This is useful if the client code wants to collect several different {@link AceFileVisitor}
     * implementations that all need to parse an ace file
     * so they can all visit the ace file at the same time
     * so the ace file only has to be parsed once.
     * @return a new {@link AceTagsFromFileBuilder}
     * instance; never null.
     */
    public static AceTagsFromFileBuilder createBuilder(){
        return new Builder();
    }
    /**
     * Creates a new {@link AceTags} instance using data populated from
     * the given ace file.  This is the same as:
     * <pre>
        AceTagsFromFileBuilder builder = createBuilder();
        AceFileParser.parseAceFile(aceFile, builder);
        return builder.build();
        </pre>
     * @param aceFile the ace File to parse.
     * @return a new {@link AceTags} instance containing all of the
     * {@link AceTag}s contained in the given ace file.
     * @throws IOException if there is a problem parsing the ace file.
     */
    public static AceTags create(File aceFile) throws IOException{
        AceTagsFromFileBuilder builder = createBuilder();
        AceFileParser.parseAceFile(aceFile, builder);
        return builder.build();
    }
    private DefaultAceTagsFromAceFile(){
        super();
    }
    

    

   private static final class Builder extends AbstractAceFileVisitor implements AceTagsFromFileBuilder{
       /**
        * Consensus tags span multiple lines of the ace file so we need to build
        * up the consensus tags as we parse.
        */
       private DefaultConsensusAceTag.Builder consensusTagBuilder;
       private AceTagsBuilder aceTagsBuilder = DefaultAceTags.createBuilder();
       
    /**
    * {@inheritDoc}
    */
    @Override
    public AceTags build() {
        return aceTagsBuilder.build();
    }

    @Override
    public synchronized void visitBeginConsensusTag(String id, String type, String creator,
            long gappedStart, long gappedEnd, Date creationDate,
            boolean isTransient) {
        super.visitBeginConsensusTag(id, type, creator, gappedStart, gappedEnd, creationDate, isTransient);
        consensusTagBuilder = new DefaultConsensusAceTag.Builder(id, 
                type, creator, creationDate, Range.create(gappedStart, gappedEnd), isTransient);

    }

    @Override
    public void visitWholeAssemblyTag(String type, String creator,
            Date creationDate, String data) {
        super.visitWholeAssemblyTag(type, creator, creationDate, data);
        aceTagsBuilder.addWholeAssemblyTag(new DefaultWholeAssemblyAceTag(type, creator, creationDate, data.trim()));
    }
    

    @Override
    public void visitConsensusTagComment(String comment) {
        super.visitConsensusTagComment(comment);
        consensusTagBuilder.addComment(comment);

    }

    @Override
    public void visitConsensusTagData(String data) {
        super.visitConsensusTagData(data);
        consensusTagBuilder.appendData(data);

    }

   

    @Override
    public void visitEndConsensusTag() {
        super.visitEndConsensusTag();
        aceTagsBuilder.addConsensusTag(consensusTagBuilder.build());

    }

   

    @Override
    public void visitReadTag(String id, String type, String creator,
            long gappedStart, long gappedEnd, Date creationDate,
            boolean isTransient) {
        super.visitReadTag(id, type, creator, gappedStart, gappedEnd, creationDate, isTransient);
        aceTagsBuilder.addReadTag(new DefaultReadAceTag(id, type, creator, creationDate, 
                Range.create(gappedStart,gappedEnd), isTransient));

    }

    /**
    * {@inheritDoc}
    */
    @Override
    protected void visitAceRead(String readId, NucleotideSequence validBasecalls,
            int offset, Direction dir, Range validRange, PhdInfo phdInfo,int ungappedFullLength) {
        
    }

    /**
     * {@inheritDoc}
     */
     @Override
     protected void visitNewContig(String contigId, NucleotideSequence consensus, int numberOfBases, int numberOfReads, boolean complimented) {
         
     }
       
   }

}
