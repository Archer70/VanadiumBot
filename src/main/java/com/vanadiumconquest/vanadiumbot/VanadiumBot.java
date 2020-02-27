package com.vanadiumconquest.vanadiumbot;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.Collection;
import java.util.stream.Collectors;

public final class VanadiumBot extends JavaPlugin {
    private DiscordApi discord;
    private String token;
    private String channelId;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        token = this.getConfig().getString("discord_key");
        channelId = this.getConfig().getString("chat_channel");

        if (token.equals("")) {
            System.out.println("No discord_key provided in config.");
            return;
        }

        if (channelId.equals("")) {
            System.out.println("No chat_channel id provided in config. Bot will be unable to send or receive chats.");
        }

        discord = new DiscordApiBuilder().setToken(token).login().join();
        this.bindDiscordEvents();
    }

    private void bindDiscordEvents() {
        // From discord.
        discord.addMessageCreateListener(event -> {
            if (isPingEvent(event)) {
                reportPlayers(event);
            }

            if (event.getChannel().getIdAsString().equals(channelId)) {
                String poster = event.getMessageAuthor().getDisplayName();
                String message = event.getMessageContent();
                this.getServer().broadcastMessage("[Discord] " + poster + " > " + message);
            }
        });

        // From minecraft.
        this.getServer().getPluginManager().registerEvents(new MessageListener(discord, channelId), this);
    }

    private void reportPlayers(MessageCreateEvent event) {
        Collection<? extends Player> players = this.getServer().getOnlinePlayers();

        if (players.isEmpty()) {
            event.getChannel().sendMessage("No players online.");
        } else {
            String names = players
                    .stream()
                    .map(player -> player.getName())
                    .collect(Collectors.joining(", "));

            event.getChannel().sendMessage("Players Online: " + names);
        }
    }

    private boolean isPingEvent(MessageCreateEvent event) {
        return event.getMessageContent().equalsIgnoreCase("!ping");
    }

    @Override
    public void onDisable() {
        if (discord != null) {
            discord.disconnect();
        }
        discord = null;
    }
}
