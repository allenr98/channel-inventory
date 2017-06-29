package com.animationlibationstudios.channel.inventory.config;

import de.btobastian.javacord.utils.LoggerUtil;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

/**
 * The plugin as bukkit plugin.
 */
public class ChannelInventory extends JavaPlugin {

    /**
     * The logger of this class.
     */
    private static final Logger logger = LoggerUtil.getLogger(ChannelInventory.class);

    @Override
    public void onEnable() {
        if (!getConfig().contains("token")) {
            getConfig().addDefault("token", "your bot`s token");
            getConfig().addDefault("adminId", "your id");
            saveConfig();
            logger.info("Created config.yml file. Please enter a valid token and restart the server!");
            getPluginLoader().disablePlugin(this);
            return;
        }
        new DiscordConfig().login(getConfig().getString("token"), getConfig().getString("adminId"));
    }
}
