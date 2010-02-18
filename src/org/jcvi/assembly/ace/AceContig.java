/*
 * Created on Dec 7, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import java.util.List;

import org.jcvi.assembly.Contig;

public interface AceContig extends Contig<AcePlacedRead>{

    AceContig without(List<AcePlacedRead> reads);
}
