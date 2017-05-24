package com.animationlibationstudios.channel.inventory;

import com.animationlibationstudios.channel.inventory.commands.RoomCommands;
import com.animationlibationstudios.channel.inventory.commands.utility.HelpCommand;
import com.animationlibationstudios.channel.inventory.commands.utility.InfoCommand;
import com.google.common.util.concurrent.FutureCallback;
import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.Javacord;
import de.btobastian.javacord.utils.LoggerUtil;
import de.btobastian.sdcf4j.CommandHandler;
import de.btobastian.sdcf4j.handler.JavacordHandler;
import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements FutureCallback<DiscordAPI> {

	/**
	 * The logger of this class.
	 */
	private static final Logger logger = LoggerUtil.getLogger(Application.class);

	private String adminId = null;

	public static void main(String[] args) {
        if (args.length != 2) {
            logger.error("arguments: application token, adminId");
            System.exit(-1);
            return;
        }
        new Application().login(args[0], args[1]);

        // Start the Spring application
		SpringApplication.run(Application.class, args);
	}

	/**
	 * Successfully connected to discord.
	 *
	 * @param api The discord api.
	 */
	@Override
	public void onSuccess(DiscordAPI api) {
		logger.info("Amount of servers: {}", api.getServers().size());
		logger.info("Connected to discord account {}", api.getYourself());
		CommandHandler handler = new JavacordHandler(api);
		handler.addPermission(adminId, "*");

		api.setMessageCacheSize(10000);

		// register commands
		handler.registerCommand(new HelpCommand(handler));
		handler.registerCommand(new InfoCommand());
		handler.registerCommand(new RoomCommands());

//        api.registerListener(new MessageListener());
	}

    /**
     * Connecting failed!
     *
     * @param throwable The reason why connection failed.
     */
    @Override
    public void onFailure(Throwable throwable) {
        logger.error("Could not connect to discord!", throwable);
    }

    /**
     * Attempts to login.
     *
     * @param token A valid token.
     */
    public void login(String token, String adminId) {
        DiscordAPI api = Javacord.getApi(token, true);
        this.adminId = adminId;
        api.connect(this);
    }
}
