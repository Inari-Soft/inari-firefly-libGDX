package com.inari.firefly.libgdx;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.inari.commons.geom.Easing;
import com.inari.commons.graphics.RGBColor;
import com.inari.firefly.graphics.BlendMode;
import com.inari.firefly.graphics.ETransform;
import com.inari.firefly.graphics.rendering.RenderingSystem;
import com.inari.firefly.graphics.shape.EShape;
import com.inari.firefly.graphics.text.EText;
import com.inari.firefly.libgdx.test.GdxFFTestApplicationAdapter;
import com.inari.firefly.physics.animation.Animation;
import com.inari.firefly.physics.animation.AnimationSystem;
import com.inari.firefly.physics.animation.EAnimation;
import com.inari.firefly.physics.animation.easing.EasingAnimation;
import com.inari.firefly.physics.animation.easing.EasingData;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.external.ShapeData;

public class EasingTest extends GdxFFTestApplicationAdapter {
    
    @Override
    public String getTitle() {
        return "EasingTest";
    }

    
    @Override
    protected void init( FFContext context ) {
        context.loadSystem( RenderingSystem.SYSTEM_KEY );
        context.loadSystem( AnimationSystem.SYSTEM_KEY );
        
        int index = 0;
        for ( Easing.Type easingType : Easing.Type.values() ) {
            context.activateSystemComponent(
                Animation.TYPE_KEY,
                createEasingAnimation( context, easingType, index )
            );
            index++;
        }
        
//        int linearAnimId = createEasingAnimation( context, Easing.Type.LINEAR, 0 );
//        int expoInAnimId = createEasingAnimation( context, Easing.Type.EXPO_IN, 1 );
//        int expoOutAnimId = createEasingAnimation( context, Easing.Type.EXPO_OUT, 2 );
//        int expoInOutAnimId = createEasingAnimation( context, Easing.Type.EXPO_IN_OUT, 3 );
//        int circInAnimId = createEasingAnimation( context, Easing.Type.CIRC_IN, 4 );
//        int circOutAnimId = createEasingAnimation( context, Easing.Type.CIRC_OUT, 5 );
//        int circInOutAnimId = createEasingAnimation( context, Easing.Type.CIRC_IN_OUT, 6 );
//        int cubInAnimId = createEasingAnimation( context, Easing.Type.CUBIC_IN, 7 );
//        int cubOutAnimId = createEasingAnimation( context, Easing.Type.CUBIC_OUT, 8 );
//        int cubInOutAnimId = createEasingAnimation( context, Easing.Type.CUBIC_IN_OUT, 9 );
//        
//        int sinInAnimId = createEasingAnimation( context, Easing.Type.SIN_IN, 10 );
//        
//        context.activateSystemComponent( Animation.TYPE_KEY, linearAnimId );
//        context.activateSystemComponent( Animation.TYPE_KEY, expoInAnimId );
//        context.activateSystemComponent( Animation.TYPE_KEY, expoOutAnimId );
//        context.activateSystemComponent( Animation.TYPE_KEY, expoInOutAnimId );
//        
//        
//        context.activateSystemComponent( Animation.TYPE_KEY, sinInAnimId );
    }


    private int createEasingAnimation( FFContext context, Easing.Type type, int position ) {
        int animId = context.getComponentBuilder( Animation.TYPE_KEY, EasingAnimation.class )
            .set( EasingAnimation.LOOPING, true )
            .set( EasingAnimation.EASING_DATA, new EasingData( type, 100, 400, 5000 ) )
        .build();
        
        float ypos = (float) position * ( 20f + 10f ) + 20f;
        
        context.getEntityBuilder()
            .set( ETransform.VIEW_ID, 0 )
            .set( ETransform.POSITION_X, 10f )
            .set( ETransform.POSITION_Y, ypos )
            .set( EText.FONT_ASSET_NAME, FFContext.DEFAULT_FONT )
            .set( EText.TEXT, type.name().replaceAll( "_", "-" ) )
            .set( EText.BLEND_MODE, BlendMode.NORMAL_ALPHA )
            .set( EText.TINT_COLOR, new RGBColor( 1, 1, 1, 1f ) )
        .activate();
        
        context.getEntityBuilder()
            .set( ETransform.VIEW_ID, 0 )
            .set( EShape.SHAPE_TYPE, ShapeData.Type.RECTANGLE )
            .set( EShape.FILL, true )
            .add( EShape.COLORS, new RGBColor( 1f, 0f, 0f, 1f ) )
            .set( EShape.VERTICES, new float[] { 100f, ypos, 20f, 20f } )
            .add( EAnimation.ANIMATION_MAPPING, ETransform.AnimationAdapter.POSITION_X.createAnimationMapping( animId ) )
        .activate();
        return animId;
    }

    public static void main (String[] arg) {
        try {
            LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
            config.resizable = true;
            config.width = 800;
            config.height = 800;
            new LwjglApplication( new EasingTest(), config );
        } catch ( Throwable t ) {
            t.printStackTrace();
        }
    }
    

}
