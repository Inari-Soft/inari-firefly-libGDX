package com.inari.firefly.libgdx.intro;

import com.inari.firefly.asset.AssetNameKey;
import com.inari.firefly.state.State;
import com.inari.firefly.state.StateChange;
import com.inari.firefly.state.StateSystem;
import com.inari.firefly.state.Workflow;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.task.Task;
import com.inari.firefly.task.TaskSystem;

public class BuildInariIntro extends Task {
    
    public static final String INTRO_NAME = "intro";
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

    protected BuildInariIntro( int id ) {
        super( id );
    }

    @Override
    public void run( FFContext context ) {
        StateSystem stateSystem = context.getSystem( StateSystem.CONTEXT_KEY );
        TaskSystem taskSystem = context.getSystem( TaskSystem.CONTEXT_KEY );
        
        taskSystem.getTaskBuilder()
            .set( 
                Task.NAME.value( INTRO_START_TASK ),
                Task.REMOVE_AFTER_RUN.value( true ) 
            )
        .buildAndNext( InitInariIntro.class )
            .set( Task.NAME, INTRO_END_TASK )
            .set( Task.REMOVE_AFTER_RUN, true )
        .build( DisposeInariIntro.class );
            
        
        int workflowId = stateSystem.getWorkflowBuilder()
            .set( Workflow.NAME, INTRO_WORKFLOW )
            .set( Workflow.START_STATE_NAME, INTRO_START_STATE )
            .set( Workflow.INIT_TASK_ID, taskSystem.getTaskId( INTRO_START_TASK ) )
        .build();
                
        stateSystem.getStateBuilder()
            .set( State.WORKFLOW_ID, workflowId )
            .set( State.NAME, INTRO_START_STATE )
        .build();
        
        stateSystem.getStateChangeBuilder()
            .set( 
                StateChange.NAME.value( INTRO_STATE_CHANGE ),
                StateChange.WORKFLOW_ID.value( workflowId ),
                StateChange.FORM_STATE_ID.value( stateSystem.getStateId( INTRO_START_STATE ) ),
                StateChange.CONDITION_TYPE_NAME.value( InariIntroFinishedCondition.class.getName() ),
                StateChange.TASK_ID.value( taskSystem.getTaskId( INTRO_END_TASK ) )
            )
        .build();
        
        stateSystem.activateWorkflow( workflowId );
    }

}
