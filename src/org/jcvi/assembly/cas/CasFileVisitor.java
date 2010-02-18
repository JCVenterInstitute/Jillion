/*
 * Created on Oct 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas;

import org.jcvi.assembly.cas.alignment.score.CasScoringScheme;
import org.jcvi.io.FileVisitor;

public interface CasFileVisitor extends FileVisitor{

    void visitMatch(CasMatch match);
    
    void visitAssemblyProgramInfo(String name, String version, String parameters);
    
    void visitMetaData(long numberOfContigSequences, long numberOfReads);
    
    void visitNumberOfReadFiles(long numberOfReadFiles);
    
    void visitNumberOfContigFiles(long numberOfContigFiles);
    
    void visitContigFileInfo(CasFileInfo contigFileInfo);
    
    void visitReadFileInfo(CasFileInfo readFileInfo);
    
    void visitScoringScheme(CasScoringScheme scheme);
    
    void visitContigDescription(CasContigDescription description);
    
    void visitContigPair(CasContigPair contigPair);
    
}
