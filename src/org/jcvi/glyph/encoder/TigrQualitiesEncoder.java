/*
 * Created on Mar 20, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.encoder;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

/**
 * The <code>DefaultQualitiesEncoder</code> provides a default implementation
 * of a {@link QualitiesEncoder}.
 * <p>
 * The encoding scheme uses a simple character offset encoding to translate
 * the range of legal quality values into the set of printable characters.
 * This is done by simple addition with the US-ASCII character table.  
 * <p>
 * Additionally, range checking and normalization is performed.  A hard limit
 * to the upper value of qualities is set.  Any values beyond that are 
 * automatically dropped the maximum value.  The only exception to this is 
 * a special legacy quality value which is transformed to the current default
 * for edited bases.
 * 
 * @author jsitz
 * @author dkatzel
 */
public final class TigrQualitiesEncoder
{
    /** ASCII code for zero. */
    public static final int ENCODING_ORIGIN = 0x30;
    
    /** The minimum quality value allowed in the ASCII encoded representation. */
    public static final byte MIN_QUALITY = 0;
    
    /** The maximum quality value allowed in the ASCII encoded representation. */
    public static final byte MAX_QUALITY = 60;
    
    /** The quality value used to mark an edited base by the old editor (TIGR Editor). */
    public static final byte LEGACY_EDITED_QUALITY = 99;
    
    /** The quality value used to mark an edited base by the current editor (Cloe). */
    public static final byte EDITED_QUALITY = 40;
    
    private TigrQualitiesEncoder(){}
    
    /**
     * Encodes an array of quality values to a database storable {@link String}.
     * This is just a format adapter for {@link #encode(byte[])} and should 
     * follow the same contract and behavior.
     * 
     * @param qualities An array of quality values as <code>short</code>s.
     * @return A {@link String} containing an encoded version of the quality
     * value array given.
     * @see #encode(byte[])
     */
    public static String encode(short[] qualities)
    {
        // Always check for nulls
        if (qualities == null)
        {
            return "";
        }
        
        final byte[] byteQualities = new byte[qualities.length];
        
        for (int i = 0; i < qualities.length; i++)
        {
            /*
             * We'll be explicit here.  We accept no quality values greater
             * than 127, so we mask off the upper 9 bits of the short.
             */
            byteQualities[i] = (byte)qualities[i];
        }
        
        return encode(byteQualities);
    }
    
    /* (non-Javadoc)
     * @see org.jcvi.flim.projectdb.QualitiesEncoder#encode(byte[])
     */
    public static String encode(byte[] qualities)
    {
        final StringBuilder qualString = new StringBuilder(qualities.length);
        
        for (byte actualQuality : qualities)
        {
            byte qualityToEncode = convertToQualityToEncode(actualQuality);
            
            qualString.append((char)(qualityToEncode + ENCODING_ORIGIN));
        }
        
        return qualString.toString();
    }

    private static byte convertToQualityToEncode(byte actualQuality) {
        /*
         * Intercept and translate disallowed values
         */
        if (actualQuality == LEGACY_EDITED_QUALITY)
        {
            /*
             * Translate old edited flag values to the new values
             */
            return EDITED_QUALITY;
        }
        if (actualQuality < MIN_QUALITY)
        {
            /*
             * Enforce a minimum quality score.
             */
            return MIN_QUALITY;
        }
        if (actualQuality > MAX_QUALITY)
        {
            /*
             * Enforce a maximum quality score.
             */
            return MAX_QUALITY;
        }
        return actualQuality;
    }
    
    /* (non-Javadoc)
     * @see org.jcvi.flim.projectdb.QualitiesEncoder#decode(java.lang.CharSequence)
     */
    public static byte[] decode(CharSequence encodedString) throws IllegalEncodedValueException
    {
        final CharBuffer qualString = CharBuffer.wrap(encodedString);
        final ByteBuffer qualValues = ByteBuffer.allocate(qualString.remaining());
        
        while(qualString.hasRemaining())
        {
            final char encodedChar = qualString.get();
            final byte decodedValue = decode(encodedChar);
            
            qualValues.put(decodedValue);
        }
        
        return qualValues.array();
    }

    public static byte decode(final char encodedChar) {
        final byte decodedValue = (byte)(encodedChar - ENCODING_ORIGIN);
        
        /*
         * Check the decodedValue
         */
        if (decodedValue < MIN_QUALITY ||
            decodedValue > MAX_QUALITY)
        {
            throw new IllegalEncodedValueException("Illegal encoded entity: '" + encodedChar + "' (Value: " + decodedValue + ")");
        }
        return decodedValue;
    }
}

