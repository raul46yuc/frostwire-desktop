package com.frostwire.gui.player;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.SwingUtilities;

import org.gudy.azureus2.core3.util.UrlUtils;
import org.limewire.util.OSUtils;

import com.frostwire.gui.library.LibraryMediator;
import com.frostwire.mplayer.MPlayer;
import com.frostwire.mplayer.MediaPlaybackState;
import com.frostwire.mplayer.PositionListener;
import com.frostwire.mplayer.StateListener;
import com.limegroup.gnutella.gui.RefreshListener;
import com.limegroup.gnutella.util.FrostWireUtils;

/**
 *  An audio player to play compressed and uncompressed music.
 */
public class AudioPlayer implements RefreshListener {

    /**
     * Our list of AudioPlayerListeners that are currently listening for events
     * from this player
     */
    private List<AudioPlayerListener> listenerList = new CopyOnWriteArrayList<AudioPlayerListener>();

    private MPlayer _mplayer;
    private AudioSource currentSong;
    private RepeatMode repeatMode;
    private boolean shuffle;
    private boolean playNextSong;

    private static AudioPlayer instance;

    public static AudioPlayer instance() {
        if (instance == null) {
            instance = new AudioPlayer();
        }
        return instance;
    }

    private AudioPlayer() {
        String playerPath = "";

        // Whether or not we're running from source or from a binary distribution
        boolean isRelease = !FrostWireUtils.getFrostWireJarPath().contains("frostwire.desktop");

        if (OSUtils.isWindows()) {
            playerPath = (isRelease) ? FrostWireUtils.getFrostWireJarPath() + File.separator + "fwplayer.exe" : "lib/native/fwplayer.exe";
            playerPath = UrlUtils.decode(playerPath);
        } else if (OSUtils.isMacOSX()) {
            String macOSFolder = new File(FrostWireUtils.getFrostWireJarPath()).getParentFile().getParent() + File.separator + "MacOS";

            playerPath = (isRelease) ? macOSFolder + File.separator + "fwplayer" : "lib/native/fwplayer";
        } else {
            playerPath = "/usr/bin/mplayer";
        }

        //System.out.println("LimeWirePlayer - player path: ["+playerPath+"]");

        MPlayer.initialise(new File(playerPath));
        _mplayer = new MPlayer();
        _mplayer.addPositionListener(new PositionListener() {
            public void positionChanged(float currentTimeInSecs) {
                notifyProgress(currentTimeInSecs);
            }
        });
        _mplayer.addStateListener(new StateListener() {
            public void stateChanged(MediaPlaybackState newState) {
                if (newState == MediaPlaybackState.Closed) { // This is the case mplayer is done with the current file
                    handleNextSong();
                }
            }
        });

        repeatMode = RepeatMode.All;
        shuffle = false;
        playNextSong = true;
    }

    public AudioSource getCurrentSong() {
        return currentSong;
    }

    public RepeatMode getRepeatMode() {
        return repeatMode;
    }

    public void setRepeatMode(RepeatMode repeatMode) {
        this.repeatMode = repeatMode;
    }

    public boolean isShuffle() {
        return shuffle;
    }

    public void setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
    }

    /**
     * Adds the specified AudioPlayer listener to the list
     */
    public void addAudioPlayerListener(AudioPlayerListener listener) {
        listenerList.add(listener);
    }

    /**
     * Removes the specified AudioPlayer listener from the list
     */
    public void removeAudioPlayerListener(AudioPlayerListener listener) {
        listenerList.remove(listener);
    }

    public MediaPlaybackState getState() {
        return _mplayer.getCurrentState();
    }

    /**
     * Loads a AudioSource into the player to play next
     */
    public void loadSong(AudioSource source, boolean play, boolean playNextSong) {
        currentSong = source;
        this.playNextSong = playNextSong;
        notifyOpened(source);
        if (play) {
            playSong();
        }
    }

    public void loadSong(AudioSource audioSource) {
        loadSong(audioSource, false, false);
    }

    /**
     * Begins playing a song
     */
    public void playSong() {
        _mplayer.stop();

        if (currentSong.getFile() != null) {
            _mplayer.open(currentSong.getFile().getAbsolutePath());
        } else if (currentSong.getURL() != null) {
            _mplayer.open(currentSong.getURL().toString());
        }

        notifyState(getState());
    }

    /**
     * Toggle pause the current song
     */
    public void togglePause() {
        _mplayer.togglePause();
        notifyState(getState());
    }

    /**
     * Stops the current song
     */
    public void stop() {
        _mplayer.stop();
        notifyState(getState());
    }

    /**
     * Seeks to a new location in the current song
     */
    public void seek(float timeInSecs) {
        _mplayer.seek(timeInSecs);
        notifyState(getState());
    }

    /**
     * Sets the gain(volume) for the outputline
     * 
     * @param gain - [0.0 <-> 1.0]
     * @throws IOException - thrown when the soundcard does not support this
     *         operation
     */
    public void setVolume(double fGain) {
        _mplayer.setVolume((int) (fGain * 200));
    }

    public static boolean isPlayableFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".mp3") || name.endsWith(".ogg") || name.endsWith(".wav") || name.endsWith(".wma") || name.endsWith(".m4a");
    }

    /**
     * Notify listeners when a new audio source has been opened. 
     * 
     * @param properties - any properties about the source that we extracted
     */
    protected void notifyOpened(final AudioSource audioSource) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                fireOpened(audioSource);
            }
        });
    }

    /**
     * Notify listeners about an AudioPlayerEvent. This creates general state
     * modifications to the player such as the transition from opened to 
     * playing to paused to end of song.
     * 
     * @param code - the type of player event.
     * @param position in the stream when the event occurs.
     * @param value if the event was a modification such as a volume update,
     *        list the new value
     */
    protected void notifyState(final MediaPlaybackState state) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                fireState(state);
            }
        });
    }

    /**
     * fires a progress event off a new thread. This lets us safely fire events
     * off of the player thread while using a lock on the input stream
     */
    protected void notifyProgress(final float currentTimeInSecs) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                fireProgress(currentTimeInSecs);
            }
        });
    }

    /**
     * This is fired every time a new song is loaded and ready to play. The
     * properties map contains information about the type of song such as bit
     * rate, sample rate, media type(MPEG, Streaming,etc..), etc..
     */
    protected void fireOpened(AudioSource audioSource) {
        for (AudioPlayerListener listener : listenerList) {
            listener.songOpened(this, audioSource);
        }
    }

    /**
     * Fired every time a byte stream is written to the sound card. This lets 
     * listeners be aware of what point in the entire file is song is currently
     * playing. This also returns a copy of the written byte[] so it can get
     * passed along to objects such as a FFT for visual feedback of the song
     */
    protected void fireProgress(float currentTimeInSecs) {
        for (AudioPlayerListener listener : listenerList) {
            listener.progressChange(this, currentTimeInSecs);
        }
    }

    /**
     * Fired every time the state of the player changes. This allows a listener
     * to be aware of state transitions such as from OPENED -> PLAYING ->
     * STOPPED -> EOF
     */
    protected void fireState(MediaPlaybackState state) {
        for (AudioPlayerListener listener : listenerList) {
            listener.stateChange(this, state);
        }
    }

    /**
     * returns the current state of the player and position of the song being
     * played
     */
    public void refresh() {
        notifyState(getState());
    }

    private void handleNextSong() {
        if (!playNextSong) {
            return;
        }

        AudioSource song = null;

        if (getRepeatMode() == RepeatMode.Song) {
            song = currentSong;
        } else if (isShuffle()) {
            song = LibraryMediator.instance().getNextRandomSong(currentSong);
        } else if (getRepeatMode() == RepeatMode.All) {
            song = LibraryMediator.instance().getNextContinuousSong(currentSong);
        } else {
            song = LibraryMediator.instance().getNextSong(currentSong);
        }

        if (song != null) {
            System.out.println(song.getFile());
            loadSong(song, true, true);
        }
    }
}