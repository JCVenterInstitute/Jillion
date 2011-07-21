package org.jcvi.experimental.primerDesign;

import org.jcvi.experimental.primerDesign.gridjob.PrimerDesignerArrayGridJob;

import java.util.List;
import java.util.ArrayList;

import java.io.File;

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
public class PrimerDesignerArrayGridJobsVisitor implements PrimerDesignerJobOutputVisitor {

    private List<PrimerDesignerArrayGridJob> gridJobs;

    public PrimerDesignerArrayGridJobsVisitor() {
    }

    public List<PrimerDesignerArrayGridJob> getGridJobs() {
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
        PrimerDesignerArrayGridJob.Builder builder = new PrimerDesignerArrayGridJob.Builder();
        builder.setProjectCode(projectCode);
        builder.setArchitecture(architecture);

        try {
            DefaultPrimerDesignerScriptFileVisitor visitor = new DefaultPrimerDesignerScriptFileVisitor();
            PrimerDesignerScriptFileParser.parsePrimerDesignerScriptFile(primerDesignerScript,visitor);
            builder.setConfigFile(visitor.getConfigFile());
            builder.setGffFile(visitor.getGffFile());
            builder.setRenamedPdfFile(visitor.getPdfConversionTargetFilename());
            builder.setPrimerFastaFile(visitor.getPrimerFastaFile());
        } catch (Exception e) {
            throw new PrimerDesignerRequestJobCreationException("can't parse primer designer script "
                + primerDesignerScript + " to find array job parameters");
        }
        
        gridJobs.add(builder.build());
    }

    @Override
    public void visitFile() {
        gridJobs = new ArrayList<PrimerDesignerArrayGridJob>();
    }

    @Override
    public void visitEndOfFile() {
    }
}