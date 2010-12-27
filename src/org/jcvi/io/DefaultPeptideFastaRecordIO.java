
package org.jcvi.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import org.jcvi.fasta.DefaultEncodedPeptideFastaRecord;
import org.jcvi.fasta.PeptideSequenceFastaRecord;
import org.jcvi.fasta.SequenceFastaRecordUtil;

/*
 * {@code DefaultPeptideFastaRecordIO} is the default implementation for
 * the ObjectIO of {@link PeptideSequenceFastaRecord} objects. 
 * 
 * @author naxelrod
 */
 
public class DefaultPeptideFastaRecordIO extends AbstractObjectIO<PeptideSequenceFastaRecord> implements PeptideFastaRecordIO {

	public DefaultPeptideFastaRecordIO() {
		super();
	}
	
	public DefaultPeptideFastaRecordIO(String inFile) throws IOException {
		super(inFile);
	}

	public DefaultPeptideFastaRecordIO(BufferedReader input) {
		super(input);
	}

	public DefaultPeptideFastaRecordIO(URL url) {
		super(url);
	}

	@Override
	public Iterator<PeptideSequenceFastaRecord> iterator() {

		return new Iterator<PeptideSequenceFastaRecord>() {
			
			private PeptideSequenceFastaRecord currentRecord = null;
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
			public PeptideSequenceFastaRecord next() {
				if (currentRecord != null) {
					PeptideSequenceFastaRecord tmp = currentRecord;
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
							PeptideSequenceFastaRecord prevRecord = null;
							if(currentBody != null){
								prevRecord = new DefaultEncodedPeptideFastaRecord(currentId, currentComment, currentBody.toString());
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
					return new DefaultEncodedPeptideFastaRecord(currentId, currentComment, currentBody.toString()); 
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
