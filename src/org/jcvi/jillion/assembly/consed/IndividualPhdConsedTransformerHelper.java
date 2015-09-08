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
package org.jcvi.jillion.assembly.consed;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.jcvi.jillion.assembly.consed.ace.PhdInfo;
import org.jcvi.jillion.assembly.consed.ace.WholeAssemblyAceTag;
import org.jcvi.jillion.assembly.consed.phd.Phd;
import org.jcvi.jillion.assembly.consed.phd.PhdBallWriter;
import org.jcvi.jillion.assembly.consed.phd.PhdDataStore;
import org.jcvi.jillion.assembly.consed.phd.PhdDirDataStore;
import org.jcvi.jillion.assembly.consed.phd.PhdWriter;
import org.jcvi.jillion.core.io.IOUtil;

class IndividualPhdConsedTransformerHelper implements PhdConsedTransformerHelper{

	private final File phdDir;
	
	IndividualPhdConsedTransformerHelper(File consedRootDir) throws IOException{		
		phdDir = new File(consedRootDir, "phd_dir");
		IOUtil.mkdirs(phdDir);
			
	}
	
	public PhdInfo writePhd(Phd phd, Date phdDate) throws IOException{
		String id = phd.getId();
		File phdFile = new File(phdDir, String.format("%s.phd.1", id));
		PhdInfo info = new PhdInfo(phd.getId(), phdFile.getName(), phdDate);
		PhdWriter writer =null;
		try{
			writer= new PhdBallWriter(phdFile);
			writer.write(phd);			
			return info;
		}finally{
			IOUtil.closeAndIgnoreErrors(writer);
		}
	}
	@Override
	public PhdDataStore createDataStore() throws IOException {
		return new PhdDirDataStore(phdDir);
	}
	@Override
	public WholeAssemblyAceTag createPhdBallWholeAssemblyTag() {
		return null;
	}
	
	
}
