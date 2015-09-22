package com.inari.firefly.libgdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.inari.commons.geom.Easing;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.graphics.RGBColor;
import com.inari.firefly.animation.AnimationSystem;
import com.inari.firefly.animation.ColorEasingAnimation;
import com.inari.firefly.animation.EasingData;
import com.inari.firefly.asset.AssetNameKey;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.control.ComponentControllerSystem;
import com.inari.firefly.control.EController;
import com.inari.firefly.controller.SpriteTintColorAnimationController;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.Entity;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.renderer.TextureAsset;
import com.inari.firefly.renderer.sprite.ESprite;
import com.inari.firefly.renderer.sprite.SpriteAsset;
import com.inari.firefly.state.State;
import com.inari.firefly.state.StateChange;
import com.inari.firefly.state.StateChangeCondition;
import com.inari.firefly.state.StateSystem;
import com.inari.firefly.state.Workflow;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFTimer;
import com.inari.firefly.system.FireFly;
import com.inari.firefly.task.Task;
import com.inari.firefly.task.TaskSystem;

public abstract class FireflyApplicationAdapter extends ApplicationAdapter {
    
    public static final String GAME_WORKFLOW_NAME = "FireflyGameWorkflow";
    public static final int GAME_WORKFLOW_ID = 0;
    
    private static final String INTRO_NAME = "intro";
    
    private static final int INTRO_STATE_ID = 0;
    private static final int INIT_GAME_STATE_ID = 1;
    private static final int INIT_INTRO_STATE_TASK_ID = 0;
    private static final int DISPOSE_INTRO_STATE_TASK_ID = 1;
    private static final String INIT_GAME_STATE_NAME = "initGameState";
    private static final int INIT_GAME_STATE_CHANGE_CONDITION_ID = 0;
    private static final int LOGO_ENTITY_ID = 0;

    private static final AssetNameKey INARI_ASSET_KEY = new AssetNameKey( INTRO_NAME, "inari" );
    private static final AssetNameKey INARI_SPRITE_ASSET_KEY = new AssetNameKey( INTRO_NAME, "inariSprite" );
    
    private FireFly firefly;
    
    @Override
    public final void create () {
        Gdx.graphics.setTitle( getTitle() );
        firefly = new FireFly( GDXConfiguration.GDX_INIT_MAP );
        FFContext context = firefly.getContext();
        
        StateSystem stateSystem = context.getComponent( StateSystem.CONTEXT_KEY );
        TaskSystem taskSystem = context.getComponent( TaskSystem.CONTEXT_KEY );
        
        taskSystem
            .getTaskBuilder( InitIntroTask.class )
                .setAttribute( Task.NAME, "InitIntroTask" )
                .setAttribute( Task.REMOVE_AFTER_RUN, true )
            .buildAndNext( INIT_INTRO_STATE_TASK_ID, DisposeIntroTask.class )
                .setAttribute( Task.NAME, "DisposeIntroTask" )
                .setAttribute( Task.REMOVE_AFTER_RUN, true )
            .build( DISPOSE_INTRO_STATE_TASK_ID );
            
        
        stateSystem
            .getWorkflowBuilder()
                .setAttribute( Workflow.NAME, GAME_WORKFLOW_NAME )
            .build( GAME_WORKFLOW_ID );
        stateSystem
            .getStateBuilder()
                .setAttribute( State.WORKFLOW_ID, GAME_WORKFLOW_ID )
                .setAttribute( State.NAME, "Intro" )
                .setAttribute( State.INIT_TASK_ID, INIT_INTRO_STATE_TASK_ID )
                .setAttribute( State.DISPOSE_TASK_ID, DISPOSE_INTRO_STATE_TASK_ID )
            .buildAndNext( INTRO_STATE_ID )
                .setAttribute( State.WORKFLOW_ID, GAME_WORKFLOW_ID )
                .setAttribute( State.NAME, INIT_GAME_STATE_NAME )
            .build( INIT_GAME_STATE_ID );
        stateSystem
            .getStateChangeConditionBuilder( IntroFinishedCondition.class )
                .setAttribute( StateChangeCondition.NAME, "initGameCondition" )
            .build( INIT_GAME_STATE_CHANGE_CONDITION_ID );
        stateSystem
            .getStateChangeBuilder()
                .setAttribute( StateChange.NAME, "initGameStateChange" )
                .setAttribute( StateChange.WORKFLOW_ID, GAME_WORKFLOW_ID )
                .setAttribute( StateChange.FORM_STATE_ID, INTRO_STATE_ID )
                .setAttribute( StateChange.TO_STATE_ID, INIT_GAME_STATE_ID )
                .setAttribute( StateChange.CONDITION_ID, INIT_GAME_STATE_CHANGE_CONDITION_ID )
            .build();
        
        
        State gameInitState = stateSystem.getState( INIT_GAME_STATE_ID );
        initContext( context, gameInitState );
        
        stateSystem.initWorkflow( GAME_WORKFLOW_ID, INTRO_STATE_ID );
    }
    
    @Override
    public final void render () {
        firefly.update();
        firefly.render();
    }
    
    public abstract String getTitle();
    public abstract void initContext( FFContext context, State gameInitState );
    
    
    
    public static final class InitIntroTask extends Task {

        public InitIntroTask( int id ) {
            super( id );
        }

        @Override
        public void run( FFContext context ) {
            AssetSystem assetSystem = context.getComponent( AssetSystem.CONTEXT_KEY );
            EntitySystem entitySystem = context.getComponent( EntitySystem.CONTEXT_KEY );
            AnimationSystem animationSystem = context.getComponent( AnimationSystem.CONTEXT_KEY );
            ComponentControllerSystem controllerSystem = context.getComponent( ComponentControllerSystem.CONTEXT_KEY );
            
            animationSystem
                .getAnimationBuilder( ColorEasingAnimation.class )
                    .setAttribute( ColorEasingAnimation.LOOPING, false )
                    .setAttribute( ColorEasingAnimation.EASING_DATA_ALPHA, new EasingData( Easing.Type.LINEAR, 0.0f, 1.0f, 1000 ) )
                .build( 0 );
            
            controllerSystem
                .getComponentBuilder( SpriteTintColorAnimationController.class )
                    .setAttribute( SpriteTintColorAnimationController.TINT_COLOR_ANIMATION_ID, 0 )
                .build( 0 );
                    
            
            assetSystem
                .getAssetBuilder( TextureAsset.class )
                    .setAttribute( TextureAsset.NAME, INARI_ASSET_KEY.name )
                    .setAttribute( TextureAsset.ASSET_GROUP, INARI_ASSET_KEY.group )
                    .setAttribute( TextureAsset.RESOURCE_NAME, "firefly/inari.png" )
                    .setAttribute( TextureAsset.TEXTURE_WIDTH, 385 )
                    .setAttribute( TextureAsset.TEXTURE_HEIGHT, 278 )
                .buildAndNext( SpriteAsset.class )
                    .setAttribute( SpriteAsset.NAME, INARI_SPRITE_ASSET_KEY.name )
                    .setAttribute( SpriteAsset.ASSET_GROUP, INARI_SPRITE_ASSET_KEY.group )
                    .setAttribute( SpriteAsset.TEXTURE_ID, assetSystem.getAssetTypeKey( INARI_ASSET_KEY ).id )
                    .setAttribute( SpriteAsset.TEXTURE_REGION, new Rectangle( 0, 0, 385, 278 ) )
                .build();
            
            assetSystem.loadAsset( INARI_ASSET_KEY );
            assetSystem.loadAsset( INARI_SPRITE_ASSET_KEY );
            
            Entity logoEntity = entitySystem
                    .getEntityBuilder()
                        .setAttribute( ETransform.VIEW_ID, 0 )
                        .setAttribute( ETransform.XPOSITION, 100 )
                        .setAttribute( ETransform.XPOSITION, 100 )
                        .setAttribute( ESprite.SPRITE_ID, assetSystem.getAssetTypeKey( INARI_SPRITE_ASSET_KEY ).id )
                        .setAttribute( ESprite.TINT_COLOR, new RGBColor( 1f, 1f, 1f, 0f ) )
                        .setAttribute( EController.CONTROLLER_IDS, new int[] { 0 } )
                    .build( LOGO_ENTITY_ID );
            entitySystem.activate( logoEntity.getId() );
        }
        
    }
    
    public static final class DisposeIntroTask extends Task {

        public DisposeIntroTask( int id ) {
            super( id );
        }

        @Override
        public void run( FFContext context ) {
            AssetSystem assetSystem = context.getComponent( AssetSystem.CONTEXT_KEY );
            EntitySystem entitySystem = context.getComponent( EntitySystem.CONTEXT_KEY );
            
            entitySystem.deleteAll();
            assetSystem.deleteAssets( INTRO_NAME );
        }
        
    }

    public static final class IntroFinishedCondition extends StateChangeCondition {

        public IntroFinishedCondition( int stateChangeConditionId, FFContext context ) {
            super( stateChangeConditionId, context );
        }

        @Override
        public boolean check( Workflow workflow, FFTimer timer ) {
            ESprite logoSprite = context.getComponent( EntitySystem.CONTEXT_KEY ).getComponent( LOGO_ENTITY_ID, ESprite.class );
            RGBColor tintColor = logoSprite.getTintColor();
            return ( tintColor.a >= 1 );
        }
    }

}
