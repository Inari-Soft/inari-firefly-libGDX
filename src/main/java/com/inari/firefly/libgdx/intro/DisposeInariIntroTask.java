package com.inari.firefly.libgdx.intro;

import com.inari.firefly.system.FFContext;
import com.inari.firefly.task.Task;

public final class DisposeInariIntroTask extends Task {

    public DisposeInariIntroTask( int id ) {
        super( id );
    }

    @Override
    public final void run( FFContext context ) {
        InariIntro inariIntro = context.getComponent( InariIntro.CONTEXT_KEY );
        inariIntro.dispose( context );
        context.putComponent( InariIntro.CONTEXT_KEY, null );
    }
    
}