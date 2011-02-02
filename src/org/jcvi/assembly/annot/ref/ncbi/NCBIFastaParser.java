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
 * Created on Jan 28, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.annot.ref.ncbi;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.fastX.fasta.seq.DefaultNucleotideEncodedSequenceFastaRecord;
import org.jcvi.io.IOUtil;

public class NCBIFastaParser {

    private static Pattern FASTA_PATTERN = Pattern.compile("<div\\s+class='recordbody'>>(\\S+)\\s+(.*?)([A,C,G,T,-,N,M,K,B,D,H,V,R,Y,S,W]+)\\s*</div>");
    
    public static DefaultNucleotideEncodedSequenceFastaRecord parseFastaFrom(InputStream ncbiFastaPage) throws IOException{
        String response = IOUtil.readStream(ncbiFastaPage);
        Matcher matcher = FASTA_PATTERN.matcher(response);
        if(matcher.find()){
            final String bases = matcher.group(3).replaceAll("\\s+", "");
            return new DefaultNucleotideEncodedSequenceFastaRecord(matcher.group(1),matcher.group(2),bases);
        }
        throw new RuntimeException("could not parse fasta data from ncbi");
    }
}
