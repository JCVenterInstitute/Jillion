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

package org.jcvi.command.grid;

import org.ggf.drmaa.DrmaaException;
import org.ggf.drmaa.Session;
import org.jcvi.command.Command;
/**
 * {@code JcviGridJob} is a JCVI Grid specific {@link GridJob}.
 * @author dkatzel
 *
 *
 */
public class JcviGridJob extends GridJob{
    /**
     * JCVI Grid has several different "queues" which allow
     * jobs to run for limited periods of time.  Queues for faster
     * running jobs have higher scheduling priority.
     * @author dkatzel
     *
     *
     */
    public enum Queue{
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
        DEFAULT("default");
        
        private final String queueName;

        /**
         * @param queueName
         */
        private Queue(String queueName) {
            this.queueName = queueName;
        }

        public String getQueueName() {
            return queueName;
        }
        
        
    }
    private Queue queue;
    /**
     * @param copy
     * @throws DrmaaException
     */
    public JcviGridJob(GridJob copy) throws DrmaaException {
        super(copy);
    }

    /**
     * @param session
     * @param remoteCommand
     * @param projectCode
     * @throws DrmaaException
     */
    public JcviGridJob(Session session, Command remoteCommand,
            String projectCode) throws DrmaaException {
        super(session, remoteCommand, projectCode);
    }

    public void queue(Queue queue){
        this.queue = queue;
    }
    @Override
    protected void finishJobTemplate() throws DrmaaException {
        if(queue !=null){
            this.setNativeSpec(String.format("-l %s", queue.getQueueName()));
        }
        super.finishJobTemplate();
    }
    
    
}

