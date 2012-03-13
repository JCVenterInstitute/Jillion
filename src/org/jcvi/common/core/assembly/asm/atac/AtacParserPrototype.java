package org.jcvi.common.core.assembly.asm.atac;

import java.io.IOException;
import java.io.InputStream;

import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.io.fileServer.ResourceFileServer;

public class AtacParserPrototype implements AtacFileVisitor{

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		ResourceFileServer resources = new ResourceFileServer(AtacParserPrototype.class);
		InputStream in = resources.getFileAsStream("example.atac");
		AtacFileParser.parse(in, new AtacParserPrototype());
		IOUtil.closeAndIgnoreErrors(in);
	}

	@Override
	public void visitLine(String line) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitFile() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitEndOfFile() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitMatch(AtacMatch match) {
		System.out.println(match);
		
	}

	@Override
	public void visitComment(String comment) {
		System.out.printf("comment = '%s'%n",comment);
		
	}

}
