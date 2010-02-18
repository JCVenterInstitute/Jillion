/*
 * Created on Dec 18, 2008
 *
 * @author dkatzel
 */
package org.jcvi.assembly.annot.ref.writer;

import java.io.IOException;
import java.util.List;

import org.jcvi.assembly.annot.ref.RefGene;

public interface RefGeneWriter {

    void write(List<RefGene> refGenes) throws IOException;
}
