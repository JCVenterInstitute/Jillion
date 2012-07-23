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

package org.jcvi.common.core.seq.read.trace.sanger.chromat;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@code SignalStrength} contains 
 * chromatogram signal strength metadata which is 
 * included in some chromatogram file encodings.
 * @author dkatzel
 *
 *
 */
public final class SignalStrength {
	 private final int aSignal,cSignal,gSignal,tSignal;
	 
    /**
     * The comment key in a chromtogram file which contains the signal strength data.
     */
    private static final String SIGNAL_STRENGTH_COMMENT_NAME = "SIGN";
    /**
     * The pattern of the comment value in a chromtogram file which contains the signal strength data.
     */
    private static final Pattern SIGNAL_PATTERN = Pattern.compile("A:(\\d+),C:(\\d+),G:(\\d+),T:(\\d+)");
    /**
     * Parse a signal strength encoded string into a signalStrength object.
     * @param signalComment
     * @return a new SignalStrength object (never null)
     * @throws IllegalArgumentException if the signalComment is not formatted correctly
     * @throws NullPointerException if signalComment is null.
     */
    public static SignalStrength parseSignalStrength(String signalComment){
        if(signalComment == null){
            throw new NullPointerException("signalComment can not be null");
        }
        Matcher matcher = SIGNAL_PATTERN.matcher(signalComment);
        if(matcher.matches()){
            return new SignalStrength(
                    Integer.parseInt(matcher.group(1)),
                    Integer.parseInt(matcher.group(2)),
                    Integer.parseInt(matcher.group(3)),
                    Integer.parseInt(matcher.group(4)));
        }
        throw new IllegalArgumentException("signal strength comment did not match expected pattern: "+ signalComment);
    }
    /**
     * Create a new SignalStrength object using the comment contained
     * in the given Chromatogram.
     * @param chromatogram
     * @return
     * @throws IllegalArgumentException if the signalComment is not formatted correctly
     * @throws NullPointerException if signalComment is null.
     * @throws NoSuchElementException if the chromatogram does not contain a signal comment
     */
    public static SignalStrength parseSignalStrength(Chromatogram chromatogram){
        if(chromatogram == null){
            throw new NullPointerException("chromatogram can not be null");
        }
        Map<String, String> comments = chromatogram.getComments();
        if(!comments.containsKey(SIGNAL_STRENGTH_COMMENT_NAME)){
           throw new NoSuchElementException("chromatogram does not have signal strength comment"); 
        }
        return parseSignalStrength(comments.get(SIGNAL_STRENGTH_COMMENT_NAME));
    }
   

    private SignalStrength(int aSignal, int cSignal, int gSignal, int tSignal) {
        this.aSignal = aSignal;
        this.cSignal = cSignal;
        this.gSignal = gSignal;
        this.tSignal = tSignal;
    }

    /**
     * @return the aSignal
     */
    public int getASignal() {
        return aSignal;
    }

    /**
     * @return the cSignal
     */
    public int getCSignal() {
        return cSignal;
    }

    /**
     * @return the gSignal
     */
    public int getGSignal() {
        return gSignal;
    }

    /**
     * @return the tSignal
     */
    public int getTSignal() {
        return tSignal;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + aSignal;
        result = prime * result + cSignal;
        result = prime * result + gSignal;
        result = prime * result + tSignal;
        return result;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }
        SignalStrength other = (SignalStrength) obj;
        if (aSignal != other.aSignal){
            return false;
        }
        if (cSignal != other.cSignal){
            return false;
        }
        if (gSignal != other.gSignal){
            return false;
        }          
        if (tSignal != other.tSignal){
            return false;
        }
        return true;
    }

   
    
    
    
}
