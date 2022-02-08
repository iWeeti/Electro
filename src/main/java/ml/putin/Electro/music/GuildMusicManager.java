package ml.putin.Electro.music;

import com.sedmelluq.discord.lavaplayer.filter.equalizer.EqualizerFactory;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.core.entities.User;

import java.util.HashMap;
import java.util.Map;

/**
 * Holder for both the player and a track scheduler for one guild.
 */
public class GuildMusicManager {
    /**
     * Audio player for the guild.
     */
    public final AudioPlayer player;
    /**
     * Track scheduler for the player.
     */
    public final TrackScheduler scheduler;
    public RadioScheduler radioScheduler = null;

    public final EqualizerFactory equalizer = new EqualizerFactory();
    private boolean eqEnabled = false;
    public int skipVotes = 0;
    public Map<User, Boolean> skipVoters = new HashMap<>();

    /**
     * Creates a player and a track scheduler.
     * @param manager Audio player manager to use for creating the player.
     */
    public GuildMusicManager(AudioPlayerManager manager) {
        player = manager.createPlayer();
        scheduler = new TrackScheduler(player);
        player.addListener(scheduler);
    }

    public boolean toggleSkipVote(User user) {
        boolean newState = !skipVoters.computeIfAbsent(user, (u) -> false);
        skipVoters.put(user, newState);
        if (newState) {
            skipVotes++;
        } else {
            skipVotes--;
        }
        return newState;
    }

    public void toggleEqualizer() {
        if (!eqEnabled) {
            player.setFrameBufferDuration(500);
            player.setFilterFactory(equalizer);
            eqEnabled = true;
        } else {
            player.setFrameBufferDuration(0);
            player.setFilterFactory(null);
            eqEnabled = false;
        }
    }

    public void toggleEqualizer(boolean state) {
        eqEnabled = state;
        if (state) {
            player.setFrameBufferDuration(500);
            player.setFilterFactory(equalizer);
        } else {
            player.setFrameBufferDuration(0);
            player.setFilterFactory(null);
        }
    }

    /**
     * @return Wrapper around AudioPlayer to use it as an AudioSendHandler.
     */
    public AudioPlayerSendHandler getSendHandler() {
        return new AudioPlayerSendHandler(player);
    }
}