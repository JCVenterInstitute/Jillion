package org.jcvi.common.examples;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.fasta.FastaFileParser;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
import org.jcvi.jillion.fasta.FastaVisitor;
import org.jcvi.jillion.fasta.FastaVisitorCallback;
import org.jcvi.jillion.fasta.FastaVisitorCallback.FastaVisitorMemento;

public class MementoVisiting {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		File fastaFile = new File("/path/to/fasta");
		
		FastaFileParser parser = FastaFileParser.create(fastaFile);
		
		Visitor visitor = new Visitor();
		
		parser.accept(visitor);
		
		FastaVisitorMemento memento = visitor.getMemento();
		if(memento !=null){
			FastaVisitor  differentVisitor = null;
			//start visiting starting where the memento was created
			parser.accept(differentVisitor, memento);
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
