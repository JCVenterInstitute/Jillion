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
 * Created on Nov 11, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sanger.phd;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.JoinedStringBuilder;
import org.jcvi.jillion.trace.sanger.PositionSequence;

public final class PhdWriter {
    private static final String BEGIN_SEQUENCE = "BEGIN_SEQUENCE";
    private static final String BEGIN_COMMENT = "BEGIN_COMMENT";
    private static final String END_SEQUENCE = "END_SEQUENCE";
    private static final String END_COMMENT = "END_COMMENT";
    
    private static final String BEGIN_DNA = "BEGIN_DNA";
    private static final String END_DNA = "END_DNA";
    
    private static final String BEGIN_TAG = "BEGIN_TAG\n";
    private static final String END_TAG = "END_TAG\n";
    
    private PhdWriter(){
    	//can not instantiate
    }
    public static void writePhd(Phd phd, OutputStream out) throws IOException{
        try{
            StringBuilder phdRecord = new StringBuilder();
            
            phdRecord.append( String.format("%s %s%n%n",BEGIN_SEQUENCE, phd.getId()));
            
            phdRecord.append(createComments(phd));
            phdRecord.append(writeDnaSection(phd));
            phdRecord.append( String.format("%n"));
            List<PhdReadTag> tags = phd.getReadTags();
            if(!tags.isEmpty()){
            	 phdRecord.append(writeReadTags(tags));
            }
            phdRecord.append(String.format("%s%n",END_SEQUENCE));
            phdRecord.append(createWholeReadItems(phd));
            write(out, phdRecord.toString());
        }catch(Throwable t){
            throw new IOException("error writing phd record for "+phd.getId(), t);
        }
        
    }

    private static String writeReadTags(List<PhdReadTag> tags) {
		List<String> printedTags = new ArrayList<String>(tags.size());
    	for(PhdReadTag tag : tags){
			StringBuilder builder = new StringBuilder(500);
			builder.append(BEGIN_TAG)
				.append(String.format("TYPE:%s%n",tag.getType()))
				.append(String.format("SOURCE:%s%n",tag.getSource()));
			Range range = tag.getUngappedRange();
			builder.append(String.format("UNGAPPED_READ_POS:%d %d%n",
					range.getBegin(Range.CoordinateSystem.RESIDUE_BASED),
					range.getEnd(Range.CoordinateSystem.RESIDUE_BASED)));
			
			builder.append(String.format("DATE: %s%n", PhdUtil.formatReadTagDate(tag.getDate())));
			if(tag.getComment() !=null){
				builder.append(BEGIN_COMMENT).append(String.format("%n"));
				builder.append(tag.getComment());
				builder.append(END_COMMENT).append(String.format("%n"));
			}
			if(tag.getFreeFormData() !=null){
				builder.append(tag.getFreeFormData());
			}
			builder.append(END_TAG);
			printedTags.add(builder.toString());
		}
		return new JoinedStringBuilder(printedTags)
					.glue(String.format("%n"))
					.build();
	}
	private static StringBuilder createWholeReadItems(Phd phd) {
        StringBuilder tags = new StringBuilder();
        for(PhdWholeReadItem tag : phd.getWholeReadItems()){
        	String lines = new JoinedStringBuilder(tag.getLines())
        						.glue(String.format("%n"))
        						.build();
            tags.append(String.format("WR{%n%s%n}%n",lines));
        }
        return tags;
        
        
    }

    private static StringBuilder writeDnaSection(Phd phd) {
        StringBuilder dna = new StringBuilder();
        dna.append(String.format("%s%n",BEGIN_DNA));
        dna.append(writeCalledInfo(phd));
        dna.append(String.format("%s%n",END_DNA));   
        return dna;
    }

    private static StringBuilder writeCalledInfo( Phd phd){
       
        NucleotideSequence nucleotideSequence = phd.getNucleotideSequence();
        int seqLength = (int)nucleotideSequence.getLength();
		Iterator<Nucleotide> basesIter = nucleotideSequence.iterator();
        Iterator<PhredQuality> qualIter = phd.getQualitySequence().iterator();
       
        PositionSequence peaks = phd.getPositionSequence();
        StringBuilder result = new StringBuilder(seqLength *10);
        if(peaks==null){
            while(basesIter.hasNext()){
            	result.append(String.format("%s %d%n",
            			basesIter.next(), 
                        qualIter.next().getQualityScore()));
            }
        }else{
        	 //optimization to convert to array instead 
            //of iterating over Position objects
            //this way we get primitives.
        	short[] positions = phd.getPositionSequence().toArray();
        	int i=0;
            while(basesIter.hasNext()){
            	result.append(String.format("%s %d %d%n",
            			basesIter.next(), 
                        qualIter.next().getQualityScore(),
                        IOUtil.toUnsignedShort(positions[i])));
            	i++;
            }
        }
       
        return result;
        
    }

    private static StringBuilder createComments(Phd phd) {
        StringBuilder comments = new StringBuilder();
        
        comments.append( BEGIN_COMMENT+"\n");
        comments.append( String.format("%n"));
        for(Entry<String, String> entry :phd.getComments().entrySet()){
            comments.append(String.format("%s: %s%n",entry.getKey(),entry.getValue()));
        }
        comments.append(String.format("%n"));
        comments.append(END_COMMENT+"\n");
        comments.append(String.format("%n"));
        return comments;
    }


    private static void write(OutputStream out, String data) throws IOException{
        out.write(data.getBytes("UTF-8"));
    }
}
