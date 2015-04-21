package org.jcvi.jillion_experimental.align.blast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Test;

public class TestBlastXmlNoDefLine {

	@Test
	public void noDeflineShouldUseAccessionAsId() throws IOException{
		ResourceHelper helper = new ResourceHelper(TestBlastXmlNoDefLine.class);
		
		File xml = helper.getFile("files/rota.blast.xml.NODEFLINE.out");
		
		MyBlastVisitor visitor = new MyBlastVisitor();
		XmlFileBlastParser.create(xml)
							.parse(visitor);
		
		Set<String> uniqueNames = new HashSet<>();
		for(Hsp<?,?> hsp :visitor.hsps){
			assertEquals("No definition line found", hsp.getSubjectDefinition());
			assertTrue(hsp.getSubjectId().contains("RVA"));
			
			uniqueNames.add(hsp.getSubjectId());
			
		}
		
		assertEquals(visitor.numberOfHits, uniqueNames.size());
		
	}
}
