package ml.putin.Electro.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.User;

public class TrackAndRequester {

    public User requester;
    public AudioTrack track;

    public TrackAndRequester(User requester, AudioTrack track) {
        this.requester = requester;
        this.track = track;
    }
}
