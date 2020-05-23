/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mmmaaaaaakbot;

/**
 *
 * @author Mark Masone
 */
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.*;
import net.dv8tion.jda.api.managers.AudioManager;

public class MmmaaaaaakBot extends ListenerAdapter {

    private final AudioPlayerManager playerManager;
    private final AudioPlayer player;
    private final BotAudioLoadResultHandler botAudioLoadResultHandler;
    private final Connection conn;
    private boolean spam = true;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {  
        try (BufferedReader br = new BufferedReader(new FileReader("token.txt"))) {
            String token = br.readLine();
            JDABuilder builder = new JDABuilder(token);
            builder.setActivity(Activity.playing("Boggle"));
            builder.addEventListeners(new MmmaaaaaakBot());
            builder.build();
        } catch (IOException | LoginException ex) {
            Logger.getLogger(MmmaaaaaakBot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public MmmaaaaaakBot() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:sqlite/mmmaaaaaakBot.db");
        } catch (SQLException ex) {
            Logger.getLogger(MmmaaaaaakBot.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.conn = conn;
        this.playerManager = new DefaultAudioPlayerManager();
        this.player = playerManager.createPlayer();
        TrackScheduler trackScheduler = new TrackScheduler();
        player.addListener(trackScheduler);
        botAudioLoadResultHandler = new BotAudioLoadResultHandler(player);
        AudioSourceManagers.registerRemoteSources(playerManager);
    }

    private void playTrack(Guild guild, VoiceChannel channel, String url) {
        AudioManager manager = guild.getAudioManager();
        playerManager.loadItem(url,botAudioLoadResultHandler);
        manager.setSendingHandler(new AudioPlayerSendHandler(player));
        manager.openAudioConnection(channel);
    }
    
    /* Find out if the person who typed the message is in a voice channel */
    private VoiceChannel getAuthorVoiceChannel(Guild guild, User author) {
        for(GuildChannel gc : guild.getChannels()) {
            if(gc.getType() == ChannelType.VOICE) {
                for(Member m : gc.getMembers()) {
                    if(m.getUser() == author) {
                        return (VoiceChannel)gc;
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        User author = event.getAuthor();
        
        // Only listen to non-bots.
        if(!author.isBot()) {
            Guild guild = event.getGuild();
            VoiceChannel voiceChannel = getAuthorVoiceChannel(guild,author);
            Message message = event.getMessage();
            MessageChannel messageChannel = event.getChannel();
            String rawMessage = message.getContentRaw();
            String lcMessage = rawMessage.toLowerCase();
            String url = null;
            String msg = null;
            
            if(rawMessage.startsWith("!")) { // Commands
                
                String pattern = "^\\!learn words \"([^\"]*)\"( url <([^\"]*)>)?( msg \"([^\"]*)\")?$";
                Pattern p = Pattern.compile(pattern,Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(rawMessage);

                if(m.find()) { // Learn new trigger words.
                    String words = m.group(1);
                    url = m.group(3);
                    msg = m.group(5);
                    
                    if(url == null && msg == null) {
                        messageChannel.sendMessage("THERE'S ONLY WORDS THERE! WHAT DOES THAT MEAN? " + words + "?").queue();
                    } else {
                    
                        if(msg == null) {
                            msg = url;
                        }
                        System.out.println("words: " + words);
                        System.out.println("url: " + url);
                        System.out.println("msg: " + msg);
                        String sql = "insert into trigger_words(words,url,msg) values(?,?,?)";
                        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                            pstmt.setString(1, words.toLowerCase());
                            pstmt.setString(2, url);
                            pstmt.setString(3, msg);
                            pstmt.executeUpdate();
                            messageChannel.sendMessage("Childrens do learn.").queue();
                        } catch (SQLException e) {
                            System.out.println(e.getMessage());
                            /* 19 is SQLite's error code for null or unique 
                            constraint violation. */
                            if(e.getErrorCode() == 19) { 
                                messageChannel.sendMessage("I already know those words.").queue();
                            }
                        }
                    }
                } else { // Look for other commands.
                
                    try {
                        Command command = Command.valueOf(rawMessage.substring(1).toUpperCase());
                        switch(command) {
                            case HELP:
                                String info = "";
                                for(Command c : Command.values()) {
                                    info += "!" + c + ": " + c.getDescription() + "\n";
                                }
                                messageChannel.sendMessage(info).queue();
                                break;
                            case SEE:
                                String avatar = author.getAvatarUrl();
                                if(avatar == null) {
                                    messageChannel.sendMessage("I no see.").queue();
                                } else {
                                    messageChannel.sendMessage(avatar).queue();
                                }
                                break;
                            case NOSPAM:
                                spam = false;
                                messageChannel.sendMessage("I don't like spam anymore.").queue();
                                break;
                            case SPAM:
                                messageChannel.sendMessage("I love spam-a-lot.").queue();
                                spam = true;
                                break;
                        }
                    } catch(IllegalArgumentException e) {
                        System.out.println("No such command: " + rawMessage);
                    }
                }
            } else { // Trigger words

                if(lcMessage.contains("boggle")) {
                    url = "https://www.youtube.com/watch?v=2e3-c_0WJgo";
                    msg = "You have a medical disorder!";
                } else { 
                    if(lcMessage.contains("no") || lcMessage.contains("would you make me a sandwich")) {
                        url = "https://www.youtube.com/watch?v=4O9IFZjiIgk";
                    } else if(lcMessage.contains("swooce")) {
                        url = "https://www.youtube.com/watch?v=B_OM9IqA_8A";
                    } else if(lcMessage.contains("the other my song")) {
                        url = "https://www.youtube.com/watch?v=RdP4MAdloC4";
                    } else if(lcMessage.contains("my song")) {
                        url = "https://www.youtube.com/watch?v=V_RNor8wtlA";
                    } else if(lcMessage.contains("popular song")) {
                        url = "https://www.youtube.com/watch?v=m5hfLDTm6u0";
                    }
                    msg = url;
                }
                
                try {
                    String sql = "select * from trigger_words";
                    Statement stmt = conn.createStatement();
                    ResultSet rs    = stmt.executeQuery(sql);
                    while (rs.next()) {
                        if(lcMessage.contains(rs.getString("words"))) {
                            url = rs.getString("url");
                            msg = rs.getString("msg");
                            break;
                        }
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(MmmaaaaaakBot.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                if(spam && msg != null) {
                    messageChannel.sendMessage(msg).queue();
                }
                
                if(url != null && voiceChannel != null) {
                    playTrack(guild,voiceChannel,url);
                }
            }
        }
    }
}
