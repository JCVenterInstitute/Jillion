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
 * Created on Nov 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.pyro.sff;

public abstract class AbstractSffFileVisitor implements SffFileVisitor {

    @Override
    public boolean visitCommonHeader(SFFCommonHeader commonHeader) {
        return true;
    }

    @Override
    public boolean visitReadData(SFFReadData readData) {
        return true;
    }

    @Override
    public boolean visitReadHeader(SFFReadHeader readHeader) {
        return true;
    }

    @Override
    public void visitEndOfFile() {

    }

    @Override
    public void visitFile() {

    }

}
