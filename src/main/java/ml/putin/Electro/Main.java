package ml.putin.Electro;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import me.duncte123.botcommons.messaging.EmbedUtils;
import me.duncte123.botcommons.web.WebUtils;
import ml.putin.Electro.commands.InfoCommand;
import ml.putin.Electro.commands.PingCommand;
import ml.putin.Electro.commands.music.*;
import ml.putin.Electro.music.PlayerManager;
import ml.putin.Electro.utils.DefaultEmbeds;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Random;
import java.util.List;

public class Main {

    private static MongoClient mongoClient =MongoClients.create();
    public static MongoDatabase database = mongoClient.getDatabase("electro");
    public static MongoCollection<Document> playlists = mongoClient.getDatabase("putin").getCollection("playlists");
    public static JDA jda;
    public static Random r = new Random();
    public static int commandsRan = 0;
    public static EventWaiter waiter = new EventWaiter();


    public static void main(String[] args) throws IOException, LoginException, IllegalArgumentException, RateLimitedException {
        WebUtils.setUserAgent("Mozilla/5.0 Electro JDA/iWeeti#0997");
        EmbedUtils.setEmbedBuilder(
                () -> new EmbedBuilder()
                        .setColor(getRandomColor())
                        .setFooter("Electro", "http://putin.ml:4276/i/electro.gif")
                        .setTimestamp(Instant.now())

        );

        List<String> list = Files.readAllLines(Paths.get("/home/iweeti/IdeaProjects/Electro/src/main/resources/config.txt"));
        String token = list.get(0);
        String ownerId = list.get(1);

        CommandClientBuilder client = new CommandClientBuilder();

        client.setGame(Game.listening("to the best music there is!"));
        client.setOwnerId(ownerId);
        client.setEmojis("\uD83D\uDE03", "\uD83D\uDE2E", "\uD83D\uDE26");
        client.setAlternativePrefix("<@591200174551072768>");
        client.setPrefix("e.");


        // TODO: Convert the commands into JDA-Utilities commands and import them here.

        client.setHelpConsumer(event -> {
            event.reply(DefaultEmbeds.helpEmbed(event));
        });

        client.addCommands(
                new InfoCommand(),
                new PingCommand(),
                new BassBoostCommand(),
                new JoinCommand(),
                new LeaveCommand(),
                new NPCommand(),
                new PauseCommand(),
                new PlayCommand(),
                new QueueCommand(waiter),
                new RemoveCommand(),
                new RockCommand(),
                new SeekCommand(),
                new ShuffleCommand(),
                new SkipCommand(),
                new StopCommand(),
                new VolumeCommand()
//                new QuizCommand()
        );


        new JDABuilder(AccountType.BOT)
                .setToken(token)
                .setStatus(OnlineStatus.ONLINE)
                .setGame(Game.streaming("loading..", "http://putin.ml:4276"))

                .addEventListener(Main.waiter)
                .addEventListener(client.build())

                .build();
        // new Main();
    }

    public static Color getRandomColor() {
        float r = Main.r.nextFloat();
        float g = Main.r.nextFloat();
        float b = Main.r.nextFloat();
        return new Color(r, g, b);
    }
}
