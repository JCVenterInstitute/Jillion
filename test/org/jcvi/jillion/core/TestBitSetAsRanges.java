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
package org.jcvi.jillion.core;

import java.util.BitSet;

import org.jcvi.jillion.internal.core.util.GrowableIntArray;

public class TestBitSetAsRanges extends AbstractTestAsRangeOpperations<BitSet>{

    public TestBitSetAsRanges() {
        super(Ranges::asRanges, Ranges::asRanges);
    }
    protected BitSet createInput(GrowableIntArray array){
        return createInput(array.toArray());
    }
    protected BitSet createInput(int...ints){
        BitSet bs = new BitSet();
        for(int i=0; i< ints.length;i++){
            bs.set(ints[i]);
        }
        
        return bs;
    }
}
