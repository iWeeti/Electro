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

import java.awt.*;
import java.util.List;

public class VolumeCommand extends Command {

    public VolumeCommand() {

        name = "volume";
        help = "Set or get the current volume.";
        aliases = new String[]{"vol", "v"};
        arguments = "[volume]";
        this.botPermissions = new Permission[] {Permission.MESSAGE_EMBED_LINKS };
    }

    private int cap(int val, int min, int max) {
        if (val < min)
            return min;
        if (val > max)
            return max;
        return val;
    }

    @Override
    protected void execute(CommandEvent event) {
        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());

        if (event.getArgs().isEmpty()) {
            event.getChannel().sendMessage(EmbedUtils.defaultEmbed().setTitle("Volume")
                    .setDescription(musicManager.player.getVolume() + "")
                    .setColor(Color.red).build()).queue();

            return;
        }

        int vol;
        try {
            vol = cap(Integer.valueOf(event.getArgs()), 0, 1000);
        } catch (Exception e) {
            event.getChannel().sendMessage(DefaultEmbeds.missingArgs("volume").build()).queue();
            return;
        }
        musicManager.player.setVolume(vol);

        event.getChannel().sendMessage(EmbedUtils.defaultEmbed().setTitle("Volume")
                .setDescription("Set volume to " + vol).build()).queue();

    }
}
