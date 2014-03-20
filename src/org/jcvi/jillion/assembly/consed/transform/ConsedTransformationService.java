package org.jcvi.jillion.assembly.consed.transform;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.assembly.AssemblyTransformationService;
import org.jcvi.jillion.assembly.AssemblyTransformer;
import org.jcvi.jillion.assembly.consed.ConsedUtil;
import org.jcvi.jillion.assembly.consed.phd.PhdDataStore;

public class ConsedTransformationService implements AssemblyTransformationService{

	File consedDir;
	File aceFile;
	private final PhdDataStore phdDataStore;
	
	private ConsedTransformationService(Builder builder) throws IOException{
		consedDir = builder.consedDir;
		if(builder.includeQualities){
			
			phdDataStore = ConsedUtil.createPhdDataStoreFor(consedDir);
		}else{
			phdDataStore = null;
		}
		File editDir = ConsedUtil.getEditDirFor(consedDir);
		if(builder.aceVersion ==null){
			//get latest ace			
			aceFile = ConsedUtil.getLatestAceFile(editDir, builder.acePrefix);
		}else{
			aceFile = ConsedUtil.getAceFile(editDir, builder.acePrefix, builder.aceVersion.intValue());
		}
		if(aceFile == null){
			throw new IOException("specified ace file not found in edit_dir : " + editDir.getAbsolutePath());
		}
	}
	
	@Override
	public void transform(AssemblyTransformer transformer) throws IOException {
		if(transformer ==null){
			throw new NullPointerException("transformer can not be null");
		}
		
	}

	public static class Builder{
		private final File consedDir;
		private final File editDir;
		private final String acePrefix;
		
		private Integer aceVersion =null;
		private boolean includeQualities = false;
		
		
		public Builder(File consedDir, String acePrefix) {
			if(!consedDir.exists()){
				throw new IllegalArgumentException("consed directory does not exist : "+ consedDir.getAbsolutePath());
			}
			if(acePrefix == null){
				throw new NullPointerException("ace prefix can not be null");
			}
			editDir = ConsedUtil.getEditDirFor(consedDir);
			if(!editDir.exists()){
				throw new IllegalStateException("edit_dir does not exist : " + consedDir.getAbsolutePath());
			}
			this.consedDir = consedDir;
			this.acePrefix = acePrefix;
		}
		
		public Builder includeQualities(boolean includeQualities){
			this.includeQualities = includeQualities;
			return this;
		}
		
		public Builder useAceVersion(int version){			
			File ace = ConsedUtil.getAceFile(editDir, acePrefix, version);
			if(ace ==null){
				throw new IllegalArgumentException("ace file with version " + version + " does not exist in " + editDir.getAbsolutePath());
			}
			this.aceVersion = version;
			return this;
		}
		
		public ConsedTransformationService build() throws IOException{
			return new ConsedTransformationService(this);
		}
		
		
	}



}
