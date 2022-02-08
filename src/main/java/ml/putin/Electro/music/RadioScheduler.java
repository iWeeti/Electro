package ml.putin.Electro.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import ml.putin.Electro.Main;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;

/**
 * This class schedules tracks for the audio player. It contains the queue of tracks.
 */
public class RadioScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private PlayerManager playerManager = PlayerManager.getInstance();
    private Guild guild;

    /**
     * @param player The audio player this scheduler uses
     */
    public RadioScheduler(AudioPlayer player, Guild guild) {
        this.player = player;
        this.guild = guild;
    }

    public void start() {
        System.out.println("Starting to play main player's music.");
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(guild);
        musicManager.radioScheduler = this;
        playerManager.getRadioManagers().put(guild.getIdLong(), musicManager);
        player.startTrack(playerManager.getMainRadioPlayer().getPlayingTrack().makeClone(), false);
        player.getPlayingTrack().setPosition(playerManager.getMainRadioPlayer().getPlayingTrack().getPosition());
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        Main.jda.getPresence().setGame(Game.listening(track.getInfo().title));

        for (GuildMusicManager musicManager : playerManager.getRadioManagers().values()){
            if (musicManager.player != playerManager.getMainRadioPlayer())
                System.out.println("Starting next song.");

//                musicManager.scheduler.queue(playerManager.getMainRadioPlayer().getPlayingTrack().makeClone());
                if (musicManager.player.getPlayingTrack() != null) {
                    musicManager.scheduler.nextTrack();
                }
        }
    }
}