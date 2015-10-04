package com.inari.firefly.libgdx.filter;

import com.badlogic.gdx.graphics.Pixmap;
import com.inari.firefly.filter.IColorFilter;

public final class ColorFilteredTextureData extends FilteredTextureData {

    private final IColorFilter filter;

    public ColorFilteredTextureData( String resourcePath, IColorFilter filter ) {
        super( resourcePath );
        this.filter = filter;
    }

    @Override
    protected final void applyFilter( Pixmap pixmap ) {
        for ( int y = 0; y < pixmap.getHeight(); y++ ) {
            for ( int x = 0; x < pixmap.getWidth(); x++ ) {
                pixmap.drawPixel( x, y, filter.filter( pixmap.getPixel( x, y ) ) );
            }
        }
    }

}
