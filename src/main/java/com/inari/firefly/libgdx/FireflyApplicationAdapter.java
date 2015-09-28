package com.inari.firefly.libgdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.inari.firefly.Disposable;
import com.inari.firefly.libgdx.intro.InariIntro;
import com.inari.firefly.libgdx.intro.IntroInit;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FireFly;

public abstract class FireflyApplicationAdapter extends ApplicationAdapter implements IntroInit {

    private FireFly firefly;
    private Disposable introDispose;
    
    @Override
    public final void create () {
        Gdx.graphics.setTitle( getTitle() );
        firefly = new FireFly( GDXConfiguration.GDX_INIT_MAP );
        FFContext context = firefly.getContext();
        context.putComponent( IntroInit.CONTEXT_KEY, this );
        
        introDispose = ( new InariIntro() ).load( context );
    }
    
    @Override
    public final void render () {
        firefly.update();
        firefly.render();
    }
    
    public abstract String getTitle();

    @Override
    public final void dispose( FFContext context ) {
        introDispose.dispose( context );
    }

}
