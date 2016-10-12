package com.inari.firefly.libgdx.intro;

import com.inari.firefly.control.state.StateChange;
import com.inari.firefly.control.state.StateSystem;
import com.inari.firefly.control.state.Workflow;
import com.inari.firefly.control.task.Task;
import com.inari.firefly.control.task.TaskSystem;
import com.inari.firefly.control.task.WorkflowTaskTrigger;

public class BuildInariIntro extends Task {
    
    public static final String INTRO_NAME = "intro";
    static final String INARI_LOGO_RESOURCE_PATH = "firefly/inari.png";
    
    public static final String INTRO_TEXTURE = INTRO_NAME + "_texture";
    public static final String INTRO_SPRITE = INTRO_NAME + "_sprite";
    public static final String INTRO_WORKFLOW = INTRO_NAME + "_workflow";
    static final String INTRO_START_STATE = INTRO_NAME + "_startState";
    static final String INTRO_START_TASK = INTRO_NAME + "_startTask";
    static final String INTRO_STATE_CHANGE = INTRO_NAME + "_stateChange";
    static final String INTRO_STATE_CHANGE_CONDITION = INTRO_NAME + "_stateChangeCondition";
    static final String INTRO_ANIMATION = INTRO_NAME + "_animation";

    protected BuildInariIntro( int id ) {
        super( id );
    }

    @Override
    public void runTask() {
        StateSystem stateSystem = context.getSystem( StateSystem.SYSTEM_KEY );
        TaskSystem taskSystem = context.getSystem( TaskSystem.SYSTEM_KEY );
        
        taskSystem.getTaskBuilder()
            .set( Task.NAME, INTRO_START_TASK )
            .set( Task.REMOVE_AFTER_RUN, true ) 
            .set( 
                Task.TRIGGER, 
                new WorkflowTaskTrigger( 
                    INTRO_WORKFLOW, 
                    WorkflowTaskTrigger.Type.ENTER_STATE, 
                    INTRO_START_STATE
                ) 
            )
        .build( InitInariIntro.class );
            
        
        int workflowId = stateSystem.getWorkflowBuilder()
            .set( Workflow.NAME, INTRO_WORKFLOW )
            .set( Workflow.START_STATE_NAME, INTRO_START_STATE )
            .add( Workflow.STATES, INTRO_START_STATE )
            .add( Workflow.STATE_CHANGES, new StateChange( INTRO_STATE_CHANGE, INTRO_START_STATE, null, new InariIntroFinishedCondition() ) )
        .build();

        stateSystem.activateWorkflow( workflowId );
    }

}
