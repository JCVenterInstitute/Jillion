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
import java.util.Iterator;
import java.util.Map.Entry;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.trace.sanger.Position;

public final class PhdWriter {
    private static final String BEGIN_SEQUENCE = "BEGIN_SEQUENCE";
    private static final String BEGIN_COMMENT = "BEGIN_COMMENT";
    private static final String END_SEQUENCE = "END_SEQUENCE";
    private static final String END_COMMENT = "END_COMMENT";
    
    private static final String BEGIN_DNA = "BEGIN_DNA";
    private static final String END_DNA = "END_DNA";
    
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
            phdRecord.append(String.format("%s%n",END_SEQUENCE));
            phdRecord.append(createTags(phd));
            write(out, phdRecord.toString());
        }catch(Throwable t){
            throw new IOException("error writing phd record for "+phd.getId(), t);
        }
        
    }

    private static StringBuilder createTags(Phd phd) {
        StringBuilder tags = new StringBuilder();
        for(PhdTag tag : phd.getTags()){
            tags.append(String.format("%s{%n%s%n}%n",tag.getTagName(), tag.getTagValue()));
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
       
        Iterator<Nucleotide> basesIter = phd.getNucleotideSequence().iterator();
        Iterator<PhredQuality> qualIter = phd.getQualitySequence().iterator();
        //optimization to convert to array instead 
        //of iterating over Position objects
        //this way we get primitives.
        short[] positions = phd.getPositionSequence().toArray();
        
        
        StringBuilder result = new StringBuilder(positions.length *10);
        int i=0;
        while(basesIter.hasNext()){
        	result.append(String.format("%s %d %d%n",
        			basesIter.next(), 
                    qualIter.next().getQualityScore(),
                    IOUtil.toUnsignedShort(positions[i])));
        	i++;
        }
       
        
        return result;
        
    }

    private static StringBuilder createComments(Phd phd) {
        StringBuilder comments = new StringBuilder();
        
        comments.append( BEGIN_COMMENT+"\n");
        comments.append( String.format("%n"));
        for(Entry<Object, Object> entry :phd.getComments().entrySet()){
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
