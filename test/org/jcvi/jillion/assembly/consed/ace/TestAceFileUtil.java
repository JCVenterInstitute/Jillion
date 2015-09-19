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
package org.jcvi.jillion.assembly.consed.ace;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.jcvi.jillion.assembly.consed.ace.AceFileUtil;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestAceFileUtil {

    @Test
    public void parseChromatogramMadeAroundMidnight() throws ParseException{
        String dateAsString = "Fri Jan 7 00:40:59 2011";
        Date date =AceFileUtil.parsePhdDate(dateAsString);
        Calendar calendar =Calendar.getInstance();
		calendar.setTime(date);
        assertEquals(0, calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(40, calendar.get(Calendar.MINUTE));
        assertEquals(59, calendar.get(Calendar.SECOND));
    }
}
