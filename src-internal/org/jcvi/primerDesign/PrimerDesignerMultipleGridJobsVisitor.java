package org.jcvi.primerDesign;

import org.jcvi.primerDesign.gridJob.PrimerDesignerGridJob;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

/**
 * User: aresnick
 * Date: Jul 26, 2010
 * Time: 2:48:21 PM
 * <p/>
 * $HeadURL$
 * $LastChangedRevision$
 * $LastChangedBy$
 * $LastChangedDate$
 * <p/>
 * Description:
 */
public class PrimerDesignerMultipleGridJobsVisitor implements PrimerDesignerJobOutputVisitor {

    private List<PrimerDesignerGridJob> gridJobs;

    public PrimerDesignerMultipleGridJobsVisitor() {
    }

    public List<PrimerDesignerGridJob> getGridJobs() {
        return gridJobs;
    }

    @Override
    public void visitLine(String line) {
        
    }

    @Override
    public void visitGridJob(String projectCode,
                             String architecture,
                             File stdOutputLocation,
                             File stdErrorLocation,
                             File primerDesignerScript) {
        PrimerDesignerGridJob.Builder builder = new PrimerDesignerGridJob.Builder();
        builder.setProjectCode(projectCode);
        builder.setArchitecture(architecture);
        builder.setStdOutputLocation(stdOutputLocation);
        builder.setStdErrorLocation(stdErrorLocation);
        builder.setGridJobScript(primerDesignerScript);
        gridJobs.add(builder.build());
    }

    @Override
    public void visitFile() {
        gridJobs = new ArrayList<PrimerDesignerGridJob>();
    }

    @Override
    public void visitEndOfFile() {
    }

}
