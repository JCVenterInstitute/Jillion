package org.jcvi.jillion.trace.fastq;

import java.io.IOException;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

class InMemorySortedFastqWriter implements FastqWriter{

	private final FastqWriter writer;
	private final Comparator<FastqRecord> comparator;
	
	private final Set<FastqRecord> sorted;
	
	public InMemorySortedFastqWriter(FastqWriter writer, Comparator<FastqRecord> comparator){
		Objects.requireNonNull(writer);
		Objects.requireNonNull(comparator);
		this.writer = writer;
		this.comparator = comparator;
		sorted = new TreeSet<FastqRecord>(comparator);
	}
	@Override
	public void close() throws IOException {
		for(FastqRecord record : sorted){
			writer.write(record);
		}
		sorted.clear();
		writer.close();
	}

	@Override
	public void write(FastqRecord record) throws IOException {
		sorted.add(record);
		
	}

	@Override
	public void write(String id, NucleotideSequence nucleotides,
			QualitySequence qualities) throws IOException {
		write(new FastqRecordBuilder(id, nucleotides, qualities).build());
		
	}

	@Override
	public void write(String id, NucleotideSequence sequence,
			QualitySequence qualities, String optionalComment)
			throws IOException {
		write(new FastqRecordBuilder(id, sequence, qualities).comment(optionalComment).build());
	}

}
