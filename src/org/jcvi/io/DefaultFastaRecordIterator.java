package org.jcvi.io;

import java.io.File;
import java.io.InputStream;
import java.util.Iterator;

import org.jcvi.fasta.DefaultNucleotideFastaRecordFactory;
import org.jcvi.fasta.FastaRecord;
import org.jcvi.fasta.FastaRecordFactory;
import org.jcvi.fasta.SequenceFastaRecordUtil;

public class DefaultFastaRecordIterator extends AbstractFileReader implements FastaRecordIterator {

	protected FastaRecordFactory factory = null;
	protected final DefaultNucleotideFastaRecordFactory DEFAULT_FACTORY_SINGLETON = DefaultNucleotideFastaRecordFactory.getInstance();
	
	// Use the DefaultNucleotideFastaRecordFactory by default
	public DefaultFastaRecordIterator() {
		super();
		this.factory = DEFAULT_FACTORY_SINGLETON;
	}
	
	public DefaultFastaRecordIterator(File file) {
		super(file);
		this.factory = DEFAULT_FACTORY_SINGLETON;
	}
	
	public DefaultFastaRecordIterator(File file, FastaRecordFactory factory) {
		super(file);
		this.factory = factory;
	}
	public DefaultFastaRecordIterator(InputStream fastaStream) {
		super(fastaStream);
		this.factory = DEFAULT_FACTORY_SINGLETON;
	}

	public FastaRecordFactory getFactory() {
		return factory;
	}
	public void setFactory(FastaRecordFactory factory) {
		this.factory = factory;
	}

	@Override
	public Iterator<FastaRecord> iterator() {

		return new Iterator<FastaRecord>() {
			
			private FastaRecord currentRecord = null;
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
			public FastaRecord next() {
				if (currentRecord != null) {
					FastaRecord tmp = currentRecord;
					currentRecord = null;
					return tmp;
				}

				// Read until we reach the next record
				String line;
		        StringBuilder currentBody=null;
		        
		        try {
					while ((line = reader.readLine()) != null) {
						if(line.startsWith(">"))
						{
							FastaRecord prevRecord = null;
							if(currentBody != null){
								prevRecord = factory.createFastaRecord(currentId, currentComment, currentBody.toString());
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
					return factory.createFastaRecord(currentId, currentComment, currentBody.toString()); 
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
