package com.animationlibationstudios.channel.inventory;

import com.animationlibationstudios.channel.inventory.commands.RoomCommands;
import com.animationlibationstudios.channel.inventory.commands.ServerCommands;
import com.animationlibationstudios.channel.inventory.commands.utility.HelpCommand;
import com.animationlibationstudios.channel.inventory.commands.utility.InfoCommand;
import com.animationlibationstudios.channel.inventory.config.DiscordConfig;
import com.google.common.util.concurrent.FutureCallback;
import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.Javacord;
import de.btobastian.javacord.utils.LoggerUtil;
import de.btobastian.sdcf4j.CommandExecutor;
import de.btobastian.sdcf4j.CommandHandler;
import de.btobastian.sdcf4j.handler.JavacordHandler;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource({"classpath:application.properties"})
public class Application  {

    public static String token;
    public static String adminId;

	/**
	 * The logger of this class.
	 */
	private static final Logger logger = LoggerUtil.getLogger(Application.class);

	public static void main(String[] args) {
        if (args.length != 2) {
            logger.error("arguments: application token, adminId");
            System.exit(-1);
            return;
        }

        token = args[0];
        adminId = args[1];

        // Start the Spring application
        SpringApplication.run(Application.class, args);
	}
}
