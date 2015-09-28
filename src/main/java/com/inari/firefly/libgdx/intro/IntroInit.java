package com.inari.firefly.libgdx.intro;

import com.inari.commons.lang.TypedKey;
import com.inari.firefly.Disposable;
import com.inari.firefly.system.FFContext;

public interface IntroInit extends Disposable {
    
    public static final TypedKey<IntroInit> CONTEXT_KEY = TypedKey.create( "IntroInit", IntroInit.class );

    void initContext( FFContext context );

}
