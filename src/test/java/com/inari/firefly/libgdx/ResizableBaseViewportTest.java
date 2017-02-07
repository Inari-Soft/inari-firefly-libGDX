package com.inari.firefly.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.graphics.RGBColor;
import com.inari.firefly.graphics.view.View;
import com.inari.firefly.system.FFContext;

public class ResizableBaseViewportTest extends GdxFFApplicationAdapter {
    
    @Override
    public String getTitle() {
        return "ResizableBaseViewportTest";
    }

    @Override
    protected void init( FFContext context ) {
        View baseView = context.getSystemComponent( View.TYPE_KEY, 0 );
        baseView.setClearColor( RGBColor.Colors.RED.get() );
    }

    @Override
    protected void resize( int width, int height, FFContext context ) {
       GdxGraphicsImpl graphics = (GdxGraphicsImpl) context.getGraphics();
       graphics.updateBaseViewport( width, height );
    }

    @Override
    protected void pause( FFContext context ) {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void resume( FFContext context ) {
        // TODO Auto-generated method stub
        
    }

    
    public static void main (String[] arg) {
        try {
            LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
            config.resizable = true;
            config.width = 800;
            config.height = 600;
            new LwjglApplication( new ResizableBaseViewportTest(), config );
        } catch ( Throwable t ) {
            t.printStackTrace();
        }
    }

}
