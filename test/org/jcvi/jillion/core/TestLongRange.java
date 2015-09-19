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
package org.jcvi.jillion.core;

public class TestLongRange extends AbstractTestRangeSubclasses{

	long length = 4294967295L + 1L;
	long begin = length+ 1L;
	long end = length + length;
	@Override
	protected Range getDifferentRange(){
		Range range = Range.of(begin+5L, end+5L);
		return range;
	}
	@Override
	protected long getBegin(){
		return begin;	
	}
	@Override
	protected long getEnd(){
		return end;	
	}
	@Override
	protected long getLength(){
		return length;
	}

}
