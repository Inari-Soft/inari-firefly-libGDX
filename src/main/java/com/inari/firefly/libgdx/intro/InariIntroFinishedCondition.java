package com.inari.firefly.libgdx.intro;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
            animationSystem = context.getSystem( AnimationSystem.SYSTEM_KEY );
            animationId = animationSystem.getAnimationId( BuildInariIntro.INTRO_ANIMATION );
        }
        
        return animationSystem.isFinished( animationId ) && 
               ( Gdx.input.isKeyPressed( Input.Keys.SPACE ) || Gdx.input.isTouched() || Gdx.input.isButtonPressed( Input.Buttons.LEFT ) );
    }
}