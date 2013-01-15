package org.jcvi.jillion.assembly.ace;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.assembly.ace.AbstractAceFileVisitorContigBuilder;
import org.jcvi.jillion.assembly.ace.AceContigBuilder;
import org.jcvi.jillion.assembly.ace.AceFileParser;
import org.jcvi.jillion.core.internal.ResourceHelper;
import org.junit.Test;

public class TestParseInvalidAceNotAnAceFile {

	@Test(expected = IOException.class)
	public void tryingToParseNonAceFileShouldThrowIOException() throws IOException{
		ResourceHelper resources = new ResourceHelper(TestParseInvalidAceNotAnAceFile.class);
		File nonAce = resources.getFile("files/sample.contig");
		AceFileParser.parse(nonAce, new AbstractAceFileVisitorContigBuilder() {
			
			@Override
			protected void visitContig(AceContigBuilder contig) {
				throw new IllegalStateException("should not get this far");			
			}
		});
		
		
	}
	
	
}
