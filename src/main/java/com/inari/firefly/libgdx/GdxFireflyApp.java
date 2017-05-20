package com.inari.firefly.libgdx;

import com.inari.commons.event.EventDispatcher;
import com.inari.firefly.system.FireFlyApp;

public final class GdxFireflyApp extends FireFlyApp {
    
    protected GdxFireflyApp() {
        super( new EventDispatcher(), new GdxGraphicsImpl(), new GdxAudioImpl(), new GdxTimerImpl(), new GdxInputImpl() );
    }

}
