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
package org.jcvi.jillion.core.util;

import org.jcvi.jillion.core.util.DateUtil;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestDateUtilElapsedTime {

	@Test
	public void onlyMillisecondsShouldSay0Seconds(){
		assertEquals("P0S", DateUtil.getElapsedTimeAsString(6));
	}
	@Test
	public void oneSecond(){
		assertEquals("P1S", DateUtil.getElapsedTimeAsString(1000));
	}
	@Test
	public void twoSeconds(){
		assertEquals("P2S", DateUtil.getElapsedTimeAsString(2000));
	}
	@Test
	public void oneMin(){
		assertEquals("P1M", DateUtil.getElapsedTimeAsString(1000*60));
	}
	@Test
	public void severalMins(){
		assertEquals("P13M", DateUtil.getElapsedTimeAsString(1000*60*13));
	}
	@Test
	public void severalMinsPlusSeconds(){
		assertEquals("P13M25S", DateUtil.getElapsedTimeAsString(1000*60*13 + 25000));
	}
	@Test
	public void oneHour(){
		assertEquals("P1H", DateUtil.getElapsedTimeAsString(1000*60*60));
	}
	@Test
	public void oneHourAndSeconds(){
		assertEquals("P1H3S", DateUtil.getElapsedTimeAsString(1000*60*60 + 3000));
	}
	@Test
	public void oneHourAndMins(){
		assertEquals("P1H5M", DateUtil.getElapsedTimeAsString(1000*60*60 + 1000*60*5));
	}
	
	@Test
	public void hoursMinsAndSeconds(){
		assertEquals("P21H5M52S", DateUtil.getElapsedTimeAsString(1000*60*60 *21 + 1000*60*5 + 1000*52));
	}
	@Test
	public void oneDay(){
		assertEquals("P1D", DateUtil.getElapsedTimeAsString(1000*60*60*24));
	}

}
