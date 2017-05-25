package com.inari.firefly.libgdx;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.inari.commons.geom.PositionF;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.graphics.RGBColor;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.graphics.BlendMode;
import com.inari.firefly.graphics.ETransform;
import com.inari.firefly.graphics.rendering.RenderingSystem;
import com.inari.firefly.graphics.shape.EShape;
import com.inari.firefly.graphics.text.EText;
import com.inari.firefly.graphics.view.Layer;
import com.inari.firefly.graphics.view.View;
import com.inari.firefly.system.FFContext;

public class OverlapViewTest extends GdxFFApplicationAdapter {
    
    @Override
    public String getTitle() {
        return "OverlapViewTest";
    }

    @Override
    protected void init( FFContext context ) {
        context.loadSystem( RenderingSystem.SYSTEM_KEY );
        context.loadSystem( EntitySystem.SYSTEM_KEY );
        
        context.getComponentBuilder( View.TYPE_KEY )
            .set( View.NAME, "VIEW1" )
            .set( View.CLEAR_COLOR, new RGBColor( 1, 1, 1, 1f ) )
            .set( View.BOUNDS, new Rectangle( 0, 0, 800, 400 ) )
            .set( View.LAYERING_ENABLED, true )
            .activateAndNext()
            .set( View.CLEAR_COLOR, new RGBColor( 1f, 1f, 1f, 1f ) )
            .set( View.TINT_COLOR, new RGBColor( 0.8f, 0.8f, 0.8f, 0.8f ) )
            .set( View.BLEND_MODE, BlendMode.NORMAL_ALPHA )
            .set( View.BOUNDS, new Rectangle( 100, 100, 300, 100 ) )
            .activate()
            ;
        context.getComponentBuilder( Layer.TYPE_KEY )
            .set( Layer.NAME, "Background" )
            .set( Layer.VIEW_NAME, "VIEW1" )
            .activateAndNext()
            .set( Layer.NAME, "Foreground" )
            .set( Layer.VIEW_NAME, "VIEW1" )
            .activate();
        
        context.getEntityBuilder()
            .set( ETransform.VIEW_NAME, "VIEW1" )
            .set( ETransform.LAYER_ID, 0 )
            .set( EShape.SHAPE_TYPE, EShape.Type.CIRCLE )
            .set( EShape.SEGMENTS, 10 )
            .set( EShape.VERTICES, new float[] { 100, 100, 60 , 300, 300, 60  } )
            .add( EShape.COLORS, new RGBColor( 1, 0, 0, 1 ) )
            .set( EShape.FILL, true )
        .activateAndNext()
            .set( ETransform.VIEW_ID, 2 )
            .set( ETransform.LAYER_ID, 0 )
            .set( ETransform.POSITION, new PositionF( 10f, 10f ) )
            .set( EText.FONT_ASSET_NAME, FFContext.DEFAULT_FONT )
            .set( EText.TEXT, "Test 123 Dies ist ein Text mit einem Absatz\nUnd hier geht es weiter mit dem Text" )
            .set( EText.TINT_COLOR, new RGBColor( 0, 0, 0, 1f ) )
        .activateAndNext()
            .set( ETransform.VIEW_NAME, "VIEW1" )
            .set( ETransform.LAYER_NAME, "Background" )
            .set( EShape.SHAPE_TYPE, EShape.Type.RECTANGLE )
            .set( EShape.VERTICES, new float[] { 300, 300, 300, 100 } )
            .add( EShape.COLORS, new RGBColor( 0.8f, 0.8f, 0.8f, 0.5f ) )
            .set( EShape.BLEND_MODE, BlendMode.NONE )
            .set( EShape.FILL, true )
        .activateAndNext()
            .set( ETransform.VIEW_NAME, "VIEW1" )
            .set( ETransform.LAYER_NAME, "Foreground" )
            .set( ETransform.POSITION, new PositionF( 310, 310 ) )
            .set( EText.FONT_ASSET_NAME, FFContext.DEFAULT_FONT )
            .set( EText.TEXT, "Test 456 Dies ist ein Text mit einem Absatz\nUnd hier geht es weiter mit dem Text" )
            .set( EText.TINT_COLOR, new RGBColor( 1, 1, 1, 1f ) )
        .activate()
            ;
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
            config.resizable = true;
            config.width = 800;
            config.height = 600;
            new LwjglApplication( new OverlapViewTest(), config );
        } catch ( Throwable t ) {
            t.printStackTrace();
        }
    }

}
