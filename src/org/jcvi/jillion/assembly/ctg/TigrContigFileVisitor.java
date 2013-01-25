package org.jcvi.jillion.assembly.ctg;

public interface TigrContigFileVisitor {

	public interface TigrContigVisitorCallback{
		/**
		 * {@code TigrContigVisitorMemento} is a marker
		 * interface that {@link TigrContigFileParser}
		 * instances can use to "rewind" back
		 * to the position in its contig file
		 * in order to revisit portions of the contig file. 
		 * {@link TigrContigVisitorMemento} should only be used
		 * by the {@link TigrContigFileParser} instance that
		 * generated it.
		 * @author dkatzel
		 *
		 */
		public interface TigrContigVisitorMemento{
			
		}
		/**
		 * Is this callback capable of
		 * creating {@link TigrContigVisitorMemento}s
		 * via {@link #createMemento()}.
		 * @return {@code true} if this callback
		 * can create mementos; {@code false} otherwise.
		 */
		boolean canCreateMemento();
		/**
		 * Create a {@link TigrContigVisitorMemento}
		 * 
		 * @return a {@link TigrContigVisitorMemento}; never null.
		 * @see #canCreateMemento()
		 * @throws UnsupportedOperationException if {@link #canCreateMemento()}
		 * returns {@code false}.
		 */
		TigrContigVisitorMemento createMemento();
		/**
		 * Tell the {@link TigrContigFileParser} to stop parsing
		 * the fastq file.  {@link TigrContigFileVisitor#visitEnd()}
		 * will still be called.
		 */
		void stopParsing();
	}
	
	
	TigrContigVisitor visitContig(TigrContigVisitorCallback callback, String contigId);
	
	void visitIncompleteEnd();
	
	void visitEnd();
}
