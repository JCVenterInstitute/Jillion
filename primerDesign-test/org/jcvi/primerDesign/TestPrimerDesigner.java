package org.jcvi.primerDesign;

import org.jcvi.Range;
import org.jcvi.common.core.seq.nuc.DefaultNucleotideSequence;
import org.jcvi.common.core.seq.nuc.fasta.DefaultNucleotideFastaFileDataStore;
import org.jcvi.common.core.seq.nuc.fasta.NucleotideSequenceFastaRecord;
import org.jcvi.common.core.seq.read.SequenceDirection;
import org.jcvi.datastore.DataStore;
import org.jcvi.primerDesign.PrimerDesigner;
import org.jcvi.primerDesign.domain.PrimerDesignTarget;
import org.jcvi.primerDesign.domain.DefaultPrimerDesignTarget;
import org.jcvi.primerDesign.results.PrimerDesignResult;

import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: aresnick
 * Date: Jul 28, 2010
 * Time: 9:55:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestPrimerDesigner {

    // these really aren't going to change
    private String projectCode = "8010AM";
    private String architecture = "lx24-amd64";

    private NucleotideSequenceFastaRecord templateRecord;
    private DataStore<NucleotideSequenceFastaRecord> referenceFastaRecords;
    private InputStream primerConfigurationStub;

    @Test
    public void testSingleRequestPrimerDesign() throws Exception {
        templateRecord = null;
        referenceFastaRecords =
            new DefaultNucleotideFastaFileDataStore(
                new File(this.getClass().getResource("files/454AllContigs.fasta").getFile())
        );
        primerConfigurationStub =
            this.getClass().getClassLoader().getResourceAsStream("closure.pcrStrict.config.stub");
        List<PrimerDesignTarget> targets = getTestTargets();
        Map<PrimerDesignTarget,Collection<PrimerDesignResult>> expectedResults = getExpectedResults(targets);

        PrimerDesigner.Builder builder = new PrimerDesigner.Builder();
        builder.addPrimerDesignRequest(
              new PrimerDesignRequest.Builder()
                .setProjectCode(projectCode)
                .setArchitecture(architecture)
                .setTargets(targets)
                .setPrimerConfigurationStub(primerConfigurationStub)
                .setReferenceFastaRecords(referenceFastaRecords)
                .setTemplateFastaRecord(templateRecord)
                .build());
        PrimerDesigner designer = builder.build();
        Map<PrimerDesignTarget,Collection<PrimerDesignResult>> actualResults = designer.designPrimers();


        CollectionComparison comparison = new CollectionComparison(expectedResults.keySet(),actualResults.keySet());
        assertEquals(comparison.getDifferences(),true,comparison.areEquivalent());
        for ( Object target : expectedResults.keySet() ) {
            comparison = new CollectionComparison(expectedResults.get(target),actualResults.get(target));
            assertEquals(comparison.getDifferences(),true,comparison.areEquivalent());
        }
    }

    private Map<PrimerDesignTarget,Collection<PrimerDesignResult>> getExpectedResults(List<PrimerDesignTarget> targets) {
        Map<PrimerDesignTarget,Collection<PrimerDesignResult>> expectedResults =
             new Hashtable<PrimerDesignTarget,Collection<PrimerDesignResult>>();
        expectedResults.put(targets.get(0),Collections.<PrimerDesignResult>emptyList());
        expectedResults.put(targets.get(1),Collections.<PrimerDesignResult>emptyList());
        expectedResults.put(targets.get(2),Collections.<PrimerDesignResult>emptyList());

        List<PrimerDesignResult> expectedPrimers = new ArrayList<PrimerDesignResult>();
        expectedPrimers.add(
            new PrimerDesignResult.Builder()
                .setDesignGroupID("4_10000_0001")
                .setDesignGroupLocationHash(0)
                .setParentID("contig00070")
                .setRange(Range.parseRange("17988 - 18013"))
                .setOrientation(SequenceDirection.REVERSE)
                .setPrimerSequence(new DefaultNucleotideSequence("GAACTTCAGGGTTAGCCTCGTTATC"))
                .build()
        );
        expectedPrimers.add(
            new PrimerDesignResult.Builder()
                .setDesignGroupID("4_10000_0001")
                .setDesignGroupLocationHash(0)
                .setParentID("contig00070")
                .setRange(Range.parseRange("17182 - 17205"))
                .setOrientation(SequenceDirection.FORWARD)
                .setPrimerSequence(new DefaultNucleotideSequence("CAGGCGTGGTTTATTTCATCTTG"))
                .build()
        );
        expectedPrimers.add(
            new PrimerDesignResult.Builder()
                .setDesignGroupID("4_00000_0000")
                .setDesignGroupLocationHash(0)
                .setParentID("contig00070")
                .setRange(Range.parseRange("17960 - 17983"))
                .setOrientation(SequenceDirection.REVERSE)
                .setPrimerSequence(new DefaultNucleotideSequence("GGCGCGGCATAAAGTAAGTATCT"))
                .build()
        );
        expectedPrimers.add(
            new PrimerDesignResult.Builder()
                .setDesignGroupID("4_00000_0000")
                .setDesignGroupLocationHash(0)
                .setParentID("contig00070")
                .setRange(Range.parseRange("17242 - 17266"))
                .setOrientation(SequenceDirection.FORWARD)
                .setPrimerSequence(new DefaultNucleotideSequence("TGAAAATTTATGCCTTGAAACCGA"))
                .build()
        );
        expectedResults.put(targets.get(3),expectedPrimers);

        expectedPrimers = new ArrayList<PrimerDesignResult>();
        expectedPrimers.add(
            new PrimerDesignResult.Builder()
                .setDesignGroupLocationHash(1)
                .setDesignGroupID("5_10000_0001")
                .setParentID("contig00070")
                .setRange(Range.parseRange("54340 - 54363"))
                .setOrientation(SequenceDirection.REVERSE)
                .setPrimerSequence(new DefaultNucleotideSequence("CAGATATTGTCCTGTCGCAGTCA"))
                .build()
        );
        expectedPrimers.add(
            new PrimerDesignResult.Builder()
                .setDesignGroupLocationHash(1)
                .setDesignGroupID("5_00000_0000")
                .setParentID("contig00070")
                .setRange(Range.parseRange("54300 - 54325"))
                .setOrientation(SequenceDirection.REVERSE)
                .setPrimerSequence(new DefaultNucleotideSequence("AAGCAAATTTATTGACACCCATCAC"))
                .build()
        );
        expectedPrimers.add(
            new PrimerDesignResult.Builder()
                .setDesignGroupID("5_00000_0000")
                .setDesignGroupLocationHash(1)
                .setParentID("contig00070")
                .setRange(Range.parseRange("53628 - 53651"))
                .setOrientation(SequenceDirection.FORWARD)
                .setPrimerSequence(new DefaultNucleotideSequence("CGCCAATATCTTGCTGCATAAAA"))
                .build()
        );
        expectedPrimers.add(
            new PrimerDesignResult.Builder()
                .setDesignGroupID("5_10000_0001")
                .setDesignGroupLocationHash(1)
                .setParentID("contig00070")
                .setRange(Range.parseRange("53579 - 53605"))
                .setOrientation(SequenceDirection.FORWARD)
                .setPrimerSequence(new DefaultNucleotideSequence("TCAAGTTTATGTTGCTTGTTACCCTG"))
                .build()
        );
        expectedResults.put(targets.get(4),expectedPrimers);

        return expectedResults;
    }

    private List<PrimerDesignTarget> getTestTargets() throws Exception {
        List<PrimerDesignTarget> targets = new ArrayList<PrimerDesignTarget>();
        targets.add(new DefaultPrimerDesignTarget("contig00001", Range.buildRange(1,861)));
        targets.add(new DefaultPrimerDesignTarget("contig00006", Range.buildRange(1,1803)));
        targets.add(new DefaultPrimerDesignTarget("contig00070", Range.buildRange(1,243)));
        targets.add(new DefaultPrimerDesignTarget("contig00070", Range.buildRange(17618,17660)));
        targets.add(new DefaultPrimerDesignTarget("contig00070", Range.buildRange(53990,53993)));
        return targets;
    }
}