package org.jcvi.command.grid;

import org.jcvi.common.command.grid.GridJob;
import org.jcvi.common.command.grid.GridJobExecutorService;
import org.jcvi.common.command.grid.GridJobFuture;
import org.jcvi.common.command.grid.GridUtils;

abstract class TestGridJob {
    protected static final String TEST_PROJECT_CODE = "8010AM";
    protected GridJobExecutorService gridExecutor;

    public TestGridJob() {
        this.gridExecutor = new GridJobExecutorService("gridJobTesting",10);
    }

    protected void printJobResults(GridJob job) throws Exception {
        System.out.println("Overall job status is " + GridUtils.getJobStatus(job));
        for (String jobID : job.getJobIDList()) {
            System.out.println("\nJob " + jobID);
            System.out.println(GridUtils.printJobInfo(job.getJobInfoMap().get(jobID)));
        }
    }

    protected static class JobCancellation implements Runnable {

        private GridJobFuture future;
        private int cancellationDelay;

        public JobCancellation(GridJobFuture future, int cancellationDelay) {
            this.future = future;
            this.cancellationDelay = cancellationDelay;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(cancellationDelay * 1000);
            } catch (InterruptedException ie) {
                // do nothing
            }

            future.cancel(true);
        }
    }
}