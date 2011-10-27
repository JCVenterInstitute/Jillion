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

package org.jcvi.experimental.primerDesign2;

/**
 * @author dkatzel
 *
 *
 */
public class Config {

    public enum PrimerDesign{
        /**
         * The minimum depth that any of the target feature region(s)
         * must be covered by the set of successfully designed primer pairs
         * if the expanding coverage primer design algorithm is used,
         * this value equals the number of primer pairs designed
         * for the target region(s).
         */
        MIN_COVERAGE_DEPTH("coverageDepth"),
        /**
         * Identifies regions where primers are not allowed to land.
         */
        AVOID_REGIONS_FILE_PATH("avoidRegionsFile"),
        
        FORWARD_ADAPTER_SEQUENCE("forwardAdapterSequence"),
        REVERSE_ADAPTER_SEQUENCE("reverseAdapterSequence"),
        
        
        ;
        
        private final String configText;

        /**
         * @param configText
         */
        private PrimerDesign(String configText) {
            this.configText = configText;
        }

        public String getConfigText() {
            return configText;
        }
        
    }
}
