package com.inari.firefly.libgdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FireFlyApp;

public abstract class GdxFFTestAdapter extends ApplicationAdapter {
    
    private FireFlyApp firefly;
    
    @Override
    public final void create () {
        Gdx.graphics.setTitle( getTitle() );
        firefly = new GdxFireflyApp();
        FFContext context = firefly.getContext();
        initTest( context );
    }
    
    public abstract void initTest( FFContext context );

    @Override
    public final void render () {
        firefly.update();
        firefly.render();
    }
    
    public abstract String getTitle();


}
