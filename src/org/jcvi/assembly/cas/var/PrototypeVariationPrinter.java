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

package org.jcvi.assembly.cas.var;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * @author dkatzel
 *
 *
 */
public class PrototypeVariationPrinter {

    /**
     * @param args
     * @throws FileNotFoundException 
     */
    public static void main(String[] args) throws FileNotFoundException {
        File varFile = new File("/usr/local/scratch/dkatzel/casSNP/vars.mixed.log");
        VariationLogFileVisitor visitor = new VariationLogFileVisitor() {
            
            @Override
            public void visitFile() {
                
            }
            
            @Override
            public void visitEndOfFile() {
                
            }
            
            @Override
            public void visitLine(String line) {
                
            }
            
            @Override
            public void visitVariation(Variation variation) {
               System.out.println("\t"+variation);
                
            }
            
            @Override
            public boolean visitContig(String id) {
                System.out.println(id);
                return true;
            }
        };
        
        VariationLogFileParser.parseVariationFile(varFile, visitor);

    }

}
