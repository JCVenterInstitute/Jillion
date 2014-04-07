/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.common.examples;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.fasta.FastaFileParser;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
import org.jcvi.jillion.fasta.FastaVisitor;
import org.jcvi.jillion.fasta.FastaVisitorCallback;
import org.jcvi.jillion.fasta.FastaVisitorCallback.FastaVisitorMemento;
import org.jcvi.jillion.fasta.FastaParser;

public class MementoVisiting {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		File fastaFile = new File("/path/to/fasta");
		
		FastaParser parser = FastaFileParser.create(fastaFile);
		
		Visitor visitor = new Visitor();
		
		parser.parse(visitor);
		
		FastaVisitorMemento memento = visitor.getMemento();
		if(memento !=null){
			FastaVisitor  differentVisitor = null;
			//start visiting starting where the memento was created
			parser.parse(differentVisitor, memento);
		}

	}

	private static class Visitor implements FastaVisitor{

		FastaVisitorMemento memento;
		
		@Override
		public FastaRecordVisitor visitDefline(FastaVisitorCallback callback,
				String id, String optionalComment) {
			if(id.equals("id") && callback.canCreateMemento()){
				memento =callback.createMemento();
			}
			return null;
		}

		
		public final FastaVisitorMemento getMemento() {
			return memento;
		}


		@Override
		public void visitEnd() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void halted() {
			// TODO Auto-generated method stub
			
		}
		
	}
}
