package org.jcvi.seqmodel.io;

import java.net.URL;
import java.util.Iterator;

import org.jcvi.fasta.PeptideSequenceFastaRecord;
import org.jcvi.glyph.aa.AminoAcidEncodedGlyphs;
import org.jcvi.io.AbstractObjectIO;
import org.jcvi.io.DefaultPeptideFastaRecordIO;
import org.jcvi.seqmodel.AminoAcidSequence;

/*
 * {@code FastaPeptideSequenceIO} is a reader and writer of {@link AminoAcidSequence}
 * objects generated from a fasta-formatted input stream.
 * 
 * The featureType attribute is used to set the type of feature for all objects
 * read in given a fasta file.
 * 
 * @author naxelrod
 */

public class FastaPeptideSequenceIO extends AbstractObjectIO<AminoAcidSequence> implements SequenceIO<AminoAcidSequence>{

	
	private String featureType;
	
	public FastaPeptideSequenceIO(URL url) {
		super(url);
	}
	
	public String getFeatureType() {
		return featureType;
	}
	public void setFeatureType(String featureType) {
		this.featureType = featureType;
	}
	
	@Override
	public Iterator<AminoAcidSequence> iterator() {
		
		// Get our iterator of FastaRecord objects from our input
		final Iterator<PeptideSequenceFastaRecord> fastaIterator = new DefaultPeptideFastaRecordIO(this.getInput()).iterator();
		
		if (fastaIterator == null) {
			return null;
		}
		
		return new Iterator<AminoAcidSequence>() {
			@Override
			public boolean hasNext() {
				return fastaIterator.hasNext();
			}

			@Override
			public AminoAcidSequence next() {

				PeptideSequenceFastaRecord fasta = fastaIterator.next();
				
				if (fasta != null) {
					String id = fasta.getIdentifier();
					AminoAcidEncodedGlyphs glyphs = fasta.getValues();
					
					AminoAcidSequence s = new AminoAcidSequence(fasta.getIdentifier(), glyphs);
					
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
