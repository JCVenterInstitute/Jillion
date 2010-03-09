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

package org.jcvi.assembly.tasm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.jcvi.Range;
import org.jcvi.sequence.SequenceDirection;

/**
 * @author dkatzel
 *
 *
 */
public class TigrAssemblyFilePrinter implements TigrAssemblyFileVisitor{

    private final PrintWriter writer;
    public TigrAssemblyFilePrinter(){
        this.writer = new PrintWriter(System.out,true);
    }
    
    /**
     * @param writer
     */
    public TigrAssemblyFilePrinter(PrintWriter writer) {
        this.writer = writer;
    }

    @Override
    public void visitContigAttribute(String key, String value) {
        writer.printf("Contig attr[ %s : %s ]%n", key,value);
        
    }

    @Override
    public void visitReadAttribute(String key, String value) {
        writer.printf("\t\tRead attr[ %s : %s ]%n", key,value);
        
    }

    @Override
    public void visitConsensusBasecallsLine(String consensus) {
        writer.printf("Contig consensus = %s%n", consensus);
        
    }

    @Override
    public void visitNewContig(String contigId) {
        writer.printf("Contig Id = %s%n", contigId);
        
    }

    @Override
    public void visitNewRead(String readId, int offset, Range validRange,
            SequenceDirection dir) {
        if(readId.equals("SBPQA03T48E02PA1950R")){
            System.out.println("here");
        }
        writer.printf("\tRead %s start = %d validRange = %s dir = %s%n", readId, offset,validRange,dir);
        
    }

    @Override
    public void visitReadBasecallsLine(String lineOfBasecalls) {
        writer.printf("\t%s%n", lineOfBasecalls);
        
        
    }

    @Override
    public void visitLine(String line) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visitEndOfFile() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visitFile() {
        // TODO Auto-generated method stub
        
    }

    public static void main(String[] args) throws FileNotFoundException{
        File tasmFile = new File("/home/dkatzel/.eclipse/JCVI-JavaCommon/src/org/jcvi/assembly/tasm/30542-upload.tasm");
        TigrAssemblyFileVisitor visitor = new TigrAssemblyFilePrinter();
        TigrAssemblyFileParser.parse(tasmFile, visitor);
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitBeginContigBlock() {
        writer.println("{");
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitBeginReadBlock() {
        writer.println("\t{");
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitEndContigBlock() {
        writer.println("}");
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitEndReadBlock() {
        writer.println("\t}");
        
    }
}
