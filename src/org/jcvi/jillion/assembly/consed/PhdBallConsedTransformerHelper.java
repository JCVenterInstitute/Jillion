package org.jcvi.jillion.assembly.consed;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.jcvi.jillion.assembly.consed.ace.PhdInfo;
import org.jcvi.jillion.assembly.consed.ace.WholeAssemblyAceTag;
import org.jcvi.jillion.assembly.consed.phd.Phd;
import org.jcvi.jillion.assembly.consed.phd.PhdBallWriter;
import org.jcvi.jillion.assembly.consed.phd.PhdDataStore;
import org.jcvi.jillion.assembly.consed.phd.PhdFileDataStoreBuilder;
import org.jcvi.jillion.assembly.consed.phd.PhdWriter;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.DateUtil;

class PhdBallConsedTransformerHelper implements PhdConsedTransformerHelper{

	private final File phdDir, phdBallFile;	
	private final PhdWriter phdBallWriter;
	
	
	PhdBallConsedTransformerHelper(File consedRootDir) throws IOException{		

		phdDir = new File(consedRootDir, "phdball_dir");
		IOUtil.mkdirs(phdDir);
		phdBallFile = new File(phdDir, "phd.ball.1");
		phdBallWriter = new PhdBallWriter(phdBallFile);	
		
				
	}
	
	@Override
	public PhdInfo writePhd(Phd phd, Date phdDate) throws IOException{
		PhdInfo info = new PhdInfo(phd.getId(), 
				//file name has to be readId.phd.1 otherwise consed
				//can't find it
				//EVEN IF the read is in the phdball!
				String.format("%s.phd.1", phd.getId()), 
				phdDate);
		phdBallWriter.write(phd);
		return info;
	}

	@Override
	public PhdDataStore createDataStore() throws IOException {
		phdBallWriter.close();
		return  new PhdFileDataStoreBuilder(phdBallFile)
						//.hint(DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_MEMORY)
						.build();

	}
	@Override
	public WholeAssemblyAceTag createPhdBallWholeAssemblyTag() {
		return new WholeAssemblyAceTag("phdBall", "consed",
                DateUtil.getCurrentDate(), "../phdball_dir/"+phdBallFile.getName());
	}
	
	
}
