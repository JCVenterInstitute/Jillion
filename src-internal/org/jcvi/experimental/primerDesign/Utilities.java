package org.jcvi.experimental.primerDesign;

import java.io.File;
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