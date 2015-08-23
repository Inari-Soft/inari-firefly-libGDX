/*******************************************************************************
 * Copyright (c) 2015, Andreas Hefti, inarisoft@yahoo.de 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/ 
package com.inari.firefly.libgdx;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.geom.Position;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.graphics.RGBColor;
import com.inari.commons.lang.indexed.Indexer;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.asset.Asset;
import com.inari.firefly.asset.event.AssetEvent;
import com.inari.firefly.renderer.BlendMode;
import com.inari.firefly.renderer.TextureAsset;
import com.inari.firefly.renderer.sprite.SpriteAsset;
import com.inari.firefly.renderer.sprite.SpriteRenderable;
import com.inari.firefly.sound.Sound;
import com.inari.firefly.sound.SoundAsset;
import com.inari.firefly.sound.event.SoundEvent;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.ILowerSystemFacade;
import com.inari.firefly.system.Input;
import com.inari.firefly.system.view.View;
import com.inari.firefly.system.view.event.ViewEvent;

public final class GDXLowerSystemImpl implements ILowerSystemFacade {
    
    private final static float FBO_SCALER = 1.5f;
    
    private final DynArray<Texture> textures;
    private final DynArray<TextureRegion> sprites;
    private final DynArray<Viewport> viewports;
    private final DynArray<com.badlogic.gdx.audio.Sound> sounds;
    private final DynArray<Music> music;
    
    private final SpriteBatch spriteBatch;
    
    private Viewport activeViewport = null;
    private BlendMode currentBlendMode = BlendMode.NONE;
    
    GDXLowerSystemImpl() {
        textures = new DynArray<Texture>( Indexer.getIndexedObjectSize( TextureAsset.class ) );
        sprites = new DynArray<TextureRegion>( Indexer.getIndexedObjectSize( SpriteAsset.class ) );
        viewports = new DynArray<Viewport>( Indexer.getIndexedObjectSize( View.class ) );
        sounds = new DynArray<com.badlogic.gdx.audio.Sound>();
        music = new DynArray<Music>();
        spriteBatch = new SpriteBatch();
    }
    
    @Override
    public void init( FFContext context ) {
       IEventDispatcher eventDispatcher = context.getComponent( FFContext.EVENT_DISPATCHER );
       eventDispatcher.register( AssetEvent.class, this );
       eventDispatcher.register( ViewEvent.class, this );
       eventDispatcher.register( SoundEvent.class, this );
       
        // TODO init sprite batch???
    }
    
    @Override
    public final void dispose( FFContext context ) {
        IEventDispatcher eventDispatcher = context.getComponent( FFContext.EVENT_DISPATCHER );
        eventDispatcher.unregister( AssetEvent.class, this );
        eventDispatcher.unregister( ViewEvent.class, this );
        eventDispatcher.unregister( SoundEvent.class, this );
        
        for ( Viewport viewport : viewports ) {
            viewport.dispose();
        }
        for ( Texture texture : textures ) {
            texture.dispose();
        }
        sprites.clear();
        textures.clear();
        viewports.clear();
    }
    
    @Override
    public final void onAssetEvent( AssetEvent event ) {
        Asset asset = event.asset;
        switch ( event.eventType ) {
            case ASSET_LOADED: {
                if ( asset.getComponentType() == TextureAsset.class ) {
                    createTexture( (TextureAsset) asset );
                } else if ( asset.getComponentType() == SpriteAsset.class ) {
                    createSprite( (SpriteAsset) asset );
                } else if ( asset.getComponentType() == SoundAsset.class ) {
                    createSound( (SoundAsset) asset );
                }
                break;
            }
            case ASSET_DISPOSED: 
            case ASSET_DELETED: {
                if ( asset.getComponentType() == TextureAsset.class ) {
                    deleteTexture( (TextureAsset) asset );
                } else if ( asset.getComponentType() == SpriteAsset.class ) {
                    deleteSprite( (SpriteAsset) asset );
                } else if ( asset.getComponentType() == SoundAsset.class ) {
                    deleteSound( (SoundAsset) asset );
                }
                break;
            }
            default: {}
        }
    }
    
    

    @Override
    public final void onSoundEvent( SoundEvent event ) {
        if ( event.sound.isStreaming() ) {
            
        } else {
            
        }
        
    }

    @Override
    public void soundAttributesChanged( Sound sound ) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public final void onViewEvent( ViewEvent event ) {
        View view = event.view;
        int viewportId = view.index();
        switch ( event.eventType ) {
            case VIEW_CREATED: {
                Viewport viewport;
                if ( !view.isBase() ) {
                    viewport = createVirtualViewport( view );
                } else {
                    viewport = createBaseViewport( view );
                }
                viewports.set( viewportId, viewport );
                break;
            }
            case VIEW_DELETED: {
                Viewport viewport = viewports.remove( viewportId );
                if ( viewport != null ) {
                    viewport.dispose();
                }
                break;
            }
            default: {}
        }
    }

    @Override
    public final void startRendering( View view ) {
        activeViewport = viewports.get( view.index() );
        activeViewport.update( spriteBatch, view.getWorldPosition(), view.getZoom(), view.getClearColor() );
        if ( !view.isBase() ) {
            activeViewport.fbo.begin();
        } else {
            spriteBatch.begin();
        }
    }

    @Override
    public final void renderSprite( SpriteRenderable spriteRenderable, float xpos, float ypos ) {
        setColorAndBlendMode( spriteRenderable );
        TextureRegion sprite = sprites.get( spriteRenderable.getSpriteId() );
        spriteBatch.draw( sprite, xpos, xpos );
    }
    
    @Override
    public void renderSprite( SpriteRenderable spriteRenderable, float x, float y, float pivotx, float pivoty, float scalex, float scaley,float rotation ) {
        setColorAndBlendMode( spriteRenderable );
        TextureRegion sprite = sprites.get( spriteRenderable.getSpriteId() );
        spriteBatch.draw( sprite, x, y, pivotx, pivoty, sprite.getRegionWidth(), sprite.getRegionHeight(), scalex, scaley, rotation );
    }

    private void setColorAndBlendMode( SpriteRenderable sprite ) {
        RGBColor renderColor = sprite.getTintColor();
        BlendMode blendMode = sprite.getBlendMode();
        spriteBatch.setColor( renderColor.r, renderColor.g, renderColor.b, renderColor.a );
        if ( currentBlendMode != blendMode ) {
            currentBlendMode = blendMode;
            spriteBatch.setBlendFunction( currentBlendMode.gl11SourceConst, currentBlendMode.gl11DestConst );
        }
    }

    @Override
    public final void endRendering( View view ) {
        spriteBatch.flush();
        if ( !view.isBase() ) {
            activeViewport.fbo.end();
        } else {
            spriteBatch.end();
        }
        activeViewport = null;
    }

    @Override
    public final void flush( Iterator<View> virtualViews ) {
        if ( virtualViews != null && virtualViews.hasNext() ) {
            
            spriteBatch.begin();
            
            while ( virtualViews.hasNext() ) {
                View virtualView = virtualViews.next();
                Viewport viewport = viewports.get( virtualView.index() );
                Rectangle bounds = virtualView.getBounds();
                spriteBatch.draw( viewport.fboTexture, bounds.x, bounds.y );
            }
            
            spriteBatch.end();
        }
        
        spriteBatch.flush();
    }

    private void deleteSprite( SpriteAsset asset ) {
        sprites.remove( asset.index() );
    }

    private void deleteTexture( TextureAsset asset ) {
        Texture texture = textures.remove( asset.index() );
        texture.dispose();
    }

    private void createSprite( SpriteAsset asset ) {
        Rectangle textureRegion = asset.getTextureRegion();
        Texture texture = textures.get( asset.getTextureId() );
        TextureRegion sprite = new TextureRegion( texture, textureRegion.x, textureRegion.y, textureRegion.width, textureRegion.height );
        sprite.flip( false, true );
        
        sprites.set( asset.index(), sprite );
    }

    private void createTexture( TextureAsset asset ) {
        Texture texture = new Texture( asset.getResourceName() );
        asset.setWidth( texture.getWidth() );
        asset.setHeight( texture.getHeight() );
        // TODO handle with dynamic attributes for Texture
        
        textures.set( asset.index(), texture );
    }
    
    private void createSound( SoundAsset asset ) {
        if ( asset.isStreaming() ) {
            music.set( 
                asset.index(), 
                Gdx.audio.newMusic( Gdx.files.internal( asset.getResourceName() ) ) 
            );
        } else {
            sounds.set( 
                asset.index(), 
                Gdx.audio.newSound( Gdx.files.internal( asset.getResourceName() ) ) 
            );
        }
    }
    
    private void deleteSound( SoundAsset asset ) {
        if ( asset.isStreaming() ) {
            Music music = this.music.remove( asset.index() );
            if ( music != null ) {
                music.dispose();
            }
        } else {
            com.badlogic.gdx.audio.Sound sound = sounds.remove( asset.index() );
            if ( sound != null ) {
                sound.dispose();
            }
        }
    }
    
    private Viewport createBaseViewport( View view ) {
        OrthographicCamera camera = new OrthographicCamera( Gdx.graphics.getWidth(), Gdx.graphics.getHeight() );
        return new Viewport( camera, null, null );
    }

    private Viewport createVirtualViewport( View view ) {
        Rectangle bounds = view.getBounds();
        OrthographicCamera camera = new OrthographicCamera( bounds.width, bounds.height );
        FrameBuffer frameBuffer = new FrameBuffer( Format.RGB565, (int) ( bounds.width * FBO_SCALER ), (int) ( bounds.height * FBO_SCALER ), false ) ;
        TextureRegion textureRegion = new TextureRegion( frameBuffer.getColorBufferTexture() );
        textureRegion.flip( false, true );
        
        return new Viewport( camera, frameBuffer, textureRegion );
    }

    private static final class Viewport {
        final OrthographicCamera camera;
        final FrameBuffer fbo;
        final TextureRegion fboTexture;
        
        Viewport( OrthographicCamera camera, FrameBuffer fbo, TextureRegion fboTexture ) {
            super();
            this.camera = camera;
            this.fbo = fbo;
            this.fboTexture = fboTexture;
        }
        
        final void dispose() {
            if ( fbo != null ) {
                fbo.dispose();
            }
        }

        final void update( SpriteBatch spriteBatch, Position worldPosition, float zoom, RGBColor clearColor ) {
            camera.setToOrtho( true, Gdx.graphics.getWidth() * zoom, Gdx.graphics.getHeight() * zoom );
            camera.position.x = camera.position.x + worldPosition.x;
            camera.position.y = camera.position.y + worldPosition.y;
            camera.update();
            spriteBatch.setProjectionMatrix( camera.combined );
            
            Gdx.graphics.getGL20().glClearColor( clearColor.r, clearColor.g, clearColor.b, clearColor.a );
            Gdx.graphics.getGL20().glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );
        }
    }

    @Override
    public int getScreenWidth() {
        return Gdx.graphics.getWidth();
    }

    @Override
    public int getScreenHeight() {
        return Gdx.graphics.getHeight();
    }

    @Override
    public Input getInput() {
        // TODO Auto-generated method stub
        return null;
    }

}
