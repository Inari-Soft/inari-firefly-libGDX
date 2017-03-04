package com.inari.firefly.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.inari.firefly.graphics.BlendMode;
import com.inari.firefly.libgdx.test.GdxFFTestApplicationAdapter;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.RenderEvent;
import com.inari.firefly.system.RenderEventListener;

public class BlendTest extends GdxFFTestApplicationAdapter {

    @Override
    public String getTitle() {
        return "BlendTest";
    }

    @Override
    protected void init( FFContext context ) {
        final ShapeRenderer renderer = new ShapeRenderer();
        OrthographicCamera camera = new OrthographicCamera( 800, 600 );
        camera.setToOrtho( true, 800, 600 );
        camera.update();
        renderer.setProjectionMatrix( camera.combined );
        
        
        final BlendMode[] blendModes = BlendMode.values();
        
        
        
        context.registerListener( RenderEvent.TYPE_KEY, new RenderEventListener() {
            
            Color color1 = new Color( 1f, 0f, 0f, .5f );
            Color color2 = new Color( 0f, 0f, 1f, .5f );
            
            float blendAlpha = 0f;
            float dAlpha = 0.1f;
            float clearAlpha = 0f;
            float dClearAlpha = 0.1f;
        
            @Override
            public void render( RenderEvent event ) {
                
                
                blendAlpha += dAlpha;
                if ( blendAlpha < 0f ) {
                    blendAlpha = 0f;
                    dAlpha = dAlpha * -1;
                    clearAlpha += dClearAlpha;
                } else if ( blendAlpha > 1f ) {
                    blendAlpha = 1f;
                    dAlpha = dAlpha * -1;
                }
                if ( clearAlpha < 0f ) {
                    clearAlpha = 0f;
                    dClearAlpha = dClearAlpha * -1;
                    clearAlpha += dClearAlpha;
                } else if ( clearAlpha > 1f ) {
                    clearAlpha = 1f;
                    dClearAlpha = dClearAlpha * -1;
                }
                
                Gdx.gl.glClearColor( clearAlpha, clearAlpha, clearAlpha, 1f );
                Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );
                Gdx.gl.glEnable( GL20.GL_BLEND );

                Gdx.gl.glBlendColor( 0f, 0f, 0f, blendAlpha );
                color1.a = blendAlpha;
                color2.a = blendAlpha;
                
                int index = 1;
                for ( int y = 0; y < 4; y++ ) {
                    for ( int x = 0; x < 4; x++ ) {
                        Gdx.gl.glBlendFunc( blendModes[ index ].gl11SourceConst, blendModes[ index ].gl11DestConst );
                        
                        renderer.begin( ShapeType.Filled );
                        renderer.setColor( color1 );
                        renderer.rect( 50 * x, 50 * y, 20, 20 );
                        renderer.end();
                        
                        Gdx.gl.glBlendFunc( blendModes[ index ].gl11SourceConst, blendModes[ index ].gl11DestConst );
                        
                        renderer.begin( ShapeType.Filled );
                        renderer.setColor( color2 );
                        renderer.rect( 50 * x + 10, 50 * y + 10, 20, 20 );
                        renderer.end();
                        
                        index++;
                    }
                }
                
                Gdx.gl.glDisable( GL20.GL_BLEND );
            }
        } );
        
    }

    public static void main (String[] arg) {
        try {
            LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
            config.resizable = true;
            config.width = 800;
            config.height = 600;
            new LwjglApplication( new BlendTest(), config );
        } catch ( Throwable t ) {
            t.printStackTrace();
        }
    }

}
