package ml.putin.Electro.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import me.duncte123.botcommons.messaging.EmbedUtils;
import ml.putin.Electro.Main;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.User;

import javax.sound.midi.Track;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class schedules tracks for the audio player. It contains the queue of tracks.
 */
public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private BlockingQueue<TrackAndRequester> queue;
    public boolean isRadio = false;
    public TrackAndRequester np;
    public boolean isQuiz = false;

    /**
     * @param player The audio player this scheduler uses
     */
    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    public void shuffleQueue() {
        List<TrackAndRequester> oldQueue = new LinkedList<>();
        for (int i = 0; i < queue.size(); i++) {
            oldQueue.add(queue.poll());
        }

        for (int i = 0; i < oldQueue.size(); i++) {
            TrackAndRequester track = oldQueue.get(Main.r.nextInt(oldQueue.size()));
            queue.add(track);
            oldQueue.remove(track);
        }
    }

    /**
     * Add the next track to queue or play right away if nothing is in the queue.
     *
     * @param track The track to play or add to queue.
     */
    public void queue(AudioTrack track, User requester) {
        // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
        // something is playing, it returns false and does nothing. In that case the player was already playing so this
        // track goes to the queue instead.
        if (!player.startTrack(track, true)) {
            queue.offer(new TrackAndRequester(requester, track));
        } else {
            np = new TrackAndRequester(requester, track);
        }
    }

    public BlockingQueue<TrackAndRequester> getQueue() {
        return queue;
    }

    /**
     * Start the next track, stopping the current one if it is playing.
     */
    public void nextTrack() {
        // Start the next tr    ack, regardless of if something is already playing or not. In case queue was empty, we are
        // giving null to startTrack, which is a valid argument and will simply stop the player.
        TrackAndRequester next = queue.poll();
        np = next;
        if (next != null) {
            player.startTrack(next.track, false);
        } else {
            player.startTrack(null, false);
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        if (endReason.mayStartNext) {
            if (queue.isEmpty()) {
                PlayerManager.getPlayerTextChannel(player).sendMessage(EmbedUtils.defaultEmbed()
                        .setTitle("Stopped.")
                        .setDescription("Queue is empty, maybe add more songs?")
                        .build()).queue();
            }
            nextTrack();
        }
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        if (!isRadio && track != null && track.getInfo() != null && !isQuiz) {
            EmbedBuilder builder = EmbedUtils.defaultEmbed()
                    .setTitle("Now Playing")
                    .setDescription("[" + track.getInfo().title + "](" + track.getInfo().uri + ")")
                    .setFooter(String.format("Requested by %s", np.requester.getAsTag()), np.requester.getAvatarUrl())
                    .setThumbnail("http://i.ytimg.com/vi/" + track.getInfo().identifier + "/maxresdefault.jpg");

            PlayerManager.getPlayerTextChannel(player).sendMessage(builder.build()).queue();
        }
    }
}