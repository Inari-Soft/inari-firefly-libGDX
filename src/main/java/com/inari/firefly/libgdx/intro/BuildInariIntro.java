package com.inari.firefly.libgdx.intro;

import com.inari.firefly.state.StateChange;
import com.inari.firefly.state.StateSystem;
import com.inari.firefly.state.Workflow;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.task.Task;
import com.inari.firefly.task.TaskSystem;

public class BuildInariIntro extends Task {
    
    public static final String INTRO_NAME = "intro";
    static final String INARI_LOGO_RESOURCE_PATH = "firefly/inari.png";
    
    static final String INTRO_TEXTURE = INTRO_NAME + "_texture";
    static final String INTRO_SPRITE = INTRO_NAME + "_sprite";
    static final String INTRO_WORKFLOW = INTRO_NAME + "_workflow";
    static final String INTRO_START_STATE = INTRO_NAME + "_startState";
    static final String INTRO_START_TASK = INTRO_NAME + "_startTask";
    static final String INTRO_END_TASK = INTRO_NAME + "_endTask";
    static final String INTRO_STATE_CHANGE = INTRO_NAME + "_stateChange";
    static final String INTRO_STATE_CHANGE_CONDITION = INTRO_NAME + "_stateChangeCondition";
    static final String INTRO_ANIMATION = INTRO_NAME + "_animation";

    protected BuildInariIntro( int id ) {
        super( id );
    }

    @Override
    public void run( FFContext context ) {
        StateSystem stateSystem = context.getSystem( StateSystem.SYSTEM_KEY );
        TaskSystem taskSystem = context.getSystem( TaskSystem.SYSTEM_KEY );
        
        taskSystem.getTaskBuilder()
            .add( 
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
            .set( Workflow.INIT_TASK_NAME, INTRO_START_TASK )
            .add( Workflow.STATES, INTRO_START_STATE )
            .add( Workflow.STATE_CHANGES, new StateChange( INTRO_STATE_CHANGE, INTRO_START_STATE, null, INTRO_END_TASK, new InariIntroFinishedCondition() ) )
        .build();

        stateSystem.activateWorkflow( workflowId );
    }

}
