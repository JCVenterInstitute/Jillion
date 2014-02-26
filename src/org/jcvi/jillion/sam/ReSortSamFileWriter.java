package org.jcvi.jillion.sam;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.sam.attribute.SamAttributeValidator;
import org.jcvi.jillion.sam.header.SamHeader;

public class ReSortSamFileWriter extends AbstractReSortSamFileWriter{

	public ReSortSamFileWriter(File outputFile, File tmpDirRoot, SamHeader header,
			int maxRecordsToKeepInMemory, SamAttributeValidator attributeValidator) throws IOException {
		super(outputFile, tmpDirRoot, header, maxRecordsToKeepInMemory, attributeValidator, ".sam");
	}

	@Override
	protected SamWriter createOutputWriter(File out, SamHeader header)
			throws IOException {
		//no validation since we have already validated
		//the reads when we added them to our in memcheck
		return new PresortedSamFileWriter(out, header, NullSamAttributeValidator.INSTANCE);
	}
}
