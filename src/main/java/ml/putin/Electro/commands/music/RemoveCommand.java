package ml.putin.Electro.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.duncte123.botcommons.messaging.EmbedUtils;
import ml.putin.Electro.Constants;
import ml.putin.Electro.music.GuildMusicManager;
import ml.putin.Electro.music.PlayerManager;
import ml.putin.Electro.objects.ICommand;
import ml.putin.Electro.utils.DefaultEmbeds;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class RemoveCommand extends Command {

    public RemoveCommand() {

        name = "remove";
        help = "Removes the song from the queue with the specified index. User e.queue to view the current queue.";
        arguments = "<index>";
        this.botPermissions = new Permission[] {Permission.MESSAGE_EMBED_LINKS };
    }

    @Override
    protected void execute(CommandEvent event) {
        MessageChannel channel = event.getChannel();
        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());

        if(event.getArgs().isEmpty()) {
            channel.sendMessage(DefaultEmbeds.missingArgs("index").build()).queue();

            return;
        }

        int index = Integer.valueOf(getArguments());

        if (index < musicManager.scheduler.getQueue().size()
        || index > musicManager.scheduler.getQueue().size()) {
            channel.sendMessage(EmbedUtils.defaultEmbed()
                    .setTitle("Invalid Arguments")
                    .setDescription("Index must be ``queue > index`` and ``index <= queue``!").build()).queue();

            return;
        }

        AudioTrack track = (AudioTrack) musicManager.scheduler.getQueue().toArray()[index];
        musicManager.scheduler.getQueue().remove(track);
        channel.sendMessage(EmbedUtils.defaultEmbed().setTitle("Removed")
                .setDescription(track.getInfo().title).build()).queue();

    }
}
