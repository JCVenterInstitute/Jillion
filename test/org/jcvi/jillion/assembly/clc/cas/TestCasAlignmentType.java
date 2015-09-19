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
 * Created on Jan 20, 2010
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.clc.cas;

import org.jcvi.jillion.assembly.clc.cas.CasAlignmentType;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestCasAlignmentType {

    @Test
    public void valueOf(){
        assertSame(CasAlignmentType.LOCAL, CasAlignmentType.valueOf((byte)0));
        assertSame(CasAlignmentType.SEMI_LOCAL, CasAlignmentType.valueOf((byte)1));
        assertSame(CasAlignmentType.REVERSE_SEMI_LOCAL, CasAlignmentType.valueOf((byte)2));
        assertSame(CasAlignmentType.GLOBAL, CasAlignmentType.valueOf((byte)3));
    }
}
