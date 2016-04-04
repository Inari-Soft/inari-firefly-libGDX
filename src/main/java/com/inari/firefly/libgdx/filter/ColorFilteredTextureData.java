package com.inari.firefly.libgdx.filter;

import com.badlogic.gdx.graphics.Pixmap;
import com.inari.commons.lang.convert.IntValueConverter;

public final class ColorFilteredTextureData extends FilteredTextureData {

    private final IntValueConverter colorConverter;

    public ColorFilteredTextureData( String resourcePath, IntValueConverter colorConverter ) {
        super( resourcePath );
        this.colorConverter = colorConverter;
    }

    @Override
    protected final void applyFilter( Pixmap pixmap ) {
        for ( int y = 0; y < pixmap.getHeight(); y++ ) {
            for ( int x = 0; x < pixmap.getWidth(); x++ ) {
                pixmap.drawPixel( x, y, colorConverter.convert( pixmap.getPixel( x, y ) ) );
            }
        }
    }

}
