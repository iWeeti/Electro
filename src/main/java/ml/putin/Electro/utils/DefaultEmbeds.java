package ml.putin.Electro.utils;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.duncte123.botcommons.messaging.EmbedUtils;
import ml.putin.Electro.Main;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.awt.*;

public class DefaultEmbeds {

    public static EmbedBuilder missingArgs(String missingArgs) {
        return EmbedUtils.defaultEmbed().setTitle("Missing Arguments").setDescription("You forgot to add ``" + missingArgs + "``").setColor(Color.red);
    }

    public static MessageEmbed helpEmbed(CommandEvent event) {
        EmbedBuilder builder = EmbedUtils.defaultEmbed()
                .setTitle("Electro Help");
        StringBuilder desc = builder.getDescriptionBuilder();

            for (Command cmd : event.getClient().getCommands()) {
                if (event.getArgs().isEmpty()) {
                    desc.append(String.format("`e.%s %s` %s - %s\n",
                            cmd.getName(),
                            cmd.getArguments() == null ? "" : cmd.getArguments(),
                            String.join(", ", cmd.getAliases()),
                            cmd.getHelp()));
                } else {
                    if (cmd.getName().equalsIgnoreCase(event.getArgs())) {
                        desc.append(String.format(
                                "%s %s\n%s\n%s",
                                cmd.getName(),
                                cmd.getArguments(),
                                String.join(", ", cmd.getAliases()),
                                cmd.getHelp()
                        ));
                    }
            }
        }

        return builder.build();
    }
}
