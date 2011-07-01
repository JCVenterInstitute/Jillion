package org.jcvi.primerDesign;

import org.apache.log4j.Logger;
import org.jcvi.primerDesign.domain.PrimerDesignTarget;
import org.jcvi.primerDesign.gridJob.PrimerDesignerArrayGridJob;
import org.jcvi.primerDesign.results.PrimerDesignResult;
import org.jcvi.primerDesign.results.PrimerDesignResultsRetriever;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: aresnick
 * Date: Jul 27, 2010
 * Time: 3:57:59 PM
 * <p/>
 * $HeadURL$
 * $LastChangedRevision$
 * $LastChangedBy$
 * $LastChangedDate$
 * <p/>
 * Description:
 */
public class PrimerDesigner {
    private Map<File,PrimerDesignRequest> requestMap;
    private File primerDesignerScratchRoot;

    private Logger logger = Logger.getLogger(this.getClass());

    private PrimerDesigner(File primerDesignerScratchRoot,
                           Map<File,PrimerDesignRequest> requestMap) {
        this.primerDesignerScratchRoot = primerDesignerScratchRoot;
        this.requestMap = requestMap;
    }

    public Map<PrimerDesignTarget,Collection<PrimerDesignResult>> designPrimers() throws Exception {
        // create one batch grid job for each primer design job
        // PrimerDesignerExecutorService gridExecutor = gridMultiJobDesign();
        // use array grid jobs to create a grid array job for set set of similar primer design jobs
        logger.info("Setting up grid jobs");
        PrimerDesignerExecutorService gridExecutor = gridArrayJobDesign();
        logger.info("Launching grid jobs");
        gridExecutor.startJobs();
        logger.info("Waiting for grid jobs to complete.  This could take a while; please be patient");
        gridExecutor.waitForCompletion();

        // rip through the request maps' scratch dirs to locate and parse primer design results
        logger.info("Retrieving grid jobs' primer design results");
        return PrimerDesignResultsRetriever.retrievePrimerDesignResults(primerDesignerScratchRoot);
    }

    

    private PrimerDesignerExecutorService gridArrayJobDesign() {
        List<PrimerDesignerArrayGridJob> primerDesignJobs =
            new ArrayList<PrimerDesignerArrayGridJob>();

        for ( Map.Entry<File,PrimerDesignRequest> entry : requestMap.entrySet() ) {
            PrimerDesignRequest request = entry.getValue();

            PrimerDesignerArrayGridJobsVisitor visitor = new PrimerDesignerArrayGridJobsVisitor();
            PrimerDesignJobGenerator jobGenerator = new PrimerDesignJobGenerator(entry.getKey(),visitor);
            jobGenerator.createPrimerDesignJobs(request.getTargets(),
                                                request.getTemplateFastaRecord(),
                                                request.getReferenceFastaRecords(),
                                                request.getPrimerConfigurationStub(),
                                                request.getProjectCode());
            primerDesignJobs.addAll(visitor.getGridJobs());
        }

        // run/monitor these jobs
        PrimerDesignerExecutorService gridExecutor =
            new ArrayBasedPrimerDesignerExecutorService("primer designer grid executor",
                                                        100,
                                                        primerDesignerScratchRoot,
                                                        primerDesignJobs);
        return gridExecutor;
    }

    public static class Builder {
        private static final File PRIMER_DESIGNER_ROOT_SCRATCH_DIR = new File("/usr/local/scratch/PrimerDesigner");

        private Map<File,PrimerDesignRequest> requestMap = new HashMap<File,PrimerDesignRequest>();

        private File primerDesignerScratchRoot;

        public Builder() {
            primerDesignerScratchRoot = Utilities.getScratchFile(PRIMER_DESIGNER_ROOT_SCRATCH_DIR);
        }

        public Builder(File primerDesignerScratchRoot) {
            this.primerDesignerScratchRoot = primerDesignerScratchRoot;
            if ( this.primerDesignerScratchRoot == null ) {
                this.primerDesignerScratchRoot = Utilities.getScratchFile(PRIMER_DESIGNER_ROOT_SCRATCH_DIR);
            }
        }

        public void addPrimerDesignRequest(PrimerDesignRequest request) {
            File requestDirRoot = new File(primerDesignerScratchRoot,""+requestMap.size());
            requestMap.put(requestDirRoot,request);
        }

        public PrimerDesigner build() {
            return new PrimerDesigner(primerDesignerScratchRoot,requestMap);
        }
    }

}
