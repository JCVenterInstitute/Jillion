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

package org.jcvi.app;

import org.jcvi.app.Version.ReleaseType;
import org.jcvi.testUtil.TestUtil;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestVersion {

    private Version alphaVersion = new Version.Builder(1)
                                        .apiVersion(2)
                                        .patchVersion(3)
                                        .release(ReleaseType.ALPHA,99)
                                        .build();
    
    private Version betaVersion = new Version.Builder(1)
                                        .apiVersion(2)
                                        .patchVersion(3)
                                        .release(ReleaseType.BETA,99)
                                        .build();
    private Version newVersion = new Version.Builder(1)
                                        .apiVersion(2)
                                        .patchVersion(4)
                                        .release(ReleaseType.BETA,99)
                                        .build();
    @Test(expected = IllegalArgumentException.class)
    public void negativeCodeVersionShouldThrowIllegalArgumentException(){
        new Version.Builder(-1);
    }
    @Test(expected = IllegalArgumentException.class)
    public void negativeApiVersionShouldThrowIllegalArgumentException(){
        new Version.Builder(1).apiVersion(-1);
    }
    @Test(expected = IllegalArgumentException.class)
    public void negativePatchShouldThrowIllegalArgumentException(){
        new Version.Builder(1).apiVersion(1).patchVersion(-2);
    }
    @Test(expected = IllegalArgumentException.class)
    public void negativereleaseNumberShouldThrowIllegalArgumentException(){
        new Version.Builder(1).apiVersion(1).patchVersion(2).release(ReleaseType.ALPHA, -1);
    }
    @Test
    public void codeOnly(){
        Version version = new Version.Builder(1).build();
        assertEquals("1.0.0", version.toString());
    }
    @Test
    public void codeAndApiOnly(){
        Version version = new Version.Builder(1).apiVersion(2).build();
        assertEquals("1.2.0", version.toString());
    }
    @Test
    public void codeApiPatchOnly(){
        Version version = new Version.Builder(1)
                                .apiVersion(2)
                                .patchVersion(3)
                                .build();
        assertEquals("1.2.3", version.toString());
    }
    @Test
    public void codeAndPatchOnly(){
        Version version = new Version.Builder(1)
                                .patchVersion(3)
                                .build();
        assertEquals("1.0.3", version.toString());
    }
    @Test
    public void alphaRelease(){
        Version version = new Version.Builder(1)
                                .apiVersion(2)
                                .patchVersion(3)
                                .release(ReleaseType.ALPHA,99)
                                .build();
        assertEquals("1.2.3a99", version.toString());
    }
    @Test
    public void betaRelease(){
        Version version = new Version.Builder(1)
                                .apiVersion(2)
                                .patchVersion(3)
                                .release(ReleaseType.BETA,99)
                                .build();
        assertEquals("1.2.3b99", version.toString());
    }
    @Test
    public void internalRelease(){
        Version version = new Version.Builder(1)
                                .apiVersion(2)
                                .patchVersion(3)
                                .release(ReleaseType.INTERNAL,99)
                                .build();
        assertEquals("1.2.3x99", version.toString());
    }
    @Test
    public void releaseCandidate(){
        Version version = new Version.Builder(1)
                                .apiVersion(2)
                                .patchVersion(3)
                                .release(ReleaseType.RELEASE_CANDIDATE,99)
                                .build();
        assertEquals("1.2.3rc99", version.toString());
    }
    @Test
    public void stableReleaseShouldNotIncludeReleaseNumberIntoString(){
        Version version = new Version.Builder(1)
                                .apiVersion(2)
                                .patchVersion(3)
                                .release(ReleaseType.STABLE,99)
                                .build();
        assertEquals("1.2.3", version.toString());
    }
    
    @Test
    public void serialNumberMatchIfEqual(){
        Version sameVersion = new Version.Builder(1)
                                    .apiVersion(2)
                                    .patchVersion(3)
                                    .release(ReleaseType.ALPHA,99)
                                    .build();
        
        TestUtil.assertEqualAndHashcodeSame(sameVersion, alphaVersion);
        assertEquals(sameVersion.getSerialNumber(), alphaVersion.getSerialNumber());
        assertTrue(sameVersion.isAtLeast(alphaVersion));
    }
    @Test
    public void isAtLeast(){
        assertTrue(betaVersion.isAtLeast(alphaVersion));
        assertFalse(alphaVersion.isAtLeast(betaVersion));
        
        assertTrue(newVersion.isAtLeast(betaVersion));
        assertFalse(betaVersion.isAtLeast(newVersion));
    }
    @Test
    public void matchesAPI(){
        assertTrue(betaVersion.matchesAPI(alphaVersion));
        assertTrue(alphaVersion.matchesAPI(betaVersion));
        
        Version newAPI = new Version.Builder(1)
                                    .apiVersion(5)
                                    .build();
        
        assertFalse(betaVersion.matchesAPI(newAPI));
        assertFalse(newAPI.matchesAPI(betaVersion));
    }
    
    @Test
    public void compareTo(){
        assertTrue(betaVersion.compareTo(alphaVersion)>0);
        assertTrue(alphaVersion.compareTo(betaVersion)<0);
        assertEquals(0, alphaVersion.compareTo(alphaVersion));
    }
    
}
