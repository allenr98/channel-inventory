package com.animationlibationstudios.channel.inventory.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import de.btobastian.javacord.entities.User;

import java.util.List;

/**
 * This bot thinks every channel is a room, so there should be only one of these for any given channel.
 */
@DynamoDBDocument
public class Room {

    private String channel;
    private String name;
    private String description;
    private User roomAdmin;

    private List<Thing> things;
    private List<Npc> npcs;

    @DynamoDBAttribute(attributeName = "channel")
    public String getChannel() {
        return channel;
    }
    public void setChannel(String channel) {
        this.channel = channel;
    }

    @DynamoDBAttribute(attributeName = "name")
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @DynamoDBAttribute(attributeName = "description")
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    @DynamoDBAttribute(attributeName = "things")
    public List<Thing> getThings() {
        return this.things;
    }
    public void setThings(List<Thing> things) {
        this.things = things;
    }

    @DynamoDBAttribute(attributeName = "npcs")
    public List<Npc> getNpcs() {
        return this.npcs;
    }
    public void setNpcs(List<Npc> npcs) {
        this.npcs = npcs;
    }

    @DynamoDBAttribute(attributeName = "roomAdmin")
    public User getRoomAdmin() {
        return roomAdmin;
    }
    public void setRoomAdmin(User roomAdmin) {
        this.roomAdmin = roomAdmin;
    }
}
