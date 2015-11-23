package com.inari.firefly.libgdx;

import com.inari.commons.event.EventDispatcher;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.renderer.TextureAsset;
import com.inari.firefly.system.FireFly;

public class GdxFirefly extends FireFly {
    
    protected GdxFirefly() {
        super( new EventDispatcher(), new GdxSystemInterface(), new GdxTimerImpl(), new GdxInputImpl() );
    }

    public interface DynamicAttributes {
        public static final AttributeKey<String> TEXTURE_COLOR_FILTER_NAME = new AttributeKey<String>( "TEXTURE_COLOR_FILTER_NAME", String.class, TextureAsset.class );
    }
    
//    public static final InitMap getInitMap() {
//        return new InitMap()
//        .put( FFContext.EVENT_DISPATCHER, EventDispatcher.class )
//        .put( FFContext.TIMER, GdxTimerImpl.class )
//        .put( FFContext.INPUT, GdxInputImpl.class )
//        .put( FFContext.LOWER_SYSTEM_FACADE, GdxSystemInterface.class )
//        .put( FFContext.ENTITY_PROVIDER, EntityProvider.class )
//        .put( ActionSystem.CONTEXT_KEY, ActionSystem.class )
//        .put( AssetSystem.CONTEXT_KEY, AssetSystem.class )
//        .put( StateSystem.CONTEXT_KEY, StateSystem.class )
//        .put( ViewSystem.CONTEXT_KEY, ViewSystem.class )
//        .put( EntitySystem.CONTEXT_KEY, EntitySystem.class )
//        .put( EntityPrefabSystem.CONTEXT_KEY, EntityPrefabSystem.class )
//        .put( SpriteViewSystem.CONTEXT_KEY, SpriteViewSystem.class )
//        .put( TileGridSystem.CONTEXT_KEY, TileGridSystem.class )
//        .put( MovementSystem.CONTEXT_KEY, MovementSystem.class )
//        .put( ControllerSystem.CONTEXT_KEY, ControllerSystem.class )
//        .put( AnimationSystem.CONTEXT_KEY, AnimationSystem.class )
//        .put( SoundSystem.CONTEXT_KEY, SoundSystem.class )
//        .put( SpriteViewRenderer.CONTEXT_KEY, SpriteViewRenderer.class )
//        .put( TileGridRenderer.CONTEXT_KEY, TileGridRenderer.class )
//        .put( TaskSystem.CONTEXT_KEY, TaskSystem.class )
//        .put( TextSystem.CONTEXT_KEY, TextSystem.class )
//        .put( TextRenderer.CONTEXT_KEY, TextRenderer.class )
//        .put( SceneSystem.CONTEXT_KEY, SceneSystem.class )
//        ;
//
//    }

}
