package ml.putin.Electro.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.duncte123.botcommons.messaging.EmbedUtils;
import ml.putin.Electro.music.GuildMusicManager;
import ml.putin.Electro.music.PlayerManager;
import ml.putin.Electro.music.RadioScheduler;
import ml.putin.Electro.objects.ICommand;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.List;

public class RadioCommand extends Command {

    public RadioCommand() {

        name = "radio";
        help = "Starts the radio.";
        this.botPermissions = new Permission[] {Permission.MESSAGE_EMBED_LINKS };
    }

    @Override
    protected void execute(CommandEvent event) {
        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());

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

        musicManager.player.stopTrack();
        musicManager.scheduler.getQueue().clear();

        RadioScheduler radioScheduler = new RadioScheduler(musicManager.player, event.getGuild());
        radioScheduler.start();
        musicManager.player.addListener(radioScheduler);
        musicManager.scheduler.isRadio = true;
        event.getChannel().sendMessage("Started radio.").queue();

    }
}
