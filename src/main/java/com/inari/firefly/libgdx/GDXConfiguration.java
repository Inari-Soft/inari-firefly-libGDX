package com.inari.firefly.libgdx;

import com.inari.commons.event.EventDispatcher;
import com.inari.firefly.animation.AnimationSystem;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.control.ControllerSystem;
import com.inari.firefly.entity.EntityPrefabSystem;
import com.inari.firefly.entity.EntityProvider;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.movement.MovementSystem;
import com.inari.firefly.renderer.sprite.SpriteViewRenderer;
import com.inari.firefly.renderer.sprite.SpriteViewSystem;
import com.inari.firefly.renderer.tile.TileGridRenderer;
import com.inari.firefly.renderer.tile.TileGridSystem;
import com.inari.firefly.sound.SoundSystem;
import com.inari.firefly.state.StateSystem;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFContextImpl.InitMap;
import com.inari.firefly.system.view.ViewSystem;

public interface GDXConfiguration {
    
    public final static InitMap GDX_INIT_MAP = new InitMap()
        .put( FFContext.EVENT_DISPATCHER, EventDispatcher.class )
        .put( FFContext.TIMER, GDXTimerImpl.class )
        .put( FFContext.INPUT, GDXInputImpl.class )
        .put( FFContext.LOWER_SYSTEM_FACADE, GDXLowerSystemImpl.class )
        .put( FFContext.ENTITY_PROVIDER, EntityProvider.class )
        .put( FFContext.Systems.ASSET_SYSTEM, AssetSystem.class )
        .put( FFContext.Systems.STATE_SYSTEM, StateSystem.class )
        .put( FFContext.Systems.VIEW_SYSTEM, ViewSystem.class )
        .put( FFContext.Systems.ENTITY_SYSTEM, EntitySystem.class )
        .put( FFContext.Systems.ENTITY_PREFAB_SYSTEM, EntityPrefabSystem.class )
        .put( FFContext.Systems.SPRITE_VIEW_SYSTEM, SpriteViewSystem.class )
        .put( FFContext.Systems.TILE_GRID_SYSTEM, TileGridSystem.class )
        .put( FFContext.Systems.MOVEMENT_SYSTEM, MovementSystem.class )
        .put( FFContext.Systems.ENTITY_CONTROLLER_SYSTEM, ControllerSystem.class )
        .put( FFContext.Systems.ANIMATION_SYSTEM, AnimationSystem.class )
        .put( FFContext.Systems.SOUND_SYSTEM, SoundSystem.class )
        .put( FFContext.Renderer.SPRITE_VIEW_RENDERER, SpriteViewRenderer.class )
        .put( FFContext.Renderer.TILE_GRID_RENDERER, TileGridRenderer.class );

}
