/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mmmaaaaaakbot;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

/**
 *
 * @author Mark Masone
 */
public class TrackScheduler extends AudioEventAdapter {
  @Override
  public void onPlayerPause(AudioPlayer player) {
    System.out.println("player pause");
  }

  @Override
  public void onPlayerResume(AudioPlayer player) {
      System.out.println("player resume");
  }

  @Override
  public void onTrackStart(AudioPlayer player, AudioTrack track) {
        System.out.println("track start");
  }

  @Override
  public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
    if (endReason.mayStartNext) {
        System.out.println("start next");
        player.stopTrack();
    }
    System.out.println("track end");
    

    // endReason == FINISHED: A track finished or died by an exception (mayStartNext = true).
    // endReason == LOAD_FAILED: Loading of a track failed (mayStartNext = true).
    // endReason == STOPPED: The player was stopped.
    // endReason == REPLACED: Another track started playing while this had not finished
    // endReason == CLEANUP: Player hasn't been queried for a while, if you want you can put a
    //                       clone of this back to your queue
  }

  @Override
  public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
      System.out.println("track exception");
  }

  @Override
  public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
      System.out.println("track stuck");
  }
}
