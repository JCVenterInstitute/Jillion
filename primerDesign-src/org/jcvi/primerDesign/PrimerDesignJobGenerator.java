package org.jcvi.primerDesign;

import java.io.*;
import java.util.*;

import org.apache.log4j.Logger;

import org.jcvi.io.*;

import org.jcvi.Range;
import org.jcvi.command.Command;
import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.seq.nuc.fasta.NucleotideSequenceFastaRecord;
import org.jcvi.primerDesign.domain.PrimerDesignTarget;


/**
 * Created by IntelliJ IDEA.
 * User: aresnick
 * Date: Jul 26, 2010
 * Time: 10:50:12 AM
 * To change this template use File | Settings | File Templates.
 */
public class PrimerDesignJobGenerator {

    private static final String PRIMER_DESIGNER_FEATURES_FILENAME = "features.txt";
    private static final String PRIMER_DESIGNER_STUB_FILENAME = "primer.config.stub";
    private static final String PRIMER_DESIGNER_REFERENCE_FASTA_FILENAME = "reference.fasta";
    private static final String PRIMER_DESIGNER_REFERENCE_DIRECTORY_NAME = "reference";
    private static final String PRIMER_DESIGNER_TEMPLATE_DIRECTORY_NAME = "templates";

    private static final String PRIMER_DESIGNER_JOB_OUTPUT_FILENAME = "qsub.bat";

    /* matt script location
    private static final String PRIMER_DESIGNER_JOB_BUILDER_SCRIPT =
        new File("/usr/local/devel/BCIS/src/autofinish/trunk/primer_design/designPrimersOptionalDifferentTemplateAndReference.pl");
    */

    // kelvin script location
    private static final File PRIMER_DESIGNER_JOB_BUILDER_SCRIPT =
        new File("/usr/local/devel/DAS/software/resequencing/prod/primer_design/PrimerDesigner/ProjectTools/SuperScripts/ClosureDesign/designPrimers.pl");

    private static final Range.CoordinateSystem FEATURE_FILE_COORDINATE_SYSTEM =
        Range.CoordinateSystem.ZERO_BASED;

    // needs to be a grid accessable file system; /usr/local/scratch/something would seem to be ideal
    private File primerDesignRootDirectory;
    private PrimerDesignerJobOutputVisitor visitor;

    private Logger logger = Logger.getLogger(this.getClass());

    private File primerDesignerJobBuilderScriptErrorLogFile;
    private File primerDesignerJobBuilderScriptOutputLogFile;

    public PrimerDesignJobGenerator(File primerDesignRootDirectory,
                                    PrimerDesignerJobOutputVisitor visitor) {
        this.visitor = visitor;
        this.primerDesignRootDirectory = primerDesignRootDirectory;
        
        if ( !this.primerDesignRootDirectory.mkdirs() ) {
            throw new PrimerDesignerRequestInitializationException("Could not construct primer designer scratch directory "
                + primerDesignRootDirectory);
        }

        primerDesignerJobBuilderScriptErrorLogFile =
            new File(primerDesignRootDirectory,PRIMER_DESIGNER_JOB_BUILDER_SCRIPT.getName()+".err");
        primerDesignerJobBuilderScriptOutputLogFile =
            new File(primerDesignRootDirectory,PRIMER_DESIGNER_JOB_BUILDER_SCRIPT.getName()+".out");

    }

    public void createPrimerDesignJobs(Collection<PrimerDesignTarget> targets,
                                       NucleotideSequenceFastaRecord templateRecord,
                                       DataStore<NucleotideSequenceFastaRecord> referenceFastaRecords,
                                       InputStream primerConfigurationStub,
                                       String projectCode) {
        if ( templateRecord != null ) {
            buildTemplateFastaDirectory(templateRecord);
        }

        // generate the targets file and dump it in root dir
        File featuresFile = writeTargetsFile(targets);

        // write the stub file to the root dir
        File primerStubFile = writePrimerStubFile(primerConfigurationStub);

        // write the reference fasta file to the root dir
        File referenceFastaFile = writeReferenceFastaFile(referenceFastaRecords);

        // generate the primer design command and run it
        Command primerDesignerCommand = new Command(PRIMER_DESIGNER_JOB_BUILDER_SCRIPT.getAbsolutePath());
        primerDesignerCommand.setWorkingDir(primerDesignRootDirectory);
        // Matt feature option is f
        // primerDesignerCommand.setOption("-f",featuresFile.getAbsolutePath());
        // Kelvin feature option is t
        primerDesignerCommand.setOption("-t",featuresFile.getAbsolutePath());
        primerDesignerCommand.setOption("-r",referenceFastaFile.getAbsolutePath());
        primerDesignerCommand.setOption("-c",primerStubFile.getAbsolutePath());
        primerDesignerCommand.setOption("-p",projectCode);
        primerDesignerCommand.setOption("-o",primerDesignRootDirectory.getAbsolutePath());
        /* matt script only
        primerDesignerCommand.setOption("-a",architecture);
        if ( sourceTemplateFastaDirectory != null ) {
            primerDesignerCommand.setOption("-t",sourceTemplateFastaDirectory.getAbsolutePath());
        }
        */
        logger.debug("Setting up directories and generating base primer designer grid job script with command ["
            + primerDesignerCommand + "]");
        Process process = null;
        try {
            process = primerDesignerCommand.execute();
            int exitValue = process.waitFor();
            if ( exitValue != 0 ) {
                throw new PrimerDesignerRequestJobCreationException(
                    "primer design command " + primerDesignerCommand + " failed with exit code " + exitValue
                    + "; see error log file " + primerDesignerJobBuilderScriptErrorLogFile + " for error details"
                );
            }
        } catch (InterruptedException e ) {
            throw new PrimerDesignerRequestJobCreationException(
                "primer design command " + primerDesignerCommand + " was interrupted before it successfully completed"
            );
        } catch ( IOException ioe ) {
            throw new PrimerDesignerRequestJobCreationException(
                "primer design command " + primerDesignerCommand + " failure",ioe);
        } finally {
            if ( process != null ) {
                // close the process input stream
                IOUtil.closeAndIgnoreErrors(process.getOutputStream());

                // write the process' output and error streams to log file (and close them)
                writeProcessLogfile(process.getInputStream(),primerDesignerJobBuilderScriptOutputLogFile);
                writeProcessLogfile(process.getErrorStream(),primerDesignerJobBuilderScriptErrorLogFile);
            }
        }

        // check to make sure expected grid job generation script artifacts exist
        try {
            checkPrimerDesignSetupScriptExecution(targets);
        } catch (Exception e) {
            throw new PrimerDesignerRequestJobCreationException(
                "primer design command " + primerDesignerCommand + " exited with no error code, "
                + "but one (or more) of its expected outputs does not exist", e);
        }

        File gridJobsFile = getGridJobsFile();
        try {
            PrimerDesignerJobOutputParser.parseGridJobsFile(gridJobsFile,visitor);
        } catch (Exception ioe) {
            throw new PrimerDesignerRequestJobCreationException(
                "unable to parse expected grid jobs list " + gridJobsFile,ioe);
        }
    }

    private void writeProcessLogfile(InputStream logStream, File logfile) {
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(logfile));
            IOUtil.writeToOutputStream(logStream,out);
            out.flush();
        } catch (Exception e) {
            logger.warn("Unable to write process log file " + logfile,e);
        } finally {
            IOUtil.closeAndIgnoreErrors(logStream);
            IOUtil.closeAndIgnoreErrors(out);
        }
    }

    private void checkPrimerDesignSetupScriptExecution(Collection<PrimerDesignTarget> targets) {
        // make sure the grid jobs' template fasta files exist
        File jobTemplateFastaDirectory = getTemplateDirectory();
        List<File> missingTemplateFastaFiles = new ArrayList<File>();
        for ( PrimerDesignTarget target : targets ) {
            File referenceFasta = new File(jobTemplateFastaDirectory,target.getID()+".fasta");
            if ( !(referenceFasta.length() > 0) ) {
                missingTemplateFastaFiles.add(referenceFasta);
            }
        }

        if ( !missingTemplateFastaFiles.isEmpty() ) {
            throw new PrimerDesignerRequestJobCreationException(
                "the following expected grid job template fasta files either do not exist "
                    + " or are zero length files " + missingTemplateFastaFiles);
        }

        // make sure the grid jobs' formatted reference db files exist
        // (i.e. reference.fasta.nhr|.nin|.nsq and RefGenomeIndex)?
        File jobReferenceFastaDBDirectory = getReferenceDirectory();
        List<File> missingReferenceDBFiles = new ArrayList<File>();
        if ( !(new File(jobReferenceFastaDBDirectory,"reference.fasta.nhr").length() > 0 ) ) {
            missingReferenceDBFiles.add(new File(jobReferenceFastaDBDirectory,"reference.fasta.nhr"));
        }
        if ( !(new File(jobReferenceFastaDBDirectory,"reference.fasta.nin").length() > 0 ) ) {
            missingReferenceDBFiles.add(new File(jobReferenceFastaDBDirectory,"reference.fasta.nin"));
        }
        if ( !(new File(jobReferenceFastaDBDirectory,"reference.fasta.nsq").length() > 0 ) ) {
            missingReferenceDBFiles.add(new File(jobReferenceFastaDBDirectory,"reference.fasta.nsr"));
        }
        if ( !(new File(jobReferenceFastaDBDirectory,"RefGenomeIndex").length() > 0 ) ) {
            missingReferenceDBFiles.add(new File(jobReferenceFastaDBDirectory,"RefGenomeIndex"));
        }

        if ( !missingReferenceDBFiles.isEmpty() ) {
            throw new PrimerDesignerRequestJobCreationException(
                "the following expected grid job reference fasta db files either do not exist "
                    + " or are zero length files " + missingTemplateFastaFiles);
        }
        
        // make sure the primer design grid jobs file exists
        File gridJobsFile = getGridJobsFile(); 
        if ( !gridJobsFile.exists() ) {
            throw new PrimerDesignerRequestJobCreationException("grid jobs file " + gridJobsFile + " does not exist");
        }
    }

    private File getGridJobsFile() {
        return new File(primerDesignRootDirectory,PRIMER_DESIGNER_JOB_OUTPUT_FILENAME);
    }

    private File getTemplateDirectory() {
        return new File(primerDesignRootDirectory,PRIMER_DESIGNER_TEMPLATE_DIRECTORY_NAME);
    }

    private File getReferenceDirectory() {
        return new File(primerDesignRootDirectory,PRIMER_DESIGNER_REFERENCE_DIRECTORY_NAME);
    }

    private File writeTargetsFile(Collection<PrimerDesignTarget> targets) {
        File featuresFile = new File(primerDesignRootDirectory,PRIMER_DESIGNER_FEATURES_FILENAME);
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(featuresFile);
            for ( PrimerDesignTarget target : targets ) {
                Range targetRange = target.getRange().convertRange(FEATURE_FILE_COORDINATE_SYSTEM);
                writer.format("%s\t%d\t%d\n",
                        target.getID(),
                        targetRange.getLocalStart(),
                        targetRange.getLocalEnd());
            }
            writer.flush();
        } catch (Exception e) {
            throw new PrimerDesignerRequestJobCreationException(
                "Failure creating/writing primer designer features file " + featuresFile,e
            );
        } finally {
            IOUtil.closeAndIgnoreErrors(writer);
        }
        return featuresFile;
    }

    private File writePrimerStubFile(InputStream primerConfigurationStub) {
        File stubFile = new File(primerDesignRootDirectory,PRIMER_DESIGNER_STUB_FILENAME);
        OutputStream out = null;
        try {
            out = new FileOutputStream(stubFile);
            IOUtil.writeToOutputStream(primerConfigurationStub,out);
        } catch (Exception e) {
            throw new PrimerDesignerRequestJobCreationException(
                "Failure writing primer stub file " + stubFile,e
            );
        } finally {
            IOUtil.closeAndIgnoreErrors(primerConfigurationStub);
            IOUtil.closeAndIgnoreErrors(out);
        }
        return stubFile;
    }

    private File buildTemplateFastaDirectory(NucleotideSequenceFastaRecord templateFastaRecord) {
        File templateFastaDir = getTemplateDirectory();
        templateFastaDir.mkdirs();
        File templateFastaFile = new File(templateFastaDir,templateFastaRecord.getId());
        OutputStream out = null;
        try {
            out = new FileOutputStream(templateFastaFile);
            out.write(templateFastaRecord.toFormattedString().toString().getBytes());
            out.flush();
        } catch (Exception e) {
            throw new PrimerDesignerRequestJobCreationException(
                "Failure writing template fasta file " + templateFastaFile,e
            );
        } finally {
            IOUtil.closeAndIgnoreErrors(out);
        }
        return templateFastaDir;
    }

    private File writeReferenceFastaFile(
         DataStore<NucleotideSequenceFastaRecord> referenceFastaRecords) {
        File referenceFasta = new File(primerDesignRootDirectory,PRIMER_DESIGNER_REFERENCE_FASTA_FILENAME);
        OutputStream out = null;
        try {
            out = new FileOutputStream(referenceFasta);
            for ( Iterator<NucleotideSequenceFastaRecord> fastaRecords = referenceFastaRecords.iterator();
                  fastaRecords.hasNext(); ) {
                NucleotideSequenceFastaRecord fastaRecord = fastaRecords.next();
                out.write(fastaRecord.toFormattedString().toString().getBytes());
            }
            out.flush();
        } catch (Exception e) {
            throw new PrimerDesignerRequestJobCreationException(
                "Failure writing reference fasta file " + referenceFasta,e
            );
        } finally {
            IOUtil.closeAndIgnoreErrors(out);
        }
        return referenceFasta;
    }
}
