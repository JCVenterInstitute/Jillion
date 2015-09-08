/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.sam.attribute;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.jcvi.jillion.sam.header.SamHeader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SamAttribute.class)
public class TestChainedSamAttributeValidatorBuilder {

	@Test(expected = NullPointerException.class)
	public void nullValidatorShouldThrowNPE(){
		new ChainedSamAttributeValidatorBuilder()
				.addValidator(null);
	}
	
	@Test
	public void defaultConstructorShouldFirstUseReservedAttributeValidator(){
		ChainedSamAttributeValidatorBuilder.ChainedSamAttributeValidator validator = (ChainedSamAttributeValidatorBuilder.ChainedSamAttributeValidator)
																						new ChainedSamAttributeValidatorBuilder().build();
		
		@SuppressWarnings("unchecked")
		List<SamAttributeValidator> validators =Whitebox.getInternalState(validator, List.class);
		assertEquals(1, validators.size());
		assertTrue(validators.get(0) == ReservedAttributeValidator.INSTANCE);
	}
	
	@Test
	public void createEmptyValidator(){
		ChainedSamAttributeValidatorBuilder.ChainedSamAttributeValidator validator = (ChainedSamAttributeValidatorBuilder.ChainedSamAttributeValidator)
				new ChainedSamAttributeValidatorBuilder(false).build();
		
		@SuppressWarnings("unchecked")
		List<SamAttributeValidator> validators =Whitebox.getInternalState(validator, List.class);
		assertTrue(validators.isEmpty());
	}
	
	@Test
	public void firstValidatorFailsShouldNotCall2ndValidator() throws InvalidAttributeException{
		SamAttributeValidator v1 = createMock(SamAttributeValidator.class);
		SamAttributeValidator v2 = createMock(SamAttributeValidator.class);
		
		
		InvalidAttributeException expected = new InvalidAttributeException("expected");
		
		v1.validate(isA(SamHeader.class), isA(SamAttribute.class));
		expectLastCall().andThrow(expected);
		
		replay(v1, v2);
		
		SamAttributeValidator chain = new ChainedSamAttributeValidatorBuilder(false)
											.addValidator(v1)
											.addValidator(v2)
											.build();
		
		SamHeader mockHeader = PowerMock.createMock(SamHeader.class);
		SamAttribute mockAttr = PowerMock.createMock(SamAttribute.class);
		
		PowerMock.replay(mockHeader, mockAttr);
		try{
			chain.validate(mockHeader, mockAttr);
			fail("should throw exception");
		}catch(InvalidAttributeException actual){
			assertEquals(expected, actual);
		}
		verify(v1, v2);
		
	}
	@Test
	public void firstValidatorPassesShouldThenCall2ndValidator() throws InvalidAttributeException{
		SamAttributeValidator v1 = createMock(SamAttributeValidator.class);
		SamAttributeValidator v2 = createMock(SamAttributeValidator.class);
		
		
		InvalidAttributeException expected = new InvalidAttributeException("expected");
		
		v1.validate(isA(SamHeader.class), isA(SamAttribute.class));
		
		v2.validate(isA(SamHeader.class), isA(SamAttribute.class));
		expectLastCall().andThrow(expected);
		
		replay(v1, v2);
		
		SamAttributeValidator chain = new ChainedSamAttributeValidatorBuilder(false)
											.addValidator(v1)
											.addValidator(v2)
											.build();
		
		SamHeader mockHeader = PowerMock.createMock(SamHeader.class);
		SamAttribute mockAttr = PowerMock.createMock(SamAttribute.class);
		
		PowerMock.replay(mockHeader, mockAttr);
		try{
			chain.validate(mockHeader, mockAttr);
			fail("should throw exception");
		}catch(InvalidAttributeException actual){
			assertEquals(expected, actual);
		}
		verify(v1, v2);
		
	}
	
	@Test
	public void wholeChainPasses() throws InvalidAttributeException{
		SamAttributeValidator v1 = createMock(SamAttributeValidator.class);
		SamAttributeValidator v2 = createMock(SamAttributeValidator.class);
		
		
		
		v1.validate(isA(SamHeader.class), isA(SamAttribute.class));
		
		v2.validate(isA(SamHeader.class), isA(SamAttribute.class));		
		
		replay(v1, v2);
		
		SamAttributeValidator chain = new ChainedSamAttributeValidatorBuilder(false)
											.addValidator(v1)
											.addValidator(v2)
											.build();
		
		SamHeader mockHeader = PowerMock.createMock(SamHeader.class);
		SamAttribute mockAttr = PowerMock.createMock(SamAttribute.class);
		
		PowerMock.replay(mockHeader, mockAttr);
		
		chain.validate(mockHeader, mockAttr);
		
		verify(v1, v2);
		
	}
}
