package com.inari.firefly.libgdx;

import com.badlogic.gdx.Gdx;
import com.inari.commons.lang.list.DynArray;
import com.inari.commons.lang.list.IntMap;
import com.inari.firefly.system.Input;

public class GDXInputImpl extends Input {
    
    private IntMap buttonCodeMapping = new IntMap( -1, 255 );
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
