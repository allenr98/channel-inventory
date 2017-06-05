package com.animationlibationstudios.channel.inventory.model;

import java.util.List;

/**
 * This bot thinks every channel is a room, so there should be only one of these for any given channel.
 */
public class Room {

    private String channel;
    private String name;
    private String description;

    private List<Thing> things;
    private List<Npc> npcs;

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Thing> getThings() {
        return this.things;
    }

    public void setThings(List<Thing> things) {
        this.things = things;
    }

    public List<Npc> getNpcs() {
        return this.npcs;
    }

    public void setNpcs(List<Npc> npcs) {
        this.npcs = npcs;
    }
}
