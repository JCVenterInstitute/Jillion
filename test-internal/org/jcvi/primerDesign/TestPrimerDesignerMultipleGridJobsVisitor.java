package org.jcvi.primerDesign;

import org.jcvi.primerDesign.gridJob.PrimerDesignerGridJob;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * User: aresnick
 * Date: Jul 26, 2010
 * Time: 3:23:23 PM
 * <p/>
 * $HeadURL$
 * $LastChangedRevision$
 * $LastChangedBy$
 * $LastChangedDate$
 * <p/>
 * Description:
 */
public class TestPrimerDesignerMultipleGridJobsVisitor {

    private PrimerDesignerMultipleGridJobsVisitor visitor;

    // these really aren't going to change
    private String projectCode = "8010AM";
    private String architecture = "lx24-amd64";

    @Before
    public void instantiateVisitor() {
        visitor = new PrimerDesignerMultipleGridJobsVisitor();
    }
    @Test
    public void testVisitorEmptyFile() throws Exception {
        InputStream emptyStream = new ByteArrayInputStream(new byte[0]);
        PrimerDesignerJobOutputParser.parseGridJobsFile(emptyStream,visitor);
        assertTrue(visitor.getGridJobs().isEmpty());
    }

    @Test
    public void testFullOutputFileVisitor() throws Exception {
        File testFile =
            new File(TestPrimerDesignerMultipleGridJobsVisitor.class.getResource("files/full_qsub.bat").getFile());
        PrimerDesignerJobOutputParser.parseGridJobsFile(testFile,visitor);

        List<PrimerDesignerGridJob> expectedJobs =
            new ArrayList<PrimerDesignerGridJob>();
        expectedJobs.add(
            new PrimerDesignerGridJob(
                projectCode,
                architecture,
                new File("/usr/local/projects/HMP/HMP084/test_autofinish/primerDesigner/testResults/pcr/Strict/results/contig00001/01/runClsrPD.csh"),
                new File("/usr/local/projects/HMP/HMP084/test_autofinish/primerDesigner/testResults/pcr/Strict/results/contig00001/01"),
                new File("/usr/local/projects/HMP/HMP084/test_autofinish/primerDesigner/testResults/pcr/Strict/results/contig00001/01")
            )
        );
        expectedJobs.add(
            new PrimerDesignerGridJob(
                projectCode,
                architecture,
                new File("/usr/local/projects/HMP/HMP084/test_autofinish/primerDesigner/testResults/pcr/Strict/results/contig00006/02/runClsrPD.csh"),
                new File("/usr/local/projects/HMP/HMP084/test_autofinish/primerDesigner/testResults/pcr/Strict/results/contig00006/02"),
                new File("/usr/local/projects/HMP/HMP084/test_autofinish/primerDesigner/testResults/pcr/Strict/results/contig00006/02")
            )
        );
        expectedJobs.add(
            new PrimerDesignerGridJob(
                projectCode,
                architecture,
                new File("/usr/local/projects/HMP/HMP084/test_autofinish/primerDesigner/testResults/pcr/Strict/results/contig00008/03/runClsrPD.csh"),
                new File("/usr/local/projects/HMP/HMP084/test_autofinish/primerDesigner/testResults/pcr/Strict/results/contig00008/03"),
                new File("/usr/local/projects/HMP/HMP084/test_autofinish/primerDesigner/testResults/pcr/Strict/results/contig00008/03")
            )
        );

        CollectionComparison comparison = new CollectionComparison(expectedJobs,visitor.getGridJobs());
        assertEquals(comparison.getDifferences(),true,comparison.areEquivalent());
    }
    @Test
    public void testNoArchitectureFileVisitor() throws Exception {
        noArchitectureFileVisitorTest("files/noArchitecture_qsub.bat");
    }
    
    @Test
    public void testNoArchitectureFileVisitor2() throws Exception {
        noArchitectureFileVisitorTest("files/noArchitecture2_qsub.bat");
    }

    public void noArchitectureFileVisitorTest(String targetFile) throws Exception {
        File testFile =
            new File(TestPrimerDesignerMultipleGridJobsVisitor.class.getResource(targetFile).getFile());
        PrimerDesignerJobOutputParser.parseGridJobsFile(testFile,visitor);

        List<PrimerDesignerGridJob> expectedJobs =
            new ArrayList<PrimerDesignerGridJob>();
        expectedJobs.add(
            new PrimerDesignerGridJob(
                projectCode,
                null,
                new File("/usr/local/projects/HMP/HMP084/test_autofinish/primerDesigner/testResults/pcr/Strict/results/contig00001/01/runClsrPD.csh"),
                new File("/usr/local/projects/HMP/HMP084/test_autofinish/primerDesigner/testResults/pcr/Strict/results/contig00001/01"),
                new File("/usr/local/projects/HMP/HMP084/test_autofinish/primerDesigner/testResults/pcr/Strict/results/contig00001/01")
            )
        );
        expectedJobs.add(
            new PrimerDesignerGridJob(
                projectCode,
                null,
                new File("/usr/local/projects/HMP/HMP084/test_autofinish/primerDesigner/testResults/pcr/Strict/results/contig00006/02/runClsrPD.csh"),
                new File("/usr/local/projects/HMP/HMP084/test_autofinish/primerDesigner/testResults/pcr/Strict/results/contig00006/02"),
                new File("/usr/local/projects/HMP/HMP084/test_autofinish/primerDesigner/testResults/pcr/Strict/results/contig00006/02")
            )
        );
        expectedJobs.add(
            new PrimerDesignerGridJob(
                projectCode,
                null,
                new File("/usr/local/projects/HMP/HMP084/test_autofinish/primerDesigner/testResults/pcr/Strict/results/contig00008/03/runClsrPD.csh"),
                new File("/usr/local/projects/HMP/HMP084/test_autofinish/primerDesigner/testResults/pcr/Strict/results/contig00008/03"),
                new File("/usr/local/projects/HMP/HMP084/test_autofinish/primerDesigner/testResults/pcr/Strict/results/contig00008/03")
            )
        );

        CollectionComparison comparison = new CollectionComparison(expectedJobs,visitor.getGridJobs());
        assertEquals(comparison.getDifferences(),true,comparison.areEquivalent());
    }

    @Test
    public void testNoArchitectureOrProjectFileVisitor() throws Exception {
        File testFile =
            new File(TestPrimerDesignerMultipleGridJobsVisitor.class.getResource("files/noArchitectureAndProject_qsub.bat").getFile());
        PrimerDesignerJobOutputParser.parseGridJobsFile(testFile,visitor);

        List<PrimerDesignerGridJob> expectedJobs =
            new ArrayList<PrimerDesignerGridJob>();
        expectedJobs.add(
            new PrimerDesignerGridJob(
                null,
                null,
                new File("/usr/local/projects/HMP/HMP084/test_autofinish/primerDesigner/testResults/pcr/Strict/results/contig00001/01/runClsrPD.csh"),
                new File("/usr/local/projects/HMP/HMP084/test_autofinish/primerDesigner/testResults/pcr/Strict/results/contig00001/01"),
                new File("/usr/local/projects/HMP/HMP084/test_autofinish/primerDesigner/testResults/pcr/Strict/results/contig00001/01")
            )
        );
        expectedJobs.add(
            new PrimerDesignerGridJob(
                null,
                null,
                new File("/usr/local/projects/HMP/HMP084/test_autofinish/primerDesigner/testResults/pcr/Strict/results/contig00006/02/runClsrPD.csh"),
                new File("/usr/local/projects/HMP/HMP084/test_autofinish/primerDesigner/testResults/pcr/Strict/results/contig00006/02"),
                new File("/usr/local/projects/HMP/HMP084/test_autofinish/primerDesigner/testResults/pcr/Strict/results/contig00006/02")
            )
        );
        expectedJobs.add(
            new PrimerDesignerGridJob(
                null,
                null,
                new File("/usr/local/projects/HMP/HMP084/test_autofinish/primerDesigner/testResults/pcr/Strict/results/contig00008/03/runClsrPD.csh"),
                new File("/usr/local/projects/HMP/HMP084/test_autofinish/primerDesigner/testResults/pcr/Strict/results/contig00008/03"),
                new File("/usr/local/projects/HMP/HMP084/test_autofinish/primerDesigner/testResults/pcr/Strict/results/contig00008/03")
            )
        );

        CollectionComparison comparison = new CollectionComparison(expectedJobs,visitor.getGridJobs());
        assertEquals(comparison.getDifferences(),true,comparison.areEquivalent());
    }
}
