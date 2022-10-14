package org.jcvi.jillion.core.residue.aa;

import org.jcvi.jillion.core.residue.Frame;

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
	
	private boolean mergeCodons = true;
	private Integer numberOfBasesToTranslate=null;
	
	public static TranslationOptionsBuilder builder() {
		return new TranslationOptionsBuilder()
						.frame(Frame.ONE)
						.substituteStart(true)
						.mergeCodons(true)
						.readThroughStops(true)
						;
	}

	//this is to make maven javadoc work... lombok will populate class for us
	public static class TranslationOptionsBuilder{}
}