package ml.putin.Electro.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.duncte123.botcommons.messaging.EmbedUtils;
import ml.putin.Electro.Constants;
import ml.putin.Electro.music.GuildMusicManager;
import ml.putin.Electro.music.PlayerManager;
import net.dv8tion.jda.core.Permission;

import java.awt.*;

public class BassBoostCommand extends Command {

    public BassBoostCommand() {
        name = "bassboost";
        aliases = new String[]{ "bb" };
        help = "Set the bass boost state.";
        arguments = "<state off, low, medium, high, insane>";
        this.botPermissions = new Permission[] {Permission.MESSAGE_EMBED_LINKS };
    }

    private static final float[] BASS_BOOST = { 0.2f, 0.15f, 0.1f, 0.05f, 0.0f, -0.05f, -0.1f, -0.1f, -0.1f, -0.1f, -0.1f,
            -0.1f, -0.1f, -0.1f, -0.1f };
    private static final float[] BASS_BOOST_MEDIUM = { 0.5f, 0.25f, 0.1f, 0.05f, 0.0f, -0.05f, -0.1f, -0.1f, -0.1f, -0.1f, -0.1f,
            -0.1f, -0.1f, -0.1f, -0.1f };
    private static final float[] BASS_BOOST_HIGH = { 0.75f, 0.5f, 0.1f, 0.05f, 0.0f, -0.05f, -0.1f, -0.1f, -0.1f, -0.1f, -0.1f,
            -0.1f, -0.1f, -0.1f, -0.1f };
    private static final float[] BASS_BOOST_INSANE = { 1f, 0.75f, 0.1f, 0.05f, 0.0f, -0.05f, -0.1f, -0.1f, -0.1f, -0.1f, -0.1f,
            -0.1f, -0.1f, -0.1f, -0.1f };

    @Override
    protected void execute(CommandEvent event) {
        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());

        switch(event.getArgs().toLowerCase()) {
            case "off":
                musicManager.toggleEqualizer(false);
                break;
            case "low":
                musicManager.toggleEqualizer(true);
                for (int i = 0; i < BASS_BOOST.length; i++) {
                    musicManager.equalizer.setGain(i, BASS_BOOST[i]);
                }
                break;
            case "medium":
                musicManager.toggleEqualizer(true);
                for (int i = 0; i < BASS_BOOST.length; i++) {
                    musicManager.equalizer.setGain(i, BASS_BOOST_MEDIUM[i]);
                }
                break;
            case "high":
                musicManager.toggleEqualizer(true);
                for (int i = 0; i < BASS_BOOST.length; i++) {
                    musicManager.equalizer.setGain(i, BASS_BOOST_HIGH[i]);
                }
                break;
            case "insane":
                musicManager.toggleEqualizer(true);
                for (int i = 0; i < BASS_BOOST.length; i++) {
                    musicManager.equalizer.setGain(i, BASS_BOOST_INSANE[i]);
                }
                break;
            default:
                event.getChannel().sendMessage(EmbedUtils.defaultEmbed()
                        .setTitle("Missing Arguments")
                        .setDescription( "Usage: `" + Constants.PREFIX + getName() + " <state off, low, medium, high, insane>`")
                        .setColor(Color.red).build()).queue();
                return;
        }


        event.getChannel().sendMessage(EmbedUtils.defaultEmbed().setTitle("Bass Boost").setDescription(event.getArgs().toUpperCase() + "\n\nThis should take action in just a second.").build()).queue();

    }
}
