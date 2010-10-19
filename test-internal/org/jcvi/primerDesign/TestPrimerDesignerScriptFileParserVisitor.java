package org.jcvi.primerDesign;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: aresnick
 * Date: Aug 6, 2010
 * Time: 1:35:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestPrimerDesignerScriptFileParserVisitor {

    private DefaultPrimerDesignerScriptFileVisitor visitor;

    @Before
    public void instantiateVisitor() {
        visitor = new DefaultPrimerDesignerScriptFileVisitor();
    }

    @Test
    public void testPrimerDesignerScriptParser() throws Exception {
        File testFile =
            new File(TestPrimerDesignerMultipleGridJobsVisitor.class.getResource("files/runClsrPD.csh").getFile());
        PrimerDesignerScriptFileParser.parsePrimerDesignerScriptFile(testFile,visitor);
        System.out.println(visitor);
    }

}
