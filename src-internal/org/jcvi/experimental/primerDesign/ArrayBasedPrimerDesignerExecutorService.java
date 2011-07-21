package org.jcvi.experimental.primerDesign;

import org.apache.log4j.Logger;
import org.ggf.drmaa.DrmaaException;
import org.jcvi.common.command.Command;
import org.jcvi.common.command.grid.ArrayGridJobImpl;
import org.jcvi.common.command.grid.GridJobExecutorService;
import org.jcvi.common.command.grid.GridJobFuture;
import org.jcvi.common.command.grid.GridUtils;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.experimental.primerDesign.gridjob.PrimerDesignerArrayGridJob;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;


/**
* Created by IntelliJ IDEA.
* User: aresnick
* Date: Jul 28, 2010
* Time: 4:48:34 PM
* To change this template use File | Settings | File Templates.
*/
public class ArrayBasedPrimerDesignerExecutorService implements PrimerDesignerExecutorService {

    private static final String ARRAY_JOB_CONFIG_FILENAME_ROOT = "pdInput";
    private static final String ARRAY_JOB_OUTPUT_FILENAME_ROOT = "pdOutput";
    private static final String ARRAY_JOB_ERROR_FILENAME_ROOT = "pdError";

    private static final long GRID_JOB_MAX_DURATION = 7200;
    
    private List<PrimerDesignerArrayGridJob> primerDesignerGridJobs;

    private File executorRoot;
    private GridJobExecutorService gridExecutor;
    List<GridJobFuture> primerDesignJobs;

    private Logger logger = Logger.getLogger(this.getClass());

    public ArrayBasedPrimerDesignerExecutorService(String name,
                                                   int maxConcurrency,
                                                   File executorRoot,
                                                   List<PrimerDesignerArrayGridJob> primerDesignerGridJobs) {
        gridExecutor = new GridJobExecutorService(name,maxConcurrency);
        this.executorRoot = executorRoot;
        this.primerDesignerGridJobs = primerDesignerGridJobs;
    }

    @Override
    public void startJobs() throws DrmaaException {
        // this.primerDesignJobs = new ArrayList<GridJobFuture>(primerDesignJobs.size());

        // group the jobs based on project code and architecture specifications
        Map<ArrayJobKey,List<PrimerDesignerArrayGridJob>> arrayJobMap =
            new Hashtable<ArrayJobKey,List<PrimerDesignerArrayGridJob>>();
        for ( PrimerDesignerArrayGridJob primerDesignerGridJob : primerDesignerGridJobs) {
            ArrayJobKey key = new ArrayJobKey(primerDesignerGridJob.getProjectCode(),
                                              primerDesignerGridJob.getArchitecture());
            List<PrimerDesignerArrayGridJob> jobs = arrayJobMap.get(key);
            if ( jobs == null ) {
                jobs = new ArrayList<PrimerDesignerArrayGridJob>();
                arrayJobMap.put(key,jobs);
            }
            jobs.add(primerDesignerGridJob);
        }

        File arrayGridJobTemplate = getArrayGridJobTemplate();

        this.primerDesignJobs = new ArrayList<GridJobFuture>(arrayJobMap.size());
        for ( Entry<ArrayJobKey, List<PrimerDesignerArrayGridJob>> entry : arrayJobMap.entrySet() ) {
            // build job scratch dir for this key
            final ArrayJobKey key = entry.getKey();
            File arrayJobRoot = new File(executorRoot,key.toString());
            arrayJobRoot.mkdirs();

            // for each job in the job list, write its config file
            List<PrimerDesignerArrayGridJob> jobs = entry.getValue();
            for ( int i = 1; i <= jobs.size(); i++ ) {
                PrimerDesignerArrayGridJob job = jobs.get(i-1);
                File jobConfigFile = new File(arrayJobRoot,ARRAY_JOB_CONFIG_FILENAME_ROOT+"."+i);
                PrintWriter writer = null;
                try {
                    jobConfigFile.createNewFile();
                    writer = new PrintWriter(jobConfigFile);
                    writer.println(job.getConfigFile().getAbsoluteFile());
                    writer.println(job.getGffFile().getAbsoluteFile());
                    writer.println(job.getRenamedPdfFile().getAbsoluteFile());
                    writer.println(job.getPrimerFastaFile().getAbsoluteFile());
                    writer.flush();
                } catch (Exception e) {
                    throw new RuntimeException("Can't generate array grid job config file "
                        + jobConfigFile, e);
                } finally {
                    IOUtil.closeAndIgnoreErrors(writer);
                }
            }

            // create an array grid job for this set of items and kick it off
            ArrayGridJobImpl.Builder builder =
                new ArrayGridJobImpl.Builder(gridExecutor.getSession(),
                                             new Command(arrayGridJobTemplate.getAbsolutePath()),
                                             key.getProjectCode());
            builder.setArchitecture(key.getArchitecture()); // unsure if this is necessary/useful!
            builder.setInputFile(new File(arrayJobRoot,ARRAY_JOB_CONFIG_FILENAME_ROOT));
            builder.setOutputFile(new File(arrayJobRoot,ARRAY_JOB_OUTPUT_FILENAME_ROOT));
            builder.setErrorFile(new File(arrayJobRoot,ARRAY_JOB_ERROR_FILENAME_ROOT));
            builder.setBulkJobStartLoopIndex(1);
            builder.setBulkJobEndLoopIndex(jobs.size());
            builder.setBulkJobLoopIncrement(1);
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
                throw new RuntimeException("Unexpected error waiting for array grid job result", e);
            }
        }
    }

    // write the grid job template (or simply use an existing file location?)
    private File getArrayGridJobTemplate() {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            File arrayGridJobTemplate = new File(executorRoot,"runClsrPDArrayScript.sh");
            inputStream = this.getClass().getClassLoader().getResourceAsStream("runClsrPDArrayScript.sh");
            outputStream = new BufferedOutputStream(new FileOutputStream(arrayGridJobTemplate));
            IOUtil.writeToOutputStream(inputStream,outputStream);
            arrayGridJobTemplate.setExecutable(true,true);
            return arrayGridJobTemplate;
        } catch ( Exception e) {
            throw new RuntimeException("Unable to generate array grid job template",e);
        } finally {
            IOUtil.closeAndIgnoreErrors(inputStream);
            IOUtil.closeAndIgnoreErrors(outputStream);
        }
    }

    public boolean isFinished() {
        return gridExecutor.countActiveTasks() == 0;
    }

    private static class ArrayJobKey {
        private String projectCode;
        private String architecture;

        public ArrayJobKey(String projectCode, String architecture) {
            this.projectCode = projectCode;
            this.architecture = architecture;
        }

        public String getProjectCode() {
            return projectCode;
        }

        public String getArchitecture() {
            return architecture;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ArrayJobKey that = (ArrayJobKey) o;

            if (architecture != null ? !architecture.equals(that.architecture) : that.architecture != null)
                return false;
            if (projectCode != null ? !projectCode.equals(that.projectCode) : that.projectCode != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = projectCode != null ? projectCode.hashCode() : 0;
            result = 31 * result + (architecture != null ? architecture.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append(projectCode).append("-").append(architecture);
            return sb.toString();
        }
    }

}