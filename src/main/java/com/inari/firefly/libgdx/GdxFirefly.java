package com.inari.firefly.libgdx;

import com.inari.commons.event.EventDispatcher;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.renderer.TextureAsset;
import com.inari.firefly.system.FireFly;

public final class GdxFirefly extends FireFly {
    
    protected GdxFirefly() {
        super( new EventDispatcher(), new GdxGraphicsImpl(), new GdxAudioImpl(), new GdxTimerImpl(), new GdxInputImpl() );
    }

    public interface DynamicAttributes {
        public static final AttributeKey<String> TEXTURE_COLOR_FILTER_NAME = new AttributeKey<String>( "TEXTURE_COLOR_FILTER_NAME", String.class, TextureAsset.class );
    }

}
