package org.jcvi.seqmodel.io;

import java.net.URL;
import java.util.Iterator;

import org.jcvi.fasta.DefaultEncodedNucleotideFastaRecord;
import org.jcvi.fasta.NucleotideSequenceFastaRecord;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.io.AbstractObjectIO;
import org.jcvi.io.DefaultNucleotideFastaRecordIO;
import org.jcvi.seqmodel.NucleotideSequence;

/*
 * {@code FastaNucleotideSequenceIO} is a reader and writer of {@link NucleotideSequence} 
 * objects generated based on an input fasta file.
 * 
 * The featureType attribute is used to set the type of feature for all objects
 * read in given a fasta file.
 * 
 * @author naxelrod
 */
public class FastaNucleotideSequenceIO extends AbstractObjectIO<NucleotideSequence> implements SequenceIO<NucleotideSequence> {

	private String featureType;
	
	public FastaNucleotideSequenceIO() {
		super();
	}
	public FastaNucleotideSequenceIO(String inFile) {
		super(inFile);
	}
	public FastaNucleotideSequenceIO(URL inUrl) {
		super(inUrl);
	}
	public FastaNucleotideSequenceIO(String inFile, String outFile) {
		super(inFile, outFile);
	}
	public FastaNucleotideSequenceIO(URL inUrl, URL outUrl) {
		super(inUrl, outUrl);
	}
	
	public String getFeatureType() {
		return featureType;
	}
	public void setFeatureType(String featureType) {
		this.featureType = featureType;
	}
	
	@Override
	public Iterator<NucleotideSequence> iterator() {
		
		// Get our iterator of FastaRecord objects from our input
		final Iterator<NucleotideSequenceFastaRecord> fastaIterator = new DefaultNucleotideFastaRecordIO(this.getInput()).iterator();
		
		if (fastaIterator == null) {
			return null;
		}
		
		return new Iterator<NucleotideSequence>() {
			@Override
			public boolean hasNext() {
				return fastaIterator.hasNext();
			}

			@Override
			public NucleotideSequence next() {
				// Convert FastaRecord to Sequence
				DefaultEncodedNucleotideFastaRecord fasta = (DefaultEncodedNucleotideFastaRecord) fastaIterator.next();
				if (fasta != null) {
					String id = fasta.getIdentifier();
					NucleotideEncodedGlyphs glyphs = fasta.getValues();
					NucleotideSequence s = new NucleotideSequence(fasta.getIdentifier(), glyphs);
					
					// Quick hack to set the feature type for everything in our iterator
					if (featureType != null) {
						s.setType(featureType);
					}
					
					// GenBank identifiers are often encoded as gb|CY031341.1|gi|3143111
					if (id.startsWith("gb") || id.startsWith("gi")) {
						String[] fields = id.split("\\|");
						for (int i=0; i<fields.length; i+=2) {
							if (fields.length > i) {
								if (fields[i].equals("gb")) {
									s.setAccession(fields[i+1]);
								} else if (fields[i].equals("gi")) {
									s.setGiNumber(Long.valueOf(fields[i+1]));
								}
							}
						}
					}
					return s;
				}
				return null;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("no remove allowed from " + this.getClass().getCanonicalName());
			}
		};
		
	}
}
