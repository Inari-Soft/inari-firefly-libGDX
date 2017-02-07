package com.inari.firefly.libgdx;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.inari.commons.geom.PositionF;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.graphics.RGBColor;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.graphics.shape.EShape;
import com.inari.firefly.graphics.shape.ShapeRenderSystem;
import com.inari.firefly.graphics.view.View;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.external.ShapeData;

public class ResizableBaseViewportTest extends GdxFFApplicationAdapter {
    
    private static final int BASE_WIDTH = 800;
    private static final int BASE_HEIGHT = 600;
    
    @Override
    public String getTitle() {
        return "ResizableBaseViewportTest";
    }

    @Override
    protected void init( FFContext context ) {
        context.loadSystem( ShapeRenderSystem.SYSTEM_KEY );
        context.getEntityBuilder()
            .set( ETransform.VIEW_ID, 0 )
            .set( ETransform.POSITION , new PositionF( 0, 0 ) )
            .set( EShape.SHAPE_TYPE, ShapeData.Type.RECTANGLE )
            .add( EShape.COLORS, RGBColor.Colors.RED.get() )
            .set( EShape.FILL, false )
            .set( EShape.VERTICES, new float[] { 1, 1, 799, 599 } )
            .activate();
    }

    @Override
    protected void resize( int width, int height, FFContext context ) {
        View baseView = context.getSystemComponent( View.TYPE_KEY, 0 );
        PositionF worldPosition = baseView.getWorldPosition();
        Rectangle bounds = baseView.getBounds();
        
        float targetRatio = (float) height / width;
        float sourceRatio = (float) BASE_HEIGHT / BASE_WIDTH;
        boolean fitToWidth = targetRatio > sourceRatio;

        if ( fitToWidth ) {
            bounds.width = BASE_WIDTH;
            bounds.height = Math.round( ( BASE_HEIGHT / sourceRatio ) * targetRatio );
            worldPosition.y = - ( bounds.height - BASE_HEIGHT ) / 2;
        } else {
            bounds.width = Math.round( ( BASE_WIDTH / targetRatio ) * sourceRatio );
            bounds.height = BASE_HEIGHT;
            worldPosition.x = - ( bounds.width - BASE_WIDTH ) / 2;
        }
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
            config.width = BASE_WIDTH;
            config.height = BASE_HEIGHT;
            new LwjglApplication( new ResizableBaseViewportTest(), config );
        } catch ( Throwable t ) {
            t.printStackTrace();
        }
    }

}
