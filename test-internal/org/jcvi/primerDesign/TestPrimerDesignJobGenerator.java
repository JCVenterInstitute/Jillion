package org.jcvi.primerDesign;

import java.io.*;
import java.util.*;

import org.jcvi.io.IOUtil;
import org.jcvi.Range;
import org.jcvi.datastore.DataStore;
import org.jcvi.fasta.DefaultNucleotideFastaFileDataStore;
import org.jcvi.fasta.NucleotideSequenceFastaRecord;

import org.jcvi.primerDesign.domain.DefaultPrimerDesignTarget;
import org.jcvi.primerDesign.domain.PrimerDesignTarget;
import org.jcvi.primerDesign.gridJob.PrimerDesignerGridJob;

import org.junit.*;
/**
 * Created by IntelliJ IDEA.
 * User: aresnick
 * Date: Jul 28, 2010
 * Time: 9:55:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestPrimerDesignJobGenerator {

    // these really aren't going to change
    private String projectCode = "8010AM";
    private String architecture = "lx24-amd64";

    @Test
    public void testJobGeneration() throws Exception {

        // set up phun
        List<PrimerDesignTarget> targets = new ArrayList<PrimerDesignTarget>();
        targets.add(new DefaultPrimerDesignTarget("contig00001", Range.buildRange(1,861)));
        targets.add(new DefaultPrimerDesignTarget("contig00006", Range.buildRange(1,1803)));
        targets.add(new DefaultPrimerDesignTarget("contig00070", Range.buildRange(1,243)));
        targets.add(new DefaultPrimerDesignTarget("contig00070", Range.buildRange(17618,17660)));
        targets.add(new DefaultPrimerDesignTarget("contig00070", Range.buildRange(53990,53993)));
        NucleotideSequenceFastaRecord templateRecord = null; // safe for now
        DataStore<NucleotideSequenceFastaRecord> referenceFastaRecords =
            new DefaultNucleotideFastaFileDataStore(
                new File(this.getClass().getResource("files/454AllContigs.fasta").getFile())
        );
        InputStream primerConfigurationStub =
            this.getClass().getClassLoader().getResourceAsStream("closure.pcrStrict.config.stub");
        File root = new File("/usr/local/scratch/ClosurePrimerDesigner/test");
        try {
            PrimerDesignerMultipleGridJobsVisitor visitor = new PrimerDesignerMultipleGridJobsVisitor();
            PrimerDesignJobGenerator jobGenerator = new PrimerDesignJobGenerator(root,visitor);
            jobGenerator.createPrimerDesignJobs(targets,
                                                templateRecord,
                                                referenceFastaRecords,
                                                primerConfigurationStub,
                                                projectCode,
                                                architecture);
            for ( PrimerDesignerGridJob gridJob : visitor.getGridJobs() ) {
                System.out.println(gridJob);
            }
        } catch (Exception e) {
            try {
                IOUtil.recursiveDelete(root);
            } catch (IOException ioe) {
                System.err.println("Warning - unable to clean up test directory " + root);
                e.printStackTrace();
            }
        }
    }
}
