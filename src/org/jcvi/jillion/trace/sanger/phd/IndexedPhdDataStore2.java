package org.jcvi.jillion.trace.sanger.phd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStoreClosedException;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.MapUtil;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.datastore.DataStoreStreamingIterator;
import org.jcvi.jillion.trace.sanger.PositionSequence;
import org.jcvi.jillion.trace.sanger.phd.PhdBallVisitorCallback.PhdBallVisitorMemento;

final class IndexedPhdDataStore2 implements PhdDataStore{

	


	private volatile boolean closed=false;
	private final File phdFile;
	private final Map<String, PhdBallVisitorMemento> mementos;
	private final DataStoreFilter filter;
	private final PhdBallParser parser;
	
	public static PhdDataStore create(File phdBall, DataStoreFilter filter) throws FileNotFoundException, IOException{
		
		PhdBallParser parser = PhdBallParser.create(phdBall);
		
		BuilderVisitor visitor = new BuilderVisitor(parser, phdBall, filter);
		parser.accept(visitor);
		return visitor.build();
	}
	
	private IndexedPhdDataStore2(PhdBallParser parser,
			File phdFile,
			Map<String, PhdBallVisitorMemento> mementos,
			DataStoreFilter filter) {
		this.parser = parser;
		this.phdFile = phdFile;
		this.mementos = mementos;
		this.filter = filter;
	}

	private void verifyNotClosed(){
		if(closed){
			throw new DataStoreClosedException("already closed");
		}
	}
	@Override
	public StreamingIterator<String> idIterator() throws DataStoreException {
		return IteratorUtil.createStreamingIterator(mementos.keySet().iterator());
	}

	@Override
	public Phd get(String id) throws DataStoreException {
		verifyNotClosed();
		PhdBallVisitorMemento memento = mementos.get(id);
		//null memento means we don't have it
		if(memento ==null){
			return null;
		}
		SinglePhdVisitor visitor = new SinglePhdVisitor(id);
		try {
			parser.accept(visitor, memento);
			return visitor.phd;
		} catch (IOException e) {
			throw new DataStoreException("error re-parsing phd file for " + id, e);
		}

	}

	@Override
	public boolean contains(String id) throws DataStoreException {
		verifyNotClosed();
		return mementos.containsKey(id);
	}

	@Override
	public long getNumberOfRecords() throws DataStoreException {
		verifyNotClosed();
		return mementos.size();
	}

	@Override
	public boolean isClosed() {
		return closed;
	}

	@Override
	public StreamingIterator<Phd> iterator() throws DataStoreException {
		//use large iterator to re-parse entire file in one pass
		//instead of using out get(id) which requires a parse and seek
		//for each record
		return DataStoreStreamingIterator.create(this,
				PhdBallIterator2.createNewIterator(phdFile, filter));
	}

	@Override
	public void close() throws IOException {
		closed=true;		
	}
	
	
	private static final class BuilderVisitor implements PhdBallVisitor2{
		private static final int INITIAL_MAP_SIZE = MapUtil.computeMinHashMapSizeWithoutRehashing(8192);
		
		private final Map<String, PhdBallVisitorMemento> mementos = new LinkedHashMap<String, PhdBallVisitorCallback.PhdBallVisitorMemento>(INITIAL_MAP_SIZE);
		
		private final DataStoreFilter filter;
		
		private final File phdBall;
		
		private boolean visitedEntireFile=false;
		private final PhdBallParser parser;
		
		public BuilderVisitor(PhdBallParser parser, File phdBall, DataStoreFilter filter) {
			this.phdBall = phdBall;
			this.filter = filter;
			this.parser = parser;
		}

		@Override
		public void visitFileComment(String comment) {
			//ignore			
		}

		@Override
		public PhdVisitor2 visitPhd(PhdBallVisitorCallback callback, String id) {
			return handlePhd(callback, id);
		}

		@Override
		public PhdVisitor2 visitPhd(PhdBallVisitorCallback callback, String id,
				int version) {
			return handlePhd(callback, id);
		}
		
		private PhdVisitor2 handlePhd(PhdBallVisitorCallback callback, String id){
			if(filter.accept(id)){
				mementos.put(id, callback.createMemento());
			}
			return null;
		}

		@Override
		public PhdWholeReadTagVisitor visitReadTag() {
			//always ignore
			return null;
		}

		@Override
		public void visitEnd() {
			visitedEntireFile=true;
			
		}

		@Override
		public void halted() {
			//no-op			
		}
		
		public PhdDataStore build(){
			if(!visitedEntireFile){
				throw new IllegalStateException("did not visit entire file");
			}
			return new IndexedPhdDataStore2(parser,phdBall, mementos, filter);
		}
		
	}
	
	public static class SinglePhdVisitor extends AbstractPhdBallVisitor2{

		private Phd phd;
		private final String idWeWant;
		
		
		public SinglePhdVisitor(String idWeWant) {
			this.idWeWant = idWeWant;
		}

		@Override
		public PhdVisitor2 visitPhd(PhdBallVisitorCallback callback, String id) {
			return handlePhd(callback, id);
		}

		@Override
		public PhdVisitor2 visitPhd(PhdBallVisitorCallback callback, String id,
				int version) {
			return handlePhd(callback, id);
		}
		
		private PhdVisitor2 handlePhd(final PhdBallVisitorCallback callback, String id){
			if(phd !=null){
				throw new IllegalStateException("should only see one phd");
			}
			if(!idWeWant.equals(id)){
				throw new IllegalStateException("did not visit correct id: expected "+ idWeWant + " but was "+ id);
			}
			return new AbstractPhdVisitor2(id) {
				
				@Override
				protected void visitPhd(String id, Integer version,
						NucleotideSequence basecalls, QualitySequence qualities,
						PositionSequence positions, Map<String, String> comments) {
					SinglePhdVisitor.this.phd = new DefaultPhd(id, basecalls, qualities, positions, comments); 
					callback.haltParsing();
				}
			};
		}

	}

}
