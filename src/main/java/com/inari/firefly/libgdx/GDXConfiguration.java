package com.inari.firefly.libgdx;

import com.inari.commons.event.EventDispatcher;
import com.inari.firefly.animation.AnimationSystem;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.control.ComponentControllerSystem;
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
import com.inari.firefly.task.TaskSystem;

public interface GDXConfiguration {
    
    public final static InitMap GDX_INIT_MAP = new InitMap()
        .put( FFContext.EVENT_DISPATCHER, EventDispatcher.class )
        .put( FFContext.TIMER, GDXTimerImpl.class )
        .put( FFContext.INPUT, GDXInputImpl.class )
        .put( FFContext.LOWER_SYSTEM_FACADE, GDXLowerSystemImpl.class )
        .put( FFContext.ENTITY_PROVIDER, EntityProvider.class )
        .put( AssetSystem.CONTEXT_KEY, AssetSystem.class )
        .put( StateSystem.CONTEXT_KEY, StateSystem.class )
        .put( ViewSystem.CONTEXT_KEY, ViewSystem.class )
        .put( EntitySystem.CONTEXT_KEY, EntitySystem.class )
        .put( EntityPrefabSystem.CONTEXT_KEY, EntityPrefabSystem.class )
        .put( SpriteViewSystem.CONTEXT_KEY, SpriteViewSystem.class )
        .put( TileGridSystem.CONTEXT_KEY, TileGridSystem.class )
        .put( MovementSystem.CONTEXT_KEY, MovementSystem.class )
        .put( ComponentControllerSystem.CONTEXT_KEY, ComponentControllerSystem.class )
        .put( AnimationSystem.CONTEXT_KEY, AnimationSystem.class )
        .put( SoundSystem.CONTEXT_KEY, SoundSystem.class )
        .put( SpriteViewRenderer.CONTEXT_KEY, SpriteViewRenderer.class )
        .put( TileGridRenderer.CONTEXT_KEY, TileGridRenderer.class )
        .put( TaskSystem.CONTEXT_KEY, TaskSystem.class );

}
