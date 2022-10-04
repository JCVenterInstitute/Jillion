/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.residue.aa;

import java.util.List;

public interface TranslationVisitor {

    public enum FoundStartResult{
            FIND_ADDITIONAL_STARTS,
            CONTINUE,
            STOP
    }
    public enum FoundStopResult{
        READ_THROUGH,
        STOP
    }
    
   
    void visitCodon(long nucleotideCoordinate, Codon codon);
    FoundStartResult foundStart(long nucleotideCoordinate, Codon codon);
    
    FoundStopResult foundStop(long nucleotideCoordinate, Codon codon);
    
    void end();
	default void visitVariantCodon(long nucleotideCoordinate, List<Codon> codons) {
		visitCodon(nucleotideCoordinate, codons.get(0));
	}
}
