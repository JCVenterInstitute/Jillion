package org.jcvi.jillion.core.residue.aa;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.util.MapUtil;
import org.jcvi.jillion.internal.core.util.ArrayUtil;

import lombok.Data;

public class VariantProteinSequence {

	
	
	private final ProteinSequence proteinSequence;
	private final Map<Integer, Map<AminoAcid,SNP>> variants;
	
	private VariantProteinSequence(ProteinSequence proteinSequence, Map<Integer, SNPMap> variantMap) {
		this.proteinSequence = proteinSequence;
		this.variants = new ConcurrentHashMap<>();
		for(Map.Entry<Integer, SNPMap> entry : variantMap.entrySet()) {
			variants.put(entry.getKey(), entry.getValue().computeFinializeSNPs());
			
		}
	}
	
	public Builder toBuilder() {
		return new Builder(this);
	}
	
	
	public ProteinSequence getProteinSequence() {
		return proteinSequence;
	}

	public Map<Integer, Map<AminoAcid,SNP>> getVariants() {
		return variants;
	}
	public long getUngappedLength() {
		return proteinSequence.getUngappedLength();
	}
	
	public VariantProteinSequence adapt(ProteinSequence gappedSequence) {
		if(!proteinSequence.isEqualToIgnoringGaps(gappedSequence)) {
			throw new IllegalArgumentException("gapped sequence must be equal to this sequence plus gaps");
		}
		int[] oldOffsets = new int[variants.size()];
		int[] newOffsets =  new int[variants.size()];
		int i=0;
		for(Integer offset : variants.keySet()) {
			oldOffsets[i] = offset.intValue();
			newOffsets[i] = offset.intValue();
			i++;
		}
		gappedSequence.gaps().forEach(gapOffset->{
		
			for(int k=0; k< newOffsets.length;k++) {
				if(newOffsets[k]>=gapOffset) {
					newOffsets[k]++;
				}
			}
		});
		Map<Integer, Map<AminoAcid,SNP>> gappedVariants = new ConcurrentHashMap<>(MapUtil.computeMinHashMapSizeWithoutRehashing(variants.size()));
		for(int j=0; j< oldOffsets.length; j++) {
			gappedVariants.put(newOffsets[j], new EnumMap<>(variants.get(oldOffsets[j])));
		}
		//this is a hack because we can't overload constructor with different map generics
		
		VariantProteinSequence newSeq= new VariantProteinSequence(gappedSequence, Collections.emptyMap());
		newSeq.variants.putAll(gappedVariants);
		return newSeq;
	}

	@Override
	public int hashCode() {
		return Objects.hash(proteinSequence, variants);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VariantProteinSequence other = (VariantProteinSequence) obj;
		return Objects.equals(proteinSequence, other.proteinSequence) && Objects.equals(variants, other.variants);
	}




	public static class Builder {
		private ProteinSequenceBuilder proteinSequenceBuilder;
		private Map<Integer, SNPMap> variants = new ConcurrentHashMap<>();
		
		public Builder() {
			this.proteinSequenceBuilder = new ProteinSequenceBuilder();
		}
		public Builder(int initialCapacity) {
			this.proteinSequenceBuilder = new ProteinSequenceBuilder(initialCapacity);
		}
		public Builder(ProteinSequence proteinSequence) {
			this.proteinSequenceBuilder = proteinSequence.toBuilder();
		}
		public long getLength() {
			return proteinSequenceBuilder.getLength();
		}
		public long getUngappedLength() {
			return proteinSequenceBuilder.getUngappedLength();
		}
		public Builder append(AminoAcid aa) {
			proteinSequenceBuilder.append(aa);
			return this;
		}
		public Builder append(AminoAcid aa, AminoAcid... variants) {
			return append(aa, Arrays.asList(variants));
		}
		public Builder append(AminoAcid aa, Iterable<AminoAcid> variants) {
			
			SNPMap snpMap = new SNPMap();
			snpMap.isPartial=true;
			snpMap.majority = aa;
			snpMap.getVariants().put(aa, null);
			
			
			for(AminoAcid v : variants) {
				snpMap.getVariants().put(v, null);
			}
			return _append(aa, snpMap);
		}
		public Builder _append(AminoAcid majority,SNPMap snps) {
			Integer offset = (int) proteinSequenceBuilder.getLength();
			proteinSequenceBuilder.append(majority);
			
			this.variants.put(offset, snps);
			return this;
		}
		public Builder append(AminoAcid majority, Map<AminoAcid, Double> variants) {
			
			SNPMap snpMap = new SNPMap();
			snpMap.majority=majority;
			if(variants.containsKey(majority)) {
				for(Map.Entry<AminoAcid, Double> entry : variants.entrySet()) {
					snpMap.getVariants().put(entry.getKey(), entry.getValue());
				}
			}else {
				snpMap.isPartial = true;
				for(Map.Entry<AminoAcid, Double> entry : variants.entrySet()) {
					snpMap.getVariants().put(entry.getKey(), entry.getValue());
				}
			}
			
			return _append(majority, snpMap);
		}
		
		private Builder(VariantProteinSequence copy) {
			this.proteinSequenceBuilder = copy.proteinSequence.toBuilder();
			//need to make deep copy so edits don't change original enumset
			for(Map.Entry<Integer, Map<AminoAcid, SNP>> entry :  copy.variants.entrySet()) {
				
				
				variants.put(entry.getKey(), new SNPMap(entry.getValue()));
			}
		}
		public Builder trim(Range trimRange) {
			long newEnd = trimRange.getLength() -1;
			int delta = (int)trimRange.getBegin();
			
			Map<Integer, SNPMap> adjustedVariants  = new ConcurrentHashMap<>();
			for(Map.Entry<Integer, SNPMap> entry :  variants.entrySet()) {
				
				int newOffset = entry.getKey().intValue() - delta;
				if(newOffset >=0 && newOffset <=newEnd) {
					adjustedVariants.put(newOffset, entry.getValue());
				}
			}
			variants = adjustedVariants;
			proteinSequenceBuilder.trim(trimRange);
			return this;
			
			
		}
		public Builder variant(int offset, AminoAcid variant) {
			AminoAcid majority = proteinSequenceBuilder.get(offset);
			variants.computeIfAbsent(offset, k-> {
				SNPMap snpMap = new SNPMap();
				snpMap.isPartial = true;
				snpMap.variants.put(majority, null);
				snpMap.majority = majority;
				return snpMap;
			}).variants.put(variant, null);
			return this;
		}
		
		public VariantProteinSequence build() {
			return new VariantProteinSequence(proteinSequenceBuilder.build(), variants);
		}
	}
	
	@Data
	@lombok.Builder
	public static class SNP implements Comparable<SNP>{
		private final AminoAcid aa;
		private final double percent;
		private final boolean isMajority;
		
		
		@Override
		public int compareTo(SNP o) {
			return Double.compare(percent, o.percent);
		}
	}
	
	public static class SNPMap{
		private Map<AminoAcid, Double> variants = new EnumMap<>(AminoAcid.class);
		private AminoAcid majority;
		private boolean isPartial;
		
		public SNPMap() {
			
		}
		public SNPMap(Map<AminoAcid, SNP> map) {
			for(Map.Entry<AminoAcid, SNP> entry : map.entrySet()) {
				variants.put(entry.getKey(), entry.getValue().getPercent());
				if(entry.getValue().isMajority()) {
					majority=entry.getKey();
				}
			}
		}
		
		
		public Map<AminoAcid, Double> getVariants() {
			return variants;
		}
		public AminoAcid getMajority() {
			return majority;
		}
		public boolean isPartial() {
			return isPartial;
		}
		
		public Map<AminoAcid, SNP> computeFinializeSNPs(){
			if(isPartial) {
				double remaining = 1D;
				int numberToCompute=1;
				for(Map.Entry<AminoAcid, Double> entry : variants.entrySet()) {
					if(entry.getKey() != majority) {
						if(entry.getValue() ==null) {
							numberToCompute++;
						}else {
							remaining-=entry.getValue().doubleValue();
						}
						
					}
				}
				double percent = remaining/numberToCompute;
				Map<AminoAcid, SNP> ret = new EnumMap<>(AminoAcid.class);
				for(Map.Entry<AminoAcid, Double> entry : variants.entrySet()) {
					ret.put(entry.getKey(), 
							new SNP(entry.getKey(),
									entry.getValue() ==null? percent: entry.getValue(), 
									entry.getKey() == majority));
					
					
				}
				return ret;
			}
			//here is not partial
			Map<AminoAcid, SNP> ret = new EnumMap<>(AminoAcid.class);
			for(Map.Entry<AminoAcid, Double> entry : variants.entrySet()) {
				ret.put(entry.getKey(), 
						new SNP(entry.getKey(),
								entry.getValue(), 
								entry.getKey() == majority));
				
				
			}
			return ret;
		}
	}
	
}
