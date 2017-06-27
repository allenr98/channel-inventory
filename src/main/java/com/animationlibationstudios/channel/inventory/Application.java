package com.animationlibationstudios.channel.inventory;

import de.btobastian.javacord.utils.LoggerUtil;
import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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
