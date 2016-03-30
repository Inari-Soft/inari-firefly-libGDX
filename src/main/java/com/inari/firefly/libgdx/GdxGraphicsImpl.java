/*******************************************************************************
  * Copyright (c) 2015 - 2016, Andreas Hefti, inarisoft@yahoo.de 
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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.inari.commons.StringUtils;
import com.inari.commons.geom.Position;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.graphics.RGBColor;
import com.inari.commons.lang.TypedKey;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.FFInitException;
import com.inari.firefly.filter.IColorFilter;
import com.inari.firefly.graphics.BlendMode;
import com.inari.firefly.graphics.ShaderAsset;
import com.inari.firefly.graphics.SpriteRenderable;
import com.inari.firefly.graphics.TextureAsset;
import com.inari.firefly.graphics.shape.EShape;
import com.inari.firefly.graphics.sprite.SpriteAsset;
import com.inari.firefly.libgdx.filter.ColorFilteredTextureData;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.external.FFGraphics;
import com.inari.firefly.system.external.TransformData;
import com.inari.firefly.system.view.View;
import com.inari.firefly.system.view.ViewEvent;

public final class GdxGraphicsImpl implements FFGraphics {
    
    private final static float FBO_SCALER = 2f;
    
    private FFContext context;
    
    private final DynArray<Texture> textures;
    private final DynArray<TextureRegion> sprites;
    private final DynArray<Viewport> viewports;

    private final SpriteBatch spriteBatch;
    private final ShapeRenderer shapeRenderer;
    private final Matrix4 shapeTransformMatrix;
    
    private Viewport baseViewport = null;
    private View baseView = null;
    private Viewport activeViewport = null;
    private BlendMode currentBlendMode = BlendMode.NONE;
    
    GdxGraphicsImpl() {
        textures = new DynArray<Texture>( 100 );
        sprites = new DynArray<TextureRegion>( 100, 50 );
        viewports = new DynArray<Viewport>( 50 );
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        shapeTransformMatrix = shapeRenderer.getTransformMatrix();
    }
    
    @Override
    public void init( FFContext context ) {
        this.context = context;
        
       context.registerListener( ViewEvent.class, this );
    }
    
    @Override
    public final void dispose( FFContext context ) {
        context.disposeListener( ViewEvent.class, this );
        
        for ( Viewport viewport : viewports ) {
            viewport.dispose();
        }
        for ( Texture texture : textures ) {
            texture.dispose();
        }
        sprites.clear();
        textures.clear();
        viewports.clear();
        
        if ( baseViewport != null ) {
            baseViewport.dispose();
        }
        baseView = null;
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
                    baseViewport = viewport;
                    baseView = view;
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

    // TODO make it simpler
    @Override
    public final int createTexture( TextureAsset asset ) {
        Texture texture = null;
        int textureId = -1;
        String colorFilterName = asset.getDynamicAttribute( GdxFirefly.DynamicAttributes.TEXTURE_COLOR_FILTER_NAME );
        if ( !StringUtils.isBlank( colorFilterName ) ) {
            TypedKey<IColorFilter> filterKey = TypedKey.create( colorFilterName, IColorFilter.class );
            IColorFilter colorFilter = context.getProperty( filterKey );
            if ( colorFilter != null ) {
                ColorFilteredTextureData textureData = new ColorFilteredTextureData( asset.getResourceName(), colorFilter );
                texture = new Texture( textureData );
                textureId = textures.add( texture );
            }
        } 
        
        if ( textureId < 0 ) {
            textureId = createTexture( asset.getResourceName() );
            texture = textures.get( textureId );
        }
        
        asset.setWidth( texture.getWidth() );
        asset.setHeight( texture.getHeight() );

        return textureId;
    }
    
    @Override
    public final int createTexture( String resourceName ) {
        return textures.add( new Texture( Gdx.files.internal( resourceName ) ) );
    }

    @Override
    public final void disposeTexture( int textureId ) {
        Texture texture = textures.remove( textureId );
        texture.dispose();
    }

    @Override
    public final int createSprite( int textureId, Rectangle textureRegion ) {
        if ( !textures.contains( textureId ) ) {
            throw new FFInitException( "Texture with id: " + textureId + "not loaded" );
        }
        
        Texture texture = textures.get( textureId );
        TextureRegion sprite = new TextureRegion( texture, textureRegion.x, textureRegion.y, textureRegion.width, textureRegion.height );
        sprite.flip( false, true );
        
        
        return sprites.add( sprite );
    }
    
    @Override
    public final int createSprite( SpriteAsset asset ) {
        return createSprite( asset.getTextureId(), asset.getTextureRegion() );
    }

    @Override
    public final void disposeSprite( int spriteId ) {
        sprites.remove( spriteId );
    }

    @Override
    public final int createShader( String shaderProgram ) {
        // TODO
        throw new UnsupportedOperationException( "TODO" );
    }
    
    @Override
    public final int createShader( ShaderAsset shaderAsset ) {
        // TODO
        throw new UnsupportedOperationException( "TODO" );
    }

    @Override
    public final void disposeShader( int shaderId ) {
        // TODO
        throw new UnsupportedOperationException( "TODO" );
    }

    @Override
    public final void startRendering( View view ) {
        activeViewport = viewports.get( view.index() );
        activeViewport.activate( spriteBatch, view );
        spriteBatch.begin();
    }

    @Override
    public final void renderSprite( SpriteRenderable spriteRenderable, float xpos, float ypos ) {
        setColorAndBlendMode( spriteRenderable.getTintColor(), spriteRenderable.getBlendMode() );
        TextureRegion sprite = sprites.get( spriteRenderable.getSpriteId() );
        spriteBatch.draw( sprite, xpos, ypos );
    }
    
    @Override
    public final void renderSprite( SpriteRenderable spriteRenderable, TransformData transformData ) {
        setColorAndBlendMode( spriteRenderable.getTintColor(), spriteRenderable.getBlendMode() );
        TextureRegion sprite = sprites.get( spriteRenderable.getSpriteId() );
        
        spriteBatch.draw( 
            sprite, 
            transformData.getXOffset(), 
            transformData.getYOffset(), 
            transformData.getPivotX(), 
            transformData.getPivotY(), 
            sprite.getRegionWidth(), 
            sprite.getRegionHeight(), 
            transformData.getScaleX(), 
            transformData.getScaleY(), 
            transformData.getRotation() );
    }

    @Override
    public final void renderShape( EShape.Type type, float[] vertices, int segments, DynArray<RGBColor> colors, BlendMode blendMode, boolean fill ) {
        Color color1 = getShapeColor( colors, 0 );
        Color color2 = getShapeColor( colors, 1 );
        Color color3 = getShapeColor( colors, 2 );
        Color color4 = getShapeColor( colors, 3 );
        shapeRenderer.setColor( color1 );
        
        if ( blendMode != null ) {
            Gdx.gl.glEnable( GL20.GL_BLEND );
            Gdx.gl.glBlendFunc( blendMode.gl11SourceConst, blendMode.gl11DestConst );
        }
        
        shapeRenderer.begin( ( type == EShape.Type.POINT )? ShapeType.Point : ( fill )? ShapeType.Filled : ShapeType.Line );
        int index = 0;
        
        switch ( type ) {
            case POINT: {
                while ( index < vertices.length ) {
                    shapeRenderer.point( vertices[ index++ ], vertices[ index++ ], 0f );
                }
                break;
            }
            case LINE: {
                while ( index < vertices.length ) {
                    shapeRenderer.line( vertices[ index++ ], vertices[ index++ ], vertices[ index++ ], vertices[ index++ ], color1, color2 );
                }
                break;
            }
            case POLI_LINE: {
                shapeRenderer.polyline( vertices );
                break;
            }
            case POLIGON: {
                shapeRenderer.polygon( vertices );
                break;
            }
            case RECTANGLE: {
                while ( index < vertices.length ) {
                    shapeRenderer.rect( vertices[ index++ ], vertices[ index++ ], vertices[ index++ ], vertices[ index++ ], color1, color2, color3, color4 );
                }
                break;
            }
            case SQUARE: {
                while ( index < vertices.length ) {
                    shapeRenderer.rect( 
                        vertices[ index++ ], vertices[ index++ ], vertices[ index++ ], vertices[ index++ ], 
                        vertices[ index++ ], vertices[ index++ ], vertices[ index++ ], vertices[ index++ ], 
                        vertices[ index++ ], color1, color2, color3, color4 
                    );
                }
                break;
            }
            case CIRCLE: {
                while ( index < vertices.length ) {
                    shapeRenderer.circle( vertices[ index++ ], vertices[ index++ ], vertices[ index++ ], segments );
                }
                break;
            }
            case CONE: {
                while ( index < vertices.length ) {
                    shapeRenderer.cone( vertices[ index++ ], vertices[ index++ ], vertices[ index++ ], vertices[ index++ ], vertices[ index++ ], segments );
                }
                break;
            }
            case ARC: {
                while ( index < vertices.length ) {
                    shapeRenderer.arc( vertices[ index++ ], vertices[ index++ ], vertices[ index++ ], vertices[ index++ ], vertices[ index++ ], segments );
                }
                break;
            }
            case CURVE: {
                while ( index < vertices.length ) {
                    shapeRenderer.curve( 
                        vertices[ index++ ], vertices[ index++ ], vertices[ index++ ], vertices[ index++ ], 
                        vertices[ index++ ], vertices[ index++ ], vertices[ index++ ], vertices[ index++ ], segments 
                    );
                }
                break;
            }
            case TRIANGLE: {
                while ( index < vertices.length ) {
                    shapeRenderer.triangle( 
                        vertices[ index++ ], vertices[ index++ ], vertices[ index++ ], 
                        vertices[ index++ ], vertices[ index++ ], vertices[ index++ ], color1, color2, color3 
                    );
                }
                break;
            }
        }
        
        shapeRenderer.end();
        Gdx.gl.glDisable( GL20.GL_BLEND );
    }
    
    @Override
    public final void renderShape( EShape.Type type, float[] vertices, int segments, DynArray<RGBColor> colors, BlendMode blendMode, boolean fill, TransformData transformData ) {
        shapeTransformMatrix.translate( transformData.getXOffset(), transformData.getYOffset(), 0f );
        if ( transformData.hasScale() ) {
            shapeTransformMatrix.scale( transformData.getScaleX(), transformData.getScaleY(), 0f );
        }
        if ( transformData.hasRotation() ) {
            shapeTransformMatrix.rotate( transformData.getPivotX(), transformData.getPivotY(), 0f, transformData.getRotation() );
        }
        shapeRenderer.updateMatrices();
        
        renderShape( type, vertices, segments, colors, blendMode, fill );
        
        shapeTransformMatrix.idt();
        shapeRenderer.updateMatrices();
    }

    @Override
    public final void endRendering( View view ) {
        spriteBatch.flush();
        if ( !view.isBase() ) {
            activeViewport.fbo.end();
        } 
        spriteBatch.end();
        activeViewport = null;
    }

    @Override
    public final void flush( Iterator<View> virtualViews ) {
        if ( virtualViews != null && virtualViews.hasNext() ) {
            
            baseViewport.activate( spriteBatch, baseView );
            spriteBatch.begin();
            
            while ( virtualViews.hasNext() ) {
                View virtualView = virtualViews.next();
                Viewport viewport = viewports.get( virtualView.index() );
                Rectangle bounds = virtualView.getBounds();
                setColorAndBlendMode( virtualView.getTintColor(), virtualView.getBlendMode() );
                spriteBatch.draw( viewport.fboTexture, bounds.x, bounds.y, bounds.width, bounds.height );
            }
            
            spriteBatch.end();
        }
        
        spriteBatch.flush();
    }

    private Viewport createBaseViewport( View view ) {
        Rectangle bounds = view.getBounds();
        OrthographicCamera camera = new OrthographicCamera( bounds.width, bounds.height );
        return new Viewport( camera, null, null );
    }

    private Viewport createVirtualViewport( View view ) {
        Rectangle bounds = view.getBounds();
        OrthographicCamera camera = new OrthographicCamera( bounds.width, bounds.height );
        FrameBuffer frameBuffer = new FrameBuffer( Format.RGB565, (int) ( bounds.width * FBO_SCALER ), (int) ( bounds.height * FBO_SCALER ), false ) ;
        TextureRegion textureRegion = new TextureRegion( frameBuffer.getColorBufferTexture() );
        textureRegion.flip( false, false );
        
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

        final void activate( SpriteBatch spriteBatch, View view ) {
            Position worldPosition = view.getWorldPosition();
            float zoom = view.getZoom();
            RGBColor clearColor = view.getClearColor();
            Rectangle bounds = view.getBounds();
            
            camera.setToOrtho( true, bounds.width * zoom, bounds.height * zoom );
            camera.position.x = camera.position.x + worldPosition.x;
            camera.position.y = camera.position.y + worldPosition.y;
            camera.update();
            spriteBatch.setProjectionMatrix( camera.combined );
            
            if ( fbo != null ) {
                fbo.begin();
            }
            
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
    
    private Color getShapeColor( DynArray<RGBColor> colors, int index ) {
        if ( !colors.contains( index ) ) {
            return shapeRenderer.getColor();
        }
        
        RGBColor rgbColor = colors.get( index );
        return new Color( rgbColor.r, rgbColor.g, rgbColor.b, rgbColor.a );
    }

    private void setColorAndBlendMode( RGBColor renderColor, BlendMode blendMode ) {
        spriteBatch.setColor( renderColor.r, renderColor.g, renderColor.b, renderColor.a );
        if ( currentBlendMode != blendMode ) {
            currentBlendMode = blendMode;
            if ( currentBlendMode != BlendMode.NONE ) {
                spriteBatch.enableBlending();
                spriteBatch.setBlendFunction( currentBlendMode.gl11SourceConst, currentBlendMode.gl11DestConst );
            } else {
                spriteBatch.disableBlending();
            }
        }
    }

}
