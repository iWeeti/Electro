package ml.putin.Electro.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.duncte123.botcommons.messaging.EmbedUtils;
import ml.putin.Electro.music.GuildMusicManager;
import ml.putin.Electro.music.PlayerManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class NPCommand extends Command {

    public NPCommand() {

        name = "np";
        aliases = new String[]{"now", "current"};
        help = "Shows the now playing song.";
        botPermissions = new Permission[] {Permission.MESSAGE_EMBED_LINKS };
    }


    @Override
    protected void execute(CommandEvent event) {
        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());

        if (musicManager.player.getPlayingTrack() == null) {

            event.getChannel().sendMessage(EmbedUtils.defaultEmbed().setTitle("Not playing")
                    .setColor(Color.red).build()).queue();

            return;
        }

        AudioTrack track = musicManager.player.getPlayingTrack();

        EmbedBuilder builder = EmbedUtils.defaultEmbed()
                .setTitle("Now Playing")
                .setDescription("[" + track.getInfo().title + "](" + track.getInfo().uri + ")\n" +
                                "``[" + formatMillis(track.getPosition()) + "/" + formatMillis(track.getDuration()) + "]``")
                .setFooter(String.format("Requested by %s", musicManager.scheduler.np.requester.getAsTag()), musicManager.scheduler.np.requester.getAvatarUrl());

        if (track.getInfo().identifier != null) {
            builder.setThumbnail("http://i.ytimg.com/vi/" +  track.getInfo().identifier + "/maxresdefault.jpg");
        }
        event.getChannel().sendMessage(builder.build()).queue();

    }

    public static String formatMillis(long millis) {
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }
}
