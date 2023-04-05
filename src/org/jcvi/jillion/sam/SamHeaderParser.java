package org.jcvi.jillion.sam;

import org.jcvi.jillion.sam.header.SamHeader;

/**
 * {@link SamVisitor} that just gets the {@link SamHeader}
 * from the BAM file since that's all we need.
 * @author dkatzel
 *
 */
public class SamHeaderParser extends AbstractSamVisitor{
	private SamHeader header;
	
	@Override
	public void visitHeader(SamVisitorCallback callback, SamHeader header) {
		this.header = header;
		//since we only need the header we can stop
		//parsing the BAM now before we get 
		//to any SAMRecords.
		callback.haltParsing();
		
	}

	public SamHeader getHeader() {
		return header;
	}
	
}