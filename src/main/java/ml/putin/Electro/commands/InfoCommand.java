package ml.putin.Electro.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.duncte123.botcommons.messaging.EmbedUtils;
import ml.putin.Electro.Main;
import net.dv8tion.jda.core.Permission;

public class InfoCommand extends Command {

    public InfoCommand() {

        name = "info";
        help = "Shows info about the bot.";
        botPermissions = new Permission[] {Permission.MESSAGE_EMBED_LINKS };
    }

    @Override
    protected void execute(CommandEvent event) {
        int total = event.getClient().getCommands().stream().mapToInt(cmd -> event.getClient().getCommandUses(cmd)).sum();
        event.getChannel().sendMessage(EmbedUtils.defaultEmbed()
                .setTitle("Info")
                .setDescription(String.format("Guilds: %s\nCommands Ran: %s", event.getJDA().getGuilds().size(), total))
                .build()).queue();
    }
}
