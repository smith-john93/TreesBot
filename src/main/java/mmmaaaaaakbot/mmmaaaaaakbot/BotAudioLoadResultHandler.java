/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mmmaaaaaakbot.mmmaaaaaakbot;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

/**
 *
 * @author Mark Masone
 */
public class BotAudioLoadResultHandler implements AudioLoadResultHandler {
    
    private final AudioPlayer player;
    
    public BotAudioLoadResultHandler(AudioPlayer player) {
        this.player = player;
    }
    
    @Override
    public void trackLoaded(AudioTrack track) {
        System.out.println("track loaded");
        player.playTrack(track);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        System.out.println("playlist loaded");
    }

    @Override
    public void noMatches() {
        System.out.println("no matches");
    }

    @Override
    public void loadFailed(FriendlyException throwable) {
        System.out.println("load failed");
    }
}
