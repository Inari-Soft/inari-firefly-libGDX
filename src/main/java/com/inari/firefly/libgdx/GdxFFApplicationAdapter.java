package com.inari.firefly.libgdx;

import java.util.ArrayList;
import java.util.Collection;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.inari.firefly.Callback;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.dynattr.DynamicAttribueMapper;
import com.inari.firefly.libgdx.intro.InariIntro;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFContextImpl.InitMap;
import com.inari.firefly.system.FireFly;

public abstract class GdxFFApplicationAdapter extends ApplicationAdapter implements Callback {

    private FireFly firefly;
    
    @Override
    public final void create () {
        Gdx.graphics.setTitle( getTitle() );
        InitMap initMap = getInitMap();
        if ( initMap == null ) {
            initMap = GdxConfiguration.getInitMap();
        }
        firefly = new FireFly( initMap );
        FFContext context = firefly.getContext();
        
        Collection<AttributeKey<?>> dynamicAttributes = getDynamicAttributes();
        if ( dynamicAttributes != null ) {
            for ( AttributeKey<?> dynamicAttribute : dynamicAttributes ) {
                DynamicAttribueMapper.addDynamicAttribute( dynamicAttribute );
            }
        }
        
        InariIntro inariIntro = new InariIntro( this );
        context.putComponent( InariIntro.CONTEXT_KEY, inariIntro );
        inariIntro.load( context );
    }
    
    protected InitMap getInitMap() {
        return GdxConfiguration.getInitMap();
    }
    
    protected Collection<AttributeKey<?>> getDynamicAttributes() {
        return new ArrayList<AttributeKey<?>>();
    }
    
    @Override
    public final void callback( FFContext context ) {
        init( context );
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
