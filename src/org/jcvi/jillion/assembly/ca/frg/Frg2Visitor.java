/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
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
/*
 * Created on Jul 17, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.ca.frg;

import java.util.List;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.io.TextFileVisitor;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

public interface Frg2Visitor extends TextFileVisitor{
    
    public enum FrgAction {
        ADD,
        MODIFY,
        DELETE,
        IGNORE;
        
        public static FrgAction parseAction(char action){
            switch(action){
                case 'A': return ADD;
                case 'M': return MODIFY;
                case 'D': return DELETE;
                case 'I': return IGNORE;
                default:
                    throw new IllegalArgumentException("not a Frg action : "+ action);
            }
        }
    }
    
    void visitLibrary(FrgAction action, 
                        String id,
                        MateOrientation orientation,
                        Distance distance);
    
    void visitFragment(FrgAction action,
                String fragmentId, 
                String libraryId,
                NucleotideSequence bases,
                QualitySequence qualities ,
                Range validRange,
                Range vectorClearRange,
                String source);
    
    void visitLink(FrgAction action, List<String> fragIds);
}
