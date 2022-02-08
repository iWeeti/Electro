package ml.putin.Electro.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.duncte123.botcommons.messaging.EmbedUtils;
import ml.putin.Electro.Constants;
import ml.putin.Electro.music.GuildMusicManager;
import ml.putin.Electro.music.PlayerManager;
import ml.putin.Electro.objects.ICommand;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class PauseCommand extends Command {

    public PauseCommand() {

        name = "pause";
        help = "Inverts pause state.";
        aliases = new String[]{"resume"};
        this.botPermissions = new Permission[] {Permission.MESSAGE_EMBED_LINKS };
    }

    @Override
    protected void execute(CommandEvent event) {
        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());

        musicManager.player.setPaused(!musicManager.player.isPaused());

        String state = musicManager.player.isPaused() ? "Paused" : "Resumed";

        event.getChannel().sendMessage(EmbedUtils.defaultEmbed().setTitle(state).build()).queue();

    }
}
