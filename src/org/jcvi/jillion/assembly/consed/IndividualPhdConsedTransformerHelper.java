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
