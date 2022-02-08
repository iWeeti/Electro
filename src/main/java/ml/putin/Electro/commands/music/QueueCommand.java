package ml.putin.Electro.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.Menu;
import com.jagrosh.jdautilities.menu.Paginator;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import me.duncte123.botcommons.messaging.EmbedUtils;
import ml.putin.Electro.Main;
import ml.putin.Electro.music.GuildMusicManager;
import ml.putin.Electro.music.PlayerManager;
import ml.putin.Electro.music.TrackAndRequester;
import ml.putin.Electro.objects.ICommand;
import ml.putin.Electro.utils.DefaultEmbeds;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class QueueCommand extends Command {

    private final Paginator.Builder pbuilder;
    public QueueCommand(EventWaiter waiter) {

        name = "queue";
        aliases = new String[]{"q"};
        help = "Shows the current queue.";
        this.botPermissions = new Permission[] {Permission.MESSAGE_EMBED_LINKS };
        pbuilder = new Paginator.Builder()
                .setColor(Main.getRandomColor())
                .setItemsPerPage(10)
                .setText("Queue")
                .setFinalAction(m -> {
                    try {
                        m.clearReactions().queue();
                    } catch (PermissionException ex) {
                        m.delete().queue();
                    }
                })
                .setColumns(1)
                .showPageNumbers(true)
                .setEventWaiter(waiter)
                .useNumberedItems(true)
                .waitOnSinglePage(true)
                .setTimeout(1, TimeUnit.MINUTES);
    }

    @Override
    protected void execute(CommandEvent event) {
        int page = 1;
        if (!event.getArgs().isEmpty()) {
            try {
                page = Integer.parseInt(event.getArgs());
            } catch (NumberFormatException e) {
                event.reply(DefaultEmbeds.missingArgs("pagenumber").build());
                return;
            }
        }

        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());

        if (musicManager.scheduler.getQueue().isEmpty()) {
            event.getChannel().sendMessage(EmbedUtils.defaultEmbed().setTitle("Queue empty.").build()).queue();

            return;
        }

        BlockingQueue<TrackAndRequester> queue = musicManager.scheduler.getQueue();

        LinkedList<String> songNames = new LinkedList();
        for (TrackAndRequester tar : queue) {
            AudioTrackInfo info = tar.track.getInfo();
            songNames.add("[" + info.title + "](" + info.uri + ")\n" );
        }
        String[] songNameArr = new String[songNames.size()];
        for (int i = 0; i < songNames.size(); i++) {
            songNameArr[i] = songNames.get(i);
        }

        pbuilder.clearItems();
        pbuilder.addItems(songNameArr);
        pbuilder.setUsers(event.getAuthor());

        pbuilder.build().paginate(event.getChannel(), page);

//        EmbedBuilder builder = EmbedUtils.defaultEmbed().setTitle("Queue");


//        event.getChannel().sendMessage(builder.build()).queue();
    }
}
