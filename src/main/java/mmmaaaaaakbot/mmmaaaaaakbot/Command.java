/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mmmaaaaaakbot.mmmaaaaaakbot;

/**
 *
 * @author Mark Masone
 */
public enum Command {
    HELP("Help"),
    SEE("Show the avatar of the person who typed this command."),
    NOSPAM("Disable bot trigger word messages."),
    SPAM("Enable bot trigger word messages."),
    LEARN("Learn new trigger words. !learn \"_words_\" <_URL_> \"_message_\""),
    WORDS("List current trigger words, their URLs and messages.");
    
    private final String desc;
    
    Command(String desc) {
        this.desc = desc;
    }
    
    public String getDescription() {
        return desc;
    }
}
