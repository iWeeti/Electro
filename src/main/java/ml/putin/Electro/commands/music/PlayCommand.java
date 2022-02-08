package ml.putin.Electro.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.io.MessageInput;
import com.sedmelluq.discord.lavaplayer.track.DecodedTrackHolder;
import me.duncte123.botcommons.messaging.EmbedUtils;
import ml.putin.Electro.Constants;
import ml.putin.Electro.Main;
import ml.putin.Electro.music.GuildMusicManager;
import ml.putin.Electro.music.PlayerManager;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import org.bson.Document;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Filters.eq;

public class PlayCommand extends Command {

    public PlayCommand() {

        name = "play";
        aliases = new String[]{"p", "sing"};
        help = "Plays a song";
        arguments = "<song>";
        this.botPermissions = new Permission[] {Permission.MESSAGE_EMBED_LINKS };
        guildOnly = true;
    }

    private boolean isUrl(String input) {
        try {
            new URL(input);

            return true;
        } catch (MalformedURLException ignored) {
            return false;
        }
    }

    @Override
    protected void execute(CommandEvent event) {
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

        if (event.getArgs().isEmpty()) {
            if (PlayerManager.getInstance().getGuildMusicManager(event.getGuild()).player.isPaused()) {
                PlayerManager.getInstance().getGuildMusicManager(event.getGuild()).player.setPaused(false);
                channel.sendMessage(EmbedUtils.defaultEmbed()
                        .setTitle("Resumed").build()).queue();
                return;
            }
            channel.sendMessage(EmbedUtils.defaultEmbed()
                    .setTitle("Missing Arguments")
                    .setDescription("Usage: ``" + Constants.PREFIX + "play <song>``")
                    .setColor(Color.red).build()).queue();

            return;
        }

        String input = event.getArgs();

        if (!isUrl(input) && !input.startsWith("ytsearch:")  //)
                &&
                !event.getArgs().split(" ")[0].equalsIgnoreCase("pl") &&
                !event.getArgs().split(" ")[0].equalsIgnoreCase("playlist")
                )
        {
            input = "ytsearch:" + input;
        }
        else {
            if (event.getArgs().split(" ")[0].equalsIgnoreCase("pl") ||
                    event.getArgs().split(" ")[0].equalsIgnoreCase("playlist")) {

                String query = event.getArgs().replaceFirst("pl ", "").replaceFirst("playlist ", "");
                Main.playlists.find(eq("name", query)).first((document, throwable) -> {
                    try {
                        ArrayList<Document> songs = (ArrayList<Document>) document.get("songs");
                        PlayerManager manager = PlayerManager.getInstance();
                        GuildMusicManager musicManager = manager.getGuildMusicManager(event.getGuild());
                        if (PlayerManager.getPlayerTextChannel(musicManager.player) == null) {
                            PlayerManager.setPlayerTextchannel(musicManager.player, event.getTextChannel());
                        }
                        songs.forEach(song -> {
                            byte[] bytes = Base64.getDecoder().decode(song.getString("track"));
                            MessageInput inputStream = new MessageInput(new ByteArrayInputStream(bytes));
                            DecodedTrackHolder holder;
                            try{
                                while ((holder = manager.getPlayerManager().decodeTrack(inputStream)) != null) {
                                    musicManager.scheduler.queue(holder.decodedTrack, event.getAuthor());
                                }
                            } catch (IOException e) {
                                return;
                            }
                        });
                        channel.sendMessage(EmbedUtils.defaultEmbed()
                                .setTitle("Queued playlist")
                                .setDescription(String.format("Name: %s\nLength: %s", query, songs.size())).build()).queue();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });
                return;
            }
        }

        PlayerManager manager = PlayerManager.getInstance();

        manager.loadAndPlay(event.getTextChannel(), input, event.getAuthor());
        manager.getGuildMusicManager(event.getGuild()).player.setPaused(false);
    }
}
