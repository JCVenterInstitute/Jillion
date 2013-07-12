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
