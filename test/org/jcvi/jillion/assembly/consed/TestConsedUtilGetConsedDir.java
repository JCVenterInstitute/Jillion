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
package org.jcvi.jillion.assembly.consed;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;
public class TestConsedUtilGetConsedDir {

	@Test
	public void getConsedDir(){
		File consedDir = new File("consed");
		File editDir = new File(consedDir, "edit_dir");
		File ace = new File(editDir, "foo.ace.1");
		
		assertEquals(consedDir, ConsedUtil.getConsedDirFor(ace));
	}
}
