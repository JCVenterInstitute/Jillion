package org.jcvi.jillion.spi;

import org.jcvi.jillion.core.residue.Residue;

import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum UnknownInvalidCharacterHandler implements InvalidCharacterHandler{

    INSTANCE;

    private Map<Class<?>, Object> unknownMap = new ConcurrentHashMap<>();

    @Override
    public <R extends Residue<R>> R handle(Class<R> residueType, char invalidCharacter) {
        return (R) unknownMap.computeIfAbsent(residueType, c->
            ResidueHelper.getAllResiduesFor((Class<R>) c)
                    .filter(Residue::isUnknown)
                    .findAny()
                    .orElseThrow()

        );
    }
}
