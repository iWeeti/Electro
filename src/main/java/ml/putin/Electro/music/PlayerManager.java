package ml.putin.Electro.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.duncte123.botcommons.messaging.EmbedUtils;
import ml.putin.Electro.Main;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;
import org.w3c.dom.Text;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PlayerManager {

    private static PlayerManager INSTANCE;
    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> musicManagers;
    private final Map<Long, GuildMusicManager> radioManagers;
    private static final Map<AudioPlayer, TextChannel> playerChannels = new HashMap<>();
    private AudioPlayer mainPlayer;

    public PlayerManager() {
        this.musicManagers = new HashMap<>();
        this.radioManagers = new HashMap<>();

        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    public AudioPlayerManager getPlayerManager() {
        return playerManager;
    }

    public static void setPlayerTextchannel(AudioPlayer player, TextChannel channel ) {
        playerChannels.put(player, channel);
    }

    public static TextChannel getPlayerTextChannel(AudioPlayer player){
        return playerChannels.get(player);
    }

    public synchronized GuildMusicManager getGuildMusicManager(Guild guild) {
        long guildId = guild.getIdLong();
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    public Map<Long, GuildMusicManager> getRadioManagers() {
        return radioManagers;
    }

    public AudioPlayer getMainRadioPlayer() {
        if (mainPlayer == null) {
            System.out.println("Getting main radio player.");
            Guild guild = Main.jda.getGuildById(591984433331044352L);
            AudioManager audioManager = guild.getAudioManager();
            VoiceChannel channel = Main.jda.getVoiceChannelById(591984433331044360L);
            audioManager.openAudioConnection(channel);
            audioManager.setAutoReconnect(true);
            GuildMusicManager musicManager = getGuildMusicManager(guild);
            musicManager.player.addListener(new RadioScheduler(musicManager.player, guild));
            mainPlayer = musicManager.player;
            loadAndPlay(guild, "ytsearch: high enough", Main.jda.getSelfUser());

        }

        return mainPlayer;
    }

    public void loadAndPlay(TextChannel channel, String trackUri, User requester) {

        GuildMusicManager musicManager = getGuildMusicManager(channel.getGuild());

        if (!playerChannels.containsKey(musicManager.player) || !playerChannels.get(musicManager.player).equals(channel)) {
            playerChannels.put(musicManager.player, channel);
        } else {
            playerChannels.replace(musicManager.player, channel);
        }


        final boolean ignorePlaylist;
        ignorePlaylist = trackUri.startsWith("ytsearch:");

        playerManager.loadItemOrdered(musicManager, trackUri, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                channel.sendMessage(EmbedUtils.defaultEmbed()
                        .setTitle("Adding to queue")
                        .setImage("http://i.ytimg.com/vi/" + track.getIdentifier() + "/maxresdefault.jpg")
                        .setDescription("[" + track.getInfo().title + "](" + track.getInfo().uri + ")").build()).queue();
                play(musicManager, track, requester);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                AudioTrack firstTrack = audioPlaylist.getSelectedTrack();

                if (firstTrack == null) {
                    EmbedBuilder builder = EmbedUtils.defaultEmbed();
                    if (!ignorePlaylist) {
                        builder.setTitle("Adding playlist to queue");
                        builder.setDescription(audioPlaylist.getName());
                        channel.sendMessage(builder.build()).queue();
                        for (AudioTrack track : audioPlaylist.getTracks()) {
                            play(musicManager, track, requester);
                        }
                    } else {
                        builder.setTitle("Adding to queue");
                        builder.setThumbnail("http://i.ytimg.com/vi/" + audioPlaylist.getTracks().get(0).getIdentifier() + "/maxresdefault.jpg");
                        builder.setDescription("[" + audioPlaylist.getTracks().get(0).getInfo().title + "](" + audioPlaylist.getTracks().get(0).getInfo().uri + ")");
                        channel.sendMessage(builder.build()).queue();
                        play(musicManager, audioPlaylist.getTracks().get(0), requester);
                    }
                } else {
                    channel.sendMessage(EmbedUtils.defaultEmbed()
                            .setTitle("Adding to queue")
                            .setThumbnail("http://i.ytimg.com/vi/" + firstTrack.getIdentifier() + "/maxresdefault.jpg")
                            .setDescription("[" + firstTrack.getInfo().title + "](" + firstTrack.getInfo().uri + ")").build()).queue();
                    play(musicManager, firstTrack, requester);
                }
            }

            @Override
            public void noMatches() {
                channel.sendMessage(EmbedUtils.defaultEmbed()
                        .setTitle("No Matches")
                        .setColor(Color.red).build()).queue();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                channel.sendMessage(EmbedUtils.defaultEmbed()
                        .setTitle("Could not play")
                        .setColor(Color.red).build()).queue();
            }
        });
    }

    public void loadAndPlay(Guild guild, String trackUri, User requester) {

        GuildMusicManager musicManager = getGuildMusicManager(guild);

        final boolean ignorePlaylist;
        ignorePlaylist = trackUri.startsWith("ytsearch:");

        playerManager.loadItemOrdered(musicManager, trackUri, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                play(musicManager, track, requester);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                AudioTrack firstTrack = audioPlaylist.getSelectedTrack();

                if (firstTrack == null) {
                    if (!ignorePlaylist) {
                        for (AudioTrack track : audioPlaylist.getTracks()) {
                            play(musicManager, track, requester);
                        }
                    } else {
                        play(musicManager, audioPlaylist.getTracks().get(0), requester);
                    }
                } else {
                    play(musicManager, firstTrack, requester);
                }
            }

            @Override
            public void noMatches() {

            }

            @Override
            public void loadFailed(FriendlyException e) {

            }
        });
    }

    private void play(GuildMusicManager musicManager, AudioTrack track, User requester) {
        musicManager.scheduler.queue(track, requester);
    }

//    public AudioPlayer getPlayer() {
//        return mainPlayer;
//    }

    public static synchronized PlayerManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }

        return INSTANCE;
    }
}
