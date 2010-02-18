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
 * Created on Oct 28, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas;

import java.io.PrintStream;

import org.jcvi.assembly.cas.alignment.score.CasScoringScheme;

public class CasFileTrace implements CasFileVisitor{

    private final PrintStream out;
    public CasFileTrace(){
        this(System.out);
    }
    public CasFileTrace(PrintStream out){
        this.out = out;
    }
    @Override
    public void visitAssemblyProgramInfo(String name, String version,
            String parameters) {
       out.println(
               String.format("assembly program info:{%n\tname = %s%n\t version = %s%n\t params = %s%n}",
                       name, version,parameters));
        
    }

    @Override
    public void visitContigDescription(CasContigDescription description) {
        out.println("contig description : " + description);
        
    }

    @Override
    public void visitContigFileInfo(CasFileInfo contigFileInfo) {
        out.println("contig file info: "+ contigFileInfo);
        
    }

    @Override
    public void visitContigPair(CasContigPair contigPair) {
        out.println("contig pair: "+ contigPair);
        
    }

    @Override
    public void visitMatch(CasMatch match) {
        out.println(String.format("visiting match : {%n\t%s%n}",match));
        
    }

    @Override
    public void visitMetaData(long numberOfContigSequences, long numberOfReads) {
        out.println("numberOfcontigSequences = "+ numberOfContigSequences);
        out.println("numberOfReads = "+ numberOfReads);
        
    }

    @Override
    public void visitReadFileInfo(CasFileInfo readFileInfo) {
        out.println("read file info: "+ readFileInfo);
        
    }

    @Override
    public void visitScoringScheme(CasScoringScheme scheme) {
        out.println("scoring scheme" + scheme);
        
    }

    @Override
    public void visitEndOfFile() {
        out.println("end file");
        
    }

    @Override
    public void visitNumberOfContigFiles(long numberOfContigFiles) {
        out.println("# contig info files ="+ numberOfContigFiles);
        
    }

    @Override
    public void visitNumberOfReadFiles(long numberOfReadFiles) {
        out.println("# read info files ="+ numberOfReadFiles);
        
    }

    @Override
    public void visitFile() {
       out.println("beginning file");
        
    }

}
