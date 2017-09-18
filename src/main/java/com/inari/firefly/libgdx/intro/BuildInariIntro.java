package com.inari.firefly.libgdx.intro;

import com.inari.firefly.control.state.StateChange;
import com.inari.firefly.control.state.Workflow;
import com.inari.firefly.control.state.WorkflowEventTrigger;
import com.inari.firefly.control.task.Task;

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
        context.getComponentBuilder( Task.TYPE_KEY, InitInariIntro.class )
            .set( Task.NAME, INTRO_START_TASK )
            .set( Task.REMOVE_AFTER_RUN, true ) 
            .add( 
                Task.TRIGGER, 
                new WorkflowEventTrigger(
                    INTRO_WORKFLOW, 
                    WorkflowEventTrigger.Type.ENTER_STATE, 
                    INTRO_START_STATE
                ) 
            )
        .build();
        context.getComponentBuilder( Workflow.TYPE_KEY )
            .set( Workflow.NAME, INTRO_WORKFLOW )
            .set( Workflow.START_STATE_NAME, INTRO_START_STATE )
            .add( Workflow.STATES, INTRO_START_STATE )
            .add( Workflow.STATE_CHANGES, new StateChange( INTRO_STATE_CHANGE, INTRO_START_STATE, null, new InariIntroFinishedCondition() ) )
        .activate();
    }

}
