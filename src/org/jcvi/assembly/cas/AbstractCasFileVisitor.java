/*
 * Created on Oct 30, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas;

import org.jcvi.assembly.cas.alignment.score.CasScoringScheme;

public abstract class AbstractCasFileVisitor implements CasFileVisitor {

    @Override
    public void visitAssemblyProgramInfo(String name, String version,
            String parameters) {

    }

    @Override
    public void visitContigDescription(CasContigDescription description) {}

    @Override
    public void visitContigFileInfo(CasFileInfo contigFileInfo) {}

    @Override
    public void visitContigPair(CasContigPair contigPair) {}

    @Override
    public void visitMatch(CasMatch match) {}

    @Override
    public void visitMetaData(long numberOfContigSequences, long numberOfReads) {}

    @Override
    public void visitNumberOfContigFiles(long numberOfContigFiles) {}

    @Override
    public void visitNumberOfReadFiles(long numberOfReadFiles) {}

    @Override
    public void visitReadFileInfo(CasFileInfo readFileInfo) {}

    @Override
    public void visitScoringScheme(CasScoringScheme scheme) {}

    @Override
    public void visitEndOfFile() {}

    @Override
    public void visitFile() {}

}
