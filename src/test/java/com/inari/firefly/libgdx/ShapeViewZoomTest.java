package com.inari.firefly.libgdx;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.inari.commons.geom.PositionF;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.graphics.shape.EShape;
import com.inari.firefly.graphics.shape.ShapeRenderSystem;
import com.inari.firefly.graphics.view.View;
import com.inari.firefly.system.FFContext;

public class ShapeViewZoomTest extends GdxFFApplicationAdapter {
    
    @Override
    public String getTitle() {
        return "ShapeTest";
    }

    @Override
    protected void init( FFContext context ) {
        context.loadSystem( ShapeRenderSystem.SYSTEM_KEY );
        context.loadSystem( EntitySystem.SYSTEM_KEY );
        
        View baseView = context.getSystemComponent( View.TYPE_KEY, 0 );
        baseView.setZoom( 0.5f );
        baseView.setBlendMode( null );
        
        context.getComponentBuilder( EntitySystem.Entity.ENTITY_TYPE_KEY )
            .set( ETransform.VIEW_ID, 0 )
            .set( ETransform.LAYER_ID, 0 )
            .set( ETransform.POSITION, new PositionF( 100, 100 ) )
            .set( ETransform.POSITION, new PositionF( 125, 125 ) )
            .set( EShape.SHAPE_TYPE, EShape.Type.RECTANGLE )
            .set( EShape.FILL, false )
            .set( EShape.VERTICES, new float[] { 100, 100, 50f, 50f } )
        .activate();
    }

    @Override
    protected void resize( int width, int height, FFContext context ) {
        // TODO Auto-generated method stub
        
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
            new LwjglApplication( new ShapeViewZoomTest(), config );
        } catch ( Throwable t ) {
            t.printStackTrace();
        }
    }

}
