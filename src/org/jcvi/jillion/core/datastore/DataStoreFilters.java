package org.jcvi.jillion.core.datastore;

import java.util.Collection;
/**
 * {@code DataStoreFilters} is a utility class
 * that contains factory methods to create several
 * different types of {@link DataStoreFilter}
 * objects.
 * @author dkatzel
 *
 */
public final class DataStoreFilters {

	private DataStoreFilters(){
		//can not instantiate
	}
	/**
	 * Get a {@link DataStoreFilter} instance
	 * whose {@link DataStoreFilter#accept(String)}
	 * always returns {@code true}.
	 * @return a {@link DataStoreFilter} instance;
	 * never null.  May return the same instance
	 * multiple times.
	 */
	public static DataStoreFilter alwaysAccept(){
		return DataStoreFilterSingletons.ALWAYS_ACCEPT;
	}
	/**
	 * Get a {@link DataStoreFilter} instance
	 * whose {@link DataStoreFilter#accept(String)}
	 * always returns {@code false}.
	 * @return a {@link DataStoreFilter} instance;
	 * never null.  May return the same instance
	 * multiple times.
	 */
	public static DataStoreFilter neverAccept(){
		return DataStoreFilterSingletons.NEVER_ACCEPT;
	}
	/**
	 * Create a new {@link DataStoreFilter} instance
	 * whose {@link DataStoreFilter#accept(String)}
	 * returns {@code true}
	 * if and only if the id is contained
	 * in the given collection of ids 
	 * (and {@code false} otherwise).
	 * 
	 * @param ids a collection of ids that should 
	 * be accepted by the new {@link DataStoreFilter};
	 * can not be null.
	 * @return a new instance; never null.
	 * @throws NullPointerException if ids is null.
	 */
	public static DataStoreFilter newIncludeFilter(Collection<String> ids){
		if(ids==null){
			throw new NullPointerException("ids can not be null");
		}
		return new IncludeDataStoreFilter(ids);
	}
	/**
	 * Create a new {@link DataStoreFilter} instance
	 * whose {@link DataStoreFilter#accept(String)}
	 * returns {@code false}
	 * if and only if the id is contained
	 * in the given collection of ids
	 * (and {@code true} otherwise).
	 * @param ids a collection of ids that should 
	 * be accepted by the new {@link DataStoreFilter};
	 * can not be null.
	 * @return a new instance; never null.
	 * @throws NullPointerException if ids is null.
	 */
	public static DataStoreFilter newExcludeFilter(Collection<String> ids){
		if(ids==null){
			throw new NullPointerException("ids can not be null");
		}
		return new ExcludeDataStoreFilter(ids);
	}
	/**
	 * Create a new {@link DataStoreFilter}
	 * that wraps the given {@link DataStoreFilter}
	 * and returns the opposite value
	 * of the wrapped {@link DataStoreFilter}.
	 * @param filter the {@link DataStoreFilter} to invert;
	 * can not be null.
	 * @return a new {@link DataStoreFilter}
	 * that returns {@code true} if the wrapped
	 * filter's {@link DataStoreFilter#accept(String)}
	 * returns {@code false} and vice versa.
	 */
	public static DataStoreFilter invertFilter(DataStoreFilter filter){
		if(filter==null){
			throw new NullPointerException("filter can not be null");
		}
		return new InverseDataStoreFilter(filter);
	}
	
	private static final class IncludeDataStoreFilter implements DataStoreFilter{

	    private final Collection<String> ids;

	    /**
	     * Create a DataStoreFilters.newIncludeFilter.
	     * @param ids this list of ids that should be accepted
	     * by this filter.
	     */
	    public IncludeDataStoreFilter(Collection<String> ids) {
	        this.ids = ids;
	    }

	    @Override
	    public boolean accept(String id) {
	        return ids.contains(id);
	    }

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ids.hashCode();
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof IncludeDataStoreFilter)) {
				return false;
			}
			IncludeDataStoreFilter other = (IncludeDataStoreFilter) obj;
			if (!ids.equals(other.ids)) {
				return false;
			}
			return true;
		}
	    
	    
	}
	
	private static final class ExcludeDataStoreFilter implements DataStoreFilter{

	    private final Collection<String> ids;

	    /**
	     * Create a DataStoreFilters.newIncludeFilter.
	     * @param ids this list of ids that should be accepted
	     * by this filter.
	     */
	    public ExcludeDataStoreFilter(Collection<String> ids) {
	        this.ids = ids;
	    }

	    @Override
	    public boolean accept(String id) {
	        return !ids.contains(id);
	    }

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ids.hashCode();
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof ExcludeDataStoreFilter)) {
				return false;
			}
			ExcludeDataStoreFilter other = (ExcludeDataStoreFilter) obj;
			if (!ids.equals(other.ids)) {
				return false;
			}
			return true;
		}
	    
	    
	}
	
	private static final class InverseDataStoreFilter implements DataStoreFilter{

	    private final DataStoreFilter filter;
	    
	    
	    /**
	     * Create a new InverseDataStoreFilter which returns
	     * the oppoiste answers as the given filter.
	     * @param filter the fitler to invert.
	     */
	    public  InverseDataStoreFilter(DataStoreFilter filter) {
	        this.filter = filter;
	    }


	    /**
	     * Accepts the opposite of what the wrapped
	     * filter would have accepted.
	     * @return {@code true} if the wrapped filter
	     * would have returned {@code false}; {@code false} if the wrapped filter
	     * would have returned {@code true}.
	     */
	    @Override
	    public boolean accept(String id) {
	        return !filter.accept(id);
	    }
	}
	
	private enum DataStoreFilterSingletons implements DataStoreFilter{

	    ALWAYS_ACCEPT{
	    	/**
	 	    * Every id is always accepted.
	 	    * @return {@code true}.
	 	    */
	 	    @Override
	 	    public boolean accept(String id) {
	 	        return true;
	 	    }
	    },

	    NEVER_ACCEPT{
	    	/**
	 	    * No id is ever accepted.
	 	    * @return {@code false}.
	 	    */
	 	    @Override
	 	    public boolean accept(String id) {
	 	        return false;
	 	    }
	    },
	    ;
	}

	    
}
