package org.jcvi.primerDesign;

import org.jcvi.datastore.DataStore;
import org.jcvi.fasta.NucleotideSequenceFastaRecord;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.io.IOUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: aresnick
 * Date: Aug 20, 2010
 * Time: 3:44:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class Utilities {

    private Utilities() {}

    // todo:need a better way to do this!
    public static File getScratchFile(File root) {
            String randomDirectory = (""+Math.random()).replace(".","");
            return new File(root,randomDirectory);
    }
}