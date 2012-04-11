package org.jcvi.common.core.seq.read.trace.pyro.sff;

import java.io.File;
import java.math.BigInteger;

public class RocheIndexedSffFileDataStore {

	public static class ManifestCreatorVisitor implements SffFileVisitor{

		private final File sffFile;
		
		
		private ManifestCreatorVisitor(File sffFile) {
			this.sffFile = sffFile;
		}

		@Override
		public void visitFile() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visitEndOfFile() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public CommonHeaderReturnCode visitCommonHeader(SffCommonHeader commonHeader) {
			BigInteger offsetToIndex =commonHeader.getIndexOffset();
			long indexLength =commonHeader.getIndexLength();
			//stop parsing file
			return CommonHeaderReturnCode.STOP;
		}

		@Override
		public ReadHeaderReturnCode visitReadHeader(SffReadHeader readHeader) {
	
			return ReadHeaderReturnCode.STOP;
		}

		@Override
		public ReadDataReturnCode visitReadData(SffReadData readData) {
			// TODO Auto-generated method stub
			return ReadDataReturnCode.STOP;
		}
		
	}
}
