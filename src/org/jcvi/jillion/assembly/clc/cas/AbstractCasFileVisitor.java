/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.clc.cas;

/**
 * {@code AbstractCasFileVisitor} is a 
 * {@link CasFileVisitor} implementation that
 * implements each method with a default empty stub.
 * This allows subclasses to only override the methods
 * they care about without cluttering up the subclass with 
 * many empty methods.
 * @author dkatzel
 *
 */
public abstract class AbstractCasFileVisitor implements CasFileVisitor{

	@Override
	public void visitAssemblyProgramInfo(String name, String version,
			String parameters) {
		//no-op		
	}

	@Override
	public void visitMetaData(long numberOfReferenceSequences,
			long numberOfReads) {
		//no-op	
	}

	@Override
	public void visitNumberOfReadFiles(long numberOfReadFiles) {
		//no-op	
	}

	@Override
	public void visitNumberOfReferenceFiles(long numberOfReferenceFiles) {
		//no-op	
	}

	@Override
	public void visitReferenceFileInfo(CasFileInfo referenceFileInfo) {
		//no-op	
	}

	@Override
	public void visitReadFileInfo(CasFileInfo readFileInfo) {
		//no-op	
	}

	@Override
	public void visitScoringScheme(CasScoringScheme scheme) {
		//no-op	
	}

	@Override
	public void visitReferenceDescription(CasReferenceDescription description) {
		//no-op	
	}

	@Override
	public void visitContigPair(CasContigPair contigPair) {
		//no-op	
	}

	@Override
	public void visitEnd() {
		//no-op	
	}

	@Override
	public void halted() {
		//no-op	
	}

	/**
	 * Unless overridden, this method will
	 * return {@code null} to always skip the underlying matches.
	 * {@inheritDoc}
	 */
	@Override
	public CasMatchVisitor visitMatches(CasVisitorCallback callback) {
		// always skip
		return null;
	}

}
