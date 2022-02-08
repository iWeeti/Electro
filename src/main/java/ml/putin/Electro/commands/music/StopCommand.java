package ml.putin.Electro.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.duncte123.botcommons.messaging.EmbedUtils;
import ml.putin.Electro.music.GuildMusicManager;
import ml.putin.Electro.music.PlayerManager;
import ml.putin.Electro.objects.ICommand;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.List;

public class StopCommand extends Command {

    public StopCommand() {

        name = "stop";
        help = "Stops the music and clears the queue.";
        this.botPermissions = new Permission[] {Permission.MESSAGE_EMBED_LINKS };
    }

    @Override
    protected void execute(CommandEvent event) {
        PlayerManager manager = PlayerManager.getInstance();
        GuildMusicManager musicManager = manager.getGuildMusicManager(event.getGuild());

        if (musicManager.player.getPlayingTrack() == null) {

            event.getChannel().sendMessage(EmbedUtils.defaultEmbed().setTitle("Not playing")
                    .setColor(Color.red).build()).queue();

            return;
        }

        musicManager.scheduler.getQueue().clear();
        musicManager.player.stopTrack();
        musicManager.player.setPaused(false);


        event.getChannel().sendMessage(EmbedUtils.defaultEmbed().setTitle("Stopped, and cleared queue").build()).queue();

    }
}
