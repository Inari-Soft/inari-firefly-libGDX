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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.inari.commons.GeomUtils;
import com.inari.commons.geom.PositionF;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.graphics.RGBColor;
import com.inari.commons.lang.functional.IntFunction;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.FFInitException;
import com.inari.firefly.graphics.BlendMode;
import com.inari.firefly.graphics.ShaderAsset;
import com.inari.firefly.graphics.SpriteRenderable;
import com.inari.firefly.graphics.shape.EShape;
import com.inari.firefly.graphics.view.View;
import com.inari.firefly.graphics.view.ViewEvent;
import com.inari.firefly.libgdx.filter.ColorFilteredTextureData;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.external.FFGraphics;
import com.inari.firefly.system.external.ShapeData;
import com.inari.firefly.system.external.SpriteData;
import com.inari.firefly.system.external.TextureData;
import com.inari.firefly.system.external.TransformData;

public final class GdxGraphicsImpl implements FFGraphics {
    
    private final static float FBO_SCALER = 2.0f;
    
    private final DynArray<Texture> textures;
    private final DynArray<TextureRegion> sprites;
    private final DynArray<ViewportData> viewports;
    private final DynArray<ShaderProgram> shaders;

    private final SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;
    
    private ViewportData baseViewport = null;
    private View baseView = null;
    private ViewportData activeViewport = null;
    private int activeShaderId = -1;
    private int activeShapeShaderId = -1;
    private BlendMode currentBlendMode = BlendMode.NONE;
    
    GdxGraphicsImpl() {
        textures = DynArray.create( Texture.class, 100 ); 
        sprites = DynArray.create( TextureRegion.class, 100, 50 );
        viewports = DynArray.create( ViewportData.class, 20 );
        shaders = DynArray.create( ShaderProgram.class );
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
    }
    
    @Override
    public void init( FFContext context ) {
       context.registerListener( ViewEvent.TYPE_KEY, this );
    }
    
    @Override
    public final void dispose( final FFContext context ) {
        context.disposeListener( ViewEvent.TYPE_KEY, this );
        
        for ( ViewportData viewport : viewports ) {
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
    
    /**
     * Converts an x-coordinate given in logical screen coordinates to
     * backbuffer coordinates.
     */
    public static int toBackBufferX(int logicalX) {
        return (int)(logicalX * Gdx.graphics.getBackBufferWidth() / (float)Gdx.graphics.getWidth());
    }

    /**
     * Convers an y-coordinate given in backbuffer coordinates to
     * logical screen coordinates
     */
    public static int toBackBufferY(int logicalY) {
        return (int)(logicalY * Gdx.graphics.getBackBufferHeight() / (float)Gdx.graphics.getHeight());
    }

    @Override
    public final void onViewEvent( final ViewEvent event ) {
        switch ( event.getType() ) {
            case VIEW_CREATED: {
                final View view = event.getView();
                final ViewportData viewport;
                if ( !view.isBase() ) {
                    viewport = createVirtualViewport( view );
                } else {
                    viewport = createBaseViewport( view );
                    baseViewport = viewport;
                    baseView = view;
                }
                viewports.set( view.index(), viewport );
                break;
            }
            case VIEW_DELETED: {
                final ViewportData viewport = viewports.remove( event.getView().index() );
                if ( viewport != null ) {
                    viewport.dispose();
                }
                break;
            }
            default: {}
        }
    }

    @Override
    public final int createTexture( final TextureData data ) {
        Texture texture = null;
        int textureId = -1;
        final IntFunction colorFunction = data.getColorConverter();
        if ( colorFunction != null ) {
            ColorFilteredTextureData textureData = new ColorFilteredTextureData( data.getResourceName(), colorFunction );
            texture = new Texture( textureData );
            textureId = textures.add( texture );
        } else {
            texture = new Texture( Gdx.files.internal( data.getResourceName() ), data.isMipmap() );
            textureId = textures.add( texture );
        }
        
        data.setTextureWidth( texture.getWidth() );
        data.setTextureHeight( texture.getHeight() );
        
        return textureId;
    }

    @Override
    public final void disposeTexture( int textureId ) {
        Texture texture = textures.remove( textureId );
        texture.dispose();
    }

    @Override
    public final int createSprite( final SpriteData data ) {
        int textureId = data.getTextureId();
        Rectangle textureRegion = data.getTextureRegion();
        if ( !textures.contains( textureId ) ) {
            throw new FFInitException( "Texture with id: " + textureId + "not loaded" );
        }
        
        final Texture texture = textures.get( textureId );
        final TextureRegion sprite = new TextureRegion( texture, textureRegion.x, textureRegion.y, textureRegion.width, textureRegion.height );
        
        if ( data.isHorizontalFlip() ) {
            if ( data.isVerticalFlip() ) {
                sprite.flip( true, false );
            } else {
                sprite.flip( false, false );
            }
        } else if ( data.isVerticalFlip() ) {
            sprite.flip( true, true );
        } else {
            sprite.flip( false, true );
        }
        
        return sprites.add( sprite );
    }

    @Override
    public final void disposeSprite( int spriteId ) {
        sprites.remove( spriteId );
    }
    
    @Override
    public final int createShader( final ShaderAsset shaderAsset ) {
        String vertexShader = shaderAsset.getVertexShaderProgram();
        String fragmentShader = shaderAsset.getFragmentShaderProgram();
        
        if ( vertexShader == null ) {
            try {
                vertexShader = Gdx.files.internal( shaderAsset.getVertexShaderResourceName() ).readString();
            } catch ( Exception e ) {
                throw new FFInitException( "Failed to load vertex shader from resource: " + shaderAsset.getVertexShaderResourceName(), e );
            }
        }
        
        if ( fragmentShader == null ) {
            try {
                fragmentShader = Gdx.files.internal( shaderAsset.getFragmentShaderResourceName() ).readString();
            } catch ( Exception e ) {
                throw new FFInitException( "Failed to load fragment shader from resource: " + shaderAsset.getFragmentShaderResourceName(), e );
            }
        }
        
        ShaderProgram shaderProgram = new ShaderProgram( vertexShader, fragmentShader );
        if ( shaderProgram.isCompiled() ) {
            String compileLog = shaderProgram.getLog();
            System.out.println( "Shader Compiled: " + compileLog );
        } else {
            throw new FFInitException( "ShaderAsset with id: " + shaderAsset.index() + " and name: " + shaderAsset.getName() + " failed to compile:" + shaderProgram.getLog() );
        }
        
        return shaders.add( shaderProgram );
    }

    @Override
    public final void disposeShader( int shaderId ) {
        ShaderProgram shaderProgram = shaders.remove( shaderId );
        
        if ( shaderProgram != null ) {
            shaderProgram.dispose();
        }
    }

    @Override
    public final void startRendering( final View view, boolean clear ) {
        activeViewport = viewports.get( view.index() );
        activeViewport.activate( spriteBatch, shapeRenderer, view, clear );
        spriteBatch.begin();
    }

    @Override
    public final void renderSprite( final SpriteRenderable spriteRenderable, float xpos, float ypos ) {
        setColorAndBlendMode( spriteRenderable.getTintColor(), spriteRenderable.getBlendMode() );
        TextureRegion sprite = sprites.get( spriteRenderable.getSpriteId() );
        setShaderForSpriteBatch( spriteRenderable );
        spriteBatch.draw( sprite, xpos, ypos );
    }
    
    @Override
    public final void renderSprite( final SpriteRenderable spriteRenderable, float xpos, float ypos, float scale ) {
        setColorAndBlendMode( spriteRenderable.getTintColor(), spriteRenderable.getBlendMode() );
        TextureRegion sprite = sprites.get( spriteRenderable.getSpriteId() );
        setShaderForSpriteBatch( spriteRenderable );
        spriteBatch.draw( sprite, xpos, ypos, 0, 0, sprite.getRegionWidth(), sprite.getRegionHeight(), scale, scale, 0 );
    }

    @Override
    public final void renderSprite( final SpriteRenderable spriteRenderable, final TransformData transformData ) {
        setColorAndBlendMode( spriteRenderable.getTintColor(), spriteRenderable.getBlendMode() );
        TextureRegion sprite = sprites.get( spriteRenderable.getSpriteId() );
        setShaderForSpriteBatch( spriteRenderable );
        
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
    public final void renderShape( final ShapeData data ) {
        int shaderId = data.getShaderId();
        if ( shaderId != activeShapeShaderId ) {
            if ( shaderId < 0 ) {
                shapeRenderer = new ShapeRenderer();
            } else {
                ShaderProgram shaderProgram = shaders.get( shaderId );
                shapeRenderer = new ShapeRenderer( 1000, shaderProgram );
            }
        }
        
        DynArray<RGBColor> colors = data.getColors();
        getShapeColor( colors, 0, SHAPE_COLOR_1 );
        shapeRenderer.setColor( SHAPE_COLOR_1 );
        getShapeColor( colors, 1, SHAPE_COLOR_2 );
        getShapeColor( colors, 2, SHAPE_COLOR_3 );
        getShapeColor( colors, 3, SHAPE_COLOR_4 );
        
        ShapeData.Type type = data.getShapeType();
        ShapeType shapeType = ( type == EShape.Type.POINT )? ShapeType.Point : ( data.isFill() )? ShapeType.Filled : ShapeType.Line;
        
        boolean restartSpriteBatch = false;
        if ( spriteBatch.isDrawing() ) {
            restartSpriteBatch = true;
            spriteBatch.end();
        }
        shapeRenderer.begin( shapeType );

        BlendMode blendMode = data.getBlendMode();
        float[] vertices = data.getVertices();
        int segments = data.getSegments();
        if ( blendMode != null ) {
            Gdx.gl.glEnable( GL20.GL_BLEND );
            Gdx.gl.glBlendColor( 1f, 1f, 1f, 1f );
            Gdx.gl.glBlendFunc( blendMode.gl11SourceConst, blendMode.gl11DestConst );
        }
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
                    shapeRenderer.line( vertices[ index++ ], vertices[ index++ ], vertices[ index++ ], vertices[ index++ ], SHAPE_COLOR_1, SHAPE_COLOR_2 );
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
                    shapeRenderer.rect( vertices[ index++ ], vertices[ index++ ], vertices[ index++ ], vertices[ index++ ], SHAPE_COLOR_1, SHAPE_COLOR_2, SHAPE_COLOR_3, SHAPE_COLOR_4 );
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
                        vertices[ index++ ], vertices[ index++ ], vertices[ index++ ], SHAPE_COLOR_1, SHAPE_COLOR_2, SHAPE_COLOR_3 
                    );
                }
                break;
            }
        }
        
        shapeRenderer.flush();
        shapeRenderer.end();
        
        if ( restartSpriteBatch ) {
            spriteBatch.begin();
        }
        
        Gdx.gl.glDisable( GL20.GL_BLEND );
    }
    
    @Override
    public final void renderShape( ShapeData data, TransformData transformData ) {
        shapeRenderer.identity();
        shapeRenderer.translate( transformData.getXOffset(), transformData.getYOffset(), 0f );
        if ( transformData.hasScale() ) {
            shapeRenderer.translate( transformData.getPivotX(), transformData.getPivotY(), 0f );
            shapeRenderer.scale( transformData.getScaleX(), transformData.getScaleY(), 0f );
            shapeRenderer.translate( -transformData.getPivotX(), -transformData.getPivotY(), 0f );
        }
        if ( transformData.hasRotation() ) {
            shapeRenderer.translate( transformData.getPivotX(), transformData.getPivotY(), 0f );
            shapeRenderer.rotate( 0f, 0f, 1f, transformData.getRotation() );
            shapeRenderer.translate( -transformData.getPivotX(), -transformData.getPivotY(), 0f );
        }

        renderShape( data );
        
        shapeRenderer.identity();
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
    public final void flush( DynArray<View> virtualViews ) {
        if ( virtualViews != null && !virtualViews.isEmpty() ) {
            
            baseViewport.activate( spriteBatch, shapeRenderer, baseView, true );
            spriteBatch.begin();
            
            for ( int i = 0; i < virtualViews.size(); i++ ) {
                View virtualView = virtualViews.get( i );
                if ( !virtualView.isActive() ) {
                    continue;
                }
 
                ViewportData viewport = viewports.get( virtualView.index() );
                Rectangle bounds = virtualView.getBounds();
                setColorAndBlendMode( virtualView.getTintColor(), virtualView.getBlendMode() );
                spriteBatch.draw( viewport.fboTexture, bounds.x, bounds.y, bounds.width, bounds.height );
            }
            
            spriteBatch.end();
        }
        
        spriteBatch.flush();
        currentBlendMode = BlendMode.NONE;
    }
    
    @Override
    public final byte[] getScreenshotPixels( Rectangle area ) {
        int flippedY = getScreenHeight() - area.height + area.y;
        int size = area.width * area.height * 3;
        ByteBuffer screenContents = ByteBuffer.allocateDirect( size ).order( ByteOrder.LITTLE_ENDIAN );
        GL11.glReadPixels( area.x, flippedY, area.width, area.height, GL12.GL_BGR, GL11.GL_UNSIGNED_BYTE, screenContents );
        byte[] array = new byte[ size ]; 
        byte[] inversearray = new byte[ size ]; 
        screenContents.get( array );
        
        for ( int y = 0; y < area.height; y++ ) {
            System.arraycopy( 
                array, 
                GeomUtils.getFlatArrayIndex( 0, area.height - y - 1, area.width * 3 ), 
                inversearray, 
                GeomUtils.getFlatArrayIndex( 0, y, area.width * 3 ), 
                area.width * 3 
            );
        }
        
        return inversearray;
    }
    
    

    private ViewportData createBaseViewport( View view ) {
        Rectangle bounds = view.getBounds();
        OrthographicCamera camera = new OrthographicCamera( bounds.width, bounds.height );
        return new ViewportData( camera, null, null );
    }

    private ViewportData createVirtualViewport( View view ) {
        Rectangle bounds = view.getBounds();
        OrthographicCamera camera = new OrthographicCamera( bounds.width, bounds.height );
        FrameBuffer frameBuffer = new FrameBuffer( Format.RGBA8888, (int) ( bounds.width * FBO_SCALER ), (int) ( bounds.height * FBO_SCALER ), false ) ;
        TextureRegion textureRegion = new TextureRegion( frameBuffer.getColorBufferTexture() );
        textureRegion.flip( false, false );
        
        return new ViewportData( camera, frameBuffer, textureRegion );
    }

    private static final class ViewportData {
        final OrthographicCamera camera;
        final FrameBuffer fbo;
        final TextureRegion fboTexture;
        
        ViewportData( OrthographicCamera camera, FrameBuffer fbo, TextureRegion fboTexture ) {
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

        final void activate( SpriteBatch spriteBatch, ShapeRenderer shapeRenderer, View view, boolean clear ) {
            PositionF worldPosition = view.getWorldPosition();
            float zoom = view.getZoom();
            RGBColor clearColor = view.getClearColor();
            Rectangle bounds = view.getBounds();
            
            camera.setToOrtho( true, bounds.width * zoom, bounds.height * zoom );
            camera.position.x = camera.position.x + worldPosition.x;
            camera.position.y = camera.position.y + worldPosition.y;
            camera.update();
            spriteBatch.setProjectionMatrix( camera.combined );
            shapeRenderer.setProjectionMatrix( camera.combined );
            
            if ( fbo != null ) {
                fbo.begin();
            }
            
            if ( clear ) {
                Gdx.gl.glClearColor( clearColor.r, clearColor.g, clearColor.b, clearColor.a );
                Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );
            }
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
    
    private final static Color SHAPE_COLOR_1 = new Color();
    private final static Color SHAPE_COLOR_2 = new Color();
    private final static Color SHAPE_COLOR_3 = new Color();
    private final static Color SHAPE_COLOR_4 = new Color();
    private void getShapeColor( final DynArray<RGBColor> colors, int index, final Color color ) {
        if ( !colors.contains( index ) ) {
            color.set( shapeRenderer.getColor() );
            return;
        }
        
        RGBColor rgbColor = colors.get( index );
        color.set( rgbColor.r, rgbColor.g, rgbColor.b, rgbColor.a );
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
    
    private void setShaderForSpriteBatch( SpriteRenderable spriteRenderable ) {
        int shaderId = spriteRenderable.getShaderId();
        if ( shaderId != activeShaderId ) {
            if ( shaderId < 0 ) {
                spriteBatch.setShader( null );
                activeShaderId = -1;
            } else {
                ShaderProgram shaderProgram = shaders.get( shaderId );
                spriteBatch.setShader( shaderProgram );
                activeShaderId = shaderId;
            }
        }
    }

}
