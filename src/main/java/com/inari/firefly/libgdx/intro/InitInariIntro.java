package com.inari.firefly.libgdx.intro;

import com.inari.commons.geom.Easing;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.graphics.RGBColor;
import com.inari.firefly.animation.AnimationSystem;
import com.inari.firefly.animation.ColorEasingAnimation;
import com.inari.firefly.animation.EasingData;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.control.ControllerSystem;
import com.inari.firefly.control.EController;
import com.inari.firefly.controller.entity.SpriteTintColorAnimationController;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.renderer.TextureAsset;
import com.inari.firefly.renderer.sprite.ESprite;
import com.inari.firefly.renderer.sprite.SpriteAsset;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.task.Task;

public final class InitInariIntro extends Task {

    public InitInariIntro( int id ) {
        super( id );
    }

    @Override
    public final void run( FFContext context ) {
        AssetSystem assetSystem = context.getSystem( AssetSystem.CONTEXT_KEY );
        EntitySystem entitySystem = context.getSystem( EntitySystem.CONTEXT_KEY );
        AnimationSystem animationSystem = context.getSystem( AnimationSystem.CONTEXT_KEY );
        ControllerSystem controllerSystem = context.getSystem( ControllerSystem.CONTEXT_KEY );
        
        animationSystem
            .getAnimationBuilder()
                .set( ColorEasingAnimation.NAME, BuildInariIntro.INTRO_ANIMATION )
                .set( ColorEasingAnimation.LOOPING, false )
                .set( ColorEasingAnimation.EASING_DATA_ALPHA, new EasingData( Easing.Type.LINEAR, 0.0f, 1.0f, 1000 ) )
            .build( ColorEasingAnimation.class );
        
        controllerSystem
            .getControllerBuilder()
                .set( SpriteTintColorAnimationController.TINT_COLOR_ANIMATION_ID, 0 )
            .build( SpriteTintColorAnimationController.class );
                
        
        assetSystem
            .getAssetBuilder(  )
                .set( TextureAsset.NAME, BuildInariIntro.INARI_ASSET_KEY.name )
                .set( TextureAsset.ASSET_GROUP, BuildInariIntro.INARI_ASSET_KEY.group )
                .set( TextureAsset.RESOURCE_NAME, BuildInariIntro.INARI_LOGO_RESOURCE_PATH )
                .set( TextureAsset.TEXTURE_WIDTH, BuildInariIntro.INTRO_TEX_WIDTH )
                .set( TextureAsset.TEXTURE_HEIGHT, BuildInariIntro.INTRO_TEX_HEIGHT )
            .buildAndNext( TextureAsset.class )
                .set( SpriteAsset.NAME, BuildInariIntro.INARI_SPRITE_ASSET_KEY.name )
                .set( SpriteAsset.ASSET_GROUP, BuildInariIntro.INARI_SPRITE_ASSET_KEY.group )
                .set( SpriteAsset.TEXTURE_ID, assetSystem.getAssetTypeKey( BuildInariIntro.INARI_ASSET_KEY ).id )
                .set( SpriteAsset.TEXTURE_REGION, new Rectangle( 0, 0, BuildInariIntro.INTRO_TEX_WIDTH, BuildInariIntro.INTRO_TEX_HEIGHT ) )
            .build( SpriteAsset.class );
        
        assetSystem.loadAsset( BuildInariIntro.INARI_ASSET_KEY );
        assetSystem.loadAsset( BuildInariIntro.INARI_SPRITE_ASSET_KEY );
        
        int logoEntityId = entitySystem
                .getEntityBuilder()
                    .set( ETransform.VIEW_ID, 0 )
                    .set( ETransform.XPOSITION, context.getScreenWidth() / 2 - BuildInariIntro.INTRO_TEX_WIDTH / 2 )
                    .set( ETransform.YPOSITION, context.getScreenHeight() / 2 - BuildInariIntro.INTRO_TEX_HEIGHT / 2 )
                    .set( ESprite.SPRITE_ID, assetSystem.getAssetTypeKey( BuildInariIntro.INARI_SPRITE_ASSET_KEY ).id )
                    .set( ESprite.TINT_COLOR, new RGBColor( 1f, 1f, 1f, 0f ) )
                    .set( EController.CONTROLLER_IDS, new int[] { 0 } )
                .build();
        entitySystem.activate( logoEntityId );
    }
    
}