package ml.putin.Electro.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.duncte123.botcommons.messaging.EmbedUtils;
import ml.putin.Electro.Main;
import ml.putin.Electro.music.GuildMusicManager;
import ml.putin.Electro.music.PlayerManager;
import ml.putin.Electro.objects.ICommand;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ShuffleCommand extends Command {

    public ShuffleCommand() {

        name = "shuffle";
        help = "Shuffles the current queue.";
        this.botPermissions = new Permission[] {Permission.MESSAGE_EMBED_LINKS };
    }

    @Override
    protected void execute(CommandEvent event) {
        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());
        if (musicManager.scheduler.getQueue().isEmpty()) {
            event.getChannel().sendMessage(EmbedUtils.defaultEmbed()
                    .setTitle("Queue is empty")
                    .setColor(Color.red)
                    .build()).queue();
            return;
        }
        musicManager.scheduler.shuffleQueue();
        event.getChannel().sendMessage(EmbedUtils.defaultEmbed().setTitle("Shuffled the queue").build()).queue();

    }
}
