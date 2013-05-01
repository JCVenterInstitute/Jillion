package org.jcvi.jillion.assembly.consed.phd;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

final class DefaultPhdDataStore{

	

	public static PhdDataStore create(File phdBall, DataStoreFilter filter) throws IOException{
		PhdBallParser parser = PhdBallParser.create(phdBall);
		
		DefaultPhdDataStoreBuilderVisitor visitor = new DefaultPhdDataStoreBuilderVisitor(filter);
		parser.accept(visitor);
		return visitor.build();
	}
	public static PhdDataStore create(InputStream inputStream, DataStoreFilter filter) throws IOException {
		PhdBallParser parser = PhdBallParser.create(inputStream);
		
		DefaultPhdDataStoreBuilderVisitor visitor = new DefaultPhdDataStoreBuilderVisitor(filter);
		parser.accept(visitor);
		return visitor.build();
	}
	
	public static class DefaultPhdDataStoreBuilderVisitor implements PhdBallVisitor {
		
		private final DataStoreFilter filter;
		
		private final Map<String, Phd> phds = new LinkedHashMap<String, Phd>();
		
		public DefaultPhdDataStoreBuilderVisitor(DataStoreFilter filter) {
			this.filter = filter;
		}

		@Override
		public void visitFileComment(String comment) {
			// TODO Auto-generated method stub
		
		}
		
		@Override
		public PhdVisitor visitPhd(PhdBallVisitorCallback callback, String id,
				Integer version) {
			return handlePhd(id, version);
		}
		
		private PhdVisitor handlePhd(String id, Integer version){
			if(!filter.accept(id)){
				return null;
			}
			return new AbstractPhdVisitor(id, version) {
				
				@Override
				protected void visitPhd(String id, Integer version,
						NucleotideSequence basescalls, QualitySequence qualities,
						PositionSequence positions, Map<String, String> comments,
						List<PhdWholeReadItem> wholeReadItems, List<PhdReadTag> readTags) {
					phds.put(id, new DefaultPhd(id, basescalls, qualities, positions,comments,wholeReadItems,readTags));
					
				}
			};
		}
		
		@Override
		public void visitEnd() {
			// TODO Auto-generated method stub
		
		}
		
		@Override
		public void halted() {
			// TODO Auto-generated method stub
		
		}
		
		public PhdDataStore build(){
			return DataStoreUtil.adapt(PhdDataStore.class, phds);
		}
		
		}


	
}
