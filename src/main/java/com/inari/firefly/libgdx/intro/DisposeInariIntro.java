package com.inari.firefly.libgdx.intro;

import com.inari.firefly.animation.AnimationSystem;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.control.ControllerSystem;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.state.StateSystem;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.task.Task;
import com.inari.firefly.task.TaskSystem;

public final class DisposeInariIntro extends Task {

    public DisposeInariIntro( int id ) {
        super( id );
    }

    @Override
    public final void run( FFContext context ) {
        AssetSystem assetSystem = context.getSystem( AssetSystem.CONTEXT_KEY );
        EntitySystem entitySystem = context.getSystem( EntitySystem.CONTEXT_KEY );
        StateSystem stateSystem = context.getSystem( StateSystem.CONTEXT_KEY );
        TaskSystem taskSystem = context.getSystem( TaskSystem.CONTEXT_KEY );
        AnimationSystem animationSystem = context.getSystem( AnimationSystem.CONTEXT_KEY );
        ControllerSystem controllerSystem = context.getSystem( ControllerSystem.CONTEXT_KEY );
        
        entitySystem.deleteAll();
        assetSystem.deleteAssets( BuildInariIntro.INTRO_NAME );
        stateSystem.clear();
        taskSystem.clear();
        animationSystem.clear();
        controllerSystem.clear();
    }
    
}