package org.jcvi.jillion.core.residue.nt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Ranges;
import org.jcvi.jillion.core.residue.nt.Nucleotide.InvalidCharacterHandler;
import org.jcvi.jillion.core.residue.nt.VariantNucleotideSequence.Variant.VariantBuilder;
import org.jcvi.jillion.core.util.SingleThreadAdder;
import org.jcvi.jillion.internal.core.util.ArrayUtil;
import org.jcvi.jillion.internal.core.util.GrowableIntArray;
import org.jcvi.jillion.internal.core.util.iter.PrimitiveArrayIterators;

public class VariantNucleotideSequence implements INucleotideSequence<VariantNucleotideSequence, VariantNucleotideSequence.Builder>{

	private final NucleotideSequence nucleotideSequence;
	private final Map<Integer, Variant> variants;
	//for faster iteration
	private final GrowableIntArray variantOffsets;
	
	public static VariantNucleotideSequence.Builder builder(){
		return new Builder();
	}
	public static VariantNucleotideSequence.Builder builder(NucleotideSequence sequence){
		return new Builder(sequence);
	}
	public static VariantNucleotideSequence of(NucleotideSequence sequence) {
		return new VariantNucleotideSequence(sequence, Collections.emptyMap());
	}
	
	private VariantNucleotideSequence(NucleotideSequence nucleotideSequence, Map<Integer, Variant> variants) {
		this.nucleotideSequence = nucleotideSequence;
		this.variants = variants;
		
		variantOffsets = new GrowableIntArray(variants.keySet());
		variantOffsets.sort();
	}
	
	@Override
	public NucleotideSequence toNucleotideSequence() {
		return nucleotideSequence;
	}
	public Builder toBuilder() {
		return new Builder(this);
	}
	public Builder toBuilder(List<Range> ranges) {
		return new Builder(this, ranges);
	}
	@Override
	public String toString() {
		Iterator<Variant> iter = variantIterator();
		StringBuilder builder = new StringBuilder((int) (nucleotideSequence.getLength() + variants.size()*10));
		while(iter.hasNext()) {
			builder.append(iter.next());
			
		}
		return builder.toString();
	}
	
	public NucleotideSequence getNucleotideSequence() {
		return nucleotideSequence;
	}

	public Map<Integer, Variant> getVariants() {
		return variants;
	}

	public long getLength() {
		return nucleotideSequence.getLength();
	}
	
	
	@Override
	public boolean isDna() {
		boolean isDna = nucleotideSequence.isDna();
		if(isDna ) {
			//check variants to see if any Uracil in there?
			boolean hasRna = variants.values().stream().flatMap(v-> v.minorityAlleles()).anyMatch(a-> a.base == Nucleotide.Uracil);
			return !hasRna;
		}
		return isDna;
		
	}
	public VariantNucleotideSequence trim(Range trimRange) {
		return new Builder(this).trim(trimRange).build();
	}
	public Iterator<Nucleotide> iterator(){
		return nucleotideSequence.iterator();
	}
	public Iterator<Nucleotide> reverseComplementIterator(){
		return nucleotideSequence.reverseComplementIterator();
	}
	public Iterator<Variant> variantIterator(){
		return new VariantIterator();
	}
	public Iterator<Variant> reverseComplementVariantIterator(){
		return new ReverseVariantIterator();
	}
	private static class VariantAndOffset{
		private int offset;
		private VariantBuilder variant;
		
		public VariantAndOffset(int offset, VariantBuilder variant) {
			this.offset = offset;
			this.variant = variant;
		}
		
		private VariantAndOffset(VariantAndOffset copy) {
			this.offset = copy.offset;
			this.variant = copy.variant.copy();
		}
		
		public VariantAndOffset copy() {
			return new VariantAndOffset(this);
		}

		public int getOffset() {
			return offset;
		}

		public void setOffset(int offset) {
			this.offset = offset;
		}

		public VariantBuilder getVariant() {
			return variant;
		}

		public void setVariant(VariantBuilder variant) {
			this.variant = variant;
		}
		public void shift(int amount) {
			this.offset +=amount;
		}
	}
	public static class Variant{
		private final Nucleotide majority;
		private final Map<Nucleotide, MinorityAllele> minorityAlleles;
		
		private Variant(Nucleotide onlyBase) {
			this.majority = onlyBase;
			this.minorityAlleles = Collections.emptyMap();
		}
		private Variant(Nucleotide majority, Map<Nucleotide, MinorityAllele> minorityAlleles) {
			this.majority = majority;
			this.minorityAlleles = minorityAlleles;
		}

		public Nucleotide getMajorityAllele() {
			return majority;
		}
		public double getMajorityPercentage() {
			return 1 - minorityAlleles.values().stream().mapToDouble(MinorityAllele::getPercent).sum();
		}
		
		public Stream<MinorityAllele> minorityAlleles(){
			return minorityAlleles.values().stream();
		}

		public List<Nucleotide> getOrderedAlleles(){
			return minorityAlleles.values().stream().sorted().map(MinorityAllele::getBase)
			.collect(Collectors.toCollection(()->{
				List<Nucleotide> n = new ArrayList<>(1+ minorityAlleles.size());
				n.add(majority);
				return n;
			}));
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(majority, minorityAlleles);
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Variant other = (Variant) obj;
			return majority == other.majority && Objects.equals(minorityAlleles, other.minorityAlleles);
		}
		@Override
		public String toString() {
			return toString(Variant::_printJustBases);
		}
		private static String _printJustBases(Variant v) {
			if(v.minorityAlleles.isEmpty()) {
				return v.majority.toString();
			}
			return v.minorityAlleles.values().stream().sorted().map(ma -> ma.base.toString()).collect(Collectors.joining("/", "{"+v.majority+"/", "}"));
		}
		private static String _printWithPercentages(Variant v) {
			if(v.minorityAlleles.isEmpty()) {
				return v.majority.toString();
			}
			StringBuilder builder = new StringBuilder();
			double majorityPercent=1-
										v.minorityAlleles.values().stream().sorted()
										.peek(ma -> builder.append(String.format(" %s %.2f",ma.getBase(), ma.getPercent()*100)))
										.mapToDouble(MinorityAllele::getPercent)
										.sum();
			return String.format("{%s %.2f%s}", v.majority, majorityPercent *100, builder);
		}
		public String toString(Function<Variant, String> printer) {
			return printer.apply(this);
		}
		public Variant complement() {
			VariantBuilder builder = new VariantBuilder(majority.complement());
			for(MinorityAllele allele : minorityAlleles.values()) {
				builder.addAllele(allele.complement());
			}
			return builder.build();
		}
		
		public VariantBuilder toBuilder() {
			return new VariantBuilder(this);
		}
		
		public static class VariantBuilder{
			private Nucleotide majority;
			private EnumMap<Nucleotide, MinorityAllele> minorityAlleles= new EnumMap<>(Nucleotide.class);
			
			public VariantBuilder(Nucleotide majority) {
				this.majority = Objects.requireNonNull(majority);
			}
			public VariantBuilder addAllele(MinorityAllele allele) {
//				if(majority == allele.getBase()) {
//					throw new IllegalArgumentException("already specified as majority : " + allele + " " + minorityAlleles);
//				}
				minorityAlleles.put(allele.getBase(), allele);
				return this;
			}
			public VariantBuilder(Variant v) {
				this.majority = v.getMajorityAllele();
				this.minorityAlleles.putAll(v.minorityAlleles);
			}
			public VariantBuilder complement() {
				this.majority = majority.complement();
				EnumMap<Nucleotide, MinorityAllele> complemented = new EnumMap<>(Nucleotide.class);
				for(Map.Entry<Nucleotide, MinorityAllele> entry : minorityAlleles.entrySet()) {
					complemented.put(entry.getKey().complement(), entry.getValue().complement());
				}
				this.minorityAlleles = complemented;
				return this;
			}
			public VariantBuilder addAllele(Nucleotide n, double percent) {
				
				return addAllele(new MinorityAllele(n, percent));
			}
			public VariantBuilder copy() {
				VariantBuilder copy = new VariantBuilder(majority);
				copy.minorityAlleles.putAll(minorityAlleles);
				return copy;
			}
			public Variant build() {
				return new Variant(majority, minorityAlleles);
			}
		}
	}
	
	public static class MinorityAllele implements Comparable<MinorityAllele>{
		private final Nucleotide base;
		private final double percent;
		public MinorityAllele(Nucleotide base, double percent) {
			if(percent <0 || percent >1) {
				throw new IllegalArgumentException("percent must be between [0..1]");
			}
			this.base = Objects.requireNonNull(base);
			this.percent = percent;
		}
		public Nucleotide getBase() {
			return base;
		}
		public double getPercent() {
			return percent;
		}
		
		public MinorityAllele complement() {
			return new MinorityAllele(base.complement(), percent);
		}
		@Override
		public int hashCode() {
			return Objects.hash(base, percent);
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MinorityAllele other = (MinorityAllele) obj;
			return base == other.base && Double.doubleToLongBits(percent) == Double.doubleToLongBits(other.percent);
		}
		@Override
		public int compareTo(MinorityAllele o) {
			return -Double.compare(percent, o.percent);
		}
		@Override
		public String toString() {
			return "MinorityAllele [base=" + base + ", percent=" + percent + "]";
		}
		
		
		
	}
	private class ReverseVariantIterator implements Iterator<Variant>{
		private int currentOffset;
		private Iterator<Nucleotide> delegate;
		public ReverseVariantIterator() {
			currentOffset=(int)(nucleotideSequence.getLength()-1);
			delegate = nucleotideSequence.reverseComplementIterator();
		}

		@Override
		public boolean hasNext() {
			return delegate.hasNext();
		}

		@Override
		public Variant next() {
			if(!hasNext()) {
				throw new NoSuchElementException();
			}
			Nucleotide majority = delegate.next();
			Variant v=null;
			if(variantOffsets.binarySearch(currentOffset) >=0) {
				//has variants
				v= variants.get(currentOffset).complement();
				
			}
			
			currentOffset--;
			if(v ==null) {
				return new Variant(majority);
			}
			return v;
		}
	}
	private class VariantIterator implements Iterator<Variant>{
		private int currentOffset;
		private Iterator<Nucleotide> delegate;
		public VariantIterator() {
			currentOffset=0;
			delegate = nucleotideSequence.iterator();
		}

		@Override
		public boolean hasNext() {
			return delegate.hasNext();
		}

		@Override
		public Variant next() {
			if(!hasNext()) {
				throw new NoSuchElementException();
			}
			Nucleotide majority = delegate.next();
			Variant v = null;
			if(variantOffsets.binarySearch(currentOffset) >=0) {
				//has variants
				v = variants.get(currentOffset);
				
			}
			currentOffset++;
			if(v ==null) {
				return new Variant(majority);
			}
			return v;
		}
		
		
	}

	public static class Builder implements INucleotideSequenceBuilder<VariantNucleotideSequence, Builder>{
		private NucleotideSequenceBuilder nucleotideSequence;
		private Set<VariantAndOffset> variants = new HashSet<>();
		
		
		public Builder() {
			this.nucleotideSequence = new NucleotideSequenceBuilder();
		}
		public Builder(int initialCapacity) {
			this.nucleotideSequence = new NucleotideSequenceBuilder(initialCapacity);
		}
		
		public Builder append(Nucleotide n) {
			this.nucleotideSequence.append(n);
			return this;
		}
		
		public Builder append(Nucleotide n, Map<Nucleotide, Double> variants) {
			Integer offset = (int)nucleotideSequence.getLength();
			this.nucleotideSequence.append(n);
			VariantBuilder variantBuilder = new VariantBuilder(n);
			for(Map.Entry<Nucleotide, Double> entry: variants.entrySet()) {
				variantBuilder.addAllele(entry.getKey(), entry.getValue());
			}
			this.variants.add(new VariantAndOffset(offset, variantBuilder));
			return this;
		}
		public Builder ungap() {
			if(nucleotideSequence.getNumGaps()==0) {
				return this;
			}
			//need to do this in 2 passes:
			//first pass remove variants at gap offsets
			//second pass adjust upstream variant offsets accordingly
			int gapOffsets[] = nucleotideSequence.getGapOffsets();
			
			variants.removeIf(vo -> Arrays.binarySearch(gapOffsets, vo.getOffset()) >=0);
			
			//only do 2nd pass if we have any variants left
			if(!variants.isEmpty()) {
				
				
				//reverse gap offsets for easier time shifting things
				ArrayUtil.reverse(gapOffsets);
				
				PrimitiveIterator.OfInt iter =  PrimitiveArrayIterators.create(gapOffsets);
				while(iter.hasNext()) {
					int gapOffset = iter.nextInt();
					Iterator<VariantAndOffset> adderIter = variants.iterator();
					while(adderIter.hasNext()) {
						VariantAndOffset adder = adderIter.next();
						if(adder.getOffset() >= gapOffset) {
							adder.shift(-1);
						}
					}
				}
			}
			nucleotideSequence.ungap();
			
			
			return this;
		}
		public Builder(NucleotideSequence nucleotideSequence) {
			this.nucleotideSequence = nucleotideSequence.toBuilder();
		}
		
		private Builder(VariantNucleotideSequence copy) {
			this.nucleotideSequence = copy.nucleotideSequence.toBuilder();
			//need to make deep copy so edits don't change original enumset
			for(Map.Entry<Integer, Variant> entry :  copy.variants.entrySet()) {
				variants.add(new VariantAndOffset(entry.getKey(), entry.getValue().toBuilder()));
			}
		}
		private Builder(VariantNucleotideSequence copy, Range trimRange) {
			this.nucleotideSequence = copy.nucleotideSequence.toBuilder(trimRange);
			//need to make deep copy so edits don't change original enumset
			int delta = (int) trimRange.getBegin();
			for(Map.Entry<Integer, Variant> entry :  copy.variants.entrySet()) {
				if(trimRange.intersects(entry.getKey())) {
					variants.add(new VariantAndOffset(entry.getKey()-delta, entry.getValue().toBuilder()));
				}
			}
		}
		private Builder(VariantNucleotideSequence copy, List<Range> trimRanges) {
			this.nucleotideSequence = copy.nucleotideSequence.toBuilder(trimRanges);
			//need to make deep copy so edits don't change original enumset
			
			for(Map.Entry<Integer, Variant> entry :  copy.variants.entrySet()) {
				int current=0;
				int variantOffset = entry.getKey();
				for(Range r : trimRanges) {
					if(r.intersects(variantOffset)) {
						variants.add(new VariantAndOffset(current + variantOffset- (int)r.getBegin(), entry.getValue().toBuilder()));
						//can't break incase downstream ranges overlap it again and we need to have the variant multiple times!
						//break;
					}
					current+=r.getLength();
				}
			}
		}
		public Builder trim(Range trimRange) {
			int delta = -(int)trimRange.getBegin();
			Iterator<VariantAndOffset> iter = variants.iterator();
			while(iter.hasNext()) {
				VariantAndOffset va = iter.next();
				if(!trimRange.intersects(va.getOffset())) {
					iter.remove();
				}else {
					va.shift(delta);
				}
			}
			nucleotideSequence.trim(trimRange);
			return this;
			
			
		}
		public Builder variant(int offset, Nucleotide variant, double percent) {
			VariantAndOffset found=null;
			for(VariantAndOffset va : variants) {
				if(offset == va.getOffset()) {
					found= va;
					break;
				}
			}
			if(found ==null) {
				found = new VariantAndOffset(offset,new VariantBuilder(nucleotideSequence.get(offset)));
				variants.add(found);
			}
			
			found.variant.addAllele(variant, percent);
			return this;
		}
		
		public Builder reverseComplement() {
			nucleotideSequence.reverseComplement();
			int end = (int) nucleotideSequence.getLength()-1;

			for(VariantAndOffset va : variants) {
				
				int newOffset = end- va.getOffset();
				va.setOffset(newOffset);
				va.variant.complement();
				
			}
			return this;
		}
		public VariantNucleotideSequence build() {
			Map<Integer, Variant> built = new TreeMap<>();
			for(VariantAndOffset  entry :  variants) {
				built.put(entry.getOffset(), entry.getVariant().build());
			}
			return new VariantNucleotideSequence(nucleotideSequence.build(), built);
		}
		@Override
		public Builder append(Iterable<Nucleotide> sequence) {
			nucleotideSequence.append(sequence);
			return this;
		}
		@Override
		public Builder append(String sequence) {
			nucleotideSequence.append(sequence);
			return this;
		}
		@Override
		public Builder insert(int offset, String sequence) {
			long oldLength = nucleotideSequence.getLength();
			nucleotideSequence.insert(offset, sequence);
			long newLength = nucleotideSequence.getLength();
			int delta = (int)(newLength - oldLength);
			variants.forEach(va -> {
				if(va.getOffset() >= offset) {
					va.shift(delta);
				}
				
			});
			
			this.nucleotideSequence.insert(offset, sequence);
			return this;
		}
		@Override
		public long getLength() {
			return nucleotideSequence.getLength();
		}
		@Override
		public long getUngappedLength() {
			return nucleotideSequence.getUngappedLength();
		}
		@Override
		public Builder replace(int offset, Nucleotide replacement) {
			this.nucleotideSequence.replace(offset, replacement);
			return this;
		}
		@Override
		public Builder replace(Range range, Nucleotide[] replacement) {
			this.nucleotideSequence.replace(range, replacement);
			return this;
		}
		@Override
		public Builder replace(Range range, Builder replacement) {
			Iterator<VariantAndOffset> iter = variants.iterator();
			int shiftAmount = (int)(replacement.getLength() - range.getLength());
			while(iter.hasNext()) {
				VariantAndOffset v = iter.next();
				if(range.intersects(v.getOffset())) {
					iter.remove();
				}else if(range.getEnd()< v.getOffset()) {
					v.shift(shiftAmount);
				}
			}
			
			this.nucleotideSequence.replace(range, replacement.nucleotideSequence);
			replacement.variants.stream().forEach(va->{
				VariantAndOffset copy = va.copy();
				copy.shift((int)range.getBegin());
				variants.add(copy);
			});
			return this;
		}
		@Override
		public Builder delete(Range range) {
			int deleteLength = -(int) range.getLength();
			Iterator<VariantAndOffset> iter = variants.iterator();
			while(iter.hasNext()) {
				VariantAndOffset v = iter.next();
				if(range.intersects(v.getOffset())) {
					iter.remove();
				}else if(range.getEnd() < v.getOffset()) {
					v.shift(deleteLength);
				}
			}
			
			this.nucleotideSequence.delete(range);
			return this;
		}
		@Override
		public int getNumGaps() {
			return nucleotideSequence.getNumGaps();
		}
		@Override
		public Builder prepend(String sequence) {
			if(variants.isEmpty()) {
				nucleotideSequence.prepend(sequence);
				return this;
			}
			//adjust variant offsets
			long oldLength = nucleotideSequence.getLength();
			nucleotideSequence.prepend(sequence);
			long newLength = nucleotideSequence.getLength();
			int delta = (int) (newLength-oldLength);
			variants.forEach(va-> va.shift(delta));
			return this;
		}
		@Override
		public Builder insert(int offset, Iterable<Nucleotide> sequence) {
			long oldLength = nucleotideSequence.getLength();
			nucleotideSequence.insert(offset, sequence);
			long newLength = nucleotideSequence.getLength();
			int delta = (int)(newLength - oldLength);
			variants.forEach(va -> {
				if(va.getOffset() >= offset) {
					va.shift(delta);
				}
				
			});
			return this;
		}
		@Override
		public Builder insert(int offset, Nucleotide base) {
			nucleotideSequence.insert(offset, base);

			variants.forEach(va -> {
				if(va.getOffset() >= offset) {
					va.shift(1);
				}
				
			});
			return this;
		}
		@Override
		public Builder prepend(Iterable<Nucleotide> sequence) {
			if(variants.isEmpty()) {
				nucleotideSequence.prepend(sequence);
				return this;
			}
			//adjust variant offsets
			long oldLength = nucleotideSequence.getLength();
			nucleotideSequence.prepend(sequence);
			long newLength = nucleotideSequence.getLength();
			int delta = (int) (newLength-oldLength);
			variants.forEach(va-> va.shift(delta));
			return this;
		}
		@Override
		public Builder prepend(Builder otherBuilder) {
			int shift = (int) otherBuilder.nucleotideSequence.getLength();
			variants.forEach(va-> va.shift(shift));
			
			nucleotideSequence.prepend(otherBuilder.nucleotideSequence);
			otherBuilder.variants.forEach(va -> variants.add(va.copy()));
			return this;
			
		}
		@Override
		public Builder copy() {
			Builder copy = new Builder();
			copy.nucleotideSequence = nucleotideSequence.copy();
			copy.variants = this.variants.stream().map(VariantAndOffset::copy).collect(Collectors.toSet());
			return copy;
		}
		@Override
		public Builder reverse() {
			this.nucleotideSequence.reverse();
			int length = (int) nucleotideSequence.getLength();
			int delta = length-1;
        	variants.forEach(va -> va.setOffset(delta - va.getOffset()));
        	
			return this;
		}
		@Override
		public Builder turnOffDataCompression(boolean turnOffDataCompression) {
			nucleotideSequence.turnOffDataCompression(turnOffDataCompression);
			return this;
		}
		@Override
		public Nucleotide get(int offset) {
			return nucleotideSequence.get(offset);
		}
		@Override
		public Builder getSelf() {
			return this;
		}
		@Override
		public Iterator<Nucleotide> iterator() {
			return nucleotideSequence.iterator();
		}
		@Override
		public Builder clear() {
			this.nucleotideSequence.clear();
			this.variants.clear();
			return this;
		}
		@Override
		public Builder insert(int offset, Builder otherBuilder) {
			long oldLength = nucleotideSequence.getLength();
			nucleotideSequence.insert(offset, otherBuilder.nucleotideSequence);
			long newLength = nucleotideSequence.getLength();
			int delta = (int)(newLength - oldLength);
			variants.forEach(va -> {
				if(va.getOffset() >= offset) {
					va.shift(delta);
				}
				
			});
			otherBuilder.variants.stream()
								.map(va -> {
									VariantAndOffset copy = va.copy();
									copy.shift(offset);
									return copy;
								})
								.forEach(variants::add);
			
			return this;
		}
		@Override
		public Builder insert(int offset, NucleotideSequence sequence) {
			long oldLength = nucleotideSequence.getLength();
			nucleotideSequence.insert(offset, sequence);
			long newLength = nucleotideSequence.getLength();
			int delta = (int)(newLength - oldLength);
			variants.forEach(va -> {
				if(va.getOffset() >= offset) {
					va.shift(delta);
				}
				
			});
			return this;
		}
		@Override
		public Builder insert(int offset, Nucleotide[] sequence) {
			long oldLength = nucleotideSequence.getLength();
			nucleotideSequence.insert(offset, sequence);
			long newLength = nucleotideSequence.getLength();
			int delta = (int)(newLength - oldLength);
			variants.forEach(va -> {
				if(va.getOffset() >= offset) {
					va.shift(delta);
				}
				
			});
			return this;
		}
		@Override
		public Builder insert(int offset, char[] sequence) {
			long oldLength = nucleotideSequence.getLength();
			nucleotideSequence.insert(offset, sequence);
			long newLength = nucleotideSequence.getLength();
			int delta = (int)(newLength - oldLength);
			variants.forEach(va -> {
				if(va.getOffset() >= offset) {
					va.shift(delta);
				}
				
			});
			return this;
		}
		@Override
		public Builder append(char[] sequence) {
			nucleotideSequence.append(sequence);
			return this;
		}
		@Override
		public Builder append(Nucleotide[] sequence) {
			nucleotideSequence.append(sequence);
			return this;
		}
		@Override
		public Builder append(NucleotideSequence sequence) {
			nucleotideSequence.append(sequence);
			return this;
		}
		@Override
		public Builder append(NucleotideSequence sequence, Range range) {
			nucleotideSequence.append(sequence, range);
			return this;
		}
		@Override
		public Builder append(NucleotideSequenceBuilder otherBuilder) {
			nucleotideSequence.append(otherBuilder);
			return this;
		}
		@Override
		public Builder setInvalidCharacterHandler(InvalidCharacterHandler invalidCharacterHandler) {
			nucleotideSequence.setInvalidCharacterHandler(invalidCharacterHandler);
			return this;
		}
		@Override
		public Builder prepend(Nucleotide n) {
			
			nucleotideSequence.prepend(n);
			
			variants.forEach(va-> va.shift(1));
			return this;
		}
		@Override
		public Builder prepend(NucleotideSequence sequence) {
			if(variants.isEmpty()) {
				nucleotideSequence.prepend(sequence);
				return this;
			}
			//adjust variant offsets
			long oldLength = nucleotideSequence.getLength();
			nucleotideSequence.prepend(sequence);
			long newLength = nucleotideSequence.getLength();
			int delta = (int) (newLength-oldLength);
			variants.forEach(va-> va.shift(delta));
			return this;
		}
		@Override
		public Range toGappedRange(Range ungappedRange) {
			return this.nucleotideSequence.toGappedRange(ungappedRange);
		}
		@Override
		public Range toUngappedRange(Range gappedRange) {
			return this.nucleotideSequence.toUngappedRange(gappedRange);
		}
	}

	@Override
	public List<Integer> getGapOffsets() {
		return nucleotideSequence.getGapOffsets();
	}
	@Override
	public List<Range> getRangesOfGaps() {
		return nucleotideSequence.getRangesOfGaps();
	}
	@Override
	public int getNumberOfGaps() {
		return nucleotideSequence.getNumberOfGaps();
	}
	@Override
	public boolean isGap(int gappedOffset) {
		return nucleotideSequence.isGap(gappedOffset);
	}
	@Override
	public long getUngappedLength() {
		return nucleotideSequence.getUngappedLength();
	}
	@Override
	public int getNumberOfGapsUntil(int gappedOffset) {
		return nucleotideSequence.getNumberOfGapsUntil(gappedOffset);
	}
	@Override
	public int getUngappedOffsetFor(int gappedOffset) {
		return nucleotideSequence.getUngappedOffsetFor(gappedOffset);
	}
	@Override
	public int getGappedOffsetFor(int ungappedOffset) {
		return nucleotideSequence.getGappedOffsetFor(ungappedOffset);
	}
	@Override
	public Builder toBuilder(Range range) {
		return new Builder(this, range);
	}
	@Override
	public VariantNucleotideSequence asSubtype() {
		return this;
	}
	@Override
	public Nucleotide get(long offset) {
		return nucleotideSequence.get(offset);
	}
	@Override
	public Iterator<Nucleotide> iterator(Range range) {
		return nucleotideSequence.iterator(range);
	}
	@Override
	public Builder newEmptyBuilder() {
		return new Builder();
	}
	@Override
	public Builder newEmptyBuilder(int initialCapacity) {
		return new Builder(initialCapacity);
	}
	public Map<Integer, Variant> getVariantsThatIntersectGappedRange(List<Range> gappedSequenceRanges) {
		Map<Integer, Variant> ret= new LinkedHashMap<>();
		for(Range r : gappedSequenceRanges) {
			addAllIntersectingVariants(r, ret::put);
		}
		return ret;
	}
	public Map<Integer, Variant> getVariantsThatIntersectGappedRange(Range gappedSequenceRange) {
		Map<Integer, Variant> ret= new LinkedHashMap<>();

		addAllIntersectingVariants(gappedSequenceRange, ret::put);
		return ret;
		
	}
	
	private void addAllIntersectingVariants(Range gappedSequenceRange, BiConsumer<Integer, Variant> consumer) {
		gappedSequenceRange.forEachValue(gappedOffset ->{
			int asInt = (int)gappedOffset;
			if(variantOffsets.binarySearch(asInt) >=0) {
				Integer boxed = Integer.valueOf(asInt);
				consumer.accept(boxed, variants.get(boxed));
			}
		});
	}
	
	

	


	
}
