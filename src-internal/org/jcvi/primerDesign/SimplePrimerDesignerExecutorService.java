package org.jcvi.primerDesign;

import org.ggf.drmaa.DrmaaException;
import org.jcvi.command.Command;
import org.jcvi.command.grid.BatchGridJobImpl;
import org.jcvi.command.grid.GridJobFuture;
import org.jcvi.command.grid.GridJobExecutorService;
import org.jcvi.command.grid.GridUtils;
import org.jcvi.primerDesign.gridJob.PrimerDesignerGridJob;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
* Created by IntelliJ IDEA.
* User: aresnick
* Date: Jul 28, 2010
* Time: 4:48:34 PM
* To change this template use File | Settings | File Templates.
*/
public class SimplePrimerDesignerExecutorService implements PrimerDesignerExecutorService {

    private static final long GRID_JOB_MAX_DURATION = 7200;

    private List<PrimerDesignerGridJob> primerDesignerGridJobs;

    private GridJobExecutorService gridExecutor;
    private List<GridJobFuture> primerDesignJobs;

    private Logger logger = Logger.getLogger(this.getClass());

    public SimplePrimerDesignerExecutorService(String name,
                                               int maxConcurrency,
                                               List<PrimerDesignerGridJob> primerDesignerGridJobs) {
        gridExecutor = new GridJobExecutorService(name,maxConcurrency);
        this.primerDesignerGridJobs = primerDesignerGridJobs;
    }

    @Override
    public void startJobs() throws DrmaaException {
        this.primerDesignJobs = new ArrayList<GridJobFuture>(primerDesignerGridJobs.size());

        for ( PrimerDesignerGridJob primerDesignerGridJob : primerDesignerGridJobs) {
            BatchGridJobImpl.Builder builder =
                new BatchGridJobImpl.Builder(gridExecutor.getSession(),
                                             new Command(primerDesignerGridJob.getGridJobScript().getAbsolutePath()),
                                             primerDesignerGridJob.getProjectCode());
            builder.setArchitecture(primerDesignerGridJob.getArchitecture()); // unsure if this is necessary/useful!
            builder.setOutputFile(primerDesignerGridJob.getStdOutputLocation());
            builder.setErrorFile(primerDesignerGridJob.getStdErrorLocation());
            builder.setTimeout(GRID_JOB_MAX_DURATION);
            this.primerDesignJobs.add(gridExecutor.submit(builder.build()));
        }
    }

    @Override
    public void waitForCompletion() {
        for ( GridJobFuture future : primerDesignJobs ) {
            try {
                future.get();
                logger.trace("Array grid job cumulative status is " + GridUtils.getJobStatus(future.getJob()));
                for ( String jobID : future.getJob().getJobIDList() ) {
                    logger.trace("Job id is " + jobID);
                    logger.trace(GridUtils.printJobInfo(future.getJob().getJobInfoMap().get(jobID)));
                }
            } catch (Exception e) {
                throw new RuntimeException("Unexpected error waiting for grid job result", e);
            }
        }
    }

    public boolean isFinished() {
        return gridExecutor.countActiveTasks() == 0;
    }

}
