package com.inari.firefly.libgdx;

import com.inari.commons.event.EventDispatcher;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.graphics.TextureAsset;
import com.inari.firefly.system.FireFlyApp;

public final class GdxFireflyApp extends FireFlyApp {
    
    protected GdxFireflyApp() {
        super( new EventDispatcher(), new GdxGraphicsImpl(), new GdxAudioImpl(), new GdxTimerImpl(), new GdxInputImpl() );
    }

    public interface DynamicAttributes {
        public static final AttributeKey<String> TEXTURE_COLOR_CONVERTER_NAME = new AttributeKey<String>( "TEXTURE_COLOR_CONVERTER_NAME", String.class, TextureAsset.class );
    }

}
