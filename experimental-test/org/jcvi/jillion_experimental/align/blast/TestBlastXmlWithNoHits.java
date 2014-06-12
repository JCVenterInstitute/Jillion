package org.jcvi.jillion_experimental.align.blast;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Test;
public class TestBlastXmlWithNoHits {

	@Test
	public void shouldNotVisitHit() throws IOException{
		File xmlFile = new ResourceHelper(TestBlastXmlWithNoHits.class)
									.getFile("files/noHits.xml");
		
		BlastParser parser = XmlFileBlastParser.create(xmlFile);
		
		BlastVisitor mockVisitor = createMock(BlastVisitor.class);
		mockVisitor.visitInfo("blastn", "blastn 2.2.26 [Sep-21-2011]", "/bio/db/blast/miRNA_hair", "1 <unknown description>");
		
		mockVisitor.visitEnd();
		
		replay(mockVisitor);
		parser.parse(mockVisitor);
		verify(mockVisitor);
	}
}
