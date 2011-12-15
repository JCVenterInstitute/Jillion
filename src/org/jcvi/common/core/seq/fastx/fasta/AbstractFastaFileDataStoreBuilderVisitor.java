package org.jcvi.common.core.seq.fastx.fasta;
import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.seq.fastx.FastXFilter;
import org.jcvi.common.core.seq.fastx.NullFastXFilter;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.Symbol;

public abstract class AbstractFastaFileDataStoreBuilderVisitor<S extends Symbol, T extends Sequence<S>, F extends FastaRecord<S, T>, D extends DataStore<F>> implements FastaFileDataStoreBuilderVisitor<S,T,F,D>{
	private final FastaDataStoreBuilder<S,T,F,D> builder;
	private final FastXFilter filter;
	public AbstractFastaFileDataStoreBuilderVisitor(FastaDataStoreBuilder<S,T,F,D> builder){
		this(builder,null);
	}
	public AbstractFastaFileDataStoreBuilderVisitor(FastaDataStoreBuilder<S,T,F,D> builder, FastXFilter filter){
		if(builder==null){
			throw new NullPointerException("builder can not be null");
		}
		if(filter ==null){
			this.filter = NullFastXFilter.INSTANCE;
		}else{
			this.filter = filter;
		}
		this.builder = builder;
		
	}
	
	@Override
	public D build() {
		return builder.build();
	}
	
	/**
	 * no-op.
	 */
	@Override
	public void visitLine(String line) {
		//no-op
	}
	/**
	 * no-op.
	 */
	@Override
	public void visitFile() {
		//no-op
		
	}
	/**
	 * no-op.
	 */
	@Override
	public void visitEndOfFile() {
		//no-op			
	}
	/**
	 * no-op.
	 * @return true.
	 */
	@Override
	public boolean visitDefline(String defline) {
		return true;
	}
	/**
	 * no-op.
	 * @return true.
	 */
	@Override
	public boolean visitBodyLine(String bodyLine) {
		return true;
	}
	/**
	 * Adds the current record to this datastore by 
	 * delegating the parameters to {@link #createFastaRecord(String, String, String)}
	 * and inputing the resulting {@link FastaRecord} to
	 * {@link #addFastaRecord(FastaRecord)}.
	 * <p/>
	 * {@inheritDoc}
	 */
	@Override
	public boolean visitRecord(String id, String comment, String entireBody) {
		addFastaRecord(createFastaRecord(id, comment, entireBody));
		return true;
	}
	
	protected abstract F createFastaRecord(String id, String comment, String entireBody);

	@Override
	public FastaDataStoreBuilder<S, T, F, D> addFastaRecord(F fastaRecord) {
		if(filter.accept(fastaRecord.getId())){
			builder.addFastaRecord(fastaRecord);
		}
		return this;
	}
	/**
	 * @return true.
	 */
	@Override
	public boolean supportsAddFastaRecord() {
		return true;
	}
	
}