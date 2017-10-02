/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.residue.nt;

import static org.junit.Assert.assertSame;

import org.junit.Test;
public class TestNucleotideGetBasesFor {

	@Test
	public void mirrorsGetAmbiguityFor(){
		for(Nucleotide n :Nucleotide.getDnaValues()){
			assertSame(n, Nucleotide.getAmbiguityFor(n.getBasesFor()));
		}
	}
}
