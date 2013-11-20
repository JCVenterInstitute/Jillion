package org.jcvi.jillion_experimental.align.blast;

import java.io.File;
import java.io.IOException;

public class TestTabularRotaBlastResults extends AbstractTestRotaBlastResults{

	public TestTabularRotaBlastResults() throws IOException {
		super(TabularBlastParser.create(new File(TestTabularRotaBlastResults.class.getResource("files/rota.tab.out").getFile())));
	}

}
