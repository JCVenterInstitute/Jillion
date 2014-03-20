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
final class DefaultScanRate implements ScanRate{

    private final int time, period, line;
    
    
    /**
     * @param time
     * @param period
     * @param line
     */
    private DefaultScanRate(int time, int period, int line) {
        this.time = time;
        this.period = period;
        this.line = line;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int getTime() {
        return time;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int getScanPeriod() {
        return period;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int getFirstScanLine() {
        return line;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + line;
        result = prime * result + period;
        result = prime * result + time;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ScanRate)) {
            return false;
        }
        ScanRate other = (ScanRate) obj;
        if (line != other.getFirstScanLine()) {
            return false;
        }
        if (period != other.getScanPeriod()) {
            return false;
        }
        if (time != other.getTime()) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DefaultScanRate [time=" + time + ", period=" + period
                + ", line=" + line + "]";
    }

    public static final class Builder implements org.jcvi.jillion.core.util.Builder<DefaultScanRate>{
        private int time,line,period;
        
        public Builder time(int time){
            this.time = time;
            return this;
        }
        public Builder firstScanLine(int line){
            this.line = line;
            return this;
        }
        public Builder period(int period){
            this.period = period;
            return this;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public DefaultScanRate build() {
            return new DefaultScanRate(time, period, line);
        }
        
    }
}
