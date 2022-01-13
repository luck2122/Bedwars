package de.luck212.bw.teams;

import org.bukkit.ChatColor;

public enum Teams {
    TeamRED("Team-Rot", ChatColor.RED),
    TeamBLUE("Team-Blau", ChatColor.BLUE);

    private String name;
    private ChatColor chatColor;

    private Teams(String name, ChatColor color){
        this.chatColor = color;
        this.name = name;
    }

    public ChatColor getChatColor() {
        return chatColor;
    }

    public String getName() {
        return name;
    }
}