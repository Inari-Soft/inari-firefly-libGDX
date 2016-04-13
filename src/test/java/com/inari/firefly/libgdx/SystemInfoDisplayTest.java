package com.inari.firefly.libgdx;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.info.FrameRateInfo;

public class SystemInfoDisplayTest extends GdxFFApplicationAdapter {
    
    @Override
    public String getTitle() {
        return "IntroTest";
    }

    @Override
    protected void init( FFContext context ) {
        context.getSystemInfoDisplay()
            .addSystemInfo( new FrameRateInfo() )
            .setActive( true );
    }

    @Override
    protected void resize( int width, int height, FFContext context ) {
    }

    @Override
    protected void pause( FFContext context ) {
    }

    @Override
    protected void resume( FFContext context ) {
    }
    
    public static void main (String[] arg) {
        try {
            LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
            config.resizable = false;
            config.width = 800;
            config.height = 600;
            config.foregroundFPS = 0;
            new LwjglApplication( new SystemInfoDisplayTest(), config );
        } catch ( Throwable t ) {
            t.printStackTrace();
        }
    }

}
