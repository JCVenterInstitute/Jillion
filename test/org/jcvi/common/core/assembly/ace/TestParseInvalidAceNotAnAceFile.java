package org.jcvi.common.core.assembly.ace;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.Test;

public class TestParseInvalidAceNotAnAceFile {

	@Test(expected = IOException.class)
	public void tryingToParseNonAceFileShouldThrowIOException() throws IOException{
		ResourceFileServer resources = new ResourceFileServer(TestParseInvalidAceNotAnAceFile.class);
		File nonAce = resources.getFile("files/sample.contig");
		AceFileParser.parse(nonAce, new AbstractAceFileVisitorContigBuilder() {
			
			@Override
			protected void visitContig(AceContig contig) {
				throw new IllegalStateException("should not get this far");			
			}
		});
		
		
	}
	
	
}
