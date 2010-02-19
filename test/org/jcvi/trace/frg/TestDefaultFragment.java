/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on May 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.frg;

import org.jcvi.Range;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.sequence.Library;
import org.jcvi.testUtil.TestUtil;
import org.jcvi.trace.Trace;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
public class TestDefaultFragment {

    String id = "fragId";
    String comment = "a comment";
    String libraryId = "libraryId";
    
    NucleotideEncodedGlyphs bases = createMock(NucleotideEncodedGlyphs.class);
    EncodedGlyphs<PhredQuality> qualities = createMock(EncodedGlyphs.class);
    
    Library library = createMock(Library.class);
    Range validRange = Range.buildRange(10, 20);
    Range clearRange = Range.buildRange(12,20);
    long basesLength=21;
    DefaultFragment sut;
    
    @Before
    public void setup(){
       expect(library.getId()).andStubReturn(libraryId);
       expect(bases.getLength()).andStubReturn(basesLength);
       
        sut = new DefaultFragment(id,bases,qualities, validRange, clearRange,library,comment);
        replay(library, bases);
    }
    
    @Test
    public void traceConstructor(){
        Trace trace = createMock(Trace.class);
        expect(trace.getBasecalls()).andReturn(bases);
        expect(trace.getQualities()).andReturn(qualities);
        replay(trace);
        DefaultFragment traceSut =new DefaultFragment(id,trace, validRange, clearRange,library,comment);
        
        assertFragmentGettersCorrect(traceSut);
        
        verify(trace);
    }

    private void assertFragmentGettersCorrect(DefaultFragment fragment) {
       
        assertEquals(id, fragment.getId());
        assertEquals(bases, fragment.getBasecalls());
        assertEquals(bases, fragment.getEncodedGlyphs());
        assertEquals(qualities, fragment.getQualities());
        assertEquals(validRange, fragment.getValidRange());
        assertEquals(clearRange, fragment.getVectorClearRange());
        assertEquals(library, fragment.getLibrary());
        assertEquals(comment, fragment.getComment());
        assertEquals(basesLength, fragment.getLength());
        assertEquals(libraryId, fragment.getLibraryId());
    }
    @Test
    public void constructor(){
        assertFragmentGettersCorrect(sut);
    }
    
    @Test
    public void nullIdShouldThrowIllegalArgumentException(){
        try{
            new DefaultFragment(null,bases,qualities, validRange, clearRange,library,comment);
            fail("should throw IllegalArgumentException when Id is null");
        }catch(IllegalArgumentException e){
            assertEquals(e.getMessage(), "id can not be null");
        }
    }
    @Test
    public void equalsSameRef(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    
    @Test
    public void equalsSameValues(){
        DefaultFragment sameValues = new DefaultFragment(id,bases,qualities, validRange, clearRange,library,comment);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }
    
    @Test
    public void notEqualToNull(){
       assertFalse(sut.equals(null));
    }
    @Test
    public void notEqualToDifferentClass(){
       assertFalse(sut.equals("not a defaultFragment"));
    }
    
    @Test
    public void differentBasesShouldStillBeEqual(){
        NucleotideEncodedGlyphs differentBases = createMock(NucleotideEncodedGlyphs.class);
        DefaultFragment hasDifferentBases = new DefaultFragment(id,differentBases,qualities, validRange, clearRange,library,comment);
        TestUtil.assertEqualAndHashcodeSame(sut, hasDifferentBases);
    }
    
    @Test
    public void differentQualitiesShouldStillBeEqual(){
        EncodedGlyphs<PhredQuality> differentQualities = createMock(EncodedGlyphs.class);
        DefaultFragment hasDifferentQualities = new DefaultFragment(id,bases,differentQualities, validRange, clearRange,library,comment);
        TestUtil.assertEqualAndHashcodeSame(sut, hasDifferentQualities);
    }
    @Test
    public void differentValildRangeShouldStillBeEqual(){
        DefaultFragment hasDifferentValidRange = new DefaultFragment(id,bases,qualities, validRange.shiftLeft(1), clearRange,library,comment);
        TestUtil.assertEqualAndHashcodeSame(sut, hasDifferentValidRange);
    }
    
    @Test
    public void differentClearRangeShouldStillBeEqual(){
        DefaultFragment hasDifferentClearRange = new DefaultFragment(id,bases,qualities, validRange, clearRange.shiftLeft(1),library,comment);
        TestUtil.assertEqualAndHashcodeSame(sut, hasDifferentClearRange);
    }
    @Test
    public void differentCommentStillBeEqual(){
        DefaultFragment hasDifferentComment = new DefaultFragment(id,bases,qualities, validRange, 
                                                    clearRange,library,"different"+comment);
        TestUtil.assertEqualAndHashcodeSame(sut, hasDifferentComment);
    }
    
    @Test
    public void differentLibraryStillBeEqual(){
        Library differentLibrary = createMock(Library.class);
        DefaultFragment hasDifferentLibrary = new DefaultFragment(id,bases,qualities, validRange, 
                                                    clearRange,differentLibrary,comment);
        TestUtil.assertEqualAndHashcodeSame(sut, hasDifferentLibrary);
    }
    
    @Test
    public void differentIdShouldNotBeEqual(){
        DefaultFragment hasDifferentId = new DefaultFragment("different"+id,bases,qualities, validRange, 
                                                    clearRange,library,comment);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasDifferentId);
    }
}
