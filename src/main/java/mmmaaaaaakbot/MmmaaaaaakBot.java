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
import java.util.logging.Level;
import java.util.logging.Logger;
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
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        JDABuilder builder = new JDABuilder("NzEyNzc4Nzk3OTQxNzg0NTg2.XsWhzw.yJVTqCwqusc_9PGwExr8dnbWy_8");
        builder.setActivity(Activity.playing("Boggle"));
        builder.addEventListeners(new MmmaaaaaakBot());
        try {
            builder.build();
        } catch (LoginException ex) {
            Logger.getLogger(MmmaaaaaakBot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public MmmaaaaaakBot() {
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
            String rawMessage = message.getContentRaw().toLowerCase();
            String url = null;
            
            if(rawMessage.contains("boggle")) {
                url = "https://www.youtube.com/watch?v=2e3-c_0WJgo";
                messageChannel.sendMessage("You have a medical disorder!").queue();
            } else if(rawMessage.contains("no") || rawMessage.contains("would you make me a sandwich")) {
                url = "https://www.youtube.com/watch?v=4O9IFZjiIgk";
                messageChannel.sendMessage(url).queue();
            } else if(rawMessage.contains("swooce")) {
                url = "https://www.youtube.com/watch?v=B_OM9IqA_8A";
                messageChannel.sendMessage(url).queue();
            } else if(rawMessage.equals("!see?")) {
                String avatar = author.getAvatarUrl();
                if(avatar == null) {
                    messageChannel.sendMessage("I no see.").queue();
                } else {
                    messageChannel.sendMessage(avatar).queue();
                }
            } else if(rawMessage.contains("the other my song")) {
                url = "https://www.youtube.com/watch?v=RdP4MAdloC4";
                messageChannel.sendMessage(url).queue();
            } else if(rawMessage.contains("my song")) {
                url = "https://www.youtube.com/watch?v=V_RNor8wtlA";
                messageChannel.sendMessage(url).queue();
            } else if(rawMessage.contains("popular song")) {
                url = "https://www.youtube.com/watch?v=m5hfLDTm6u0";
            }
            
            if(url != null && voiceChannel != null) {
                playTrack(guild,voiceChannel,url);
            }
        }
    }
}
