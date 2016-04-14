package com.inari.firefly.libgdx.intro;

import com.inari.commons.geom.Easing;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.graphics.RGBColor;
import com.inari.firefly.animation.easing.ColorEasingAnimation;
import com.inari.firefly.animation.easing.EasingData;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.control.ControllerSystem;
import com.inari.firefly.control.task.Task;
import com.inari.firefly.controller.entity.SpriteTintColorAnimationController;
import com.inari.firefly.entity.EEntity;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.graphics.TextureAsset;
import com.inari.firefly.graphics.sprite.ESprite;
import com.inari.firefly.graphics.sprite.SpriteAsset;
import com.inari.firefly.physics.animation.AnimationSystem;

public final class InitInariIntro extends Task {

    public InitInariIntro( int id ) {
        super( id );
    }

    @Override
    public final void runTask() {
        AssetSystem assetSystem = context.getSystem( AssetSystem.SYSTEM_KEY );
        EntitySystem entitySystem = context.getSystem( EntitySystem.SYSTEM_KEY );
        AnimationSystem animationSystem = context.getSystem( AnimationSystem.SYSTEM_KEY );
        ControllerSystem controllerSystem = context.getSystem( ControllerSystem.SYSTEM_KEY );
        
        animationSystem.getAnimationBuilder()
            .set( ColorEasingAnimation.NAME, BuildInariIntro.INTRO_ANIMATION )
            .set( ColorEasingAnimation.LOOPING, false )
            .set( ColorEasingAnimation.EASING_DATA_ALPHA, new EasingData( Easing.Type.LINEAR, 0.0f, 1.0f, 500 ) )
        .activate( ColorEasingAnimation.class );
        
        controllerSystem.getControllerBuilder()
            .set( SpriteTintColorAnimationController.ANIMATION_ID, 0 )
        .build( SpriteTintColorAnimationController.class );
                
        assetSystem .getAssetBuilder()
            .set( TextureAsset.NAME, BuildInariIntro.INTRO_TEXTURE )
            .set( TextureAsset.RESOURCE_NAME, BuildInariIntro.INARI_LOGO_RESOURCE_PATH )
        .activate( TextureAsset.class );
        TextureAsset textureAsset = assetSystem.getAssetAs( BuildInariIntro.INTRO_TEXTURE, TextureAsset.class );
        assetSystem .getAssetBuilder()
            .set( SpriteAsset.NAME, BuildInariIntro.INTRO_SPRITE )
            .set( SpriteAsset.TEXTURE_ASSET_NAME, BuildInariIntro.INTRO_TEXTURE )
            .set( SpriteAsset.TEXTURE_REGION, new Rectangle( 0, 0, textureAsset.getTextureWidth(), textureAsset.getTextureHeight() ) )
        .activate( SpriteAsset.class );
        
        entitySystem.getEntityBuilder()
            .set( ETransform.VIEW_ID, 0 )
            .set( ETransform.XPOSITION, context.getScreenWidth() / 2 - textureAsset.getTextureWidth() / 2 )
            .set( ETransform.YPOSITION, context.getScreenHeight() / 2 - textureAsset.getTextureHeight() / 2 )
            .set( ESprite.SPRITE_ASSET_NAME, BuildInariIntro.INTRO_SPRITE )
            .set( ESprite.TINT_COLOR, new RGBColor( 1f, 1f, 1f, 0f ) )
            .add( EEntity.CONTROLLER_IDS, 0 )
        .activate();
    }
    
}