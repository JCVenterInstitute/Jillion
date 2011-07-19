package org.jcvi.primerDesign;

import org.jcvi.common.core.io.TextFileVisitor;

import java.io.File;

/**
* Created by IntelliJ IDEA.
* User: aresnick
* Date: Aug 6, 2010
* Time: 11:43:39 AM
* To change this template use File | Settings | File Templates.
*/
public interface PrimerDesignerScriptFileVisitor extends TextFileVisitor {

    void visitPrimerDesignerCommand(File command, File configFile);
    void visitPdfConversionCommand(File command, File gffFile);
    void visitPdfRenameCommand(File originalFilename, File targetFilename);
    void visitPrimerCritiquorCommand(File command, File primerFastaFile, File configFile);
}
