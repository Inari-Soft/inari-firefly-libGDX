package com.inari.firefly.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.inari.commons.lang.list.DynArray;
import com.inari.commons.lang.list.IntBag;
import com.inari.firefly.FFInitException;
import com.inari.firefly.audio.SoundAsset;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.external.FFAudio;

public final class GdxAudioImpl implements FFAudio {
    
    private final DynArray<com.badlogic.gdx.audio.Sound> sounds;
    private final IntBag lastPlayingSoundOnChannel;
    private final DynArray<Music> music;
    
    GdxAudioImpl() {
        sounds = DynArray.create( com.badlogic.gdx.audio.Sound.class );
        lastPlayingSoundOnChannel = new IntBag( 5, -1 );
        music = DynArray.create( Music.class );
    }
    
    @Override
    public final void init( FFContext context ) throws FFInitException {
    }

    @Override
    public final void dispose( FFContext context ) {
        for ( com.badlogic.gdx.audio.Sound sound : sounds ) {
            sound.dispose();
        }
        sounds.clear();
        
        for ( Music m : music ) {
            m.dispose();
        }
        music.clear();
    }
    
    @Override
    public final long playSound( int soundId, int channel, boolean looping, float volume, float pitch, float pan ) {
        Sound sound = sounds.get( soundId );
        if ( sound == null ) {
            return -1;
        }
        
        if ( channel >= 0 && channel < lastPlayingSoundOnChannel.length() ) {
            int lastPlayedSoundId = lastPlayingSoundOnChannel.get( channel );
            if ( sounds.contains( lastPlayedSoundId ) ) {
                sounds.get( lastPlayedSoundId ).stop();
            }
        }
        lastPlayingSoundOnChannel.set( channel, soundId );
        
        long play = sound.play( volume, pitch, pan );
        if ( looping ) {
            sound.setLooping( play, true );
        }
        return play;
    }

    @Override
    public final void changeSound( int soundId, long instanceId, float volume, float pitch, float pan ) {
        Sound sound = sounds.get( soundId );
        if ( sound == null ) {
            return;
        }
        
        sound.setPan( instanceId, pan, volume );
        sound.setPitch( instanceId, pitch );
        sound.setVolume( instanceId, volume );
    }

    @Override
    public final void stopSound( int soundId, long instanceId ) {
        Sound sound = sounds.get( soundId );
        if ( sound == null ) {
            return;
        }
        
        sound.stop( instanceId );
    }

    @Override
    public final void playMusic( int soundId, boolean looping, float volume, float pan ) {
        Music sound = music.get( soundId );
        if ( sound == null || sound.isPlaying() ) {
            return;
        }
        
        sound.setPan( pan, volume );
        sound.setLooping( looping );
        
        sound.play();
    }

    @Override
    public final void changeMusic( int soundId, float volume, float pan ) {
        Music sound = music.get( soundId );
        if ( sound == null || sound.isPlaying() ) {
            return;
        }
        
        sound.setPan( pan, volume );
    }

    @Override
    public final void stopMusic( int soundId ) {
        Music sound = music.get( soundId );
        if ( sound == null || !sound.isPlaying() ) {
            return;
        }
        
        sound.stop();
    }
    
    @Override
    public final int createSound( SoundAsset asset ) {
        if ( asset.isStreaming() ) {
            return music.add( Gdx.audio.newMusic( Gdx.files.classpath( asset.getResourceName() ) ) );
        } else {
            return sounds.add( Gdx.audio.newSound( Gdx.files.classpath( asset.getResourceName() ) ) );
        }
    }
    
    @Override
    public final void disposeSound( SoundAsset asset ) {
        if ( asset.isStreaming() ) {
            Music music = this.music.remove( asset.index() );
            if ( music != null ) {
                music.dispose();
            }
        } else {
            if ( sounds.contains( asset.index() ) ) {
                com.badlogic.gdx.audio.Sound sound = sounds.remove( asset.index() );
                if ( sound != null ) {
                    sound.dispose();
                }
            }
        }
    }

}
