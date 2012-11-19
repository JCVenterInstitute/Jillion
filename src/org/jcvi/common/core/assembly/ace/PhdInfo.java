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
 * Created on Feb 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.ace;

import java.util.Date;

/**
 * {@code PhdInfo} is a value class
 * that contains information
 * to link an ace read
 * to a record in the corresponding
 * phd file.
 * @author dkatzel
 */
public interface PhdInfo{
	/**
	 * Get the name of the trace
	 * in the phd file.  This is usually
	 * the read id or a variation
	 * of the read id.
	 * @return a String;
	 * never null.
	 */
    String getTraceName();
    /**
     * Get the name of the phd file
     * that contains information
     * for this read.
     * @return a String; never null.
     */
    String getPhdName();
    /**
     * Get the {@link Date}
     * that this phd file was
     * last modified.
     * @return a {@link Date};
     * never null.The implementations
     * of method
     * might return new (but equal) 
     * Date instances
     * each time it is called.
     */
    Date getPhdDate();
}
