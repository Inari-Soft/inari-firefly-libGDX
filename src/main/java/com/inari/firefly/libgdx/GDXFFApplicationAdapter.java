package com.inari.firefly.libgdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.inari.firefly.app.Callback;
import com.inari.firefly.app.FFApplicationManager;
import com.inari.firefly.component.dynattr.DynamicAttribueMapper;
import com.inari.firefly.libgdx.intro.InariIntro;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFContextImpl.InitMap;
import com.inari.firefly.system.FireFly;

public abstract class GDXFFApplicationAdapter extends ApplicationAdapter implements Callback {

    private FireFly firefly;
    private FFApplicationManager applicationManager;
    
    @Override
    public final void create () {
        Gdx.graphics.setTitle( getTitle() );
        InitMap initMap = getInitMap();
        if ( initMap == null ) {
            initMap = GDXConfiguration.getInitMap();
        }
        firefly = new FireFly( initMap );
        FFContext context = firefly.getContext();
        
        createGDXSpecificDynamicAttribute();
        
        applicationManager = getApplicationManager();
        
        InariIntro inariIntro = new InariIntro( this );
        context.putComponent( InariIntro.CONTEXT_KEY, inariIntro );
        inariIntro.load( context );
    }
    
    private void createGDXSpecificDynamicAttribute() {
        DynamicAttribueMapper.addDynamicAttribute( GDXConfiguration.DynamicAttributes.TEXTURE_COLOR_FILTER_NAME );
    }

    @Override
    public final void render () {
        firefly.update();
        firefly.render();
    }
    
    public abstract String getTitle();
    protected abstract FFApplicationManager getApplicationManager();
    protected abstract InitMap getInitMap();

    /** This is the callback from InariIntro called when the intro has finished and disposed.
     */
    @Override
    public final void callback( FFContext context ) {
        context.putComponent( InariIntro.CONTEXT_KEY, null );
        if ( applicationManager != null ) {
            applicationManager.init( context );
        } else {
            Gdx.app.exit();
        }
    }

    @Override
    public final void resize( int width, int height ) {
        // TODO???
    }

    @Override
    public final void pause() {
        if ( applicationManager != null ) {
            applicationManager.handlePause( firefly.getContext() );
        }
    }

    @Override
    public final void resume() {
        if ( applicationManager != null ) {
            applicationManager.handleResume( firefly.getContext() );
        }
    }

    @Override
    public final void dispose() {
        if ( applicationManager != null ) {
            applicationManager.dispose( firefly.getContext() );
        }
        
        firefly.dispose();
    }

}
