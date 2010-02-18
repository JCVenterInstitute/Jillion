/*
 * Created on Oct 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas;

import java.util.List;

import org.jcvi.assembly.cas.alignment.score.CasScoringScheme;

public interface CasFile {

    int getNumberOfContigSequences();
    int getNumberOfReads();
    String getNameOfAssemblyProgram();
    String getVersionOfAssemblyProgram();
    String getParametersOfAssemblyProgram();
    List<CasFileInfo> getContigFileInfos();
    List<CasFileInfo> getReadFileInfos();
    CasScoringScheme getScoringScheme();
    List<CasContigDescription> getContigDescriptions();
    List<CasContigPair> getContigPairs();
}
