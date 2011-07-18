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
 * Created on Jun 8, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice.consensus;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import org.jcvi.assembly.slice.DefaultSlice;
import org.jcvi.assembly.slice.Slice;
import org.jcvi.assembly.slice.SliceElement;
import org.jcvi.common.core.seq.nuc.NucleotideGlyph;
import org.jcvi.util.MapValueComparator;

import static org.jcvi.assembly.slice.TestSliceUtil.*;
import static org.jcvi.common.core.seq.read.SequenceDirection.FORWARD;
import static org.jcvi.common.core.seq.read.SequenceDirection.REVERSE;
public final class ConsensusCallerTestUtil {

    
    
    private static List<Slice> createSlicesForContextCase1(){
        return createSlicesFrom(Arrays.asList("AAG","AGG"),
                    new byte[][]{new byte[]{30,30,35},new byte[]{35,30,28}},
                    Arrays.asList(FORWARD,FORWARD));
    }

    private static List<Slice> createSlicesForContextCase2(){
        return createSlicesFrom(Arrays.asList("TTG","TCG"),
                    new byte[][]{new byte[]{30,20,30},new byte[]{30,40,30}},
                    Arrays.asList(FORWARD,FORWARD));
    }

    private static List<Slice> createSlicesForContextCase3(){
        return createSlicesFrom(Arrays.asList("AGG","AAG"),
                    new byte[][]{new byte[]{30,46,30},new byte[]{30,15,30}},
                    Arrays.asList(FORWARD,FORWARD));
    }

    private static List<Slice> createSlicesForContextCase4(){
        return createSlicesFrom(Arrays.asList("ACG","A-G"),
                    new byte[][]{new byte[]{40,20,15},new byte[]{36,36,40}},
                    Arrays.asList(FORWARD,FORWARD));
    }

    private static List<Slice> createSlicesForContextCase5(){
        return createSlicesFrom(Arrays.asList("C-T","CTT"),
                    new byte[][]{new byte[]{20,20,24},new byte[]{35,44,38}},
                    Arrays.asList(FORWARD,FORWARD));
    }

    private static List<Slice> createSlicesForContextCase6(){
        return createSlicesFrom(Arrays.asList("TTC","TCC","TCC"),
                    new byte[][]{new byte[]{30,30,25},new byte[]{34,30,20},new byte[]{25,30,25}},
                    Arrays.asList(FORWARD,FORWARD,FORWARD));
    }

    private static List<Slice> createSlicesForContextCase7(){
        return createSlicesFrom(Arrays.asList("CAA","CGA","CGA"),
                    new byte[][]{new byte[]{40,15,30},new byte[]{22,35,35},new byte[]{25,32,40}},
                    Arrays.asList(FORWARD,REVERSE,FORWARD));
    }

    private static List<Slice> createSlicesForContextCase8(){
        return createSlicesFrom(Arrays.asList("TCG","TTG","TTG"),
                    new byte[][]{new byte[]{25,45,35},new byte[]{30,20,25},new byte[]{35,15,22}},
                    Arrays.asList(FORWARD,FORWARD,FORWARD));
    }

    private static List<Slice> createSlicesForContextCase9(){
        return createSlicesFrom(Arrays.asList("CCG","CCG","C-G"),
                    new byte[][]{new byte[]{20,15,22},new byte[]{15,20,20},new byte[]{45,40,40}},
                    Arrays.asList(FORWARD,FORWARD,FORWARD));
    }

    private static List<Slice> createSlicesForContextCase10(){
        return createSlicesFrom(Arrays.asList("A-T","A-T","AAT"),
                    new byte[][]{new byte[]{40,40,45},new byte[]{40,40,42},new byte[]{45,40,35}},
                    Arrays.asList(FORWARD,FORWARD,FORWARD));
    }

    private static List<Slice> createSlicesForContextCase11(){
        return createSlicesFrom(Arrays.asList("CGG","CGG","CAG","CGG"),
                    new byte[][]{new byte[]{35,30,39},new byte[]{30,30,30},new byte[]{32,30,35},new byte[]{20,30,25}},
                    Arrays.asList(FORWARD,REVERSE,FORWARD,FORWARD));
    }

    private static List<Slice> createSlicesForContextCase12(){
        return createSlicesFrom(Arrays.asList("GTA","GCA","GCA","GCA"),
                    new byte[][]{new byte[]{20,15,15},new byte[]{40,40,40},new byte[]{35,35,38},new byte[]{35,30,29}},
                    Arrays.asList(FORWARD,FORWARD,FORWARD,REVERSE));
    }

    private static List<Slice> createSlicesForContextCase13(){
        return createSlicesFrom(Arrays.asList("CAA","CGA","CGA","CGA"),
                    new byte[][]{new byte[]{40,45,45},new byte[]{15,20,22},new byte[]{18,15,20},new byte[]{20,22,15}},
                    Arrays.asList(FORWARD,FORWARD,FORWARD,REVERSE));
    }

    private static List<Slice> createSlicesForContextCase14(){
        return createSlicesFrom(Arrays.asList("GAT","GCT","GAT","GCT"),
                    new byte[][]{new byte[]{40,45,42},new byte[]{40,40,38},new byte[]{30,25,28},new byte[]{32,35,30}},
                    Arrays.asList(FORWARD,FORWARD,FORWARD,REVERSE));
    }

    private static List<Slice> createSlicesForContextCase15(){
        return createSlicesFrom(Arrays.asList("G-C","GGC","GGC","G-C"),
                    new byte[][]{new byte[]{40,35,35},new byte[]{25,20,24},new byte[]{22,25,20},new byte[]{35,35,40}},
                    Arrays.asList(FORWARD,FORWARD,FORWARD,REVERSE));
    }

    private static List<Slice> createSlicesForContextCase16(){
        return createSlicesFrom(Arrays.asList("GAT","GAT","GCT","GAT","GAT"),
                    new byte[][]{new byte[]{35,30,30},new byte[]{30,30,25},new byte[]{30,30,35},new byte[]{25,30,20},new byte[]{20,30,25}},
                    Arrays.asList(FORWARD,FORWARD,FORWARD,REVERSE,FORWARD));
    }

    private static List<Slice> createSlicesForContextCase17(){
        return createSlicesFrom(Arrays.asList("ATC","AGC","AGC","AGC","AGC"),
                    new byte[][]{new byte[]{18,15,20},new byte[]{35,40,36},new byte[]{40,38,38},new byte[]{38,32,40},new byte[]{36,25,38}},
                    Arrays.asList(FORWARD,FORWARD,FORWARD,REVERSE,FORWARD));
    }

    private static List<Slice> createSlicesForContextCase18(){
        return createSlicesFrom(Arrays.asList("CCC","CTC","CTC","CTC","CTC"),
                    new byte[][]{new byte[]{45,45,40},new byte[]{15,20,18},new byte[]{20,15,19},new byte[]{24,22,20},new byte[]{15,17,20}},
                    Arrays.asList(FORWARD,FORWARD,FORWARD,REVERSE,FORWARD));
    }

    private static List<Slice> createSlicesForContextCase19(){
        return createSlicesFrom(Arrays.asList("TTA","TTA","TCA","TCA","TTA"),
                    new byte[][]{new byte[]{30,30,30},new byte[]{32,30,39},new byte[]{25,30,37},new byte[]{35,30,32},new byte[]{30,32,17}},
                    Arrays.asList(FORWARD,FORWARD,FORWARD,REVERSE,FORWARD));
    }

    private static List<Slice> createSlicesForContextCase20(){
        return createSlicesFrom(Arrays.asList("AGT","AGT","AGT","AAT","AAT"),
                    new byte[][]{new byte[]{38,35,25},new byte[]{40,40,30},new byte[]{40,45,20},new byte[]{20,15,25},new byte[]{35,20,40}},
                    Arrays.asList(FORWARD,FORWARD,REVERSE,FORWARD,FORWARD));
    }

    private static List<Slice> createSlicesForContextCase21(){
        return createSlicesFrom(Arrays.asList("ATC","ACC","ATC","ACC","ACC"),
                    new byte[][]{new byte[]{40,45,40},new byte[]{18,15,25},new byte[]{30,40,35},new byte[]{20,18,15},new byte[]{35,20,35}},
                    Arrays.asList(FORWARD,FORWARD,REVERSE,FORWARD,FORWARD));
    }

    private static List<Slice> createSlicesForContextCase22(){
        return createSlicesFrom(Arrays.asList("T-G","TTG","TTG","T-G","TTG"),
                    new byte[][]{new byte[]{20,20,25},new byte[]{40,40,35},new byte[]{40,38,40},new byte[]{20,15,15},new byte[]{30,35,35}},
                    Arrays.asList(FORWARD,FORWARD,REVERSE,FORWARD,FORWARD));
    }

	private static List<Slice> createSlicesForContextCase23(){
        return createSlicesFrom(Arrays.asList("G-A","GGA","G-A","GGA","G-A"),
                    new byte[][]{new byte[]{45,38,38},new byte[]{35,20,30},new byte[]{40,30,30},new byte[]{30,25,35},new byte[]{42,35,35}},
                    Arrays.asList(FORWARD,FORWARD,REVERSE,FORWARD,FORWARD));
    }

	private static List<Slice> createSlicesForContextCase24(){
        return createSlicesFrom(Arrays.asList("C-C","C-C","C-C","CCC","CCC"),
                    new byte[][]{new byte[]{20,20,20},new byte[]{25,25,25},new byte[]{22,22,28},new byte[]{45,40,29},new byte[]{40,39,25}},
                    Arrays.asList(FORWARD,FORWARD,FORWARD,FORWARD,FORWARD));
    }

	private static List<Slice> createSlicesForContextCase25(){
        return createSlicesFrom(Arrays.asList("AGG","AGG","A-G","A-G","AGG"),
                    new byte[][]{new byte[]{30,35,32},new byte[]{33,30,35},new byte[]{15,15,20},new byte[]{20,20,25},new byte[]{40,45,33}},
                    Arrays.asList(FORWARD,REVERSE,FORWARD,FORWARD,FORWARD));
    }

	private static List<Slice> createSlicesForContextCase26(){
        return createSlicesFrom(Arrays.asList("CAG","CAG","CAG","CAG","CAG","CCG"),
                    new byte[][]{new byte[]{30,30,30},new byte[]{35,30,35},new byte[]{30,30,32},new byte[]{32,30,33},new byte[]{26,30,25},new byte[]{35,32,30}},
                    Arrays.asList(FORWARD,REVERSE,FORWARD,FORWARD,FORWARD,FORWARD));
    }

	private static List<Slice> createSlicesForContextCase27(){
        return createSlicesFrom(Arrays.asList("AGG","AGG","ATG","AGG","AGG","AGG"),
                    new byte[][]{new byte[]{15,20,18},new byte[]{20,15,18},new byte[]{40,45,44},new byte[]{19,20,25},new byte[]{17,20,25},new byte[]{15,18,30}},
                    Arrays.asList(FORWARD,REVERSE,FORWARD,FORWARD,FORWARD,FORWARD));
    }

	private static List<Slice> createSlicesForContextCase28(){
        return createSlicesFrom(Arrays.asList("CCC","CCC","CCC","CCC","CCC","CGC"),
                    new byte[][]{new byte[]{44,45,40},new byte[]{33,35,38},new byte[]{33,25,36},new byte[]{32,32,33},new byte[]{44,40,38},new byte[]{18,19,22}},
                    Arrays.asList(FORWARD,FORWARD,REVERSE,FORWARD,FORWARD,FORWARD));
    }

	private static List<Slice> createSlicesForContextCase29(){
        return createSlicesFrom(Arrays.asList("TAG","TTG","TTG","TTG","TTG","TAG"),
                    new byte[][]{new byte[]{29,30,35},new byte[]{29,25,20},new byte[]{28,30,25},new byte[]{25,30,33},new byte[]{30,35,36},new byte[]{30,35,33}},
                    Arrays.asList(FORWARD,FORWARD,REVERSE,FORWARD,FORWARD,FORWARD));
    }

	private static List<Slice> createSlicesForContextCase30(){
        return createSlicesFrom(Arrays.asList("GAT","GCT","GCT","GCT","GCT","GAT"),
                    new byte[][]{new byte[]{44,45,46},new byte[]{30,30,36},new byte[]{23,22,39},new byte[]{30,30,30},new byte[]{21,21,31},new byte[]{46,48,45}},
                    Arrays.asList(FORWARD,FORWARD,REVERSE,FORWARD,FORWARD,FORWARD));
    }

	private static List<Slice> createSlicesForContextCase31(){
        return createSlicesFrom(Arrays.asList("CAA","CAA","CAA","CAA","CCA","CCA"),
                    new byte[][]{new byte[]{35,30,25},new byte[]{25,30,32},new byte[]{28,30,32},new byte[]{39,30,33},new byte[]{15,20,20},new byte[]{20,15,15}},
                    Arrays.asList(FORWARD,FORWARD,REVERSE,FORWARD,FORWARD,FORWARD));
    }

	private static List<Slice> createSlicesForContextCase32(){
        return createSlicesFrom(Arrays.asList("GCA","GCA","GCA","GTA","GTA","GTA"),
                    new byte[][]{new byte[]{35,30,25},new byte[]{25,30,32},new byte[]{28,30,32},new byte[]{39,30,33},new byte[]{15,20,20},new byte[]{20,15,15}},
                    Arrays.asList(FORWARD,FORWARD,REVERSE,FORWARD,FORWARD,FORWARD));
    }

	private static List<Slice> createSlicesForContextCase33(){
        return createSlicesFrom(Arrays.asList("TAC","TGC","TGC","TGC","TAC","TAC"),
                    new byte[][]{new byte[]{39,40,38},new byte[]{44,45,46},new byte[]{40,42,41},new byte[]{40,40,40},new byte[]{15,20,20},new byte[]{15,15,15}},
                    Arrays.asList(FORWARD,FORWARD,REVERSE,FORWARD,FORWARD,FORWARD));
    }

	private static List<Slice> createSlicesForContextCase34(){
        return createSlicesFrom(Arrays.asList("TAT","T-T","T-T","TAT","TAT","T-T"),
                    new byte[][]{new byte[]{20,20,22},new byte[]{40,40,45},new byte[]{35,35,40},new byte[]{18,15,20},new byte[]{22,25,20},new byte[]{36,36,38}},
                    Arrays.asList(FORWARD,FORWARD,REVERSE,FORWARD,FORWARD,FORWARD));
    }

	private static List<Slice> createSlicesForContextCase35(){
        return createSlicesFrom(Arrays.asList("C-T","CGT","C-T","CGT","C-T","C-T"),
                    new byte[][]{new byte[]{29,29,35},new byte[]{33,35,33},new byte[]{25,25,30},new byte[]{22,20,34},new byte[]{45,45,45},new byte[]{40,40,42}},
                    Arrays.asList(FORWARD,FORWARD,REVERSE,FORWARD,FORWARD,FORWARD));
    }

	private static List<Slice> createSlicesForContextCase36(){
        return createSlicesFrom(Arrays.asList("ACC","ACC","ACC","AGC","ACC","ACC","ACC"),
                    new byte[][]{new byte[]{30,30,30},new byte[]{30,32,33},new byte[]{33,35,34},new byte[]{34,35,40},new byte[]{25,28,32},new byte[]{30,30,35},new byte[]{30,28,30}},
                    Arrays.asList(FORWARD,FORWARD,REVERSE,FORWARD,FORWARD,FORWARD,FORWARD));
    }

	private static List<Slice> createSlicesForContextCase37(){
        return createSlicesFrom(Arrays.asList("AAT","AAT","AAT","ACT","AAT","AAT","AAT"),
                    new byte[][]{new byte[]{38,40,35},new byte[]{35,35,30},new byte[]{40,38,35},new byte[]{35,20,25},new byte[]{40,45,44},new byte[]{41,40,45},new byte[]{35,38,35}},
                    Arrays.asList(FORWARD,FORWARD,REVERSE,FORWARD,FORWARD,FORWARD,FORWARD));
    }

	private static List<Slice> createSlicesForContextCase38(){
        return createSlicesFrom(Arrays.asList("TTT","TTT","TTT","TTT","TTT","TGT","TTT"),
                    new byte[][]{new byte[]{19,20,21},new byte[]{29,30,31},new byte[]{24,25,22},new byte[]{22,20,20},new byte[]{22,20,20},new byte[]{44,45,45},new byte[]{18,15,20}},
                    Arrays.asList(FORWARD,FORWARD,REVERSE,FORWARD,FORWARD,FORWARD,FORWARD));
    }

	private static List<Slice> createSlicesForContextCase39(){
        return createSlicesFrom(Arrays.asList("CGA","CGA","CGA","CCA","CCA","CGA","CGA"),
                    new byte[][]{new byte[]{28,30,31},new byte[]{33,35,30},new byte[]{28,25,22},new byte[]{28,28,27},new byte[]{44,45,46},new byte[]{30,32,29},new byte[]{29,31,29}},
                    Arrays.asList(FORWARD,FORWARD,FORWARD,FORWARD,FORWARD,FORWARD,REVERSE));
    }

	private static List<Slice> createSlicesForContextCase40(){
        return createSlicesFrom(Arrays.asList("TGC","TCC","TCC","TGC","TCC","TCC","TCC"),
                    new byte[][]{new byte[]{26,25,28},new byte[]{43,40,42},new byte[]{40,37,42},new byte[]{19,20,23},new byte[]{32,30,33},new byte[]{33,34,32},new byte[]{36,37,33}},
                    Arrays.asList(FORWARD,FORWARD,FORWARD,FORWARD,FORWARD,FORWARD,REVERSE));
    }

	private static List<Slice> createSlicesForContextCase41(){
        return createSlicesFrom(Arrays.asList("ACT","ACT","ACT","ACT","ATT","ATT","ACT"),
                    new byte[][]{new byte[]{35,30,28},new byte[]{25,29,33},new byte[]{15,20,30},new byte[]{17,22,25},new byte[]{45,45,45},new byte[]{40,44,44},new byte[]{25,20,20}},
                    Arrays.asList(FORWARD,FORWARD,FORWARD,FORWARD,FORWARD,FORWARD,REVERSE));
    }

	private static List<Slice> createSlicesForContextCase42(){
        return createSlicesFrom(Arrays.asList("GCT","GCT","GCT","GGT","GGT","GGT","GCT"),
                    new byte[][]{new byte[]{33,30,29},new byte[]{32,25,28},new byte[]{27,22,25},new byte[]{34,35,36},new byte[]{22,20,20},new byte[]{37,35,35},new byte[]{18,15,20}},
                    Arrays.asList(FORWARD,FORWARD,FORWARD,FORWARD,FORWARD,FORWARD,REVERSE));
    }

	private static List<Slice> createSlicesForContextCase43(){
        return createSlicesFrom(Arrays.asList("CAT","CAT","CAT","CAT","CTT","CTT","CTT"),
                    new byte[][]{new byte[]{39,45,46},new byte[]{44,45,43},new byte[]{35,40,41},new byte[]{44,38,40},new byte[]{22,20,19},new byte[]{14,15,15},new byte[]{13,18,20}},
                    Arrays.asList(FORWARD,FORWARD,FORWARD,REVERSE,FORWARD,FORWARD,REVERSE));
    }

	private static List<Slice> createSlicesForContextCase44(){
        return createSlicesFrom(Arrays.asList("GTA","GTA","GAA","GAA","GTA","GAA","GAA"),
                    new byte[][]{new byte[]{29,30,33},new byte[]{33,35,32},new byte[]{18,20,21},new byte[]{16,15,10},new byte[]{40,45,44},new byte[]{21,18,19},new byte[]{23,19,18}},
                    Arrays.asList(FORWARD,FORWARD,FORWARD,REVERSE,FORWARD,FORWARD,REVERSE));
    }

	private static List<Slice> createSlicesForContextCase45(){
        return createSlicesFrom(Arrays.asList("C-C","C-C","CGC","CGC","C-C","C-C","CGC"),
                    new byte[][]{new byte[]{30,30,40},new byte[]{35,30,30},new byte[]{18,15,20},new byte[]{22,20,28},new byte[]{26,26,45},new byte[]{30,30,40},new byte[]{23,18,25}},
                    Arrays.asList(FORWARD,REVERSE,FORWARD,REVERSE,FORWARD,FORWARD,REVERSE));
    }

	private static List<Slice> createSlicesForContextCase46(){
        return createSlicesFrom(Arrays.asList("A-C","A-C","A-C","A-C","AAC","AAC","AAC"),
                    new byte[][]{new byte[]{20,20,20},new byte[]{15,15,25},new byte[]{10,10,22},new byte[]{25,20,20},new byte[]{40,45,48},new byte[]{30,40,35},new byte[]{35,38,40}},
                    Arrays.asList(FORWARD,REVERSE,FORWARD,REVERSE,FORWARD,FORWARD,REVERSE));
    }

	private static List<Slice> createSlicesForContextCase47(){
        return createSlicesFrom(Arrays.asList("G-T","G-T","GCT","G-T","G-T","G-T","GCT"),
                    new byte[][]{new byte[]{20,20,25},new byte[]{20,20,25},new byte[]{40,42,40},new byte[]{24,24,28},new byte[]{22,22,30},new byte[]{20,20,30},new byte[]{40,45,40}},
                    Arrays.asList(FORWARD,REVERSE,FORWARD,REVERSE,FORWARD,FORWARD,REVERSE));
    }

	private static List<Slice> createSlicesForContextCase48(){
        return createSlicesFrom(Arrays.asList("T-T","T-T","T-T","TTT","TTT","T-T","T-T"),
                    new byte[][]{new byte[]{40,35,35},new byte[]{45,40,40},new byte[]{30,25,25},new byte[]{20,15,20},new byte[]{20,20,30},new byte[]{35,30,30},new byte[]{35,35,40}},
                    Arrays.asList(FORWARD,REVERSE,FORWARD,REVERSE,FORWARD,FORWARD,REVERSE));
    }

	private static List<Slice> createSlicesForContextCase49(){
        return createSlicesFrom(Arrays.asList("C-A","C-A","C-A","C-A","CGA","C-A","C-A"),
                    new byte[][]{new byte[]{20,20,25},new byte[]{15,15,20},new byte[]{30,30,35},new byte[]{30,30,35},new byte[]{35,40,40},new byte[]{38,38,42},new byte[]{38,38,44}},
                    Arrays.asList(FORWARD,REVERSE,FORWARD,REVERSE,FORWARD,FORWARD,REVERSE));
    }

	private static List<Slice> createSlicesForContextCase50(){
        return createSlicesFrom(Arrays.asList("TTA","T-A","T-A","T-A","T-A","T-A","T-A"),
                    new byte[][]{new byte[]{35,30,30},new byte[]{45,40,40},new byte[]{40,40,40},new byte[]{20,20,25},new byte[]{25,25,30},new byte[]{35,35,40},new byte[]{30,30,35}},
                    Arrays.asList(FORWARD,REVERSE,FORWARD,REVERSE,FORWARD,FORWARD,REVERSE));
    }

	private static List<Slice> createSlicesForContextCase51(){
        return createSlicesFrom(Arrays.asList("A--A","A--A","A--A","A--A","A--A","ACCA","ACCA"),
                    new byte[][]{new byte[]{40,40,40,40},new byte[]{40,40,40,40},new byte[]{39,39,39,45},new byte[]{35,35,35,45},new byte[]{35,35,35,40},new byte[]{25,35,20,20},new byte[]{25,35,25,20}},
                    Arrays.asList(FORWARD,REVERSE,FORWARD,REVERSE,FORWARD,FORWARD,FORWARD));
    }

	private static List<Slice> createSlicesForContextCase52(){
        return createSlicesFrom(Arrays.asList("CAGT","CCGT","CCGT","CAGT","CAGT","CATT","CATT"),
                    new byte[][]{new byte[]{25,30,35,30},new byte[]{25,30,35,35},new byte[]{30,35,35,35},new byte[]{30,30,35,30},new byte[]{30,30,35,30},new byte[]{40,45,40,40},new byte[]{40,45,43,40}},
                    Arrays.asList(FORWARD,REVERSE,REVERSE,FORWARD,FORWARD,FORWARD,FORWARD));
    }

	private static List<Slice> createSlicesForContextCase53(){
        return createSlicesFrom(Arrays.asList("A-GT","A-GT","A-GT","AA-T","AA-T","AAGT","AAGT"),
                    new byte[][]{new byte[]{45,35,35,35},new byte[]{40,40,40,40},new byte[]{40,40,45,45},new byte[]{30,30,30,35},new byte[]{35,30,30,33},new byte[]{32,30,40,44},new byte[]{33,35,45,40}},
                    Arrays.asList(FORWARD,REVERSE,REVERSE,FORWARD,FORWARD,FORWARD,FORWARD));
    }

	private static List<Slice> createSlicesForContextCase54(){
        return createSlicesFrom(Arrays.asList("G--A","G--A","G--A","GCAA","GCAA","GCAA","GCAA"),
                    new byte[][]{new byte[]{45,45,45,45},new byte[]{40,40,40,40},new byte[]{41,35,35,35},new byte[]{20,30,25,20},new byte[]{20,35,20,25},new byte[]{20,30,30,30},new byte[]{30,25,25,34}},
                    Arrays.asList(FORWARD,REVERSE,REVERSE,FORWARD,FORWARD,FORWARD,FORWARD));
    }

	private static List<Slice> createSlicesForContextCase55(){
        return createSlicesFrom(Arrays.asList("ACG","ACG","ACG","ACG","AGG","AGG","AGG"),
                    new byte[][]{new byte[]{15,17,19},new byte[]{16,16,18},new byte[]{15,14,17},new byte[]{14,12,16},new byte[]{14,17,15},new byte[]{13,19,14},new byte[]{12,20,12}},
                    Arrays.asList(FORWARD,REVERSE,REVERSE,FORWARD,FORWARD,FORWARD,FORWARD));
    }

	private static List<Slice> createSlicesForContextCase56(){
        return createSlicesFrom(Arrays.asList("TAGC","TATC","TAGC","TCTC","TCTC","TCTC","TAGC","TCGC"),
                    new byte[][]{new byte[]{30,30,30,30},new byte[]{30,30,30,31},new byte[]{29,30,28,27},new byte[]{38,30,29,26},new byte[]{33,30,31,29},new byte[]{33,33,33,30},new byte[]{35,35,33,31},new byte[]{39,40,41,48}},
                    Arrays.asList(FORWARD,REVERSE,REVERSE,FORWARD,FORWARD,FORWARD,FORWARD,FORWARD));
    }

	private static List<Slice> createSlicesForContextCase57(){
        return createSlicesFrom(Arrays.asList("CATT","CATT","CATT","CTTT","CAAT","CATT","CATT","CATT"),
                    new byte[][]{new byte[]{33,32,40,38},new byte[]{32,30,38,37},new byte[]{35,33,42,39},new byte[]{40,45,43,40},new byte[]{20,20,20,21},new byte[]{29,25,33,33},new byte[]{28,29,32,30},new byte[]{28,25,33,30}},
                    Arrays.asList(FORWARD,REVERSE,REVERSE,FORWARD,FORWARD,FORWARD,FORWARD,FORWARD));
    }

	private static List<Slice> createSlicesForContextCase58(){
        return createSlicesFrom(Arrays.asList("GGTA","GGTA","GGGA","GGGA","GATA","GATA","GGTA","GGTA"),
                    new byte[][]{new byte[]{34,33,38,35},new byte[]{35,33,39,35},new byte[]{22,25,20,20},new byte[]{26,28,22,21},new byte[]{37,36,39,37},new byte[]{38,37,40,36},new byte[]{38,39,40,39},new byte[]{35,37,41,40}},
                    Arrays.asList(FORWARD,REVERSE,REVERSE,FORWARD,FORWARD,FORWARD,FORWARD,FORWARD));
    }

	private static List<Slice> createSlicesForContextCase59(){
        return createSlicesFrom(Arrays.asList("A--T","A-AT","A-AT","AA-T","AA-T","AA-T","AA-T","AAAT"),
                    new byte[][]{new byte[]{35,33,33,33},new byte[]{30,30,30,32},new byte[]{33,30,30,30},new byte[]{32,30,30,35},new byte[]{33,34,34,34},new byte[]{30,34,34,35},new byte[]{30,34,33,33},new byte[]{41,45,40,41}},
                    Arrays.asList(FORWARD,REVERSE,REVERSE,FORWARD,FORWARD,FORWARD,FORWARD,FORWARD));
    }

	private static List<Slice> createSlicesForContextCase60(){
        return createSlicesFrom(Arrays.asList("CGA","CCA","CGA","CGA","CGA","CCA","CCA","CGG"),
                    new byte[][]{new byte[]{21,22,25},new byte[]{44,45,44},new byte[]{22,25,23},new byte[]{20,20,21},new byte[]{20,20,23},new byte[]{41,44,43},new byte[]{41,44,42},new byte[]{22,20,21}},
                    Arrays.asList(FORWARD,REVERSE,REVERSE,FORWARD,FORWARD,FORWARD,FORWARD,FORWARD));
    }

	private static List<Slice> createSlicesForContextCase61(){
        return createSlicesFrom(Arrays.asList("G-T","G-T","G-T","GTT","G-T","G-T","G-T"),
                    new byte[][]{new byte[]{44,38,38},new byte[]{28,28,28},new byte[]{36,29,29},new byte[]{47,47,47},new byte[]{40,35,35},new byte[]{33,33,35},new byte[]{35,23,23}},
                    Arrays.asList(FORWARD,REVERSE,REVERSE,FORWARD,FORWARD,FORWARD,FORWARD));
    }

	private static List<Slice> createSlicesForContextCase62(){
        return createSlicesFrom(Arrays.asList("TATC","TATC","TATC","TATC","TATC","TATC","TCAC","TATC","TATC"),
                    new byte[][]{new byte[]{44,35,35,35},new byte[]{31,32,32,29},new byte[]{47,47,35,38},new byte[]{36,47,36,47},new byte[]{40,35,40,34},new byte[]{47,47,47,47},new byte[]{36,45,38,40},new byte[]{34,35,35,35},new byte[]{35,35,33,33}},
                    Arrays.asList(FORWARD,REVERSE,REVERSE,FORWARD,FORWARD,FORWARD,FORWARD,FORWARD,FORWARD));
    }

	private static List<Slice> createSlicesForContextCase63(){
        return createSlicesFrom(Arrays.asList("T-A","T-A","T-A","T-A","T-A","T-A","T-A","T-A","T-A","TAA","T-A","T-A","T-A"),
                    new byte[][]{new byte[]{47,47,47},new byte[]{44,44,45},new byte[]{44,44,45},new byte[]{45,40,40},new byte[]{47,44,44},new byte[]{35,35,35},new byte[]{39,32,32},new byte[]{45,40,40},new byte[]{45,45,47},new byte[]{47,47,47},new byte[]{44,40,40},new byte[]{47,44,44},new byte[]{23,23,29}},
                    Arrays.asList(FORWARD,REVERSE,REVERSE,FORWARD,FORWARD,FORWARD,FORWARD,FORWARD,FORWARD,FORWARD,FORWARD,FORWARD,FORWARD));
    }

	private static List<Slice> createSlicesForContextCase64(){
        return createSlicesFrom(Arrays.asList("CGA","CAA","CGA","CGA","CAA","CGA","CAA","CGA","CGA","CGA","CAA","CAA","CAA","CGA","CGA","CGA","CGA"),
                    new byte[][]{new byte[]{24,30,31},new byte[]{32,40,16},new byte[]{44,44,45},new byte[]{40,44,44},new byte[]{32,34,36},new byte[]{33,29,14},new byte[]{31,34,31},new byte[]{47,49,49},new byte[]{47,47,47},new byte[]{47,49,47},new byte[]{41,41,47},new byte[]{38,34,35},new byte[]{39,49,28},new byte[]{40,47,47},new byte[]{40,44,40},new byte[]{34,40,40},new byte[]{19,23,33}},
                    Arrays.asList(FORWARD,REVERSE,REVERSE,FORWARD,FORWARD,FORWARD,FORWARD,FORWARD,FORWARD,FORWARD,FORWARD,FORWARD,FORWARD,FORWARD,FORWARD,FORWARD,FORWARD));
    }

	private static List<Slice> createSlicesForContextCase65(){
        return createSlicesFrom(Arrays.asList("TAA","T-A","T-A","T-A","T-A","T-A","T-A","T-A","T-A","T-A","T-A","T-A","T-A","T-A","T-A","T-A","T-A","T-A","T-A","T-A"),
                    new byte[][]{new byte[]{35,40,44},new byte[]{45,40,40},new byte[]{33,31,31},new byte[]{44,40,40},new byte[]{44,44,45},new byte[]{47,47,47},new byte[]{47,44,44},new byte[]{44,35,35},new byte[]{47,47,47},new byte[]{35,35,38},new byte[]{45,45,47},new byte[]{44,36,36},new byte[]{47,47,47},new byte[]{34,34,35},new byte[]{40,40,40},new byte[]{47,47,47},new byte[]{45,36,36},new byte[]{40,30,30},new byte[]{47,34,34},new byte[]{35,35,44}},
                    Arrays.asList(FORWARD,FORWARD,FORWARD,REVERSE,FORWARD,FORWARD,FORWARD,FORWARD,REVERSE,FORWARD,FORWARD,REVERSE,FORWARD,FORWARD,FORWARD,FORWARD,FORWARD,FORWARD,FORWARD,FORWARD));
    }

	private static List<Slice> createSlicesForContextCase66(){
        return createSlicesFrom(Arrays.asList("AAG","ATG","ATG","ATG","AAG","AAG"),
                    new byte[][]{new byte[]{14,16,13},new byte[]{21,20,22},new byte[]{47,47,47},new byte[]{39,27,27},new byte[]{17,42,35},new byte[]{35,35,35}},
                    Arrays.asList(FORWARD,REVERSE,FORWARD,FORWARD,REVERSE,FORWARD));
    }

	private static List<Slice> createSlicesForContextCase67(){
        return createSlicesFrom(Arrays.asList("T-G","TTG","T-G"),
                    new byte[][]{new byte[]{9,9,9},new byte[]{40,44,44},new byte[]{16,16,19}},
                    Arrays.asList(FORWARD,REVERSE,FORWARD));
    }

	private static List<Slice> createSlicesForContextCase68(){
        return createSlicesFrom(Arrays.asList("GTG","GTG","GAG"),
                    new byte[][]{new byte[]{47,47,47},new byte[]{45,40,44},new byte[]{28,47,41}},
                    Arrays.asList(FORWARD,REVERSE,FORWARD));
    }

	private static List<Slice> createSlicesForContextCase69(){
        return createSlicesFrom(Arrays.asList("TTC","TTC","T-C","T-C"),
                    new byte[][]{new byte[]{27,20,20},new byte[]{45,47,47},new byte[]{12,12,14},new byte[]{17,17,17}},
                    Arrays.asList(FORWARD,REVERSE,FORWARD,REVERSE));
    }

	private static List<Slice> createSlicesForContextCase70(){
        return createSlicesFrom(Arrays.asList("C-A","C-A","C-A","CAA","C-A","C-A","C-A"),
                    new byte[][]{new byte[]{44,44,47},new byte[]{31,31,35},new byte[]{35,35,44},new byte[]{40,44,44},new byte[]{40,40,44},new byte[]{36,32,32},new byte[]{27,27,34}},
                    Arrays.asList(FORWARD,REVERSE,FORWARD,REVERSE,FORWARD,REVERSE,FORWARD));
    }

	private static List<Slice> createSlicesForisolatedCase1(){
    
    return Arrays.asList(createIsolatedSliceFrom(
            "AAAAAAAAAAGGGGG",
            21,34,44,44,47,47,47,49,49,49,23,27,28,35,47
        ));
	}
	private static List<Slice> createSlicesForisolatedCase2(){
    
    return Arrays.asList(createIsolatedSliceFrom(
            "CCCCCCCCCCCTTTTTTT",
            20,28,29,34,40,44,45,47,47,47,47,29,35,39,41,42,47,47
    	));
	}
	private static List<Slice> createSlicesForisolatedCase3(){
    
    return Arrays.asList(createIsolatedSliceFrom(
            "AAAAAAACCCCCCCCCCC",
            17,29,35,45,44,47,50,19,30,35,41,41,44,47,47,47,47,47
    ));
}
	private static List<Slice> createSlicesForisolatedCase4(){
    
    return Arrays.asList(createIsolatedSliceFrom(
            "-------GGGGGGGGG",
            35,47,47,40,36,32,30,29,44,44,47,47,47,47,47,49
    ));
}
	private static List<Slice> createSlicesForisolatedCase5(){
    
    return Arrays.asList(createIsolatedSliceFrom(
            "-------GGGGGGGGG",
            35,47,47,40,36,32,30,38,40,41,47,47,47,47,47,47
    ));
}

	private static List<Slice> createSlicesForisolatedCase6(){
    
    return Arrays.asList(createIsolatedSliceFrom(
            "AAAAAAAAGGGGGGGGG",
            28,30,32,40,47,47,48,56,37,41,44,47,47,47,47,47,47
    ));
}
	private static List<Slice> createSlicesForisolatedCase7(){
    
    return Arrays.asList(createIsolatedSliceFrom(
            "-------TTTTTTTT",
            47,47,47,44,44,40,33,21,33,35,35,37,44,47,50
    ));
}
	private static List<Slice> createSlicesForisolatedCase8(){
    
    return Arrays.asList(createIsolatedSliceFrom(
            "-------TTTTTTTT",
            47,47,47,44,44,40,33,23,23,35,37,37,44,44,44
    ));
}
	private static List<Slice> createSlicesForisolatedCase9(){
    
    return Arrays.asList(createIsolatedSliceFrom(
            "-------GGGGGGGG",
            47,47,47,44,44,40,33,23,28,34,40,42,44,47,47
    ));
}
	private static List<Slice> createSlicesForisolatedCase10(){
    
    return Arrays.asList(createIsolatedSliceFrom(
            "CCCCCCCTTTTTTT",
            26,34,40,40,45,49,51,40,40,44,44,44,47,49
    ));
}
	private static List<Slice> createSlicesForisolatedCase11(){
    
    return Arrays.asList(createIsolatedSliceFrom(
            "AAAAAAGGGGGGG",
            35,36,40,40,44,47,22,32,34,35,36,36,40
    ));
}
	private static List<Slice> createSlicesForisolatedCase12(){
    
    return Arrays.asList(createIsolatedSliceFrom(
            "GGGGGGGGGTTTTTT",
            13,24,33,36,36,44,45,45,47,33,35,38,44,44,47
    ));
}
	private static List<Slice> createSlicesForisolatedCase13(){
    
    return Arrays.asList(createIsolatedSliceFrom(
            "CCCCCCCCCCCCTTT",
            14,19,20,20,23,27,31,34,36,44,47,56,40,47,49
    ));
}
	private static List<Slice> createSlicesForisolatedCase14(){
    
    return Arrays.asList(createIsolatedSliceFrom(
            "CCTTTT",
            30,35,13,27,44,44
    ));
}
	private static List<Slice> createSlicesForisolatedCase15(){
    
    return Arrays.asList(createIsolatedSliceFrom(
            "CCCCGGGGG",
            41,41,47,47,12,32,35,40,47
    ));
}
	private static List<Slice> createSlicesForisolatedCase16(){
    
    return Arrays.asList(createIsolatedSliceFrom(
            "AAAAAAAGGGGGGGG",
            36,41,45,45,47,47,47,19,25,28,30,35,45,49,56
    ));
}
	private static List<Slice> createSlicesForisolatedCase17(){
    
    return Arrays.asList(createIsolatedSliceFrom(
            "AAAAAAACCCCCCCCC",
            34,40,40,41,47,47,49,35,35,45,47,47,47,47,47,47
    ));
}
	private static List<Slice> createSlicesForisolatedCase18(){
    
    return Arrays.asList(createIsolatedSliceFrom(
            "CCCCCCTTTTTTTT",
            30,32,36,45,45,47,34,34,44,44,47,47,47,49
    ));
}
	private static List<Slice> createSlicesForisolatedCase19(){
    
    return Arrays.asList(createIsolatedSliceFrom(
            "AAAAAAAAGGGGGG",
            30,41,45,47,47,47,47,47,34,35,36,40,45,47
    ));
}

	private static List<Slice> createSlicesForisolatedCase20(){
    
    return Arrays.asList(createIsolatedSliceFrom(
            "AGGGAAGGGAGAAGAG",
            48,47,41,47,32,47,47,47,37,30,47,56,47,44,40,47
    ));
}

	private static List<Slice> createSlicesForisolatedCase21(){
    
    return Arrays.asList(createIsolatedSliceFrom(
            "AAAAAAA-AAAAAA-AAAAAAAAAAAAAAAAA",
            35,29,35,40,31,29,32,22,24,26,38,47,35,26,47,35,24,17,47,23,40,18,47,47,40,40,40,40,21,31,38,17
    ));
}

	private static List<Slice> createEmptySlice(){
    return Arrays.<Slice>asList(new DefaultSlice.Builder().build());
}
private static List<ConsensusResult> createConsensusResults(String basecalls, int... consensusQualities){
    List<ConsensusResult> result = new ArrayList<ConsensusResult>();
    for(int i=0; i< basecalls.length(); i++){
        result.add(new DefaultConsensusResult(NucleotideGlyph.getGlyphFor(basecalls.charAt(i)), consensusQualities[i]));
    }
    return result;
}

public static Map<List<Slice>, List<ConsensusResult>> generateMostCommonBasecallData(){
    Map<List<Slice>, List<ConsensusResult>> map = generateConicData();
    Map<List<Slice>, List<ConsensusResult>> ret = new LinkedHashMap<List<Slice>, List<ConsensusResult>>(map.size());
    for(List<Slice> key : map.keySet()){
    	List<ConsensusResult> consensusResults = new ArrayList<ConsensusResult>();
    	
    	for(Slice s : key){
    		if(s.getCoverageDepth()==0){
    			consensusResults.add(new DefaultConsensusResult(NucleotideGlyph.Unknown, 0));
    			continue;
    		}
    		Map<NucleotideGlyph, Integer> histogram = new EnumMap<NucleotideGlyph, Integer>(NucleotideGlyph.class);
    		for(NucleotideGlyph bases : NucleotideGlyph.getGlyphsFor("ACGT-")){
    			histogram.put(bases, Integer.valueOf(0));
    		}
    		for(SliceElement e : s){
    			histogram.put(e.getBase(),histogram.get(e.getBase()) +1);
    		}
    		SortedMap<NucleotideGlyph, Integer> sortedMap = MapValueComparator.sortDescending(histogram);
    		NucleotideGlyph mostCommonBase =sortedMap.firstKey();
    		int consensusQuality=0;
    		for(SliceElement e : s){
    			if(e.getBase() == mostCommonBase){
    				consensusQuality += e.getQuality().getNumber();
    			}else{
    				consensusQuality -= e.getQuality().getNumber();
    			}
    		}
    		consensusResults.add(new DefaultConsensusResult(mostCommonBase, consensusQuality));
    	}
    	ret.put(key, consensusResults);
    }
    
    return ret;
    
    
}

public static Map<List<Slice>, List<ConsensusResult>> generateConicData(){
    Map<List<Slice>, List<ConsensusResult>> map = new LinkedHashMap<List<Slice>, List<ConsensusResult>>();
    map.put(createEmptySlice(), createConsensusResults("-",0));
    map.put(createSlicesForContextCase1(),createConsensusResults("ARG",65,3,63));
    map.put(createSlicesForContextCase2(),createConsensusResults("TYG",60,20,60));
    map.put(createSlicesForContextCase3(),createConsensusResults("AGG",60,31,60));
    map.put(createSlicesForContextCase4(),createConsensusResults("ACG",76,16,55));
    map.put(createSlicesForContextCase5(),createConsensusResults("CTT",55,24,62));
    map.put(createSlicesForContextCase6(),createConsensusResults("TYC",89,30,70));
    map.put(createSlicesForContextCase7(),createConsensusResults("CGA",87,52,105));
    map.put(createSlicesForContextCase8(),createConsensusResults("TYG",90,10,82));
    map.put(createSlicesForContextCase9(),createConsensusResults("CCG",80,6,82));
    map.put(createSlicesForContextCase10(),createConsensusResults("AAT",125,40,122));
    map.put(createSlicesForContextCase11(),createConsensusResults("CGG",117,60,129));
    map.put(createSlicesForContextCase12(),createConsensusResults("GCA",130,90,122));
    map.put(createSlicesForContextCase13(),createConsensusResults("CRA",93,12,102));
    map.put(createSlicesForContextCase14(),createConsensusResults("GMT",142,6,138));
    map.put(createSlicesForContextCase15(),createConsensusResults("GGC",122,25,119));
    map.put(createSlicesForContextCase16(),createConsensusResults("GAT",140,90,135));
    map.put(createSlicesForContextCase17(),createConsensusResults("AGC",167,120,172));
    map.put(createSlicesForContextCase18(),createConsensusResults("CYC",119,29,117));
    map.put(createSlicesForContextCase19(),createConsensusResults("TYA",152,32,155));
    map.put(createSlicesForContextCase20(),createConsensusResults("AGT",173,85,140));
    map.put(createSlicesForContextCase21(),createConsensusResults("AYC",143,32,150));
    map.put(createSlicesForContextCase22(),createConsensusResults("TTG",150,78,150));
    map.put(createSlicesForContextCase23(),createConsensusResults("G-A",192,58,168));
    map.put(createSlicesForContextCase24(),createConsensusResults("CCC",152,12,127));
    map.put(createSlicesForContextCase25(),createConsensusResults("AGG",138,75,145));
    map.put(createSlicesForContextCase26(),createConsensusResults("CAG",188,118,185));
    map.put(createSlicesForContextCase27(),createConsensusResults("AGG",126,48,160));
    map.put(createSlicesForContextCase28(),createConsensusResults("CCC",204,158,207));
    map.put(createSlicesForContextCase29(),createConsensusResults("TWG",171,55,182));
    map.put(createSlicesForContextCase30(),createConsensusResults("GMT",194,10,227));
    map.put(createSlicesForContextCase31(),createConsensusResults("CAA",162,85,157));
    map.put(createSlicesForContextCase32(),createConsensusResults("GYA",162,25,157));
    map.put(createSlicesForContextCase33(),createConsensusResults("TRC",193,52,200));
    map.put(createSlicesForContextCase34(),createConsensusResults("TAT",171,51,185));
    map.put(createSlicesForContextCase35(),createConsensusResults("C-T",194,84,219));
    map.put(createSlicesForContextCase36(),createConsensusResults("ACC",212,148,234));
    map.put(createSlicesForContextCase37(),createConsensusResults("AAT",264,216,249));
    map.put(createSlicesForContextCase38(),createConsensusResults("TTT",178,85,179));
    map.put(createSlicesForContextCase39(),createConsensusResults("CGA",220,80,214));
    map.put(createSlicesForContextCase40(),createConsensusResults("TCC",229,133,233));
    map.put(createSlicesForContextCase41(),createConsensusResults("AYT",202,32,225));
    map.put(createSlicesForContextCase42(),createConsensusResults("GST",203,4,193));
    map.put(createSlicesForContextCase43(),createConsensusResults("CAT",211,115,224));
    map.put(createSlicesForContextCase44(),createConsensusResults("GWA",180,38,177));
    map.put(createSlicesForContextCase45(),createConsensusResults("C-C",184,63,228));
    map.put(createSlicesForContextCase46(),createConsensusResults("AAC",175,58,210));
    map.put(createSlicesForContextCase47(),createConsensusResults("GCT",186,19,218));
    map.put(createSlicesForContextCase48(),createConsensusResults("T-T",225,130,220));
    map.put(createSlicesForContextCase49(),createConsensusResults("C-A",206,131,241));
    map.put(createSlicesForContextCase50(),createConsensusResults("T-A",230,160,240));
    map.put(createSlicesForContextCase51(),createConsensusResults("A--A",239,119,144,250));
    map.put(createSlicesForContextCase52(),createConsensusResults("CAGT",220,115,92,240));
    map.put(createSlicesForContextCase53(),createConsensusResults("AAGT",255,10,145,272));
    map.put(createSlicesForContextCase54(),createConsensusResults("GCAA",216,3,20,229));
    map.put(createSlicesForContextCase55(),createConsensusResults("ASG",99,5,111));
    map.put(createSlicesForContextCase56(),createConsensusResults("TMKC",267,9,10,252));
    map.put(createSlicesForContextCase57(),createConsensusResults("CATT",245,149,241,268));
    map.put(createSlicesForContextCase58(),createConsensusResults("GGTA",265,122,195,263));
    map.put(createSlicesForContextCase59(),createConsensusResults("AAAT",264,84,64,273));
    map.put(createSlicesForContextCase60(),createConsensusResults("CSA",231,26,200));
    map.put(createSlicesForContextCase61(),createConsensusResults("G-T",263,139,235));
    map.put(createSlicesForContextCase62(),createConsensusResults("TATC",350,268,255,338));
    map.put(createSlicesForContextCase63(),createConsensusResults("T-A",552,431,535));
    map.put(createSlicesForContextCase64(),createConsensusResults("CRA",628,214,630));
    map.put(createSlicesForContextCase65(),createConsensusResults("T-A",845,707,807));
    map.put(createSlicesForContextCase66(),createConsensusResults("AWG",173,4,179));
    map.put(createSlicesForContextCase67(),createConsensusResults("TTG",65,19,72));
    map.put(createSlicesForContextCase68(),createConsensusResults("GWG",120,40,132));
    map.put(createSlicesForContextCase69(),createConsensusResults("TTC",101,38,98));
    map.put(createSlicesForContextCase70(),createConsensusResults("C-A",253,165,280));
    
    map.put(createSlicesForisolatedCase1(),createConsensusResults("A",271));
    map.put(createSlicesForisolatedCase2(),createConsensusResults("Y",148));
    map.put(createSlicesForisolatedCase3(),createConsensusResults("M",178));
    map.put(createSlicesForisolatedCase4(),createConsensusResults("G",134));
    map.put(createSlicesForisolatedCase5(),createConsensusResults("G",134));
    map.put(createSlicesForisolatedCase6(),createConsensusResults("R",76));
    map.put(createSlicesForisolatedCase7(),createConsensusResults("T",3));
    map.put(createSlicesForisolatedCase8(),createConsensusResults("T",15));
    map.put(createSlicesForisolatedCase9(),createConsensusResults("G",5));
    map.put(createSlicesForisolatedCase10(),createConsensusResults("Y",23));
    map.put(createSlicesForisolatedCase11(),createConsensusResults("R",8));
    map.put(createSlicesForisolatedCase12(),createConsensusResults("K",82));
    map.put(createSlicesForisolatedCase13(),createConsensusResults("C",235));
    map.put(createSlicesForisolatedCase14(),createConsensusResults("Y",63));
    map.put(createSlicesForisolatedCase15(),createConsensusResults("S",10));
    map.put(createSlicesForisolatedCase16(),createConsensusResults("R",21));
    map.put(createSlicesForisolatedCase17(),createConsensusResults("M",99));
    map.put(createSlicesForisolatedCase18(),createConsensusResults("Y",111));
    map.put(createSlicesForisolatedCase19(),createConsensusResults("R",114));
    map.put(createSlicesForisolatedCase20(),createConsensusResults("R",104));
    map.put(createSlicesForisolatedCase21(),createConsensusResults("A",923));
    return map;

}

public static Map<List<Slice>, List<ConsensusResult>> generateChurchillWatermanData(){
    Map<List<Slice>, List<ConsensusResult>> map = generateConicData();
    map.put(createSlicesForContextCase5(),createConsensusResults("CTT",55,24,62));
    map.put(createSlicesForContextCase6(),createConsensusResults("TCC",89,30,70));
    map.put(createSlicesForContextCase10(),createConsensusResults("A-T",125,40,122));
    map.put(createSlicesForContextCase19(),createConsensusResults("TTA",152,32,155));
    map.put(createSlicesForContextCase21(),createConsensusResults("ATC",143,32,150));
    map.put(createSlicesForContextCase29(),createConsensusResults("TTG",171,55,182));
    map.put(createSlicesForContextCase33(),createConsensusResults("TGC",193,52,200));
    map.put(createSlicesForContextCase34(),createConsensusResults("T-T",171,51,185));
    map.put(createSlicesForContextCase41(),createConsensusResults("ACT",202,32,225));
    map.put(createSlicesForContextCase44(),createConsensusResults("GTA",180,38,177));
    map.put(createSlicesForContextCase46(),createConsensusResults("AAC",175,58,210));
    map.put(createSlicesForContextCase59(),createConsensusResults("AA-T",264,84,64,273));
    map.put(createSlicesForContextCase64(),createConsensusResults("CGA",628,214,630));
    map.put(createSlicesForContextCase68(),createConsensusResults("GTG",120,40,132));
    
    map.put(createSlicesForisolatedCase2(),createConsensusResults("C",148));
    map.put(createSlicesForisolatedCase3(),createConsensusResults("C",178));
    map.put(createSlicesForisolatedCase6(),createConsensusResults("G",76));
    map.put(createSlicesForisolatedCase12(),createConsensusResults("G",82));
    map.put(createSlicesForisolatedCase14(),createConsensusResults("T",63));
    map.put(createSlicesForisolatedCase17(),createConsensusResults("C",99));
    map.put(createSlicesForisolatedCase18(),createConsensusResults("T",111));
    map.put(createSlicesForisolatedCase19(),createConsensusResults("A",114));
    map.put(createSlicesForisolatedCase20(),createConsensusResults("G",104));
    return map;
}
public static Map<List<Slice>, List<ConsensusResult>> generateAnnotationData(){
    Map<List<Slice>, List<ConsensusResult>> map = generateConicData();
    map.put(createSlicesForContextCase5(),createConsensusResults("CTT",55,24,62));
    map.put(createSlicesForContextCase11(),createConsensusResults("CRG",117,60,129));
    map.put(createSlicesForContextCase16(),createConsensusResults("GMT",140,90,135));
    map.put(createSlicesForContextCase21(),createConsensusResults("ATC",143,32,150));
    map.put(createSlicesForContextCase26(),createConsensusResults("CMG",188,118,185));
    map.put(createSlicesForContextCase27(),createConsensusResults("AKG",126,48,160));
    map.put(createSlicesForContextCase34(),createConsensusResults("T-T",171,51,185));
    map.put(createSlicesForContextCase35(),createConsensusResults("CGT",194,84,219));
    map.put(createSlicesForContextCase36(),createConsensusResults("ASC",212,148,234));
    map.put(createSlicesForContextCase38(),createConsensusResults("TKT",178,85,179));
    map.put(createSlicesForContextCase39(),createConsensusResults("CSA",220,80,214));
    map.put(createSlicesForContextCase44(),createConsensusResults("GTA",180,38,177));
    map.put(createSlicesForContextCase46(),createConsensusResults("AAC",175,58,210));
    map.put(createSlicesForContextCase49(),createConsensusResults("CGA",206,131,241));
    map.put(createSlicesForContextCase50(),createConsensusResults("TTA",230,160,240));
    map.put(createSlicesForContextCase51(),createConsensusResults("AC-A",239,119,144,250));
    map.put(createSlicesForContextCase52(),createConsensusResults("CMKT",220,115,92,240));
    map.put(createSlicesForContextCase53(),createConsensusResults("AAGT",255,10,145,272));
    map.put(createSlicesForContextCase57(),createConsensusResults("CWTT",245,149,241,268));
    map.put(createSlicesForContextCase58(),createConsensusResults("GRTA",265,122,195,263));
    map.put(createSlicesForContextCase61(),createConsensusResults("GTT",263,139,235));
    map.put(createSlicesForContextCase62(),createConsensusResults("TMWC",350,268,255,338));
    map.put(createSlicesForContextCase63(),createConsensusResults("TAA",552,431,535));
    map.put(createSlicesForContextCase65(),createConsensusResults("TAA",845,707,807));
    map.put(createSlicesForContextCase70(),createConsensusResults("CAA",253,165,280));
    
    map.put(createSlicesForisolatedCase1(),createConsensusResults("R",271));
    map.put(createSlicesForisolatedCase13(),createConsensusResults("Y",235));
    
    return map;
}
public static Map<List<Slice>, List<ConsensusResult>> generateNoAmbiguityData(){
    Map<List<Slice>, List<ConsensusResult>> map = new LinkedHashMap<List<Slice>, List<ConsensusResult>>();
    map.put(createEmptySlice(), createConsensusResults("-",0));
    map.put(createSlicesForContextCase1(),createConsensusResults("AAG",65,3,63));
    map.put(createSlicesForContextCase2(),createConsensusResults("TCG",60,20,60));
    map.put(createSlicesForContextCase3(),createConsensusResults("AGG",60,31,60));
    map.put(createSlicesForContextCase4(),createConsensusResults("A-G",76,16,55));
    map.put(createSlicesForContextCase5(),createConsensusResults("CTT",55,24,62));
    map.put(createSlicesForContextCase6(),createConsensusResults("TCC",89,30,70));
    map.put(createSlicesForContextCase7(),createConsensusResults("CGA",87,52,105));
    map.put(createSlicesForContextCase8(),createConsensusResults("TCG",90,10,82));
    map.put(createSlicesForContextCase9(),createConsensusResults("C-G",80,6,82));
    map.put(createSlicesForContextCase10(),createConsensusResults("A-T",125,40,122));
    map.put(createSlicesForContextCase11(),createConsensusResults("CGG",117,60,129));
    map.put(createSlicesForContextCase12(),createConsensusResults("GCA",130,90,122));
    map.put(createSlicesForContextCase13(),createConsensusResults("CGA",93,12,102));
    map.put(createSlicesForContextCase14(),createConsensusResults("GCT",142,6,138));
    map.put(createSlicesForContextCase15(),createConsensusResults("G-C",122,25,119));
    map.put(createSlicesForContextCase16(),createConsensusResults("GAT",140,90,135));
    map.put(createSlicesForContextCase17(),createConsensusResults("AGC",167,120,172));
    map.put(createSlicesForContextCase18(),createConsensusResults("CTC",119,29,117));
    map.put(createSlicesForContextCase19(),createConsensusResults("TTA",152,32,155));
    map.put(createSlicesForContextCase20(),createConsensusResults("AGT",173,85,140));
    map.put(createSlicesForContextCase21(),createConsensusResults("ATC",143,32,150));
    map.put(createSlicesForContextCase22(),createConsensusResults("TTG",150,78,150));
    map.put(createSlicesForContextCase23(),createConsensusResults("G-A",192,58,168));
    map.put(createSlicesForContextCase24(),createConsensusResults("CCC",152,12,127));
    map.put(createSlicesForContextCase25(),createConsensusResults("AGG",138,75,145));
    map.put(createSlicesForContextCase26(),createConsensusResults("CAG",188,118,185));
    map.put(createSlicesForContextCase27(),createConsensusResults("AGG",126,48,160));
    map.put(createSlicesForContextCase28(),createConsensusResults("CCC",204,158,207));
    map.put(createSlicesForContextCase29(),createConsensusResults("TTG",171,55,182));
    map.put(createSlicesForContextCase30(),createConsensusResults("GCT",194,10,227));
    map.put(createSlicesForContextCase31(),createConsensusResults("CAA",162,85,157));
    map.put(createSlicesForContextCase32(),createConsensusResults("GCA",162,25,157));
    map.put(createSlicesForContextCase33(),createConsensusResults("TGC",193,52,200));
    map.put(createSlicesForContextCase34(),createConsensusResults("T-T",171,51,185));
    map.put(createSlicesForContextCase35(),createConsensusResults("C-T",194,84,219));
    map.put(createSlicesForContextCase36(),createConsensusResults("ACC",212,148,234));
    map.put(createSlicesForContextCase37(),createConsensusResults("AAT",264,216,249));
    map.put(createSlicesForContextCase38(),createConsensusResults("TTT",178,85,179));
    map.put(createSlicesForContextCase39(),createConsensusResults("CGA",220,80,214));
    map.put(createSlicesForContextCase40(),createConsensusResults("TCC",229,133,233));
    map.put(createSlicesForContextCase41(),createConsensusResults("ACT",202,32,225));
    map.put(createSlicesForContextCase42(),createConsensusResults("GCT",203,4,193));
    map.put(createSlicesForContextCase43(),createConsensusResults("CAT",211,115,224));
    map.put(createSlicesForContextCase44(),createConsensusResults("GTA",180,38,177));
    map.put(createSlicesForContextCase45(),createConsensusResults("C-C",184,63,228));
    map.put(createSlicesForContextCase46(),createConsensusResults("AAC",175,58,210));
    map.put(createSlicesForContextCase47(),createConsensusResults("G-T",186,19,218));
    map.put(createSlicesForContextCase48(),createConsensusResults("T-T",225,130,220));
    map.put(createSlicesForContextCase49(),createConsensusResults("C-A",206,131,241));
    map.put(createSlicesForContextCase50(),createConsensusResults("T-A",230,160,240));
    map.put(createSlicesForContextCase51(),createConsensusResults("A--A",239,119,144,250));
    map.put(createSlicesForContextCase52(),createConsensusResults("CAGT",220,115,92,240));
    map.put(createSlicesForContextCase53(),createConsensusResults("AAGT",255,10,145,272));
    map.put(createSlicesForContextCase54(),createConsensusResults("GC-A",216,3,20,229));
    map.put(createSlicesForContextCase55(),createConsensusResults("ACG",99,5,111));
    map.put(createSlicesForContextCase56(),createConsensusResults("TCGC",267,9,10,252));
    map.put(createSlicesForContextCase57(),createConsensusResults("CATT",245,149,241,268));
    map.put(createSlicesForContextCase58(),createConsensusResults("GGTA",265,122,195,263));
    map.put(createSlicesForContextCase59(),createConsensusResults("AA-T",264,84,64,273));
    map.put(createSlicesForContextCase60(),createConsensusResults("CCA",231,26,200));
    map.put(createSlicesForContextCase61(),createConsensusResults("G-T",263,139,235));
    map.put(createSlicesForContextCase62(),createConsensusResults("TATC",350,268,255,338));
    map.put(createSlicesForContextCase63(),createConsensusResults("T-A",552,431,535));
    map.put(createSlicesForContextCase64(),createConsensusResults("CGA",628,214,630));
    map.put(createSlicesForContextCase65(),createConsensusResults("T-A",845,707,807));
    map.put(createSlicesForContextCase66(),createConsensusResults("ATG",173,4,179));
    map.put(createSlicesForContextCase67(),createConsensusResults("TTG",65,19,72));
    map.put(createSlicesForContextCase68(),createConsensusResults("GTG",120,40,132));
    map.put(createSlicesForContextCase69(),createConsensusResults("TTC",101,38,98));
    map.put(createSlicesForContextCase70(),createConsensusResults("C-A",253,165,280));

    map.put(createSlicesForisolatedCase1(),createConsensusResults("A",271));
    map.put(createSlicesForisolatedCase2(),createConsensusResults("C",148));
    map.put(createSlicesForisolatedCase3(),createConsensusResults("C",178));
    map.put(createSlicesForisolatedCase4(),createConsensusResults("G",134));
    map.put(createSlicesForisolatedCase5(),createConsensusResults("G",134));
    map.put(createSlicesForisolatedCase6(),createConsensusResults("G",76));
    map.put(createSlicesForisolatedCase7(),createConsensusResults("T",3));
    map.put(createSlicesForisolatedCase8(),createConsensusResults("-",15));
    map.put(createSlicesForisolatedCase9(),createConsensusResults("G",5));
    map.put(createSlicesForisolatedCase10(),createConsensusResults("T",23));
    map.put(createSlicesForisolatedCase11(),createConsensusResults("A",8));
    map.put(createSlicesForisolatedCase12(),createConsensusResults("G",82));
    map.put(createSlicesForisolatedCase13(),createConsensusResults("C",235));
    map.put(createSlicesForisolatedCase14(),createConsensusResults("T",63));
    map.put(createSlicesForisolatedCase15(),createConsensusResults("C",10));
    map.put(createSlicesForisolatedCase16(),createConsensusResults("A",21));
    map.put(createSlicesForisolatedCase17(),createConsensusResults("C",99));
    map.put(createSlicesForisolatedCase18(),createConsensusResults("T",111));
    map.put(createSlicesForisolatedCase19(),createConsensusResults("A",114));
    map.put(createSlicesForisolatedCase20(),createConsensusResults("G",104));
    map.put(createSlicesForisolatedCase21(),createConsensusResults("A",923));
    
    return map;
}
}
