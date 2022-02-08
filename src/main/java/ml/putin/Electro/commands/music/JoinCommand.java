package ml.putin.Electro.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.managers.AudioManager;

import java.awt.*;

public class JoinCommand extends Command {

    public JoinCommand() {
        name = "join";
        aliases = new String[] {"j", "connect", "c"};
        help = "Makes the bot join your voice channel.";
        this.botPermissions = new Permission[] {Permission.MESSAGE_EMBED_LINKS };
    }

    @Override
    protected void execute(CommandEvent event) {
        MessageChannel channel = event.getChannel();
        AudioManager audioManager = event.getGuild().getAudioManager();
        if (audioManager.isConnected()) {
            channel.sendMessage(EmbedUtils.defaultEmbed()
                .setTitle("Already Connected")
                .setColor(Color.red).build()).queue();
            return;
        }

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

        audioManager.openAudioConnection(voiceChannel);
        channel.sendMessage(EmbedUtils.defaultEmbed()
                .setTitle("Joining")
                .setDescription(voiceChannel.getName()).build()).queue();

    }
}
