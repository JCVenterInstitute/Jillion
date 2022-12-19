package org.jcvi.jillion.core.residue.nt;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.residue.nt.VariantNucleotideSequence.Variant;
import org.jcvi.jillion.core.util.UnAdjustedCoordinateMapper;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
@FunctionalInterface
public interface UnderlyingCoverage {

	
	List<VariantTriplet> getCoverageFor(UnderlyingCoverageParameters parameters);
	
	default List<VariantTriplet> getCoverageFor(int gappedOffset1, int gappedOffset2, int gappedOffset3){
		return getCoverageFor(gappedOffset1, gappedOffset2, gappedOffset3);
	}
	default List<VariantTriplet> getCoverageFor(int gappedOffset1, int gappedOffset2, int gappedOffset3, Direction dir){
		return getCoverageFor(UnderlyingCoverageParameters.builder()
				.gappedOffsets(gappedOffset1, gappedOffset2, gappedOffset3)
				.dir(dir)
				.build());
	}
	
	default UnderlyingCoverage map(UnAdjustedCoordinateMapper mapper) {
		Objects.requireNonNull(mapper);
		return params-> {
			UnderlyingCoverageParameters mapped = params.map(mapper);
//			System.out.println("old params = " + params);
//			System.out.println("new params = " + mapped);
			return this.getCoverageFor(mapped);
		};
	}
	
	@Data
	@Builder(toBuilder = true)
	public static class UnderlyingCoverageParameters{
		private final int gappedOffset1, gappedOffset2, gappedOffset3;
		private final int unadjustedGappedOffset1, unadjustedGappedOffset2, unadjustedGappedOffset3;
		private final Variant variant1, variant2, variant3;
		private final Nucleotide ref1, ref2, ref3;
		private final @NonNull Direction dir;
		private final BiConsumer<UnderlyingCoverageParameters, String> featureConsumer;
		
		public UnderlyingCoverageParameters map(UnAdjustedCoordinateMapper mapper) {
			Objects.requireNonNull(mapper);
			return toBuilder()
				.unadjustedGappedOffset1((int) mapper.unadjust(this.unadjustedGappedOffset1))
				.unadjustedGappedOffset2((int) mapper.unadjust(this.unadjustedGappedOffset2))
				.unadjustedGappedOffset3((int) mapper.unadjust(this.unadjustedGappedOffset3))
				.build();
		}
		
		public static UnderlyingCoverageParametersBuilder builder() {
			return new UnderlyingCoverageParametersBuilder()
										.dir(Direction.FORWARD)
										.unadjustedGappedOffsets(-1,-1,-1);
		}
		
		public static class UnderlyingCoverageParametersBuilder{
			
			public UnderlyingCoverageParametersBuilder gappedOffsets(int  gappedOffset1,int gappedOffset2, int gappedOffset3) {
				gappedOffset1(gappedOffset1);
				gappedOffset2(gappedOffset2);
				gappedOffset3(gappedOffset3);
				return this;
			}
			
			public UnderlyingCoverageParametersBuilder unadjustedGappedOffsets(int  unadjustedGappedOffset1,int unadjustedGappedOffset2, int unadjustedGappedOffset3) {
				unadjustedGappedOffset1(unadjustedGappedOffset1);
				unadjustedGappedOffset2(unadjustedGappedOffset2);
				unadjustedGappedOffset3(unadjustedGappedOffset3);
				return this;
			}
			public UnderlyingCoverageParametersBuilder refs(Nucleotide ref1,Nucleotide ref2, Nucleotide ref3) {
				ref1(ref1);
				ref2(ref2);
				ref3(ref3);
				return this;
			}
			
			public UnderlyingCoverageParameters build() {
				if(unadjustedGappedOffset1==-1) {
					unadjustedGappedOffset1 = gappedOffset1;
				}
				if(unadjustedGappedOffset2==-1) {
					unadjustedGappedOffset2 = gappedOffset2;
				}
				if(unadjustedGappedOffset3==-1) {
					unadjustedGappedOffset3 = gappedOffset3;
				}
				return new UnderlyingCoverageParameters(gappedOffset1, gappedOffset2, gappedOffset3,
						unadjustedGappedOffset1, unadjustedGappedOffset2, unadjustedGappedOffset3, 
						variant1, variant2, variant3, 
						ref1, ref2, ref3,
						dir,
						featureConsumer);
			}
		}
	}
}
