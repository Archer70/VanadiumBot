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

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        String token = this.getConfig().getString("discord_key");

        if (token.equals("")) {
            return;
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
        });

        // From minecraft.
        this.getServer().getPluginManager().registerEvents(new MessageListener(discord), this);
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
        discord.disconnect();
        discord = null;
    }
}
