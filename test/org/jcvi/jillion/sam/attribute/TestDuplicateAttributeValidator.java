/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
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
package org.jcvi.jillion.sam.attribute;

import org.jcvi.jillion.sam.SamRecordBuilder;
import org.jcvi.jillion.sam.header.SamHeader;
import org.jcvi.jillion.sam.header.SamHeaderBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestDuplicateAttributeValidator {

	private SamRecordBuilder builder;
	private SamHeader header = new SamHeaderBuilder().build();
	private SamAttribute attr1 = new SamAttribute(ReservedSamAttributeKeys.COMMENTS, "foo");
	private SamAttribute dupAttr1 = new SamAttribute(ReservedSamAttributeKeys.COMMENTS, "bar");
	private SamAttribute attr2 = new SamAttribute(ReservedSamAttributeKeys.BARCODE_SEQUENCE, "ACGT");
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Before
	public void setup(){
		
		builder = new SamRecordBuilder(header);
	}
	
	
	@Test
	public void noAttributesYetShouldPass() throws InvalidAttributeException{
		NoDuplicateAttributeValidator.INSTANCE.validate(header, builder, attr1);
	}
	
	@Test
	public void doesntHaveDuplicateShouldPass() throws InvalidAttributeException{
		builder.addAttribute(attr2);
		NoDuplicateAttributeValidator.INSTANCE.validate(header, builder, attr1);
	}
	
	@Test
	public void duplicateAttributeShouldThrowInvalidAttributeException() throws InvalidAttributeException{
		builder.addAttribute(attr1);
		
		expectedException.expect(InvalidAttributeException.class);
		expectedException.expectMessage("has duplicate key : "+ attr1.getKey());
		
		NoDuplicateAttributeValidator.INSTANCE.validate(header, builder, dupAttr1);
	}
	
	@Test
	public void addingSameReferenceToAttributeShouldThrowInvalidAttributeException() throws InvalidAttributeException{
		builder.addAttribute(attr1);
		
		expectedException.expect(InvalidAttributeException.class);
		expectedException.expectMessage("has duplicate key : "+ attr1.getKey());
		
		NoDuplicateAttributeValidator.INSTANCE.validate(header, builder, attr1);
	}
}
