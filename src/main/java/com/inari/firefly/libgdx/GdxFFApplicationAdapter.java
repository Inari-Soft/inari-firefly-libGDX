package com.inari.firefly.libgdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.inari.commons.geom.PositionF;
import com.inari.commons.geom.Rectangle;
import com.inari.firefly.asset.Asset;
import com.inari.firefly.control.ControllerSystem;
import com.inari.firefly.control.state.StateSystem;
import com.inari.firefly.control.state.WorkflowEvent;
import com.inari.firefly.control.state.WorkflowEventListener;
import com.inari.firefly.control.task.Task;
import com.inari.firefly.control.task.TaskSystem;
import com.inari.firefly.control.task.TaskSystemEvent;
import com.inari.firefly.control.task.TaskSystemEvent.Type;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.graphics.text.FontAsset;
import com.inari.firefly.graphics.view.View;
import com.inari.firefly.libgdx.intro.BuildInariIntro;
import com.inari.firefly.physics.animation.AnimationSystem;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FireFlyApp;

public abstract class GdxFFApplicationAdapter extends ApplicationAdapter implements WorkflowEventListener {

    private FireFlyApp firefly;
    
    @Override
    public final void create () {
        Gdx.graphics.setTitle( getTitle() );
        firefly = new GdxFireflyApp();
        FFContext context = firefly.getContext();
        
        context.loadSystem( StateSystem.SYSTEM_KEY );
        context.loadSystem( TaskSystem.SYSTEM_KEY );
        context.loadSystem( AnimationSystem.SYSTEM_KEY );

        context.registerListener( WorkflowEvent.TYPE_KEY, this );
        int startTaskId = context.getComponentBuilder( Task.TYPE_KEY, BuildInariIntro.class )
            .set( Task.REMOVE_AFTER_RUN, true )
            .set( Task.NAME, BuildInariIntro.INTRO_NAME )
            .build();
        context.notify( new TaskSystemEvent( Type.RUN_TASK, startTaskId ) );
    }

    @Override
    public final void onEvent( WorkflowEvent event ) {
        if ( event.type == WorkflowEvent.Type.WORKFLOW_FINISHED ) {
            
            FFContext context = firefly.getContext();
            clearIntro( context );
            loadDefaultFontAsset( context );
            init( context );
            
            context.disposeListener( WorkflowEvent.TYPE_KEY, this );
        }
    }

    private void loadDefaultFontAsset( FFContext context ) {
        context.getComponentBuilder( Asset.TYPE_KEY, FontAsset.class )
            .set( FontAsset.NAME, FFContext.DEFAULT_FONT )
            .set( FontAsset.TEXTURE_RESOURCE_NAME, "firefly/fireflyMicroFont.png" )
            .set( FontAsset.CHAR_WIDTH, 8 )
            .set( FontAsset.CHAR_HEIGHT, 16 )
            .set( FontAsset.CHAR_SPACE, 0 )
            .set( FontAsset.LINE_SPACE, 0 )
            .set( FontAsset.CHAR_TEXTURE_MAP, new char[][] {
                { 'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',' ' },
                { 'A','B','C','D','E','F','G','H','I','J','J','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',' ' },
                { '1','2','3','4','5','6','7','8','9','0','!','@','£','$','%','?','&','*','(',')','-','+','=','"','.',',',':' }
            } )
        .activate();
    }

    private void clearIntro( FFContext context ) {
        context.getSystem( EntitySystem.SYSTEM_KEY ).deleteAllActive();
        context.deleteSystemComponent( Asset.TYPE_KEY, BuildInariIntro.INTRO_TEXTURE );
        context.disposeSystem( StateSystem.SYSTEM_KEY );
        context.disposeSystem( TaskSystem.SYSTEM_KEY );
        context.disposeSystem( AnimationSystem.SYSTEM_KEY );
        context.getSystem( ControllerSystem.SYSTEM_KEY ).clearSystem();
    }
    
    @Override
    public final void render () {
        if ( firefly.exit() ) {
            firefly.dispose();
            Gdx.app.exit();
            return;
        }
        
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
        
    }
    
    protected final void fitBaseViewportToScreen( int width, int height, int baseWidth, int baseHeight, boolean centerCamera ) {
        FFContext context = firefly.getContext();
        View baseView = context.getSystemComponent( View.TYPE_KEY, 0 );
        PositionF worldPosition = baseView.getWorldPosition();
        Rectangle bounds = baseView.getBounds();
        
        float targetRatio = (float) height / width;
        float sourceRatio = (float) baseHeight / baseWidth;
        boolean fitToWidth = targetRatio > sourceRatio;

        if ( fitToWidth ) {
            bounds.width = baseWidth;
            bounds.height = Math.round( ( baseHeight / sourceRatio ) * targetRatio );
            if ( centerCamera ) {
                worldPosition.y = - ( bounds.height - baseHeight ) / 2;
            }
        } else {
            bounds.width = Math.round( ( baseWidth / targetRatio ) * sourceRatio );
            bounds.height = baseHeight;
            if ( centerCamera ) {
                worldPosition.x = - ( bounds.width - baseWidth ) / 2;
            }
        }
    }
    
    
    public abstract String getTitle();

    protected abstract void init( FFContext context );
    protected abstract void resize( int width, int height, FFContext context);
    protected abstract void pause( FFContext context );
    protected abstract void resume( FFContext context );
    
}
