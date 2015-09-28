package com.inari.firefly.libgdx.intro;

import com.inari.firefly.animation.AnimationSystem;
import com.inari.firefly.state.StateChangeCondition;
import com.inari.firefly.state.Workflow;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFTimer;

public final class InariIntroFinishedCondition implements StateChangeCondition {
    
    private AnimationSystem animationSystem;
    private int animationId = -1;

    @Override
    public final boolean check( FFContext context, Workflow workflow, FFTimer timer ) {
        if ( animationSystem == null ) {
            animationSystem = context.getComponent( AnimationSystem.CONTEXT_KEY );
            animationId = animationSystem.getAnimationId( InariIntro.INTRO_ANIMATION );
        }
        
        return animationSystem.isFinished( animationId );
    }
}