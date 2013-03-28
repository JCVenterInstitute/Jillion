/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
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

	@Override
	public CasMatchVisitor visitMatches(CasVisitorCallback callback) {
		// always skip
		return null;
	}

}
