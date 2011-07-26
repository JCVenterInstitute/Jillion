package org.jcvi.command.grid;

import org.junit.Test;

import org.jcvi.common.command.Command;
import org.jcvi.common.command.grid.GridJob;
import org.jcvi.common.command.grid.GridJobBuilder;
import org.jcvi.common.command.grid.GridJobBuilders;
import org.jcvi.common.command.grid.GridJobFuture;
import org.jcvi.common.command.grid.SimpleGridJob;


/**
 * Created by IntelliJ IDEA.
 * User: aresnick
 * Date: Aug 11, 2010
 * Time: 10:15:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestBatchGridJob extends TestGridJob {

    @Test
    public void testBatchGridJob() throws Exception {
        Command testCommand = new Command("/bin/sleep");
        testCommand.addFlag("5");

        GridJobBuilder<SimpleGridJob> builder = GridJobBuilders.createSimpleGridJobBuilder(gridExecutor.getSession(),
                                         testCommand,
                                         TestGridJob.TEST_PROJECT_CODE);
        GridJobFuture future = gridExecutor.submit(builder.build());
        int exitStatus = future.get();
        System.out.println("grid job future exit status was " + exitStatus);
        printJobResults(future.getJob());
    }

    @Test
    public void testBatchGridJobGridExecutorTimeout() throws Exception {
        Command testCommand = new Command("/bin/sleep");
        testCommand.addFlag("15");

        GridJobBuilder<SimpleGridJob> builder = GridJobBuilders.createSimpleGridJobBuilder(gridExecutor.getSession(),
                                         testCommand,
                                         TestGridJob.TEST_PROJECT_CODE);
        builder.setTimeout(Long.valueOf(5));
        GridJobFuture future = gridExecutor.submit(builder.build());
        int exitStatus = future.get();
        System.out.println("grid job future exit status was " + exitStatus);
        printJobResults(future.getJob());
    }

    @Test
    public void testBatchGridJobGridExecutorTermination() throws Exception {
        Command testCommand = new Command("/bin/sleep");
        testCommand.addFlag("15");

        GridJobBuilder<SimpleGridJob> builder = GridJobBuilders.createSimpleGridJobBuilder(gridExecutor.getSession(),
                                         testCommand,
                                         TestGridJob.TEST_PROJECT_CODE);
        GridJobFuture future = gridExecutor.submit(builder.build());
        new Thread(new JobCancellation(future,5)).start();
        int exitStatus = future.get();
        System.out.println("grid job future exit status was " + exitStatus);
        printJobResults(future.getJob());
    }

    @Test
    public void testBatchGridJobDirectTermination() throws Exception {
        Command testCommand = new Command("/bin/sleep");
        testCommand.addFlag("15");

        GridJobBuilder<SimpleGridJob> builder = GridJobBuilders.createSimpleGridJobBuilder(gridExecutor.getSession(),
                                         testCommand,
                                         TestGridJob.TEST_PROJECT_CODE);
        GridJob gridJob = builder.build();
        gridJob.runGridCommand();
        Thread.sleep(5000);
        gridJob.terminate();
        String jobID = gridJob.getJobIDList().get(0);
        System.out.println("grid job id was " + jobID);
        printJobResults(gridJob);
    }
}
