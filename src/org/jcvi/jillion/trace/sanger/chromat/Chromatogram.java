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
 * Created on Oct 3, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sanger.chromat;

import java.util.Map;

import org.jcvi.jillion.trace.sanger.SangerTrace;
/**
 * {@code Chromatogram} is an interface
 * for SangerTrace objects that also contains
 * {@link ChannelGroup} data and optional
 * comments generated from the sequencing machine.
 * @author dkatzel
 *
 *
 */
public interface Chromatogram extends SangerTrace{
   
    /**
     * Get the {@link ChannelGroup} of this Chroamtogram.
     * @return a ChannelGroup, never null.
     */
    ChannelGroup getChannelGroup();
    /**
     * Get the key-value pair comments associated with this chromatogram.
     * @return a Map of comments by key, may be empty but will
     * never be null.
     */
    Map<String,String> getComments();

}
