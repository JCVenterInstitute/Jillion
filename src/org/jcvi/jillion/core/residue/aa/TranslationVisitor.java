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

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jcvi.jillion.core.util.MapValueComparator;

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
    
   
    void visitCodon(long nucleotideStartCoordinate, long nucleotideEndCoordinate, Codon codon);
    FoundStartResult foundStart(long nucleotideStartCoordinate, long nucleotideEndCoordinate, Codon codon);
    
    FoundStopResult foundStop(long nucleotideStartCoordinate, long nucleotideEndCoordinate, Codon codon);
    
    void end();
	default void visitVariantCodon(long nucleotideStartCoordinate, long nucleotideEndCoordinate, Map<Codon, Double> codons) {
		
		visitCodon(nucleotideStartCoordinate, nucleotideEndCoordinate, MapValueComparator.sortDescending(codons).firstKey());
	}
}
