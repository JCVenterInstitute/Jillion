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
/*
 * Created on Mar 24, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.experimental.assembly.agp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.io.IOUtil;
/**
 * 
 * @author dkatzel
 *
 *@see <a href="http://www.ncbi.nlm.nih.gov/projects/genome/assembly/agp/AGP_Specification.shtml">NCBI AGP Spec</a>
 */
public final class AgpParser {

	
    private static final Pattern CONTIG_PATTERN = Pattern.compile(
            "(\\S+)\\s+(\\d+)\\s+(\\d+)\\s+\\d+\\s+([A,D,F,G,N,O,P,U,W])\\s+(\\S+)\\s+(\\d+)\\s+(\\d+)\\s+([+,-])");

    private AgpParser(){
		//private constructor.
	}
    public static void parseAgpFile(File agpFile, AgpFileVisitor visitor) throws FileNotFoundException {
        parseAgpFile(agpFile,visitor,Range.CoordinateSystem.RESIDUE_BASED);
    }

    public static void parseAgpFile(File agpFile, AgpFileVisitor visitor, Range.CoordinateSystem agpFileCoordinateSystem) throws FileNotFoundException {
        InputStream agpStream = null;
        try {
            agpStream = new BufferedInputStream(new FileInputStream(agpFile));
            parseAgpFile(agpStream, visitor, agpFileCoordinateSystem);
        } finally {
           IOUtil.closeAndIgnoreErrors(agpStream);
        }
    }

    public static void parseAgpFile(InputStream in, AgpFileVisitor visitor) {
        parseAgpFile(in, visitor, Range.CoordinateSystem.RESIDUE_BASED);
    }

    public static void parseAgpFile(InputStream in, AgpFileVisitor visitor, Range.CoordinateSystem agpFileCoordinateSystem){
        Scanner scanner = new Scanner(in, IOUtil.UTF_8_NAME);
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            Matcher matcher = CONTIG_PATTERN.matcher(line);
            if(matcher.find()){
                String scaffoldId = matcher.group(1);
                Range contigRange = Range.of(agpFileCoordinateSystem,
                                                     Long.parseLong(matcher.group(2)),
                                                     Long.parseLong(matcher.group(3)));
                String contigId = matcher.group(5);
                Direction dir = Direction.parseSequenceDirection(matcher.group(8));
                visitor.visitContigEntry(scaffoldId,contigRange,contigId,dir);
            }
        }
        visitor.visitEndOfFile();
    }    
}
