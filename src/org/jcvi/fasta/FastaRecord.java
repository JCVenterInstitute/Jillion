/**
 * 
 */
package org.jcvi.fasta;


/**
 * 
 * 
 * @author jsitz
 */
public interface FastaRecord<T>
{
    /**
     * @return A <code>String</code>.
     */
    String getIdentifier();

    /**
     * @return A <code>String</code>.
     */
    String getComments();

    CharSequence getStringRecord();

    long getChecksum();
    
    T getValues();

}