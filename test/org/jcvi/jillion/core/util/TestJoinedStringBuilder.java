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
package org.jcvi.jillion.core.util;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

public class TestJoinedStringBuilder {

  
    
    @Test
    public void joinBuilderStringsWithNoGlue(){
        assertEquals("LarryMoeCurly",
                new JoinedStringBuilder<>("Larry","Moe","Curly").build());
    }
    
    @Test
    public void joinBuilderNoElements(){
        assertEquals("",
                new JoinedStringBuilder<>().build());
    }
    
    @Test
    public void joinBuilderStringsWithGlue(){
        assertEquals("Larry,Moe,Curly",
                new JoinedStringBuilder<>("Larry","Moe","Curly")
                        .glue(",")
                        .build());
    }
    
    @Test
    public void joinBuilderObjectsWithNoGlue(){
        assertEquals("LarryMoeCurly",
                new JoinedStringBuilder<>(new Object[]{"Larry","Moe","Curly"}).build());
    }
    
    @Test
    public void joinBuilderObjectsWithGlue(){
        assertEquals("Larry,Moe,Curly",
                new JoinedStringBuilder<>(new Object[]{"Larry","Moe","Curly"})
                        .glue(",")
                        .build());
    }
    @Test
    public void joinBuilderObjectsWithPrefix(){
        assertEquals("Stooges=Larry,Moe,Curly",
                new JoinedStringBuilder<>(new Object[]{"Larry","Moe","Curly"})
                        .glue(",")
                        .prefix("Stooges=")
                        .build());
    }
    @Test
    public void joinBuilderObjectsWithSuffix(){
        assertEquals("Larry,Moe,Curly were the best stooges",
                new JoinedStringBuilder<>(new Object[]{"Larry","Moe","Curly"})
                        .glue(",")
                        .suffix(" were the best stooges")
                        .build());
    }
    

    @Test
    public void transformToUpper(){
    	assertEquals("ABC,DEF",
    			new JoinedStringBuilder<>(Arrays.asList("abc", "def"))
    					.glue(",")
    					.transform(s -> ((String)s).toUpperCase())
    					.build()
    			);
    }
    @Test
    public void transformWrapWithQuotes(){
    	assertEquals("'abc','def'",
    			new JoinedStringBuilder<>(Arrays.asList("abc", "def"))
    					.glue(",")
    					.transform(s -> "'"+s+"'")
    					.build()
    			);
    }
}
