/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion_experimental.ncbi.submit.assemblyArchive;

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
