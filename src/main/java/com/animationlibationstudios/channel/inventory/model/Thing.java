package com.animationlibationstudios.channel.inventory.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

import java.util.LinkedList;
import java.util.List;

/**
 * This bot is primarily interested in keeping track of what things are in a room, so of course we need a class for that.
 */
@DynamoDBDocument
public class Thing {

    private String name;
    private String description;
    private ThingType type;
    private int quantity;
    private String price;

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

    @DynamoDBAttribute(attributeName = "price")
    public String getPrice() {
        return price;
    }
    public void setPrice(String price) {
        this.price = price;
    }

    @DynamoDBAttribute(attributeName = "quantity")
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
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

    @DynamoDBAttribute(attributeName = "type")
    public ThingType getType() {
        return type;
    }
    public void setType(ThingType type) {
        this.type = type;
    }

    @DynamoDBAttribute(attributeName = "thingsOn")
    public List<Thing> getThingsOn() {
        return thingsOn;
    }
    public void setThingsOn(List<Thing> thingsOn) {
        this.thingsOn = thingsOn;
    }

    @DynamoDBAttribute(attributeName = "thingsIn")
    public List<Thing> getThingsIn() {
        return thingsIn;
    }
    public void setThingsIn(List<Thing> thingsIn) {
        this.thingsIn = thingsIn;
    }

    @DynamoDBAttribute(attributeName = "thingsUnder")
    public List<Thing> getThingsUnder() {
        return thingsUnder;
    }
    public void setThingsUnder(List<Thing> thingsUnder) {
        this.thingsUnder = thingsUnder;
    }

    @DynamoDBAttribute(attributeName = "thingsBehind")
    public List<Thing> getThingsBehind() {
        return thingsBehind;
    }
    public void setThingsBehind(List<Thing> thingsBehind) {
        this.thingsBehind = thingsBehind;
    }
}
