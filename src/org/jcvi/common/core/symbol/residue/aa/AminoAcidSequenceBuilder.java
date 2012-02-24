package org.jcvi.common.core.symbol.residue.aa;

import java.util.List;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.symbol.residue.ResidueSequenceBuilder;

public class AminoAcidSequenceBuilder implements ResidueSequenceBuilder<AminoAcid,AminoAcidSequence>{

	private StringBuilder builder;
	private int numberOfGaps=0;
	public AminoAcidSequenceBuilder(){
		builder = new StringBuilder();
	}
	
	public AminoAcidSequenceBuilder(int initialCapacity){
		builder = new StringBuilder(initialCapacity);
	}
	public AminoAcidSequenceBuilder(CharSequence sequence){
		builder = new StringBuilder(sequence.length());
		append(AminoAcids.parse(sequence.toString()));
	}
	@Override
	public AminoAcidSequenceBuilder append(
			AminoAcid residue) {
		if(residue==AminoAcid.Gap){
			numberOfGaps++;
		}
		builder.append(residue.asChar());
		return this;
	}

	@Override
	public ResidueSequenceBuilder<AminoAcid, AminoAcidSequence> append(
			Iterable<AminoAcid> sequence) {
		for(AminoAcid aa : sequence){
			append(aa);
		}
		return this;
	}

	@Override
	public ResidueSequenceBuilder<AminoAcid, AminoAcidSequence> append(
			ResidueSequenceBuilder<AminoAcid, AminoAcidSequence> otherBuilder) {
		return append(otherBuilder.asList());
	}

	@Override
	public ResidueSequenceBuilder<AminoAcid, AminoAcidSequence> append(
			String sequence) {
		return append(AminoAcids.parse(sequence));
	}

	@Override
	public ResidueSequenceBuilder<AminoAcid, AminoAcidSequence> insert(
			int offset, String sequence) {
		StringBuilder tempBuilder = new StringBuilder();
		for(AminoAcid aa :AminoAcids.parse(sequence)){
			if(aa == AminoAcid.Gap){
				numberOfGaps++;
			}
			tempBuilder.append(aa.asChar());
		}		
		builder.insert(offset, tempBuilder.toString());
		return this;
	}

	@Override
	public long getLength() {
		return builder.length();
	}
	@Override
	public long getUngappedLength() {
		return builder.length() - numberOfGaps;
	}
	
	@Override
	public ResidueSequenceBuilder<AminoAcid, AminoAcidSequence> replace(
			int offset, AminoAcid replacement) {
		if(AminoAcid.parse(builder.charAt(offset)) == AminoAcid.Gap){
			numberOfGaps--;			
		}
		if(replacement == AminoAcid.Gap){
			numberOfGaps++;
		}
		builder.replace(offset, offset+1, replacement.toString());
		return this;
	}

	@Override
	public ResidueSequenceBuilder<AminoAcid, AminoAcidSequence> delete(
			Range range) {
		for(AminoAcid aa : asList(range)){
			if(aa == AminoAcid.Gap){
				numberOfGaps --;
			}
		}
		builder.delete((int)range.getStart(), (int)range.getEnd()+1);
		return this;
	}

	@Override
	public int getNumGaps() {
		return numberOfGaps;
	}

	@Override
	public ResidueSequenceBuilder<AminoAcid, AminoAcidSequence> prepend(
			String sequence) {			
		return insert(0, sequence);
	}

	@Override
	public ResidueSequenceBuilder<AminoAcid, AminoAcidSequence> insert(
			int offset, Iterable<AminoAcid> sequence) {
		StringBuilder tempBuilder = new StringBuilder();
		for(AminoAcid aa :sequence){
			if(aa == AminoAcid.Gap){
				numberOfGaps++;
			}
			tempBuilder.append(aa.asChar());
		}		
		builder.insert(offset, tempBuilder.toString());
		return this;
	}

	@Override
	public ResidueSequenceBuilder<AminoAcid, AminoAcidSequence> insert(
			int offset,
			ResidueSequenceBuilder<AminoAcid, AminoAcidSequence> otherBuilder) {
		return insert(offset,otherBuilder.toString());
	}

	@Override
	public ResidueSequenceBuilder<AminoAcid, AminoAcidSequence> insert(
			int offset, AminoAcid base) {
		if(base == AminoAcid.Gap){
			numberOfGaps++;
		}
		builder.insert(offset, base.asChar());
		return this;
	}

	@Override
	public ResidueSequenceBuilder<AminoAcid, AminoAcidSequence> prepend(
			Iterable<AminoAcid> sequence) {
		return insert(0, sequence);
	}

	@Override
	public ResidueSequenceBuilder<AminoAcid, AminoAcidSequence> prepend(
			ResidueSequenceBuilder<AminoAcid, AminoAcidSequence> otherBuilder) {
		return prepend(otherBuilder.toString());
	}

	@Override
	public AminoAcidSequence build() {
		if(numberOfGaps>0){
			return new CompactAminoAcidSequence(builder.toString());
		}
		return new UngappedAminoAcidSequence(builder.toString());
	}

	@Override
	public AminoAcidSequence build(Range range) {
		return build(builder.substring((int)range.getStart(), (int)range.getEnd()+1));
	}

	private AminoAcidSequence build(String seqToBuild){
		if(numberOfGaps>0 && seqToBuild.indexOf('-')>0){
			return new CompactAminoAcidSequence(seqToBuild);
		}
		//no gaps
		return new UngappedAminoAcidSequence(seqToBuild);
	}
	@Override
	public List<AminoAcid> asList(Range range) {
		return AminoAcids.parse(builder.substring((int)range.getStart(), (int)range.getEnd()+1));
	}

	@Override
	public ResidueSequenceBuilder<AminoAcid, AminoAcidSequence> subSequence(
			Range range) {
		return new AminoAcidSequenceBuilder(builder.subSequence((int)range.getStart(), (int)range.getEnd()+1));
	}

	@Override
	public List<AminoAcid> asList() {
		return AminoAcids.parse(builder.toString());
	}

	@Override
	public ResidueSequenceBuilder<AminoAcid, AminoAcidSequence> reverse() {
		builder.reverse();
		return this;
	}

	@Override
	public ResidueSequenceBuilder<AminoAcid, AminoAcidSequence> ungap() {
		numberOfGaps=0;
		StringBuilder newBuilder = new StringBuilder(builder.length());
		for(AminoAcid aa : asList()){
			if(aa != AminoAcid.Gap){
				newBuilder.append(aa.toString());
			}
		}
		this.builder = newBuilder;
		return this;
	}

	@Override
	public String toString() {
		return builder.toString();
	}

}
