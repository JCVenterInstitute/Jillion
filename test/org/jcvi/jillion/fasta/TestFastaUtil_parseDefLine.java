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
package org.jcvi.jillion.fasta;

import org.jcvi.jillion.fasta.FastaUtil;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestFastaUtil_parseDefLine {

	@Test
	public void deflineOnlyHasId(){
		String defline = ">myId\n";
		assertEquals("myId",FastaUtil.parseIdFromDefLine(defline));
		assertNull(FastaUtil.parseCommentFromDefLine(defline));
	}
	@Test
	public void commentIsOnlyWhitespaceHasIdShouldBeConsideredNull(){
		String defline = ">myId\t\t\n";
		assertEquals("myId",FastaUtil.parseIdFromDefLine(defline));
		assertNull(FastaUtil.parseCommentFromDefLine(defline));
	}
	@Test
	public void deflineWithMultiWordComment(){
		String defline = ">myId\tmy comment\n";
		assertEquals("myId",FastaUtil.parseIdFromDefLine(defline));
		assertEquals("my comment",FastaUtil.parseCommentFromDefLine(defline));
	}
	@Test
	public void deflineWithSingleWordComment(){
		String defline = ">myId\tcomment\n";
		assertEquals("myId",FastaUtil.parseIdFromDefLine(defline));
		assertEquals("comment",FastaUtil.parseCommentFromDefLine(defline));
	}
}
