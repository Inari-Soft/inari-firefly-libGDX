package com.inari.firefly.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.inari.firefly.system.FFContext;

public class IntroTest extends GdxFFApplicationAdapter {

    @Override
    public String getTitle() {
        return "IntroTest";
    }

    @Override
    protected void init( FFContext context ) {
        dispose();
        Gdx.app.exit();
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
            config.width = 704;
            config.height = 480;
            config.fullscreen = true;
            new LwjglApplication( new IntroTest(), config );
        } catch ( Throwable t ) {
            t.printStackTrace();
        }
    }

}
