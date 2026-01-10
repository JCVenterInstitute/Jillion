package org.jcvi.jillion.spi;

import org.jcvi.jillion.core.residue.Residue;

@FunctionalInterface
public interface InvalidCharacterHandler{
    <R extends Residue<R>> R handle(Class<R> residueType, char invalidCharacter);
}
