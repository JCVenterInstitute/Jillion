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

package org.jcvi.common.core.align.blast;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.DirectedRange;
import org.jcvi.common.core.assembly.scaffold.DefaultScaffold;
import org.jcvi.common.core.assembly.scaffold.Scaffold;
import org.jcvi.common.core.util.Builder;

/**
 * @author dkatzel
 *
 *
 */
public class BlastScaffoldBuilder implements BlastVisitor, Builder<Scaffold>{
    
    
    public static Scaffold createFromTabularBlastOutput(File tabularBlast, String referenceId) throws IOException{
        BlastScaffoldBuilder builder = new BlastScaffoldBuilder(referenceId);
        TabularBlastParser.parse(tabularBlast, builder);
        return builder.build();
    }
    public static Scaffold createFromXmlBlastOutput(File xmlBlast, String referenceId){
        BlastScaffoldBuilder builder = new BlastScaffoldBuilder(referenceId);
        XmlBlastParser.parse(xmlBlast, builder);
        return builder.build();
    }
    private final DefaultScaffold.Builder scaffoldBuilder;
    private final String subjectId;

    private BlastScaffoldBuilder(String subjectId) {
        this.subjectId = subjectId;
        this.scaffoldBuilder = new DefaultScaffold.Builder(subjectId);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitLine(String line) {
        // TODO Auto-generated method stub
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitFile() {
        // TODO Auto-generated method stub
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitEndOfFile() {
        // TODO Auto-generated method stub
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitHsp(Hsp blastHit) {
        if(subjectId.equals(blastHit.getSubjectId())){
            DirectedRange directedRange = blastHit.getSubjectRange();
            String uniqueId = String.format("%s_%d_%d",blastHit.getQueryId(), 
                    directedRange.getStart()+1,
                    directedRange.getEnd()+1);
            scaffoldBuilder.add(uniqueId, directedRange.asRange(), directedRange.getDirection());
        }
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Scaffold build() {
        return scaffoldBuilder.build();
    }
}
