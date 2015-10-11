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
import com.inari.firefly.controller.SpriteTintColorAnimationController;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.Entity;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.renderer.TextureAsset;
import com.inari.firefly.renderer.sprite.ESprite;
import com.inari.firefly.renderer.sprite.SpriteAsset;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.task.Task;

public final class InitInariIntroTask extends Task {

    

    public InitInariIntroTask( int id ) {
        super( id );
    }

    @Override
    public final void run( FFContext context ) {
        AssetSystem assetSystem = context.getComponent( AssetSystem.CONTEXT_KEY );
        EntitySystem entitySystem = context.getComponent( EntitySystem.CONTEXT_KEY );
        AnimationSystem animationSystem = context.getComponent( AnimationSystem.CONTEXT_KEY );
        ControllerSystem controllerSystem = context.getComponent( ControllerSystem.CONTEXT_KEY );
        
        animationSystem
            .getAnimationBuilder( ColorEasingAnimation.class )
                .set( ColorEasingAnimation.NAME, InariIntro.INTRO_ANIMATION )
                .set( ColorEasingAnimation.LOOPING, false )
                .set( ColorEasingAnimation.EASING_DATA_ALPHA, new EasingData( Easing.Type.LINEAR, 0.0f, 1.0f, 1000 ) )
            .build();
        
        controllerSystem
            .getComponentBuilder( SpriteTintColorAnimationController.class )
                .set( SpriteTintColorAnimationController.TINT_COLOR_ANIMATION_ID, 0 )
            .build();
                
        
        assetSystem
            .getAssetBuilder( TextureAsset.class )
                .set( TextureAsset.NAME, InariIntro.INARI_ASSET_KEY.name )
                .set( TextureAsset.ASSET_GROUP, InariIntro.INARI_ASSET_KEY.group )
                .set( TextureAsset.RESOURCE_NAME, InariIntro.INARI_LOGO_RESOURCE_PATH )
                .set( TextureAsset.TEXTURE_WIDTH, InariIntro.INTRO_TEX_WIDTH )
                .set( TextureAsset.TEXTURE_HEIGHT, InariIntro.INTRO_TEX_HEIGHT )
            .buildAndNext( SpriteAsset.class )
                .set( SpriteAsset.NAME, InariIntro.INARI_SPRITE_ASSET_KEY.name )
                .set( SpriteAsset.ASSET_GROUP, InariIntro.INARI_SPRITE_ASSET_KEY.group )
                .set( SpriteAsset.TEXTURE_ID, assetSystem.getAssetTypeKey( InariIntro.INARI_ASSET_KEY ).id )
                .set( SpriteAsset.TEXTURE_REGION, new Rectangle( 0, 0, InariIntro.INTRO_TEX_WIDTH, InariIntro.INTRO_TEX_HEIGHT ) )
            .build();
        
        assetSystem.loadAsset( InariIntro.INARI_ASSET_KEY );
        assetSystem.loadAsset( InariIntro.INARI_SPRITE_ASSET_KEY );
        
        Entity logoEntity = entitySystem
                .getEntityBuilder()
                    .set( ETransform.VIEW_ID, 0 )
                    .set( ETransform.XPOSITION, context.getScreenWidth() / 2 - InariIntro.INTRO_TEX_WIDTH / 2 )
                    .set( ETransform.YPOSITION, context.getScreenHeight() / 2 - InariIntro.INTRO_TEX_HEIGHT / 2 )
                    .set( ESprite.SPRITE_ID, assetSystem.getAssetTypeKey( InariIntro.INARI_SPRITE_ASSET_KEY ).id )
                    .set( ESprite.TINT_COLOR, new RGBColor( 1f, 1f, 1f, 0f ) )
                    .set( EController.CONTROLLER_IDS, new int[] { 0 } )
                .build();
        entitySystem.activate( logoEntity.getId() );
    }
    
}