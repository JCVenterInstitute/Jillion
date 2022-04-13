package org.jcvi.jillion.core.residue.nt;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Ranges;
import org.jcvi.jillion.core.util.MemoizedSupplier;
import org.jcvi.jillion.internal.core.io.StreamUtil;
import org.jcvi.jillion.internal.core.residue.AbstractResidueSequence;
import org.jcvi.jillion.internal.core.util.GrowableByteArray;
import org.jcvi.jillion.internal.core.util.GrowableIntArray;

import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

class SimpleNucleotideSequence extends AbstractSimpleNucleotideSequence{

   

    public SimpleNucleotideSequence(GrowableByteArray data) {
       super(data);
    }
    public SimpleNucleotideSequence(Nucleotide[] data) {
    	super(data);
        
    }

    @Override
    public NucleotideSequence createNewInstance(Nucleotide[] trimRange) {
        return new SimpleNucleotideSequence(trimRange);
    }
}
