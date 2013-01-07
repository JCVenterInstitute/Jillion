package org.jcvi.common.core.assembly.ace;
/**
 * {@code SingleAceContigBuilderVisitor} is an acefile visitor
 * that builds only a single contig.  It will halt the visitor
 * after only seeing 1 complete contig.
 * @author dkatzel
 *
 */
class SingleAceContigBuilderVisitor extends AbstractAceFileVisitorContigBuilder{

	private AceContig contig;

	@Override
	protected void visitContig(AceContigBuilder contigBuilder) {
		contig = contigBuilder.build();		
	}

	public final AceContig getContig() {
		return contig;
	}
	@Override
	public EndContigReturnCode getEndContigReturnCode() {
		if(contig==null){
			return EndContigReturnCode.KEEP_PARSING;
		}
		//we have our built contig no need
		//to keep parsing
		return  EndContigReturnCode.STOP_PARSING;
	}
	
	
}
