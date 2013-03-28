/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.trace.sanger.chromat;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.jcvi.jillion.trace.sanger.chromat.Chromatogram;
import org.jcvi.jillion.trace.sanger.chromat.SignalStrength;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestSignalStrength {
    String comment= "A:200,C:500,G:123,T:42";
    @Test
    public void parseString(){
        SignalStrength actual = SignalStrength.parseSignalStrength(comment);
        assertEquals(200, actual.getASignal());
        assertEquals(500, actual.getCSignal());
        assertEquals(123, actual.getGSignal());
        assertEquals(42, actual.getTSignal());
        
    }
    @Test(expected=NullPointerException.class)
    public void parseNullStringShouldThrowNPE(){
        SignalStrength.parseSignalStrength((String)null);
    }
    @Test(expected=IllegalArgumentException.class)
    public void parseInvalidStringShouldThrowIllegalArgumentException(){
        SignalStrength.parseSignalStrength("not a signal strength comment");
    }
    @Test
    public void parseChromatogram(){
        Chromatogram mockChromo = createMock(Chromatogram.class);
        Map<String,String> comments = new HashMap<String, String>();
        comments.put("SIGN", comment);
        expect(mockChromo.getComments()).andReturn(comments);
        replay(mockChromo);
        SignalStrength actual = SignalStrength.parseSignalStrength(mockChromo);
        assertEquals(200, actual.getASignal());
        assertEquals(500, actual.getCSignal());
        assertEquals(123, actual.getGSignal());
        assertEquals(42, actual.getTSignal());
        
        verify(mockChromo);
    }
    @Test(expected = NoSuchElementException.class)
    public void parseChromatogramWithoutSignalCommentShouldThrowNoSuchElementException(){
        Chromatogram mockChromo = createMock(Chromatogram.class);
        Map<String,String> comments = new HashMap<String, String>();
        expect(mockChromo.getComments()).andReturn(comments);
        replay(mockChromo);
        SignalStrength.parseSignalStrength(mockChromo);
    }
    @Test(expected = IllegalArgumentException.class)
    public void parseChromatogramWithInvalidSignalCommentShouldThrowIllegalArgumentException(){
        Chromatogram mockChromo = createMock(Chromatogram.class);
        Map<String,String> comments = new HashMap<String, String>();
        comments.put("SIGN", "not a signal strength comment");
        expect(mockChromo.getComments()).andReturn(comments);
        replay(mockChromo);
        SignalStrength.parseSignalStrength(mockChromo);
    }
    @Test(expected=NullPointerException.class)
    public void parseNullChromatogramShouldThrowNPE(){
        SignalStrength.parseSignalStrength((Chromatogram)null);
    }
}
