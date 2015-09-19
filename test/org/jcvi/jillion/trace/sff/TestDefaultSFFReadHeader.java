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
 * Created on Oct 8, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sff;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.jcvi.jillion.trace.sff.DefaultSffReadHeader;
import org.junit.Test;

import static org.junit.Assert.*;
public class TestDefaultSFFReadHeader {
    int numberOfBases=100;
    Range qualityClip = Range.of(10,90);
    Range adapterClip= Range.of(5,95);
    String name = "sequence name";

    DefaultSffReadHeader sut = new DefaultSffReadHeader( numberOfBases,
            qualityClip, adapterClip, name);

    @Test
    public void constructor(){
        assertEquals(numberOfBases, sut.getNumberOfBases());
        assertEquals(qualityClip, sut.getQualityClip());
        assertEquals(adapterClip, sut.getAdapterClip());
        assertEquals(name, sut.getId());
    }

    @Test
    public void equalsSameRef(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    @Test
    public void notEqualsNull(){
        assertFalse(sut.equals(null));
    }
    @Test
    public void notEqualsWrongClass(){
        assertFalse(sut.equals("not a DefaultSFFReadHeader"));
    }

    @Test
    public void equalsSameValues(){
        DefaultSffReadHeader sameValues = new DefaultSffReadHeader(
                numberOfBases,
                qualityClip,
                adapterClip,
                name);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }
   
    @Test
    public void notEqualsDifferentNumberOfBases(){
        DefaultSffReadHeader differentValues = new DefaultSffReadHeader(
                numberOfBases+1,
                qualityClip,
                adapterClip,
                name);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }
    @Test
    public void notEqualsNullQualityClip(){
        DefaultSffReadHeader differentValues = new DefaultSffReadHeader(
                numberOfBases,
                null,
                adapterClip,
                name);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }
    @Test
    public void notEqualsDifferentQualityClip(){
        Range differentQualityClip = new Range.Builder(qualityClip).shift(2).build();
        DefaultSffReadHeader differentValues = new DefaultSffReadHeader(
                numberOfBases,
                differentQualityClip,
                adapterClip,
                name);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }

    @Test
    public void notEqualsNullAdapterClip(){
        DefaultSffReadHeader differentValues = new DefaultSffReadHeader(
                numberOfBases,
                qualityClip,
                null,
                name);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }
    @Test
    public void notEqualsDifferentAdapterClip(){
        Range differentAdapterClip = new Range.Builder(adapterClip).shift(2).build();
        DefaultSffReadHeader differentValues = new DefaultSffReadHeader(
                numberOfBases,
                qualityClip,
                differentAdapterClip,
                name);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }

    @Test
    public void notEqualsDifferentName(){
        DefaultSffReadHeader differentValues = new DefaultSffReadHeader(
                numberOfBases,
                qualityClip,
                adapterClip,
                "different"+name);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }

    @Test
    public void notEqualsNullName(){
        DefaultSffReadHeader differentValues = new DefaultSffReadHeader(
                numberOfBases,
                qualityClip,
                adapterClip,
                null);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }
}
