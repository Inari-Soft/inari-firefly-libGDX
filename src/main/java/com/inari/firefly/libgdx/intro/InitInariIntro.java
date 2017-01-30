package com.inari.firefly.libgdx.intro;

import com.inari.commons.geom.PositionF;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.graphics.RGBColor;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.control.task.Task;
import com.inari.firefly.entity.EEntity;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntityController;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.graphics.TextureAsset;
import com.inari.firefly.graphics.sprite.ESprite;
import com.inari.firefly.graphics.sprite.SpriteAsset;

public final class InitInariIntro extends Task {

    public InitInariIntro( int id ) {
        super( id );
    }

    @Override
    public final void runTask() {
        AssetSystem assetSystem = context.getSystem( AssetSystem.SYSTEM_KEY );
        EntitySystem entitySystem = context.getSystem( EntitySystem.SYSTEM_KEY );
        
        int controllerId = context.getComponentBuilder( EntityController.TYPE_KEY )
            .build( IntroAnimationController.class );
                
        assetSystem .getAssetBuilder()
            .set( TextureAsset.NAME, BuildInariIntro.INTRO_TEXTURE )
            .set( TextureAsset.RESOURCE_NAME, BuildInariIntro.INARI_LOGO_RESOURCE_PATH )
        .activate( TextureAsset.class );
        TextureAsset textureAsset = assetSystem.getAssetAs( BuildInariIntro.INTRO_TEXTURE, TextureAsset.class );
        assetSystem.getAssetBuilder()
            .set( SpriteAsset.NAME, BuildInariIntro.INTRO_SPRITE )
            .set( SpriteAsset.TEXTURE_ASSET_NAME, BuildInariIntro.INTRO_TEXTURE )
            .set( SpriteAsset.TEXTURE_REGION, new Rectangle( 0, 0, textureAsset.getTextureWidth(), textureAsset.getTextureHeight() ) )
        .activate( SpriteAsset.class );
        
        entitySystem.getEntityBuilder()
            .set( ETransform.VIEW_ID, 0 )
            .set( ETransform.POSITION, new PositionF( 
                context.getScreenWidth() / 2 - textureAsset.getTextureWidth() / 2,
                context.getScreenHeight() / 2 - textureAsset.getTextureHeight() / 2 
             ) )
            .set( ESprite.SPRITE_ASSET_NAME, BuildInariIntro.INTRO_SPRITE )
            .set( ESprite.TINT_COLOR, new RGBColor( 1f, 1f, 1f, 0f ) )
            .add( EEntity.CONTROLLER_IDS, controllerId )
        .activate();
    }
    
    public static class IntroAnimationController extends EntityController {

        public IntroAnimationController( int id ) {
            super( id );
        }

        @Override
        protected void update( int entityId ) {
            ESprite sprite = context.getEntityComponent( entityId, ESprite.TYPE_KEY );
            RGBColor tintColor = sprite.getTintColor();
            if( tintColor.a < 1f) {
                tintColor.a = tintColor.a + 0.05f;
            }
        }
        
    }
    
}