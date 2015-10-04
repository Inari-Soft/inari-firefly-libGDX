package com.inari.firefly.libgdx.intro;

import com.inari.commons.lang.TypedKey;
import com.inari.firefly.Disposable;
import com.inari.firefly.Loadable;
import com.inari.firefly.animation.AnimationSystem;
import com.inari.firefly.app.Callback;
import com.inari.firefly.asset.AssetNameKey;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.control.ComponentControllerSystem;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.state.State;
import com.inari.firefly.state.StateChange;
import com.inari.firefly.state.StateSystem;
import com.inari.firefly.state.Workflow;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.task.Task;
import com.inari.firefly.task.TaskSystem;

public final class InariIntro implements Loadable, Disposable {
    
    public static final TypedKey<InariIntro> CONTEXT_KEY = TypedKey.create( "InariIntro", InariIntro.class );
    
    static final String INTRO_NAME = "intro";
    
    static final String INARI_LOGO_RESOURCE_PATH = "firefly/inari.png";
    static final AssetNameKey INARI_ASSET_KEY = new AssetNameKey( INTRO_NAME, "inari" );
    static final AssetNameKey INARI_SPRITE_ASSET_KEY = new AssetNameKey( INTRO_NAME, "inariSprite" );
    
    static final String INTRO_WORKFLOW = INTRO_NAME + "_workflow";
    static final String INTRO_START_STATE = INTRO_NAME + "_startState";
    static final String INTRO_START_TASK = INTRO_NAME + "_startTask";
    static final String INTRO_END_TASK = INTRO_NAME + "_endTask";
    static final String INTRO_STATE_CHANGE = INTRO_NAME + "_stateChange";
    static final String INTRO_STATE_CHANGE_CONDITION = INTRO_NAME + "_stateChangeCondition";
    static final String INTRO_ANIMATION = INTRO_NAME + "_animation";
    
    static final int INTRO_TEX_WIDTH = 385;
    static final int INTRO_TEX_HEIGHT = 278;
    
    private final Callback callback;
    private Disposable disposable;

    public InariIntro( Callback callback ) {
        this.callback = callback;
    }

    @Override
    public final Disposable load( FFContext context ) {
        StateSystem stateSystem = context.getComponent( StateSystem.CONTEXT_KEY );
        TaskSystem taskSystem = context.getComponent( TaskSystem.CONTEXT_KEY );
        
        taskSystem.getTaskBuilder( InitInariIntroTask.class )
            .set( Task.NAME, INTRO_START_TASK )
            .set( Task.REMOVE_AFTER_RUN, true )
        .buildAndNext( DisposeInariIntroTask.class )
            .set( Task.NAME, INTRO_END_TASK )
            .set( Task.REMOVE_AFTER_RUN, true )
        .build();
            
        
        Workflow workflow = stateSystem.getWorkflowBuilder()
            .set( Workflow.NAME, INTRO_WORKFLOW )
            .set( Workflow.START_STATE_NAME, INTRO_START_STATE )
            .set( Workflow.INIT_TASK_ID, taskSystem.getTaskId( INTRO_START_TASK ) )
        .build();
                
        stateSystem.getStateBuilder()
            .set( State.WORKFLOW_ID, workflow.getId() )
            .set( State.NAME, INTRO_START_STATE )
        .build();
        
        stateSystem.getStateChangeBuilder()
            .set( StateChange.NAME, INTRO_STATE_CHANGE )
            .set( StateChange.WORKFLOW_ID, workflow.getId() )
            .set( StateChange.FORM_STATE_ID, stateSystem.getStateId( INTRO_START_STATE ) )
            .set( StateChange.CONDITION_TYPE_NAME, InariIntroFinishedCondition.class.getName() )
            .set( StateChange.TASK_ID, taskSystem.getTaskId( INTRO_END_TASK ) )
        .build();
        
        disposable = new Disposable() {

            @Override
            public void dispose( FFContext context ) {
                AssetSystem assetSystem = context.getComponent( AssetSystem.CONTEXT_KEY );
                EntitySystem entitySystem = context.getComponent( EntitySystem.CONTEXT_KEY );
                StateSystem stateSystem = context.getComponent( StateSystem.CONTEXT_KEY );
                TaskSystem taskSystem = context.getComponent( TaskSystem.CONTEXT_KEY );
                AnimationSystem animationSystem = context.getComponent( AnimationSystem.CONTEXT_KEY );
                ComponentControllerSystem controllerSystem = context.getComponent( ComponentControllerSystem.CONTEXT_KEY );
                
                entitySystem.deleteAll();
                assetSystem.deleteAssets( INTRO_NAME );
                stateSystem.clear();
                taskSystem.clear();
                animationSystem.clear();
                controllerSystem.clear();
            }
            
        };
        
        stateSystem.activateWorkflow( workflow.getId() );
        
        return disposable;
    }

    @Override
    public final void dispose( FFContext context ) {
        if ( disposable != null ) {
            disposable.dispose( context );
        }
        callback.callback( context );
    }

}
