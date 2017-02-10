package com.inari.firefly.libgdx;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.inari.commons.geom.PositionF;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.graphics.RGBColor;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.graphics.TextureAsset;
import com.inari.firefly.graphics.sprite.ESprite;
import com.inari.firefly.graphics.sprite.SpriteAsset;
import com.inari.firefly.graphics.view.View;
import com.inari.firefly.system.FFContext;

public class SpriteFlippingTest extends GdxFFApplicationAdapter {

    @Override
    public String getTitle() {
        return "SpriteFlippingTest";
    }

    @Override
    protected void init( FFContext context ) {
        View baseView = context.getSystemComponent( View.TYPE_KEY, 0 );
        baseView.setBlendMode( null );
        baseView.setClearColor( new RGBColor( 1f, 1f, 1f, 1f ) );
        
        context.getComponentBuilder( TextureAsset.TYPE_KEY, TextureAsset.class )
            .set( TextureAsset.NAME, "TestTexture" )
            .set( TextureAsset.RESOURCE_NAME, "firefly/fireflyMicroFont.png" )
        .activate();
        
        context.getComponentBuilder( SpriteAsset.TYPE_KEY, SpriteAsset.class )
            .set( SpriteAsset.NAME, "NormalSprite" )
            .set( SpriteAsset.TEXTURE_ASSET_NAME, "TestTexture" )
            .set( SpriteAsset.TEXTURE_REGION, new Rectangle( 0, 0, 8, 12 ) )
        .activate();
        
        context.getComponentBuilder( SpriteAsset.TYPE_KEY, SpriteAsset.class )
            .set( SpriteAsset.NAME, "FlippVertical" )
            .set( SpriteAsset.TEXTURE_ASSET_NAME, "TestTexture" )
            .set( SpriteAsset.TEXTURE_REGION, new Rectangle( 0, 0, 8, 12 ) )
            .set( SpriteAsset.VERTICAL_FLIP, true )
        .activate();
        
        context.getComponentBuilder( SpriteAsset.TYPE_KEY, SpriteAsset.class )
            .set( SpriteAsset.NAME, "FlippHorizontal" )
            .set( SpriteAsset.TEXTURE_ASSET_NAME, "TestTexture" )
            .set( SpriteAsset.TEXTURE_REGION, new Rectangle( 0, 0, 8, 12 ) )
            .set( SpriteAsset.HORIZONTAL_FLIP, true )
        .activate();
        
        context.getComponentBuilder( SpriteAsset.TYPE_KEY, SpriteAsset.class )
            .set( SpriteAsset.NAME, "FlippVerticalAndHorizontal" )
            .set( SpriteAsset.TEXTURE_ASSET_NAME, "TestTexture" )
            .set( SpriteAsset.TEXTURE_REGION, new Rectangle( 0, 0, 8, 12 ) )
            .set( SpriteAsset.VERTICAL_FLIP, true )
            .set( SpriteAsset.HORIZONTAL_FLIP, true )
        .activate();
        
        context.getEntityBuilder()
            .set( ETransform.VIEW_ID, 0 )
            .set( ETransform.POSITION, new PositionF( 100, 100 ) )
            .set( ESprite.SPRITE_ASSET_NAME, "NormalSprite" )
        .activate();
        context.getEntityBuilder()
            .set( ETransform.VIEW_ID, 0 )
            .set( ETransform.POSITION, new PositionF( 120, 100 ) )
            .set( ESprite.SPRITE_ASSET_NAME, "FlippVertical" )
        .activate();
        context.getEntityBuilder()
            .set( ETransform.VIEW_ID, 0 )
            .set( ETransform.POSITION, new PositionF( 140, 100 ) )
            .set( ESprite.SPRITE_ASSET_NAME, "FlippHorizontal" )
        .activate();
        context.getEntityBuilder()
            .set( ETransform.VIEW_ID, 0 )
            .set( ETransform.POSITION, new PositionF( 160, 100 ) )
            .set( ESprite.SPRITE_ASSET_NAME, "FlippVerticalAndHorizontal" )
        .activate();
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
            new LwjglApplication( new SpriteFlippingTest(), config );
        } catch ( Throwable t ) {
            t.printStackTrace();
        }
    }

}
