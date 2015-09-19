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
package org.jcvi.jillion.experimental.ncbi.submit.assemblyArchive;

import java.io.File;
import java.io.IOException;

public class LocalProjectsAliasPrototype {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		File path = new File("/usr/local/projects/Elvira/submission/2013-05-09/");
		
		System.out.println(path.getAbsolutePath());
		System.out.println(path.getCanonicalPath());
		System.out.println(path.getPath());

		System.out.println("============");
		
		File path2 = new File("/local/projects6/Elvira/submission/2013-05-09");
		
		System.out.println(path2.getAbsolutePath());
		System.out.println(path2.getCanonicalPath());
		System.out.println(path2.getPath());
	}

}
