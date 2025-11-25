package org.jcvi.jillion.spi;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class InvalidCharacterHandlerRegistry {

    private static final Map<Class<?>, InvalidCharacterHandler> map = new ConcurrentHashMap<>();


    private InvalidCharacterHandlerRegistry(){
        //can not instantiate
    }

    public static void register(Class<?> residueType,InvalidCharacterHandler handler ){
        Objects.requireNonNull(residueType);
        map.put(residueType, handler);

    }

    public InvalidCharacterHandler getHandlerFor(Class<?> residueType){
        Objects.requireNonNull(residueType);
        return map.get(residueType);
    }


}
