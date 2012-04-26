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

package org.jcvi.common.internal.command.grid;

import java.util.HashMap;
import java.util.Map;

/**
 * The JCVI Grid has several different "queues" which allow
 * jobs to run for limited periods of time.  Queues for faster
 * running jobs have higher scheduling priority.
 *
 * @author dkatzel
 */
public enum JcviQueue {
    

    /**
     * Jobs running on the fast queue can only run for 1 hour
     * before they are killed.  However, the fast queue
     * has the highest scheduling priority.
     */
    FAST("fast"),
    /**
     * Jobs running on the medium queue can run for 12 hours
     * before they are killed.
     */
    MEDIUM("medium"),
    /**
     * Jobs running on the default queue will never
     * be killed because it is taking too long, however
     * the default queue has the lowest scheduling priority.
     */
    DEFAULT("default"),
    /**
     * This queue is intended for high memory jobs
     * that will complete under 8 hours.  As of 
     * 2011, this queue has 64 CPUs and 128GB.
     * Any jobs running for longer than 8 hours on the himem queue 
     * will be killed.
     */
    HI_MEM("himem");

    private static final Map<String, JcviQueue> MAP;
    static{
    	MAP = new HashMap<String, JcviQueue>();
    	for(JcviQueue q : values()){
    		MAP.put(q.getQueueName(),q);
    	}
    }
    private final String queueName;

    /**
     * @param queueName
     */
    private JcviQueue(String queueName) {
        this.queueName = queueName;
    }

    public String getQueueName() {
        return queueName;
    }
    
    public static JcviQueue getQueueFor(String queueName){
    	if(MAP.containsKey(queueName)){
    		return MAP.get(queueName);
    	}
    	throw new IllegalArgumentException("unknown queue name : "+ queueName);
    }
}