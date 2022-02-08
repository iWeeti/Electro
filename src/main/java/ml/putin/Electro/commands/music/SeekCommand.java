package ml.putin.Electro.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.duncte123.botcommons.messaging.EmbedUtils;
import ml.putin.Electro.Constants;
import ml.putin.Electro.music.GuildMusicManager;
import ml.putin.Electro.music.PlayerManager;
import ml.putin.Electro.objects.ICommand;
import ml.putin.Electro.utils.DefaultEmbeds;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class SeekCommand extends Command {

    public SeekCommand() {

        name = "seek";
        help = "Seeks the currently playing song.";
        arguments = "<position MM:SS>";
        this.botPermissions = new Permission[] {Permission.MESSAGE_EMBED_LINKS };
    }

    @Override
    protected void execute(CommandEvent event) {
        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());

         if (event.getArgs().isEmpty()){
            event.getChannel().sendMessage(DefaultEmbeds.missingArgs("position MM:SS").build()).queue();

            return;
        }

        long m = 0;
        long s;

        String[] split = event.getArgs().split(":");

        if (split.length >= 2) {
            m = Long.valueOf(split[0]);
            s = Long.valueOf(split[1]);
        } else {
            s = Long.valueOf(split[0]);
        }

        musicManager.player.getPlayingTrack().setPosition(m * 60 * 1000 + s * 1000);
        event.getChannel().sendMessage(EmbedUtils.defaultEmbed().setTitle("Seeking").setDescription(String.join(":", split)).build()).queue();

    }
}
