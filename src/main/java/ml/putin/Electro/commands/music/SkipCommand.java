package ml.putin.Electro.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.duncte123.botcommons.messaging.EmbedUtils;
import ml.putin.Electro.music.GuildMusicManager;
import ml.putin.Electro.music.PlayerManager;
import ml.putin.Electro.objects.ICommand;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class SkipCommand extends Command {

    public SkipCommand() {

        name = "skip";
        help = "Skips the current song.";
        aliases = new String[] {"s"};
        this.botPermissions = new Permission[] {Permission.MESSAGE_EMBED_LINKS };
    }

    @Override
    protected void execute(CommandEvent event) {
        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());
        AudioManager audioManager = event.getGuild().getAudioManager();

        if (musicManager.player.getPlayingTrack() == null) {

            event.getChannel().sendMessage(EmbedUtils.defaultEmbed().setTitle("Not playing")
                    .setColor(Color.red).build()).queue();

            return;
        }

        EmbedBuilder builder = EmbedUtils.defaultEmbed().setTitle("Skip");
        boolean state = musicManager.toggleSkipVote(event.getAuthor());
        StringBuilder desc = builder.getDescriptionBuilder();
        if (state == true) {
            desc.append("You are now voting for skip.");
        } else {
            desc.append("You are no longer voting for skip.");
        }
        LinkedList<Member> members = new LinkedList<>();
        audioManager.getConnectedChannel().getMembers().forEach((member -> {
            if (!member.getUser().isBot()) {
                members.add(member);
            }
        }));
        int neededVotes = (int) Math.round((members.size() / 2)); // 33.3) * 10);
        if (neededVotes == 0)
            neededVotes = 1;
        desc.append(String.format("\nCurrent voters: %s/%s", musicManager.skipVotes, neededVotes));

        if (musicManager.skipVotes >= neededVotes) {
            event.getChannel().sendMessage(EmbedUtils.defaultEmbed().setTitle("Skipping.").build()).queue();
            musicManager.scheduler.nextTrack();
            musicManager.skipVotes = 0;
            musicManager.skipVoters.clear();
        } else {
            event.getChannel().sendMessage(builder.build()).queue();
        }

    }
}
