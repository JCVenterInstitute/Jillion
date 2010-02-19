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
 * Created on Feb 19, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.annot.ref.genePredict;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.Range;
import org.jcvi.assembly.annot.DefaultExon;
import org.jcvi.assembly.annot.Exon;
import org.jcvi.assembly.annot.Frame;
import org.jcvi.assembly.annot.Strand;
import org.jcvi.assembly.annot.ref.CodingRegion;
import org.jcvi.assembly.annot.ref.CodingRegionState;
import org.jcvi.assembly.annot.ref.DefaultCodingRegion;
import org.jcvi.assembly.annot.ref.DefaultRefGene;
import org.jcvi.assembly.annot.ref.RefGene;
import org.jcvi.assembly.annot.ref.RefUtil;
import org.jcvi.assembly.annot.ref.writer.RefGeneTxtWriter;
import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.fasta.DefaultNucleotideFastaFileDataStore;
import org.jcvi.fasta.NucleotideSequenceFastaRecord;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;

public class GenePredictionParser {

    private static final Pattern GENE_PATTERN = Pattern.compile(">(\\S+)\\.(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\S+)");
    
    public static Map<String,List<RefGene>> parse(InputStream input){
        Scanner scanner = new Scanner(input);
        ConcurrentHashMap<String,List<RefGene>> map = new ConcurrentHashMap<String, List<RefGene>>();
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            Matcher matcher = GENE_PATTERN.matcher(line);
            if(matcher.find()){
                String genome = matcher.group(1);
                int geneId = Integer.parseInt(matcher.group(2));
                Range codingRange =  RefUtil.convertOnesBasedToSpacedBased(
                        Range.buildRange(Long.parseLong(matcher.group(3)), Long.parseLong(matcher.group(4))));
                CodingRegion codingRegion = new DefaultCodingRegion(codingRange, 
                                CodingRegionState.COMPLETE,
                                CodingRegionState.COMPLETE,
                                Arrays.<Exon>asList(new DefaultExon(Frame.ONE,codingRange)));
                String geneName = matcher.group(5);
                map.putIfAbsent(genome, new ArrayList<RefGene>());
                map.get(genome).add(
                        new DefaultRefGene(geneId, geneName,genome,Strand.FORWARD,codingRange,codingRegion));
                
            }
        }
        
        return map;
    }
    
   
}
