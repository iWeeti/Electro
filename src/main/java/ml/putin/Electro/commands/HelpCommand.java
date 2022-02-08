//package ml.putin.Electro.commands;
//
//import me.duncte123.botcommons.messaging.EmbedUtils;
//import ml.putin.Electro.Constants;
//import ml.putin.Electro.objects.ICommand;
//import net.dv8tion.jda.core.EmbedBuilder;
//import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
//
//import java.awt.*;
//import java.util.List;
//
//public class HelpCommand  implements ICommand {
//
//    private final CommandManager manager;
//
//    public HelpCommand(CommandManager manager) {
//        this.manager = manager;
//    }
//
//    @Override
//    public void handle(List<String> args, GuildMessageReceivedEvent event) {
//
//        if (args.isEmpty()) {
//            generateAndSendEmbed(event);
//            return;
//        }
//
//        ICommand command = manager.getCommand(String.join("", args));
//
//        if (command == null) {
//            EmbedBuilder builder = EmbedUtils.defaultEmbed()
//                    .setColor(Color.RED)
//                    .setTitle("Command not found")
//                    .setDescription(String.format("Could not find the command: `%s`.\nUser `%shelp` to view the commands.",
//                            String.join("", args), Constants.PREFIX));
//            event.getChannel().sendMessage(builder.build()).queue();
//            return;
//        }
//
//        EmbedBuilder builder = EmbedUtils.defaultEmbed()
//                .setTitle(command.getInvoke())
//                .setDescription(command.getHelp())
//                .setColor(Color.cyan);
//
//        event.getChannel().sendMessage(builder.build()).queue();
//    }
//
//    private void generateAndSendEmbed(GuildMessageReceivedEvent event) {
//        EmbedBuilder builder = EmbedUtils.defaultEmbed()
//                .setTitle("Commands");
//        StringBuilder desc = builder.getDescriptionBuilder();
//
//        manager.getCommands().forEach(
//                (command -> desc.append("`").append(command.getInvoke()).append("`\n"))
//        );
//
//
//        desc.append("\nAnimation/Icon made by FrankTheTank#6090");
//
//        try {
//            event.getChannel().sendMessage(builder.build()).queue();
//        } catch (Exception e) {
//            event.getChannel().sendMessage("Failed to send the help embed.").queue();
//        }
//
//    }
//
//    @Override
//    public String getHelp() {
//        return "Shows a list of all the commands.\n" +
//                "Usage: `" + Constants.PREFIX + getInvoke() + " [command]`";
//    }
//
//    @Override
//    public String getInvoke() {
//        return "help";
//    }
//}
