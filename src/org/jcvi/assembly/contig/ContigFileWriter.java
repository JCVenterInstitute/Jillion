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
 * Created on Mar 12, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jcvi.assembly.Contig;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.common.core.seq.Sequence;
import org.jcvi.common.core.seq.nuc.NucleotideGlyph;
import org.jcvi.common.core.seq.nuc.NucleotideSequence;
import org.jcvi.common.core.seq.read.SequenceDirection;

public class ContigFileWriter implements Closeable{
    private static final ContigFormatReadSorter READ_SORTER = new ContigFormatReadSorter();
    private final OutputStream out;
    public ContigFileWriter(OutputStream out) {
        this.out = out;
    }
    public <PR extends PlacedRead, C extends Contig<PR>> void write(List<C> contigs) throws IOException {
        for(C contig : contigs){
            write(contig);
        }
        
    }
    public <PR extends PlacedRead, C extends Contig<PR>> void write(C contig) throws IOException,
            UnsupportedEncodingException {
        writeContigHeader(contig);
        writeBases(contig.getConsensus());
        Set<PlacedRead> readsInContig = new TreeSet<PlacedRead>(READ_SORTER);
        readsInContig.addAll(contig.getPlacedReads());
        for(PlacedRead placedRead : readsInContig){
            writePlacedReadHeader(placedRead, contig.getConsensus());
            writeBases(placedRead.getEncodedGlyphs());
        }
    }
    
   
    private void writeContigHeader(Contig<? extends PlacedRead> contig) throws IOException {
        String header = String.format("##%s %d %d bases, 00000000 checksum.\n",
                contig.getId(), contig.getNumberOfReads(), contig.getConsensus().getLength());
        
        writeToOutputStream(header);
    }

    private void writeBases(Sequence<NucleotideGlyph> consensus) throws UnsupportedEncodingException, IOException {
        StringBuilder asString = new StringBuilder();
        for(NucleotideGlyph glyph : consensus.decode()){
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
    
    private void writePlacedReadHeader(PlacedRead placedRead,NucleotideSequence consensus) throws IOException {
        StringBuilder header = new StringBuilder();
        header.append(String.format("#%s(%d) [", placedRead.getId(), placedRead.getStart()));
        int validLeft = (int)placedRead.getValidRange().getStart();
        int validRight = (int)placedRead.getValidRange().getEnd();
        if(placedRead.getSequenceDirection() == SequenceDirection.REVERSE){
            header.append("RC");
            int temp = validLeft;
            validLeft = validRight;
            validRight = temp;
        }

        header.append(String.format("] %d bases, 00000000 checksum. {%d %d} <%d %d>\n",
                placedRead.getEncodedGlyphs().getLength(), validLeft+1, validRight+1, 
                placedRead.getStart()+1-consensus.computeNumberOfInclusiveGapsInGappedValidRangeUntil((int) placedRead.getStart()), 
                placedRead.getEnd()+1-consensus.computeNumberOfInclusiveGapsInGappedValidRangeUntil((int)placedRead.getEnd())));
        writeToOutputStream(header.toString());
        
    }
 
    private static final class ContigFormatReadSorter implements Comparator<PlacedRead>, Serializable{
        /**
         * 
         */
        private static final long serialVersionUID = 1326950053628001679L;

        /**
         * Sorts PlacedRead by offset then by read length, then by id.
         */
        @Override
        public int compare(PlacedRead o1, PlacedRead o2) {
            int startComparison = Long.valueOf(o1.getStart()).compareTo(Long.valueOf(o2.getStart()));
            if(startComparison !=0){
                return startComparison;
            }
            int lengthComparison= Long.valueOf(o1.getLength()).compareTo(Long.valueOf(o2.getLength()));
            if(lengthComparison !=0){
                return lengthComparison;
            }
            int idLengthComparison =  Long.valueOf(o1.getId().length()).compareTo(Long.valueOf(o2.getId().length()));
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
