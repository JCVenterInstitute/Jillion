package org.jcvi.jillion_experimental.align.blast;

import java.io.File;
import java.io.IOException;

public class TestXmlRotaBlastResults extends AbstractTestRotaBlastResults{

	public TestXmlRotaBlastResults() throws IOException {
		super(XmlFileBlastParser.create(new File(TestXmlRotaBlastResults.class.getResource("files/rota.blast.xml.out").getFile())));
	}

}
