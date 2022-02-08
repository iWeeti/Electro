package ml.putin.Electro.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.duncte123.botcommons.messaging.EmbedUtils;
import ml.putin.Electro.Constants;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import ml.putin.Electro.objects.ICommand;

import java.util.List;

public class PingCommand extends Command {

    public PingCommand() {

        name = "ping";
        help = "Shows the heartbeat ping of the bot.";
        this.botPermissions = new Permission[] {Permission.MESSAGE_EMBED_LINKS };
    }

    @Override
    protected void execute(CommandEvent event) {
        event.getChannel().sendMessage(EmbedUtils.defaultEmbed().setTitle(":ping_pong: Pong!").build()).queue((message) ->
                message.editMessage(EmbedUtils.defaultEmbed().setTitle(":ping_pong: Pong!").setDescription("My ping is " + event.getJDA().getPing() + "ms.").build()).queue()
        );

    }
}
