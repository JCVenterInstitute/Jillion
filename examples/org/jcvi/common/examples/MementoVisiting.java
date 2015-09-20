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
