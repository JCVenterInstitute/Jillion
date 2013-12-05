package org.jcvi.jillion_experimental.align;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.Builder;
import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.Residue;
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.ProteinSequence;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion_experimental.align.AlnGroupVisitor.ConservationInfo;
/**
 * {@code AlnFileWriter} can write aln encoded
 * alignment files like those produced by Clustal.
 * @author dkatzel
 *
 * @param <R> the type of {@link Residue} to be written.
 * @param <S> type of {@link Sequence} to be written.
 */
public abstract class AlnFileWriter<R extends Residue, S extends Sequence<R>> implements AlnWriter<R,S>{
	private static final int DEFAULT_RESIDUES_PER_GROUP = 60;
	private static final int USE_ONE_GROUP = -1;
	
	private final PrintWriter out;
	private final int residuesPerGroup;
	private final Map<String,S> sequences = new LinkedHashMap<String, S>();
	private long seqLength=0;
	private final String eol;
	
	/**
	 * Create a new {@link AlnFileWriterBuilder}
	 * that will build a {@link AlnFileWriter}
	 * to write out
	 * {@link NucleotideSequence} alignments in aln format to the given output file.
	 * @param outputFile the output file to write to; can not be null.
	 * If this file already exists, then the writer will overwrite
	 * the old data.  If this file does not exist, then it will be
	 * created.
	 * @return a new {@link AlnFileWriterBuilder} instance; will never be null.
	 */
	public static AlnFileWriterBuilder<Nucleotide, NucleotideSequence> createNucleotideWriterBuilder(File outputFile){
		return new NucleotideAlnFileWriterBuilder(outputFile);
	}
	/**
	 * Create a new {@link AlnFileWriterBuilder}
	 * that will build a {@link AlnFileWriter}
	 * to write out
	 * {@link ProteinSequence} alignments in aln format to the given output file.
	 * @param outputFile the output file to write to; can not be null.
	 * If this file already exists, then the writer will overwrite
	 * the old data.  If this file does not exist, then it will be
	 * created.
	 * @return a new {@link AlnFileWriterBuilder} instance; will never be null.
	 */
	public static AlnFileWriterBuilder<AminoAcid, ProteinSequence> createAminoAcidWriterBuilder(File outputFile){
		return new AminoAcidAlnFileWriterBuilder(outputFile);
	}
	
	
	
	private AlnFileWriter(File outputFile, int residuesPerGroup, String eol) throws IOException {
		IOUtil.mkdirs(outputFile.getParentFile());
		this.out = new PrintWriter(new BufferedOutputStream(new FileOutputStream(outputFile)));
		this.residuesPerGroup = residuesPerGroup;
		this.eol = eol;
	}

	@Override
	public void close() throws IOException {
		if(!sequences.isEmpty()){
			if(residuesPerGroup == USE_ONE_GROUP){
				handleGroup(Range.ofLength(seqLength));
			}else{
				for(long i=0; i<seqLength; i+=residuesPerGroup){
					Builder builder = new Range.Builder(residuesPerGroup)
											.shift(i);
					//iterator(Range) doesn't like
					//ranges out of bounds
					//so trim the range down
					long newEnd = Math.min(seqLength-1,builder.getEnd());
					Range r = builder.setEnd(newEnd).build();
					if(!r.isEmpty()){
						handleGroup(r);
					}
					
				}
				
				
			}
		}
		out.close();
		
	}
	private void handleGroup(Range r){
		int rangeLength = (int)r.getLength();
		List<Iterator<R>> iterators = new ArrayList<Iterator<R>>(sequences.size());
		List<StringBuilder> builders = new ArrayList<StringBuilder>(sequences.size());
		int maxIdLength=0;
		for(String id : sequences.keySet()){
			int length = id.length();
			if(length > maxIdLength){
				maxIdLength = length;
			}
		}
		int lineLength = maxIdLength +1 + eol.length() + rangeLength;
		for(Entry<String,S> entry: sequences.entrySet()){
			iterators.add(entry.getValue().iterator(r));
			String id = entry.getKey();
			
			StringBuilder builder = new StringBuilder(lineLength);
			builder.append(id);
			int padding = maxIdLength - id.length();
			for(int i=0; i<padding; i++){
				builder.append(" ");
			}
			builder.append('\t');
			builders.add(builder);
		}
		StringBuilder conservationBuilder = new StringBuilder(lineLength);
		for(int i=0; i<maxIdLength; i++){
			conservationBuilder.append(" ");
		}
		conservationBuilder.append('\t');
		for(int i=0; i<rangeLength; i++){
			Set<R> uniqueValues = createNewResiudeSet();
			for(int j=0; j<iterators.size(); j++){
				R residue =iterators.get(j).next();
				builders.get(j).append(residue.getCharacter());
				uniqueValues.add(residue);
			}
			ConservationInfo info = computeConservationInfo(uniqueValues);
			conservationBuilder.append(info.asChar());
		}
		//now our text for the group is all stored in StringBuilders
		//write them to out
		for(int i=0; i<builders.size(); i++){
			out.write(builders.get(i).append(eol).toString());
		}
		//now write conservation string
		out.write(conservationBuilder.append(eol).toString());
	}
	
	
	protected abstract Set<R> createNewResiudeSet();
	protected abstract ConservationInfo computeConservationInfo(Set<R> values);

	@Override
	public void write(String id, S sequence) throws IOException {
		if(id ==null){
			throw new NullPointerException("id can not be null");
		}
		if(sequence ==null){
			throw new NullPointerException("sequence can not be null");
		}
		long currentSequenceLength = sequence.getLength();
		if(currentSequenceLength ==0){
			//empty sequence should we include it?
			//no for now
			return;
		}
		if(sequences.isEmpty()){
			seqLength = currentSequenceLength;
		}else if(seqLength != currentSequenceLength){
			throw new IOException(String.format("invalid sequence length, all sequences so far have been %d residues but this one is %d", seqLength, currentSequenceLength));
		}
		if(sequences.containsKey(id)){
			throw new IllegalArgumentException(String.format("id %s already has been written",id));
		}
		sequences.put(id, sequence);
		
	}
	
	private static final class NucleotideAlnFileWriter extends AlnFileWriter<Nucleotide, NucleotideSequence>{

		
		public NucleotideAlnFileWriter(File outputFile, int residuesPerGroup,
				String eol) throws IOException {
			super(outputFile, residuesPerGroup, eol);
		}

		@Override
		protected Set<Nucleotide> createNewResiudeSet() {
			return EnumSet.noneOf(Nucleotide.class);
		}

		@Override
		protected ConservationInfo computeConservationInfo(
				Set<Nucleotide> values) {
			//nucleotides are either conserved or not
			//we consider conserved if we only have 1 type of nucleotide
			if(values.size() ==1){
				//if the slice is all gaps
				//it's not identical
				if(values.contains(Nucleotide.Gap)){
					return ConservationInfo.NOT_CONSERVED;
				}
				return ConservationInfo.IDENTICAL;
			}
			return ConservationInfo.NOT_CONSERVED;
		}
		
	}
	
	private static final class AminoAcidAlnFileWriter extends AlnFileWriter<AminoAcid, ProteinSequence>{
		/*
		 * From Susmita - if a slice contains only the residues of
		 * one of these rows, then it is considered conserved.
		 * 
		 * 
		 	G,A,V,L,I, M        aliphatic (though some would not include G)
			S,T,C                      hydroxyl, sulfhydryl, polar
			N,Q                        amide side chains
			F,W,Y                    aromatic
			H,K,R                     basic
			D,E                         acidic
		 */
		private static Set<AminoAcid> ALIPHATIC = EnumSet.of(AminoAcid.Glycine, AminoAcid.Alanine, AminoAcid.Valine, AminoAcid.Leucine, AminoAcid.Isoleucine, AminoAcid.Methionine);
		private static Set<AminoAcid> HYDROXYL_SULFHYDRYL_POLAR = EnumSet.of(AminoAcid.Serine, AminoAcid.Threonine, AminoAcid.Cysteine);
		private static Set<AminoAcid> AMIDE_SIDE_CHAINS = EnumSet.of(AminoAcid.Asparagine, AminoAcid.Glutamine);
		
		private static Set<AminoAcid> AROMATIC = EnumSet.of(AminoAcid.Phenylalanine, AminoAcid.Tryptophan, AminoAcid.Tyrosine);
		
		private static Set<AminoAcid> BASIC = EnumSet.of(AminoAcid.Histidine, AminoAcid.Lysine, AminoAcid.Arginine);
		private static Set<AminoAcid> ACIDIC = EnumSet.of(AminoAcid.Aspartic_Acid, AminoAcid.Glutamic_Acid);
		
		
		public AminoAcidAlnFileWriter(File outputFile, int residuesPerGroup,
				String eol) throws IOException {
			super(outputFile, residuesPerGroup, eol);
		}

		@Override
		protected Set<AminoAcid> createNewResiudeSet() {
			return EnumSet.noneOf(AminoAcid.class);
		}

		@Override
		protected ConservationInfo computeConservationInfo(
				Set<AminoAcid> values) {
			/**
			 * G,A,V,L,I, M        aliphatic (though some would not include G)
				S,T,C                      hydroxyl, sulfhydryl, polar
				N,Q                        amide side chains
				F,W,Y                    aromatic
				H,K,R                     basic
				D,E                         acidic
			 */
			//nucleotides are either conserved or not
			//we consider conserved if we only have 1 type of nucleotide
			if(values.size() ==1){
				return ConservationInfo.IDENTICAL;
			}
			if(ALIPHATIC.containsAll(values) || HYDROXYL_SULFHYDRYL_POLAR.containsAll(values)
					|| AMIDE_SIDE_CHAINS.containsAll(values) || AROMATIC.containsAll(values) 
					|| ACIDIC.containsAll(values) || BASIC.containsAll(values)){
				return ConservationInfo.CONSERVED_SUBSITUTION;
			}
			return ConservationInfo.NOT_CONSERVED;
		}
		
	}
	/**
	 * {@code AlnFileWriterBuilder} is a Builder
	 * that can create new {@link AlnFileWriter}
	 * instances. 
	 * @author dkatzel
	 *
	 * @param <R> the type of {@link Residue} to be written.
	 * @param <S> type of {@link Sequence} to be written.
	 */
	public static abstract class AlnFileWriterBuilder<R extends Residue, S extends Sequence<R>>{
		
		private final File outFile;
		
		private int residuesPerGroup = DEFAULT_RESIDUES_PER_GROUP;
		private String eol = "\n";		
		
		
		private AlnFileWriterBuilder(File outFile) {
			if(outFile ==null){
				throw new NullPointerException("output File can not be null");
			}
			this.outFile = outFile;
		}
		/**
		 * Set the number of residues per "group". If not set,
		 * then the default number of 60 residues will be used.
		 * If this method is called along with {@link #forceOneGroupOnly()}
		 * then the last one called "wins".
		 * @param n the number of residues per group;
		 * must be >0.
		 * @return this
		 * @throws IllegalArgumentException if n <1.
		 * @see #forceOneGroupOnly()
		 */
		public AlnFileWriterBuilder<R,S> setNumResiduesPerGroup(int n){
			if(n <1){
				throw new IllegalArgumentException("number of residues per group must be >= 1");
			}
			this.residuesPerGroup = n;
			return this;
		}
		/**
		 * Set end of line String to use.
		 * If not set, the default is '\n'.
		 * @param eol the end of line string to use;
		 * may not be null.
		 * @return this
		 * @throws NullPointerException if eol is null.
		 */
		public AlnFileWriterBuilder<R,S> eol(String eol){
			if(eol ==null){
				throw new NullPointerException("end of line can not be null");
			}
			this.eol = eol;
			return this;
		}
		/**
		 * Force all the alignments into one group,
		 * this has the equivalent effect of making the number
		 * of residues per group infinite.
		 * If this method is called along with {@link #setNumResiduesPerGroup(int)}
		 * then the last one called "wins".
		 * @return this
		 * @see #setNumResiduesPerGroup(int)
		 */
		public AlnFileWriterBuilder<R,S> forceOneGroupOnly(){
			this.residuesPerGroup = USE_ONE_GROUP;
			return this;
		}
		/**
		 * Create a new {@link AlnFileWriter}
		 * instance using the configuration data
		 * provided so far.
		 * @return a new {@link AlnFileWriter};
		 * will never be null.
		 * @throws IOException if there is a problem creating the 
		 * writer.
		 */
		public AlnFileWriter<R, S> build() throws IOException{
			return createNew(outFile, residuesPerGroup, eol);
		}
		
		protected abstract AlnFileWriter<R, S> createNew(File f, int groupLength, String endOfLine) throws IOException;
	}
	
	public static final class NucleotideAlnFileWriterBuilder extends AlnFileWriterBuilder<Nucleotide, NucleotideSequence>{

		public NucleotideAlnFileWriterBuilder(File outFile) {
			super(outFile);
		}

		@Override
		protected AlnFileWriter<Nucleotide, NucleotideSequence> createNew(File f, int groupLength, String endOfLine) throws IOException {
			return new NucleotideAlnFileWriter(f, groupLength, endOfLine);
		}

		
	}
	
	public static final class AminoAcidAlnFileWriterBuilder extends AlnFileWriterBuilder<AminoAcid, ProteinSequence>{

		public AminoAcidAlnFileWriterBuilder(File outFile) {
			super(outFile);
		}

		@Override
		protected AlnFileWriter<AminoAcid, ProteinSequence> createNew(File f, int groupLength, String endOfLine) throws IOException {
			return new AminoAcidAlnFileWriter(f, groupLength, endOfLine);
		}

		
	}

	
}
