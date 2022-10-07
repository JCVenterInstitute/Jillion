/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.residue.aa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jcvi.jillion.core.residue.Frame;
import org.jcvi.jillion.core.residue.aa.TranslationVisitor.FoundStartResult;
import org.jcvi.jillion.core.residue.aa.TranslationVisitor.FoundStopResult;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.Triplet;
import org.jcvi.jillion.core.residue.nt.VariantNucleotideSequence;
import org.jcvi.jillion.core.util.MapUtil;
import org.jcvi.jillion.core.util.SingleThreadAdder;

import lombok.Builder;
import lombok.Data;

public enum IupacTranslationTables implements TranslationTable{
	
	
	
	
	/**
	 * Default translation table used by GenBank.
	 */
	STANDARD(1),
	VERTEBRATE_MITOCHONDRIAL(2){

		@Override
		protected void updateTable(Map<Triplet, Codon> map) {
			insertIntoTable('A', 'G', 'A', AminoAcid.STOP);
			insertIntoTable('A', 'G', 'G', AminoAcid.STOP);
			insertIntoTable('A', 'G', 'R', AminoAcid.STOP);
			
			insertIntoTable('A', 'T', 'A', AminoAcid.Methionine, true);
			insertIntoTable('A', 'T', 'R', AminoAcid.Methionine, true);
			
			insertIntoTable('T', 'G', 'A', AminoAcid.Tryptophan);
			insertIntoTable('T', 'G', 'R', AminoAcid.Tryptophan);
		}
		
	},
	YEAST_MITOCHONDRIAL(3){

		@Override
		protected void updateTable(Map<Triplet, Codon> map) {			
			
			insertIntoTable('A', 'T', 'A', AminoAcid.Methionine, true);
			insertIntoTable('A', 'T', 'R', AminoAcid.Methionine, true);
			
			insertIntoTable('C', 'T', 'A', AminoAcid.Threonine);
			insertIntoTable('C', 'T', 'C', AminoAcid.Threonine);
			insertIntoTable('C', 'T', 'G', AminoAcid.Threonine);
			insertIntoTable('C', 'T', 'T', AminoAcid.Threonine);
			
			
			insertIntoTable('C', 'T', 'M', AminoAcid.Threonine);
			insertIntoTable('C', 'T', 'R', AminoAcid.Threonine);
			insertIntoTable('C', 'T', 'W', AminoAcid.Threonine);
			insertIntoTable('C', 'T', 'S', AminoAcid.Threonine);
			insertIntoTable('C', 'T', 'Y', AminoAcid.Threonine);
			insertIntoTable('C', 'T', 'K', AminoAcid.Threonine);
			insertIntoTable('C', 'T', 'V', AminoAcid.Threonine);
			insertIntoTable('C', 'T', 'H', AminoAcid.Threonine);
			insertIntoTable('C', 'T', 'D', AminoAcid.Threonine);
			insertIntoTable('C', 'T', 'B', AminoAcid.Threonine);
			insertIntoTable('C', 'T', 'N', AminoAcid.Threonine);
			
			insertIntoTable('T', 'G', 'A', AminoAcid.Tryptophan);
			
			removeFromTable('C', 'G', 'A');
			removeFromTable('C', 'G', 'C');
			removeFromTable('C', 'G', 'R');
			

		}
		
	},

	MOLD_PROTOZOAN_COELENTERATE_MITOCHONDRIAL_AND_MYCOPLASMA_SPIROPLAMSA(4){

		@Override
		protected void updateTable(Map<Triplet, Codon> map) {
			insertIntoTable('T', 'G', 'A', AminoAcid.Tryptophan);
			insertIntoTable('T', 'G', 'R', AminoAcid.Tryptophan);
			
		}
		
	},
	/**
	 * Nucleotide Translation table used by
	 * <ul>
	 * <li>Ciliata - Oxytricha and Stylonycha, Paramecium, Tetrahymena, Oxytrichidae etc. </li>
	 * <li>Dasycladaceae - Acetabulara and Batophora</li>
	 * <li>Diplomonadida - Scope: Hexamita</li>
	 * </ul>
	 */
	CILIATE_DASYCLADACEAN_AND_HEXAMITA(6){

		@Override
		protected void updateTable(Map<Triplet, Codon> map) {
			insertIntoTable('T', 'A', 'A', AminoAcid.Glutamine);
			insertIntoTable('T', 'A', 'G', AminoAcid.Glutamine);
			insertIntoTable('T', 'A', 'R', AminoAcid.Glutamine);
			
			//not a start codons anymore
			insertIntoTable('T', 'T', 'G', AminoAcid.Leucine);
			insertIntoTable('C', 'T', 'G', AminoAcid.Leucine);
		}
		
	},
	BACTERIAL_ARCHAEL_AND_PLANT_PLASTID(11){

		@Override
		protected void updateTable(Map<Triplet, Codon> map) {
			//more start codons only?
			insertIntoTable('G', 'T', 'G', AminoAcid.Valine, true);
			
			insertIntoTable('A', 'T', 'T', AminoAcid.Isoleucine, true);
			insertIntoTable('A', 'T', 'C', AminoAcid.Isoleucine, true);
			insertIntoTable('A', 'T', 'A', AminoAcid.Isoleucine, true);
			insertIntoTable('A', 'T', 'G', AminoAcid.Isoleucine, true);
			
			insertIntoTable('A', 'T', 'M', AminoAcid.Isoleucine, true);
			insertIntoTable('A', 'T', 'R', AminoAcid.Isoleucine, true);
			insertIntoTable('A', 'T', 'W', AminoAcid.Isoleucine, true);
			insertIntoTable('A', 'T', 'S', AminoAcid.Isoleucine, true);
			insertIntoTable('A', 'T', 'Y', AminoAcid.Isoleucine, true);
			insertIntoTable('A', 'T', 'K', AminoAcid.Isoleucine, true);
			insertIntoTable('A', 'T', 'V', AminoAcid.Isoleucine, true);
			insertIntoTable('A', 'T', 'H', AminoAcid.Isoleucine, true);
			insertIntoTable('A', 'T', 'D', AminoAcid.Isoleucine, true);
			insertIntoTable('A', 'T', 'B', AminoAcid.Isoleucine, true);
			insertIntoTable('A', 'T', 'N', AminoAcid.Isoleucine, true);
		}
		
	},
	
	;

	private static final Map<Integer, IupacTranslationTables> TABLES;
	static{
		TABLES = new HashMap<Integer, IupacTranslationTables>(MapUtil.computeMinHashMapSizeWithoutRehashing(25));
		for(IupacTranslationTables table : values()){
			TABLES.put(Integer.valueOf(table.getTableNumber()), table);
		}
	}
	
	private final Map<Triplet, Codon> map = new HashMap<>(MapUtil.computeMinHashMapSizeWithoutRehashing(200));
	private final byte tableNumber;
	
	private final Map<AminoAcid, Set<Triplet>> aaToTripletMap;
	
	private IupacTranslationTables(int tableNumber){
		initialzeTable();
		updateTable(map);
		this.tableNumber = (byte)tableNumber;
		aaToTripletMap = new EnumMap<>(AminoAcid.class);
		for(Entry<Triplet, Codon> entry : map.entrySet()) {
			aaToTripletMap.computeIfAbsent(entry.getValue().getAminoAcid(), aa -> new HashSet<>()).add(entry.getKey());
		}
	}


	protected void removeFromTable(char base1, char base2, char base3){
		Triplet triplet = Triplet.create(base1, base2, base3);
		map.remove(triplet);
	}
	protected void  insertIntoTable(char base1, char base2, char base3, AminoAcid aa){
		insertIntoTable(base1, base2, base3, aa, false);
	}
	protected void  insertIntoTable(char base1, char base2, char base3, AminoAcid aa, boolean isStart){
		Triplet triplet = Triplet.create(base1, base2, base3);
		Codon.Builder builder = new Codon.Builder(triplet, aa);
		if(aa == AminoAcid.STOP){
			builder.isStop(true);
		}
		if(isStart){
			builder.isStart(true);
		}
		map.put(triplet, builder.build());
	}
	
	private void initialzeTable(){
		/*
		 * This group of Strings
		 * explains the amino acid,
		 *  if it's a start codon (M)
		 * and the triplet used to encode it.
		 * 
		 * This way of displaying the translation matrix
		 * is used by NCBI Genetic Codes page
		 * http://www.ncbi.nlm.nih.gov/Taxonomy/Utils/wprintgc.cgi
		 * 
		 * I have made some changes, mostly adding ambiguity support
		 * for wobble bases.
		 * 
		 * Non-standard translation tables will update the
		 * Map later.
		 */
		
		char[] aas =    "FFFLLLSSSSSSSSSSSSSSSYYY***CCC*WLLLLLLLLPPPPPPPPPPPPPPPHHHQQQRRRRRRRRRRRRRRRIIIIIIIMTTTTTTTTTTTTTTTNNNKKKSSSRRRVVVVVVVVVVVVVVVAAAAAAAAAAAAAAADDDEEEGGGGGGGGGGGGGGG".toCharArray();
		char[] starts = "----M----------------------------------M-------------------------------------------M------------------------------------------------------------------------------".toCharArray();
		char[] base1 =  "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG".toCharArray();
		char[] base2 =  "TTTTTTCCCCCCCCCCCCCCCAAAAAAGGGGGTTTTTTTTCCCCCCCCCCCCCCCAAAAAAGGGGGGGGGGGGGGGTTTTTTTTCCCCCCCCCCCCCCCAAAAAAGGGGGGTTTTTTTTTTTTTTTCCCCCCCCCCCCCCCAAAAAAGGGGGGGGGGGGGGG".toCharArray();
		char[] base3 =  "TCYAGRTCAGMRWSYKVHDBNTCYAGRTCYAGTCAMWYHGTCAGMRWSYKVHDBNTCYAGRTCAGMRWSYKVHDBNTCAMWYHGTCAGMRWSYKVHDBNTCYAGRTCYAGRTCAGMRWSYKVHDBNTCAGMRWSYKVHDBNTCYAGRTCAGMRWSYKVHDBN".toCharArray();			
		
		for(int i=0; i<aas.length; i++){
			insertIntoTable(base1[i], base2[i], base3[i], 
								AminoAcid.parse(aas[i]),
								starts[i] =='M');
			
		}
		
		//add gap
		insertIntoTable('-','-','-', AminoAcid.Gap, false);
	}
	
	

	@Override
	public ProteinSequence translate(NucleotideSequence sequence) {
		return translate(sequence, Frame.ONE,true);
	}
	@Override
	public ProteinSequence translate(NucleotideSequence sequence, boolean substituteStart) {
		return translate(sequence, Frame.ONE, substituteStart);
	}

	@Override
	public ProteinSequence translate(NucleotideSequence sequence, Frame frame) {
		return translate(sequence, frame, true);
	}
	@Override
	public ProteinSequence translate(NucleotideSequence sequence, Frame frame, boolean substituteStart) {
		return translate(sequence, TranslationOptions.builder().frame(frame).substituteStart(substituteStart).build());
		
	}
	@Override
	public ProteinSequence translate(NucleotideSequence sequence, Frame frame, int length, boolean substituteStart) {
		return translate(sequence,  TranslationOptions.builder()
														.frame(frame)
														.substituteStart(substituteStart)
														.numberOfBasesToTranslate(length).build());
	}

	
	private void _translate(BiFunction<Frame,Consumer<Nucleotide>, Iterator<Set<Triplet>>> tripletSupplier, TranslationOptions options, TranslationVisitor visitor) {
	//-3 because the apply() method advances the iterator and calls the consumer 
	SingleThreadAdder currentOffset = new SingleThreadAdder(-1); // start at -1 so the nuc coordinate is the END offset of that codon
	Consumer<Nucleotide> consumer = n-> currentOffset.increment();
	
	Frame frame = options.getFrame();
     if(frame ==null){
             throw new NullPointerException("frame can not be null");
     }
     if(visitor ==null){
         throw new NullPointerException("frame can not be null");
     }
     //Brian says legacy PFGRC and (BioJava and BioPerl?)
     //don't correctly handle the 'not first starts'
     //so if translation table says codon is a start
     //and we've already seen a start, then make it not the start?
     long currentStart=frame.getNumberOfBasesSkipped();
     Iterator<Set<Triplet>> iter = tripletSupplier.apply(frame, consumer);
     
     boolean seenStart=false;
    
     while(iter.hasNext()){
    	 	long offset= currentOffset.longValue();
             Set<Triplet> triplet =iter.next();
             
             if(triplet !=null){
            	 List<Codon> codons =translate(triplet);
                 Codon codon=null;
                 if(codons.size()==1) {
                	 codon= codons.get(0);
                 }else if(options.isMergeCodons()) {
                	 //ambiguities
                	 codon = Codon.merge(codons);
                 }
                     if(codon !=null) {
                    	 //handle single codon
	                     if(!seenStart && codon.isStart()){
	                         FoundStartResult result = visitor.foundStart(currentStart, offset, codon);
	                         if(result ==FoundStartResult.STOP){
	                             break;
	                         }
	                         seenStart = result != FoundStartResult.FIND_ADDITIONAL_STARTS;
	                     }else if(codon.isStop()){
	                             FoundStopResult result = visitor.foundStop(currentStart, offset, codon);
	                             if(result == FoundStopResult.STOP){
	                                 break;
	                             }
	                             
	                     }else{
	                             visitor.visitCodon(currentStart, offset, codon);
	                         
	                     }
                     }else {
                    	 //handle variant codons
                    	 visitor.visitVariantCodon(currentStart, offset, codons);
                     }
             }
             currentStart=offset+1;
     }
     visitor.end();

	}
	@Override
	public void translate(NucleotideSequence sequence, TranslationOptions options, TranslationVisitor visitor) {
		if(sequence ==null){
	             throw new NullPointerException("sequence can not be null");
	     }
		
	     _translate((f, consumer) -> f.asTriplets(sequence, options.isIgnoreGaps(), options.getNumberOfBasesToTranslate(), consumer), options, visitor);
	    
	}
	@Override
	public void translate(VariantNucleotideSequence sequence, TranslationOptions options, TranslationVisitor visitor) {
		if(sequence ==null){
            throw new NullPointerException("sequence can not be null");
	    }
		
	    _translate((f, consumer) -> f.asTriplets(sequence, options.isIgnoreGaps(), options.getNumberOfBasesToTranslate(), consumer), options, visitor);
	   
	}
	@Override
	public ProteinSequence translate(NucleotideSequence sequence, TranslationOptions options) {
		if(sequence ==null){
			throw new NullPointerException("sequence can not be null");
		}
		if(options ==null){
			throw new NullPointerException("frame can not be null");
		}
		//Brian says legacy PFGRC and (BioJava and BioPerl?)
		//don't correctly handle the 'not first starts'
		//so if translation table says codon is a start
		//and we've already seen a start, then make it not the start?
		int length = options.getNumberOfBasesToTranslate() ==null? (int)sequence.getLength(): options.getNumberOfBasesToTranslate();
		ProteinSequenceBuilder builder = new ProteinSequenceBuilder(length/3);
		
		Iterator<Set<Triplet>> iter = options.getFrame().asTriplets(sequence, options.isIgnoreGaps(), options.getNumberOfBasesToTranslate());
		
		boolean seenStart=!options.isSubstituteStart();
		
		while(iter.hasNext()){
			Set<Triplet> triplet =iter.next();
			if(triplet !=null){
				List<Codon> codons =translate(triplet);
				Codon codon=null;
                if(codons.size()==1) {
               	 codon= codons.get(0);
                }else if(options.isMergeCodons()) {
               	 //ambiguities
               	 codon = Codon.merge(codons);
                }else {
                	//pick 1st one?
                	codon= codons.get(0);
                }
				if(codon.isStart() && !seenStart){
					seenStart=true;
					//hardcode an M if this is our first start
					//which may 
					//not be the amino acid returned by 
					//#getAminoAcid() depending on the translation table
					builder.append(AminoAcid.Methionine);
				}else{
					builder.append(codon.getAminoAcid());
				}
                
			}
		}
		return builder.build();
	}
	
	
	@Override
    public void translate(NucleotideSequence sequence, Frame frame, TranslationVisitor visitor) {
		translate(sequence, TranslationOptions.builder().ignoreGaps(true).frame(frame).build(), visitor);
	}
	@Override
	public ProteinSequence translate(NucleotideSequence sequence, Frame frame, int length) {
		return translate(sequence, frame, length,true);
	}

	
	
	

	protected void updateTable(Map<Triplet, Codon> map){
		//no-op
	}
	private List<Codon> translate(Set<Triplet> triplets){
		List<Codon> codons = new ArrayList<>(3);
		for(Triplet triplet : triplets) {
			codons.add( _translate(triplet));
		}
		return codons;
		
		
	}
	/**
	 * Translate the given single triplet into a Codon
	 * @param triplet the triplet to translate, can not be null.
	 * @return a new Codon
	 * @since 6.0
	 * @throws NullPointerException if triplet is null.
	 */
	public Codon translate(Triplet triplet) {
		return _translate(Objects.requireNonNull(triplet));
	}
	private Codon _translate(Triplet triplet) {
		return map.computeIfAbsent(triplet, 
		        t -> new Codon.Builder(t, AminoAcid.Unknown_Amino_Acid).build());
	}

	public int getTableNumber(){
		return tableNumber;
	}
	
	public static TranslationTable getTableByTableNumber(int tableNumber){
		TranslationTable table = TABLES.get(tableNumber);
		if(table ==null){
			throw new IllegalArgumentException("unknown table number "+ tableNumber);
		}
		return table;
	}
	@Override
	public Map<Frame, List<Long>> findStops(NucleotideSequence sequence) {
		
		if(sequence == null){
            throw new NullPointerException("sequence can not be null");
        }
		Map<Frame,List<Long>> stops = new HashMap<Frame,List<Long>>();
		List<Long> stopCoordinates;
		long frameOffset = 0L;
		for (Frame frame: Frame.forwardFrames())
		{
			stopCoordinates = new ArrayList<Long>();
			stops.put(frame, stopCoordinates);
			// index into the original sequence, offset due to frame
			long index = frameOffset;
			Iterator<Set<Triplet>> tripletIter = frame.asTriplets(sequence);
			while (tripletIter.hasNext())
			{
				List<Codon> codons = translate(tripletIter.next());
				for(Codon codon: codons) {
					if (codon.isStop()){
						stopCoordinates.add(index);
					}
				}
				index += 3;
			}
			frameOffset++;
	    }
		return stops;
		
	}
	
	@Override
	public Set<Triplet> getTripletsFor(AminoAcid aa){
		Objects.requireNonNull(aa);
		return aaToTripletMap.getOrDefault(aa, Collections.emptySet());
	}
}
