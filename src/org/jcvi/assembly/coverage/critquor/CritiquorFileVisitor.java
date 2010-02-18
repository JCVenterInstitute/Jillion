/*
 * Created on Aug 13, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.coverage.critquor;

import org.jcvi.Range;

public interface CritiquorFileVisitor {

    void visitStartOfFile();
    void visitEndOfFile();
    void visitAmplicon(String id, String region,  Range designedRange, String forwardPrimer, String reversePrimer);
}
