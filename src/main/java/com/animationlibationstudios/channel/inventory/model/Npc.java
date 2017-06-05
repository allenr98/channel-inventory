package com.animationlibationstudios.channel.inventory.model;

/**
 * Decided it might be cool to be able to add NPCs to rooms, too.
 */
public class Npc {
    private String npcType;
    private String name;
    private String description;
    private int ac;
    private int hp;
    private String statsBlock;

    public String getNpcType() {
        return npcType;
    }

    public void setNpcType(String npcType) {
        this.npcType = npcType;
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

    public int getAc() {
        return ac;
    }

    public void setAc(int ac) {
        this.ac = ac;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public String getStatsBlock() {
        return statsBlock;
    }

    public void setStatsBlock(String statsBlock) {
        this.statsBlock = statsBlock;
    }
}
