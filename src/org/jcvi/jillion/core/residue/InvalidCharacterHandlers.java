package org.jcvi.jillion.core.residue;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.spi.InvalidCharacterHandler;
import org.jcvi.jillion.spi.ResidueHelper;
import org.jcvi.jillion.spi.UnknownInvalidCharacterHandler;

public enum InvalidCharacterHandlers implements InvalidCharacterHandler {
    ERROR_OUT{
        @Override
        public <R extends Residue<R>> R handle(Class<R> residueType, char invalidCharacter) {
            throw new IllegalArgumentException("invalid character for + " + residueType.getSimpleName() + ": '" + invalidCharacter + "' ascii value " + (int)invalidCharacter);
        }
    },
    REPLACE_WITH_UNKNOWN{
        @Override
        public <R extends Residue<R>> R handle(Class<R> residueType, char invalidCharacter) {
            return UnknownInvalidCharacterHandler.INSTANCE.handle(residueType, invalidCharacter);
        }
        },
    IGNORE{
        @Override
        public <R extends Residue<R>> R handle(Class<R> residueType, char invalidCharacter) {
           return null;
        }
    }
    ;
}
