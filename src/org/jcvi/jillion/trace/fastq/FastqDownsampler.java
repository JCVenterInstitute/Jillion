package org.jcvi.jillion.trace.fastq;

import java.io.File;
import java.io.IOException;

public interface FastqDownsampler{
	
	void downsample(File fastqFile, FastqQualityCodec codec, FastqWriter outputWriter) throws IOException;
	void downsample(FastqParser fastqParser, FastqQualityCodec codec, FastqWriter outputWriter) throws IOException;

}
