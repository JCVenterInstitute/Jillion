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
package org.jcvi.jillion.internal.trace.chromat.abi.tag.rate;

/**
 * @author dkatzel
 *
 *
 */
public final class ScanRateUtils {
    
    private static final float ONE_THOUSAND = 1000F;
    
    private ScanRateUtils(){
    	//can not instantiate
    }
    /**
     * Get the Sampling Rate (Hz) that is displayed in the
     * Seq Analysis annotation report.
     * @param scanRate the scan rate to compute; can not be null.
     * @return the sampling rate for the given scan rate.
     * @throws NullPointerException if scanRate is null.
     */
    public static float getSamplingRateFor(ScanRate scanRate){
        return ONE_THOUSAND/scanRate.getScanPeriod();
    }
}
