package org.jcvi.jillion.core.residue.aa;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.jcvi.jillion.core.residue.Frame;
import org.jcvi.jillion.core.residue.nt.UnderlyingCoverage.UnderlyingCoverageFeature;
import org.jcvi.jillion.core.residue.nt.UnderlyingCoverage.UnderlyingCoverageParameters;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder(toBuilder = true)
public class TranslationOptions{
	
	public static TranslationOptions createDefaultOptions() {
		return TranslationOptions.builder().build();
	}
	private boolean ignoreGaps;
	@NonNull
	private Frame frame= Frame.ONE;
	private boolean substituteStart=true;
	private boolean readThroughStops=true;
	private boolean explodeNucleotides=true;
	private boolean mergeCodons = true;
	private Integer numberOfBasesToTranslate=null;
	private Consumer<UnderlyingCoverageFeature> featureConsumer;
	
	public static TranslationOptionsBuilder builder() {
		return new TranslationOptionsBuilder()
						.frame(Frame.ONE)
						.substituteStart(true)
						.mergeCodons(true)
						.explodeNucleotides(true)
						.readThroughStops(true)
						;
	}

	//this is to make maven javadoc work... lombok will populate class for us
	public static class TranslationOptionsBuilder{}
}