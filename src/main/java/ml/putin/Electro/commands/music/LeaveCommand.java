package ml.putin.Electro.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.duncte123.botcommons.messaging.EmbedUtils;
import ml.putin.Electro.objects.ICommand;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.GuildVoiceState;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;

import java.awt.*;
import java.util.List;

public class LeaveCommand extends Command {

    public LeaveCommand() {

        name = "leave";
        help = "Makes the bot leave your voice channel.";
        aliases = new String[]{"dc", "disconnect"};
        this.botPermissions = new Permission[] {Permission.MESSAGE_EMBED_LINKS };
    }

    @Override
    protected void execute(CommandEvent event) {
        MessageChannel channel = event.getChannel();
        AudioManager audioManager = event.getGuild().getAudioManager();

        if (!audioManager.isConnected()) {
            channel.sendMessage(EmbedUtils.defaultEmbed()
                    .setTitle("Not Connected")
                    .setColor(Color.red).build()).queue();
            return;
        }

        GuildVoiceState memberVoiceState = event.getMember().getVoiceState();

        if (!audioManager.getConnectedChannel().getMembers().contains(event.getMember())) {
            channel.sendMessage(EmbedUtils.defaultEmbed()
                    .setTitle("You are not in my Voice Channel")
                    .setColor(Color.red).build()).queue();
            return;
        }

        audioManager.closeAudioConnection();
        channel.sendMessage(EmbedUtils.defaultEmbed()
                .setTitle("Leaving")
                .setDescription(audioManager.getConnectedChannel().getName()).build()).queue();

    }
}
