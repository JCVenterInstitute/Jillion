package org.jcvi.primerDesign.results;

import org.junit.Test;
import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.seq.fastx.fasta.nuc.DefaultNucleotideSequenceFastaRecord;

import org.jcvi.primerDesign.CollectionComparison;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

/**
 * User: aresnick
 * Date: Jul 27, 2010
 * Time: 3:13:56 PM
 * <p/>
 * $HeadURL$
 * $LastChangedRevision$
 * $LastChangedBy$
 * $LastChangedDate$
 * <p/>
 * Description:
 */
public class TestPrimerDesignerResultParser {

    @Test
    public void testResultParseWithTargetParent() {
        File testFile =
            new File(TestPrimerDesignerResultParser.class.getResource("files/primers.fasta").getFile());
        String parentID = "contig00070";
        List<PrimerDesignResult> expectedResult = new ArrayList<PrimerDesignResult>();
        expectedResult.add(buildResult(testFile,parentID,"05_00000_0000",Range.parseRange("17038,17064"),Direction.FORWARD,"ATCTGACAATCGCCAGTATTTTCTTC"));
        expectedResult.add(buildResult(testFile,parentID,"05_00000_0000",Range.parseRange("18270,18296"),Direction.REVERSE,"GCTGCTTCGTTAGGTACTTTGACAAT"));
        expectedResult.add(buildResult(testFile,parentID,"05_10000_0001",Range.parseRange("16882,16908"),Direction.FORWARD,"CTGTGGTATCGTTGGCATTTATTTTT"));
        expectedResult.add(buildResult(testFile,parentID,"05_10000_0001",Range.parseRange("18043,18069"),Direction.REVERSE,"AATATCAACTTGGCCGTCTGTATCAC"));
        


        List<PrimerDesignResult> parsedResult = PrimerDesignerResultParser.parseResultsFile(testFile,parentID);
        
        CollectionComparison comparison = new CollectionComparison(expectedResult,parsedResult);
        assertEquals(comparison.getDifferences(),true,comparison.areEquivalent());
    }

    private PrimerDesignResult buildResult(File testFile,
                                           String parentID,
                                           String designGroupID,
                                           Range range,
                                           Direction orientation,
                                           String primerSequence) {
        return
            new PrimerDesignResult.Builder(testFile)
                .setParentID(parentID)
                .setDesignGroupID(designGroupID)
                .setRange(range)
                .setOrientation(orientation)
                .setPrimerSequence(new DefaultNucleotideSequenceFastaRecord("temp",primerSequence).getValue())
                .build();
    }
}
