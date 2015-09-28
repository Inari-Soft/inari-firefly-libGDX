package com.inari.firefly.libgdx.intro;

import com.inari.firefly.system.FFContext;
import com.inari.firefly.task.Task;

public final class DisposeInariIntroTask extends Task {

    public DisposeInariIntroTask( int id ) {
        super( id );
    }

    @Override
    public final void run( FFContext context ) {
        context.getComponent( IntroInit.CONTEXT_KEY ).dispose( context );
    }
    
}