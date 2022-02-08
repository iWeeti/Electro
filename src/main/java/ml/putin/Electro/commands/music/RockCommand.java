package ml.putin.Electro.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.filter.equalizer.EqualizerFactory;
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

public class RockCommand extends Command {

    public RockCommand() {

        name = "rock";
        help = "Sets the Rock EQ state, on or off";
        arguments = "<state on, off>";
        this.botPermissions = new Permission[] {Permission.MESSAGE_EMBED_LINKS };
    }

    private static final float[] ROCK = { 0.2f, 0.15f, 0.1f, 0.01f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0.1f, 0.1f, 0.15f, 0.2f };
    @Override
    protected void execute(CommandEvent event) {
        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());

        switch(event.getArgs().toLowerCase()) {
            case "off":
                musicManager.toggleEqualizer(false);
                break;
            case "on":
                musicManager.toggleEqualizer(true);
                for (int i = 0; i < ROCK.length; i++) {
                    musicManager.equalizer.setGain(i, ROCK[i]);
                }
                break;
            default:
                event.getChannel().sendMessage(EmbedUtils.defaultEmbed()
                        .setTitle("Invalid Arguments")
                        .setDescription("Usage: `" + Constants.PREFIX + name + " <state on, off>`")
                        .setColor(Color.red).build()).queue();
                return;
        }
        event.getChannel().sendMessage(EmbedUtils.defaultEmbed().setTitle("Rock EQ").setDescription(event.getArgs().toUpperCase() + "\n\nThis should take action in just a second.").build()).queue();
    }
}
