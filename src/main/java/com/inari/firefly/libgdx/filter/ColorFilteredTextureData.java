package com.inari.firefly.libgdx.filter;

import com.badlogic.gdx.graphics.Pixmap;
import com.inari.commons.lang.functional.IntFunction;

public final class ColorFilteredTextureData extends FilteredTextureData {

    private final IntFunction colorFunction;

    public ColorFilteredTextureData( String resourcePath, IntFunction colorConverter ) {
        super( resourcePath );
        this.colorFunction = colorConverter;
    }

    @Override
    protected final void applyFilter( Pixmap pixmap ) {
        for ( int y = 0; y < pixmap.getHeight(); y++ ) {
            for ( int x = 0; x < pixmap.getWidth(); x++ ) {
                pixmap.drawPixel( x, y, colorFunction.f( pixmap.getPixel( x, y ) ) );
            }
        }
    }

}
