package com.animationlibationstudios.channel.inventory.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

/**
 * Decided it might be cool to be able to add NPCs to rooms, too.
 */
@DynamoDBDocument
public class Npc {
    private String npcType;
    private String name;
    private String description;
    private int ac;
    private int hp;
    private String statsBlock;

    @DynamoDBAttribute(attributeName = "npcType")
    public String getNpcType() {
        return npcType;
    }
    public void setNpcType(String npcType) {
        this.npcType = npcType;
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

    @DynamoDBAttribute(attributeName = "ac")
    public int getAc() {
        return ac;
    }
    public void setAc(int ac) {
        this.ac = ac;
    }

    @DynamoDBAttribute(attributeName = "hp")
    public int getHp() {
        return hp;
    }
    public void setHp(int hp) {
        this.hp = hp;
    }

    @DynamoDBAttribute(attributeName = "statsBlock")
    public String getStatsBlock() {
        return statsBlock;
    }
    public void setStatsBlock(String statsBlock) {
        this.statsBlock = statsBlock;
    }
}
