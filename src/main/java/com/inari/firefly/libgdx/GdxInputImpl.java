package com.inari.firefly.libgdx;

import java.util.BitSet;

import com.badlogic.gdx.Gdx;
import com.inari.commons.lang.list.DynArray;
import com.inari.commons.lang.list.IntBag;
import com.inari.firefly.system.external.FFInput;

public class GdxInputImpl extends FFInput {
    
    private IntBag buttonCodeMapping = new IntBag( 255, -1 );
    private BitSet pressedCodeMapping = new BitSet( 255 );
    private DynArray<InputType> inputTypeMapping = new DynArray<InputType>();

    @Override
    public final void mapKeyInput( ButtonType buttonType, int keyCode ) {
        buttonCodeMapping.set( buttonType.ordinal(), keyCode );
    }

    @Override
    public final void mapInputType( ButtonType buttonType, InputType inputType ) {
        inputTypeMapping.set( buttonType.ordinal(), inputType );
    }
    
    @Override
    public final boolean typed( ButtonType buttonType ) {
        int buttonCode = buttonType.ordinal();
        int keyCode = buttonCodeMapping.get( buttonCode );
        
        boolean pressed = isPressed( buttonType );
        if ( pressed && pressedCodeMapping.get( keyCode ) ) {
            return false;
        }

        pressedCodeMapping.set( keyCode, pressed );
        
        return pressed;
    }

    @Override
    public final boolean isPressed( ButtonType buttonType ) {
        int buttonCode = buttonType.ordinal();
        int keyCode = buttonCodeMapping.get( buttonCode );
        
        if ( keyCode >= 0 && Gdx.input.isKeyPressed( keyCode ) ) {
            return true;
        }
        
        boolean pressed = false;
        if ( inputTypeMapping.contains( buttonCode ) ) {
            switch ( inputTypeMapping.get( buttonCode ) ) {
                case MOUSE_LEFT: {
                    pressed = Gdx.input.isButtonPressed( com.badlogic.gdx.Input.Buttons.LEFT );
                    break;
                }
                case MOUSE_RIGHT: {
                    pressed = Gdx.input.isButtonPressed( com.badlogic.gdx.Input.Buttons.RIGHT );
                    break;
                }
                case MOUSE_MIDDLE: {
                    pressed = Gdx.input.isButtonPressed( com.badlogic.gdx.Input.Buttons.MIDDLE );
                    break;
                }
                case TOUCH: {
                    pressed = Gdx.input.isTouched();
                    break;
                }
                default : {};
            }
        }

        return pressed;
    }

    @Override
    public final int getXpos() {
        return Gdx.input.getX();
    }

    @Override
    public final int getYpos() {
        return Gdx.input.getY();
    }

}
