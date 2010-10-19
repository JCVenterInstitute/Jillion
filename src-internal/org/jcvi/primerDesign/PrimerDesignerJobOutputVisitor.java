package org.jcvi.primerDesign;

import org.jcvi.io.TextFileVisitor;

import java.io.File;

/**
 * User: aresnick
 * Date: Jul 26, 2010
 * Time: 2:27:18 PM
 * <p/>
 * $HeadURL$
 * $LastChangedRevision$
 * $LastChangedBy$
 * $LastChangedDate$
 * <p/>
 * Description:
 */
public interface PrimerDesignerJobOutputVisitor extends TextFileVisitor {

    void visitGridJob(String projectCode,
                      String architecture,
                      File stdOutputLocation,
                      File stdErrorLocation,
                      File primerDesignerScript);
}
