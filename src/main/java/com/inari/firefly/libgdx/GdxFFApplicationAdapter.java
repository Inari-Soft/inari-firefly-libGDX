package com.inari.firefly.libgdx;

import java.util.ArrayList;
import java.util.Collection;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.dynattr.DynamicAttribueMapper;
import com.inari.firefly.libgdx.intro.BuildInariIntro;
import com.inari.firefly.state.event.WorkflowEvent;
import com.inari.firefly.state.event.WorkflowEventListener;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FireFly;
import com.inari.firefly.task.Task;
import com.inari.firefly.task.event.TaskEvent;
import com.inari.firefly.task.event.TaskEvent.Type;

public abstract class GdxFFApplicationAdapter extends ApplicationAdapter implements WorkflowEventListener {

    private FireFly firefly;
    
    @Override
    public final void create () {
        Gdx.graphics.setTitle( getTitle() );
        firefly = new GdxFirefly();
        FFContext context = firefly.getContext();
        
        Collection<AttributeKey<?>> dynamicAttributes = getDynamicAttributes();
        if ( dynamicAttributes != null ) {
            for ( AttributeKey<?> dynamicAttribute : dynamicAttributes ) {
                DynamicAttribueMapper.addDynamicAttribute( dynamicAttribute );
            }
        }
        
        context.registerListener( WorkflowEvent.class, this );
        int startTaskId = context.getComponentBuilder( Task.TYPE_KEY )
            .set( Task.REMOVE_AFTER_RUN, true )
            .set( Task.NAME, BuildInariIntro.INTRO_NAME )
            .build( BuildInariIntro.class );
        context.notify( new TaskEvent( Type.RUN_TASK, startTaskId ) );
    }

    @Override
    public final void onEvent( WorkflowEvent event ) {
        if ( event.type == WorkflowEvent.Type.WORKFLOW_FINISHED ) {
            init( firefly.getContext() );
        }
    }

    protected Collection<AttributeKey<?>> getDynamicAttributes() {
        return new ArrayList<AttributeKey<?>>();
    }
    
    @Override
    public final void render () {
        firefly.update();
        firefly.render();
    }

    @Override
    public final void resize( int width, int height ) {
        resize( width, height, firefly.getContext() );
    }

    @Override
    public final void pause() {
        pause( firefly.getContext() );
    }

    @Override
    public final void resume() {
        resume( firefly.getContext() );
    }

    @Override
    public final void dispose() {
        firefly.dispose();
    }
    
    
    public abstract String getTitle();

    protected abstract void init( FFContext context );
    protected abstract void resize( int width, int height, FFContext context);
    protected abstract void pause( FFContext context );
    protected abstract void resume( FFContext context );
    
}
