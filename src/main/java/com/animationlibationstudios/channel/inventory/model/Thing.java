package com.animationlibationstudios.channel.inventory.model;

import java.util.LinkedList;
import java.util.List;

/**
 * This bot is primarily interested in keeping track of what things are in a room, so of course we need a class for that.
 */
public class Thing {

    private String name;
    private String description;
    private ThingType type;

    private List<Thing> thingsOn;
    private List<Thing> thingsIn;
    private List<Thing> thingsUnder;
    private List<Thing> thingsBehind;

    public Thing() {
        this.thingsOn = new LinkedList<>();
        this.thingsIn = new LinkedList<>();
        this.thingsUnder = new LinkedList<>();
        this.thingsBehind = new LinkedList<>();
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

    public ThingType getType() {
        return type;
    }

    public void setType(ThingType type) {
        this.type = type;
    }

    public List<Thing> getThingsOn() {
        return thingsOn;
    }

    public void setThingsOn(List<Thing> thingsOn) {
        this.thingsOn = thingsOn;
    }

    public List<Thing> getThingsIn() {
        return thingsIn;
    }

    public void setThingsIn(List<Thing> thingsIn) {
        this.thingsIn = thingsIn;
    }

    public List<Thing> getThingsUnder() {
        return thingsUnder;
    }

    public void setThingsUnder(List<Thing> thingsUnder) {
        this.thingsUnder = thingsUnder;
    }

    public List<Thing> getThingsBehind() {
        return thingsBehind;
    }

    public void setThingsBehind(List<Thing> thingsBehind) {
        this.thingsBehind = thingsBehind;
    }
}
