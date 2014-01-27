package org.jcvi.jillion.assembly.consed;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.jcvi.jillion.assembly.consed.ace.PhdInfo;
import org.jcvi.jillion.assembly.consed.phd.Phd;
import org.jcvi.jillion.assembly.consed.phd.PhdBallWriter;
import org.jcvi.jillion.assembly.consed.phd.PhdDataStore;
import org.jcvi.jillion.assembly.consed.phd.PhdFileDataStoreBuilder;
import org.jcvi.jillion.assembly.consed.phd.PhdWriter;
import org.jcvi.jillion.core.io.IOUtil;

class PhdBallConsedTransformerHelper implements PhdConsedTransformerHelper{

	private final File phdDir, phdBallFile;	
	private final PhdWriter phdBallWriter;
	private final Date phdDate;
	
	PhdBallConsedTransformerHelper(File consedRootDir) throws IOException{
		this(consedRootDir, new Date());
	}
	PhdBallConsedTransformerHelper(File consedRootDir, Date phdDate) throws IOException{		
		if(phdDate ==null){
			throw new NullPointerException("phdDate can not be null");
		}
		
		this.phdDate = phdDate;
		phdDir = new File(consedRootDir, "phdball_dir");
		IOUtil.mkdirs(phdDir);
		phdBallFile = new File(phdDir, "phd.ball.1");
		phdBallWriter = new PhdBallWriter(phdBallFile);			
				
	}
	
	@Override
	public PhdInfo writePhd(Phd phd) throws IOException{
		PhdInfo info = new PhdInfo(phd.getId(), phdBallFile.getName(), phdDate);
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
	
	
}
