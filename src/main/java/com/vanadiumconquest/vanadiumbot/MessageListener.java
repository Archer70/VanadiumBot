package com.vanadiumconquest.vanadiumbot;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.MessageDecoration;

public class MessageListener implements Listener {
    private DiscordApi discord;
    private TextChannel channel;

    public MessageListener(DiscordApi discordInstance, String channelId) {
        discord = discordInstance;
        channel = discord.getTextChannelById(channelId).get();
    }

    @EventHandler
    public void onPlayerMessage(AsyncPlayerChatEvent chat) {
        new MessageBuilder()
                .append(chat.getPlayer().getName() + ": ", MessageDecoration.BOLD)
                .append(chat.getMessage())
                .send(channel);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent join) {
        channel.sendMessage(join.getPlayer().getName() + " joined the server!");
    }

    @EventHandler
    public void onKick(PlayerKickEvent kick) {
        channel.sendMessage(kick.getPlayer().getName() + " was kicked for \"" + kick.getReason() + "\"");
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent death) {
        channel.sendMessage(death.getDeathMessage());
    }
}
