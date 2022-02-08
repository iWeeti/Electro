package ml.putin.Electro.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.duncte123.botcommons.messaging.EmbedUtils;
import ml.putin.Electro.music.GuildMusicManager;
import ml.putin.Electro.music.PlayerManager;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.GuildVoiceState;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public class QuizCommand extends Command {

    public QuizCommand() {
        name = "quiz";
        arguments = "[playlist]";
        help = "Starts a music quiz, you can specify a YouTube playlist to use as a playlist.";
        this.botPermissions = new Permission[] {Permission.MESSAGE_EMBED_LINKS };
    }

    @Override
    protected void execute(CommandEvent event){
        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());
        String playlistLink = "https://www.youtube.com/watch?v=Pkh8UtuejGw&list=PLx0sYbCqOb8TBPRdmBHs5Iftvv9TPboYG";
        MessageChannel channel = event.getChannel();

        GuildVoiceState memberVoiceState = event.getMember().getVoiceState();

        if (!memberVoiceState.inVoiceChannel()) {
            channel.sendMessage(EmbedUtils.defaultEmbed()
                    .setTitle("Join a Voice Channel")
                    .setColor(Color.red).build()).queue();
            return;
        }

        VoiceChannel voiceChannel = memberVoiceState.getChannel();
        Member selfMember = event.getGuild().getSelfMember();

        if (!selfMember.hasPermission(voiceChannel, Permission.VOICE_CONNECT)) {
            channel.sendMessage(EmbedUtils.defaultEmbed()
                    .setTitle("Missing Permissions")
                    .setDescription("I need the Connect permissions to be able to join a voice channel!")
                    .setColor(Color.red).build()).queue();
            return;
        }

        if (!event.getGuild().getAudioManager().isConnected()){
            event.getGuild().getAudioManager().openAudioConnection(voiceChannel);
        }

        try{

            playerManager.getPlayerManager().loadItemOrdered(musicManager, playlistLink, new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack audioTrack) {
                    event.reply(EmbedUtils.defaultEmbed().setTitle("That is not a playlist.").setColor(Color.red).build());
                }

                @Override
                public void playlistLoaded(AudioPlaylist audioPlaylist) {
                    musicManager.scheduler.getQueue().clear();
                    musicManager.scheduler.isQuiz = true;
                    if (musicManager.player.getPlayingTrack() != null) {
                        musicManager.player.stopTrack();
                    }

                    ArrayList<AudioTrack> songs = (ArrayList<AudioTrack>) audioPlaylist.getTracks();
                    Collections.shuffle(songs);

                    boolean run = true;
                    while (run) {
                        AudioTrack track = songs.get(0);
                        songs.remove(0);
                        musicManager.player.playTrack(track);
                        new LevenshteinDistance();
//                        System.out.println(match);

                        run = false;

                    }

                    musicManager.scheduler.isQuiz = false;
                    event.reply("stopped");
                }

                @Override
                public void noMatches() {
                    event.reply(EmbedUtils.defaultEmbed().setTitle("No matches found.").setColor(Color.red).build());
                }

                @Override
                public void loadFailed(FriendlyException e) {
                    event.reply(EmbedUtils.defaultEmbed().setTitle("Failed to load the playlist." + e.toString()).setColor(Color.red).build());
                }
        });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
