/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Mar 12, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.tigr.contig;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.util.JillionUtil;
/**
 * {@code CtgFileWriter} will write out {@link Contig}
 * objects in ctg format.
 * @author dkatzel
 *
 *
 */
public class CtgFileWriter implements Closeable{
    private static final CtgFormatReadSorter READ_SORTER = CtgFormatReadSorter.INSTANCE;
    private final OutputStream out;
    
    public CtgFileWriter(OutputStream out) {
        this.out = out;
    }
    public <PR extends AssembledRead, C extends Contig<PR>> void write(C contig) throws IOException,
            UnsupportedEncodingException {
        writeContigHeader(contig);
        writeBases(contig.getConsensusSequence());
        Set<PR> readsInContig = new TreeSet<PR>(READ_SORTER);
        StreamingIterator<PR> iter = null;
        try{
        	iter = contig.getReadIterator();
        	while(iter.hasNext()){
        		PR placedRead = iter.next();
        		readsInContig.add(placedRead);
        	}
        }finally{
        	IOUtil.closeAndIgnoreErrors(iter);
        }
        for(AssembledRead placedRead : readsInContig){
            writePlacedReadHeader(placedRead, contig.getConsensusSequence());
            writeBases(placedRead.getNucleotideSequence());
        }
    }
    
   
    private void writeContigHeader(Contig<? extends AssembledRead> contig) throws IOException {
        String header = String.format("##%s %d %d bases",
                contig.getId(), contig.getNumberOfReads(), contig.getConsensusSequence().getLength());
        
        writeToOutputStream(header+"\n");
    }

    private void writeBases(Sequence<Nucleotide> consensus) throws UnsupportedEncodingException, IOException {
        StringBuilder asString = new StringBuilder();
        for(Nucleotide glyph : consensus){
            asString.append(glyph);
        }
        
        String result = asString.toString().replaceAll("(.{60})", "$1\n");
        if(!result.endsWith("\n")){
            result += "\n";
        }
        writeToOutputStream(result);
        
    }
    private void writeToOutputStream(final String result) throws IOException,
            UnsupportedEncodingException {
        out.write(result.getBytes("UTF-8"));
        out.flush();
    }
    
    private void writePlacedReadHeader(AssembledRead placedRead,NucleotideSequence consensus) throws IOException {
        StringBuilder header = new StringBuilder();
        header.append(String.format("#%s(%d) [", placedRead.getId(), placedRead.getGappedStartOffset()));
        int validLeft = (int)placedRead.getReadInfo().getValidRange().getBegin();
        int validRight = (int)placedRead.getReadInfo().getValidRange().getEnd();
        if(placedRead.getDirection() == Direction.REVERSE){
            header.append("RC");
            int temp = validLeft;
            validLeft = validRight;
            validRight = temp;
        }

        header.append(String.format("] %d bases {%d %d} <%d %d>",
                placedRead.getNucleotideSequence().getLength(), validLeft+1, validRight+1, 
                placedRead.getGappedStartOffset()+1-consensus.getNumberOfGapsUntil((int) placedRead.getGappedStartOffset()), 
                placedRead.getGappedEndOffset()+1-consensus.getNumberOfGapsUntil((int)placedRead.getGappedEndOffset())));
        header.append('\n');
        writeToOutputStream(header.toString());
        
    }
    /**
     * {@code CtgFormatReadSorter} will sort the {@link AssembledRead}s
     * by start coordinate.  If multiple reads have the same start coordinate
     * in the contig, then those reads will be sorted by length (smallest first).  If there are still
     * multiple reads that have the same start AND the same length, then those reads
     * are sorted by their ids.
     * @author dkatzel
     *
     *
     */
    private static enum CtgFormatReadSorter implements Comparator<AssembledRead>, Serializable{
        /**
         * Singleton instance.
         */
        INSTANCE;

        /**
         * Sorts PlacedRead by offset then by read length, then by id.
         */
        @Override
        public int compare(AssembledRead o1, AssembledRead o2) {
        	
            int startComparison = JillionUtil.compare(o1.getGappedStartOffset(), o2.getGappedStartOffset());
            if(startComparison !=0){
                return startComparison;
            }
            int lengthComparison= JillionUtil.compare(o1.getGappedLength(), o2.getGappedLength());
            if(lengthComparison !=0){
                return lengthComparison;
            }
            int idLengthComparison =  JillionUtil.compare(o1.getId().length(), o2.getId().length());
            if(idLengthComparison !=0){
                return idLengthComparison;
            }
            return o1.getId().compareTo(o2.getId());
        }
        
    }
    @Override
    public void close() throws IOException {
        out.close();
        
    }

}
