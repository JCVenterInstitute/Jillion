
package org.jcvi.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import org.jcvi.fastX.fasta.FastaRecordFactory;
import org.jcvi.fastX.fasta.SequenceFastaRecordUtil;
import org.jcvi.fastX.fasta.seq.DefaultNucleotideFastaRecordFactory;
import org.jcvi.fastX.fasta.seq.NucleotideSequenceFastaRecord;

/*
 * {@code DefaultNucleotideFastaRecordIO} is the default implementation for
 * the ObjectIO of {@link NucleotideSequenceFastaRecord} objects. 
 * 
 * @author naxelrod
 */
public class DefaultNucleotideFastaRecordIO extends AbstractObjectIO<NucleotideSequenceFastaRecord> implements NucleotideFastaRecordIO {

	protected final FastaRecordFactory<NucleotideSequenceFastaRecord> FACTORY = DefaultNucleotideFastaRecordFactory.getInstance();
	
	public DefaultNucleotideFastaRecordIO() {
		super();
	}
	
	public DefaultNucleotideFastaRecordIO(String inFile) throws IOException {
		super(inFile);
	}

	public DefaultNucleotideFastaRecordIO(BufferedReader input) {
		super(input);
	}

	public DefaultNucleotideFastaRecordIO(URL url) {
		super(url);
	}

	@Override
	public Iterator<NucleotideSequenceFastaRecord> iterator() {

		return new Iterator<NucleotideSequenceFastaRecord>() {
			
			private NucleotideSequenceFastaRecord currentRecord = null;
	        private String currentId=null;
	        private String currentComment=null;
			
			@Override
			public boolean hasNext() {
				if (currentRecord == null) { 
					currentRecord = next(); 
				}
				return currentRecord != null;
			}

			@Override
			public NucleotideSequenceFastaRecord next() {
				if (currentRecord != null) {
					NucleotideSequenceFastaRecord tmp = currentRecord;
					currentRecord = null;
					return tmp;
				}

				// Read until we reach the next record
				String line;
		        StringBuilder currentBody=null;
		        
		        try {
					while ((line = input.readLine()) != null) {
						if(line.startsWith(">"))
						{
							NucleotideSequenceFastaRecord prevRecord = null;
							if(currentBody != null){
								prevRecord = FACTORY.createFastaRecord(currentId, currentComment, currentBody.toString());
								currentBody = null;
							}
							currentId = SequenceFastaRecordUtil.parseIdentifierFromIdLine(line);
							currentComment = SequenceFastaRecordUtil.parseCommentFromIdLine(line);
							if (prevRecord != null) 
								return prevRecord;
						}
						else 
						{
							if (currentBody == null) {
								currentBody = new StringBuilder();
							}
							currentBody.append(line + "\n");
						}				
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				// Handle last record
				if (currentBody != null) {
					return FACTORY.createFastaRecord(currentId, currentComment, currentBody.toString()); 
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
