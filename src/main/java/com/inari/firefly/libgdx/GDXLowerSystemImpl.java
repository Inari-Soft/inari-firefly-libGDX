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
import com.inari.commons.geom.Rectangle;
import com.inari.commons.graphics.RGBColor;
import com.inari.commons.lang.indexed.Indexer;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.asset.Asset;
import com.inari.firefly.asset.event.AssetEvent;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.renderer.BlendMode;
import com.inari.firefly.renderer.sprite.SpriteAsset;
import com.inari.firefly.renderer.sprite.SpriteRenderable;
import com.inari.firefly.renderer.sprite.TextureAsset;
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
    
    private Viewport baseViewport;
    private Viewport activeViewport = null;
    
    private final SpriteBatch spriteBatch;
    
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
        int viewportOrder = view.getOrder();
        switch ( event.eventType ) {
            case VIEW_CREATED: {
                Viewport viewport;
                if ( !view.isBase() ) {
                    viewport = createVirtualViewport( view );
                } else {
                    viewport = createBaseViewport( view );
                    baseViewport = viewport;
                }
                viewports.set( viewportOrder, viewport );
                break;
            }
            case VIEW_DELETED: {
                Viewport viewport = viewports.remove( viewportOrder );
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
        activeViewport = viewports.get( view.getOrder() );
        activeViewport.update( spriteBatch );
        if ( activeViewport != baseViewport ) {
            activeViewport.fbo.begin();
        }
    }

    @Override
    public final void renderSprite( SpriteRenderable renderableSprite, ETransform transform ) {
        if ( transform.hasScale() || transform.hasRotation() ) {
            TextureRegion sprite = sprites.get( renderableSprite.getSpriteId() );
            RGBColor renderColor = renderableSprite.getTintColor();
            BlendMode blendMode = renderableSprite.getBlendMode();
            if ( currentBlendMode != blendMode ) {
                currentBlendMode = blendMode;
                spriteBatch.setBlendFunction( currentBlendMode.gl11SourceConst, currentBlendMode.gl11DestConst );
            }
            
            spriteBatch.setColor( renderColor.r, renderColor.g, renderColor.b, renderColor.a );
            spriteBatch.draw(
                sprite,
                transform.getXpos(),
                transform.getYpos(),
                transform.getRotationXPos(),
                transform.getRotationYPos(),
                sprite.getRegionWidth(),
                sprite.getRegionHeight(),
                transform.getXscale(),
                transform.getYscale(),
                transform.getRotation()
            );
            
            return;
        }
        
        renderSprite( renderableSprite, transform.getXpos(), transform.getYpos() );
    }

    @Override
    public final void renderSprite( SpriteRenderable renderableSprite, float xpos, float ypos ) {
        TextureRegion sprite = sprites.get( renderableSprite.getSpriteId() );
        RGBColor renderColor = renderableSprite.getTintColor();
        spriteBatch.setColor( renderColor.r, renderColor.g, renderColor.b, renderColor.a );
        
        spriteBatch.draw( 
            sprite,
            xpos,
            xpos
        );
    }

    @Override
    public final void endRendering( View view ) {
        spriteBatch.flush();
        if ( activeViewport != baseViewport ) {
            activeViewport.fbo.end();
        }
        activeViewport = null;
    }

    @Override
    public final void flush() {
        if ( viewports.size() > 1 ) {
            
            spriteBatch.begin();
            
            for ( Viewport viewport : viewports ) {
                if ( viewport == baseViewport ) {
                    continue;
                }
                Rectangle bounds = viewport.view.getBounds();
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
        return new Viewport( view, camera, null, null );
    }

    private Viewport createVirtualViewport( View view ) {
        Rectangle bounds = view.getBounds();
        OrthographicCamera camera = new OrthographicCamera( bounds.width, bounds.height );
        FrameBuffer frameBuffer = new FrameBuffer( Format.RGB565, (int) ( bounds.width * FBO_SCALER ), (int) ( bounds.height * FBO_SCALER ), false ) ;
        TextureRegion textureRegion = new TextureRegion( frameBuffer.getColorBufferTexture() );
        textureRegion.flip( false, true );
        
        return new Viewport( view, camera, frameBuffer, textureRegion );
    }

    private static final class Viewport {
        final View view;
        final OrthographicCamera camera;
        final FrameBuffer fbo;
        final TextureRegion fboTexture;
        
        Viewport( View view, OrthographicCamera camera, FrameBuffer fbo, TextureRegion fboTexture ) {
            super();
            this.view = view;
            this.camera = camera;
            this.fbo = fbo;
            this.fboTexture = fboTexture;
        }
        
        final void dispose() {
            if ( fbo != null ) {
                fbo.dispose();
            }
        }

        final void update( SpriteBatch spriteBatch ) {
            float zoom = view.getZoom();
            camera.setToOrtho( true, Gdx.graphics.getWidth() * zoom, Gdx.graphics.getHeight() * zoom );
            spriteBatch.setProjectionMatrix( camera.combined );
            
            RGBColor clearColor = view.getClearColor();
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
