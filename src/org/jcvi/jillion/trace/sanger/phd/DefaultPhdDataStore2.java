package org.jcvi.jillion.trace.sanger.phd;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.trace.sanger.PositionSequence;

final class DefaultPhdDataStore2{

	

	public static PhdDataStore create(File phdBall, DataStoreFilter filter) throws IOException{
		PhdBallParser parser = PhdBallParser.create(phdBall);
		
		DefaultPhdDataStoreBuilderVisitor visitor = new DefaultPhdDataStoreBuilderVisitor(filter);
		parser.accept(visitor);
		return visitor.build();
	}
	
	
	public static class DefaultPhdDataStoreBuilderVisitor implements PhdBallVisitor2 {
		
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
		public PhdVisitor2 visitPhd(PhdBallVisitorCallback callback, String id) {
			return handlePhd(id);
		}
		
		@Override
		public PhdVisitor2 visitPhd(PhdBallVisitorCallback callback, String id,
				int version) {
			return handlePhd(id);
		}
		
		private PhdVisitor2 handlePhd(String id){
			if(!filter.accept(id)){
				return null;
			}
			return new AbstractPhdVisitor2(id) {
				
				@Override
				protected void visitPhd(String id, Integer version,
						NucleotideSequence basescalls, QualitySequence qualities,
						PositionSequence positions, Map<String, String> comments) {
					phds.put(id, new DefaultPhd(id, basescalls, qualities, positions,comments));
					
				}
			};
		}
		
		@Override
		public PhdWholeReadTagVisitor visitReadTag() {
			// TODO Auto-generated method stub
			return null;
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
