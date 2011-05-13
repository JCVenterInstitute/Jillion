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
 * Created on Nov 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;


public abstract class AbstractSffFileProcessor implements SffFileVisitor{

    private final SffFileVisitor parent;
    /**
     * @param parent
     */
    public AbstractSffFileProcessor(SffFileVisitor parent) {
        
        this.parent = parent;
    }

    @Override
    public boolean visitCommonHeader(SFFCommonHeader commonHeader) {
        if(parent !=null){
            return parent.visitCommonHeader(commonHeader);
        }
        return true;
    }

    

    public SffFileVisitor getParent() {
        return parent;
    }

    @Override
    public void visitEndOfFile() {
        if(parent !=null){
            parent.visitEndOfFile();
        }
    }

    @Override
    public void visitFile() {
        if(parent !=null){
            parent.visitFile();
        }
        
    }

    @Override
    public boolean visitReadData(SFFReadData readData) {
        if(parent !=null){
            return parent.visitReadData(readData);
        }
        return true;
    }

    @Override
    public boolean visitReadHeader(SFFReadHeader readHeader) {
        if(parent !=null){
            parent.visitReadHeader(readHeader);
        }
        return true;
    }

}
