package org.jcvi.jillion.trace.fastq;

import java.io.File;
import java.io.IOException;

public interface FastqDownsampler{
	
	default void downsample(File fastqFile, FastqQualityCodec codec, FastqWriter outputWriter) throws IOException{
		downsample(FastqFileParser.create(fastqFile),codec, outputWriter);
	}
	void downsample(FastqParser fastqParser, FastqQualityCodec codec, FastqWriter outputWriter) throws IOException;

	
	default void downsamplePair(File read1FastqFile, File read2FastqFile, FastqQualityCodec codec, 
			FastqWriter read1OutputWriter, FastqWriter read2OutputWriter) throws IOException{
		
		downsamplePair(FastqFileParser.create(read1FastqFile), FastqFileParser.create(read2FastqFile),
				codec, read1OutputWriter, read2OutputWriter);
	}
	void downsamplePair(FastqParser read1FastqParser, FastqParser read2FastqParser, FastqQualityCodec codec,
			FastqWriter read1OutputWriter, FastqWriter read2OutputWriter) throws IOException;
}
