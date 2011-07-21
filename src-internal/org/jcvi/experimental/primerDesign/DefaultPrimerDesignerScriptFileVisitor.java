package org.jcvi.experimental.primerDesign;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: aresnick
 * Date: Aug 6, 2010
 * Time: 12:10:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultPrimerDesignerScriptFileVisitor implements PrimerDesignerScriptFileVisitor {

    private File primerDesignerCommand;
    private File configFile;

    private File pdfConversionCommand;
    private File gffFile;

    private File pdfConversionTargetFilename;

    private File primerCritiquorCommand;
    private File primerFastaFile;

    public DefaultPrimerDesignerScriptFileVisitor() {
    }

    public File getPrimerDesignerCommand() {
        return primerDesignerCommand;
    }

    public File getConfigFile() {
        return configFile;
    }

    public File getPdfConversionCommand() {
        return pdfConversionCommand;
    }

    public File getGffFile() {
        return gffFile;
    }

    public File getPdfConversionTargetFilename() {
        return pdfConversionTargetFilename;
    }

    public File getPrimerCritiquorCommand() {
        return primerCritiquorCommand;
    }

    public File getPrimerFastaFile() {
        return primerFastaFile;
    }

    @Override
    public void visitPrimerDesignerCommand(File command, File configFile) {
        this.primerDesignerCommand = command;
        this.configFile = configFile;
    }

    @Override
    public void visitPdfConversionCommand(File command, File gffFile) {
        this.pdfConversionCommand = command;
        this.gffFile = gffFile;
    }

    @Override
    public void visitPdfRenameCommand(File originalFilename, File targetFilename) {
        this.pdfConversionTargetFilename = targetFilename;
    }

    @Override
    public void visitPrimerCritiquorCommand(File command, File primerFastaFile, File configFile) {
        this.primerCritiquorCommand = command;
        this.primerFastaFile = primerFastaFile;
    }


    @Override
    public void visitLine(String line) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visitFile() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visitEndOfFile() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
