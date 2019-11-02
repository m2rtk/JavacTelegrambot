package com.github.m2rtk.telegram;

import com.github.m2rtk.telegram.bot.JavaBot;
import com.cars.framework.secrets.DockerSecretLoadException;
import com.cars.framework.secrets.DockerSecrets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class Main {

    static {
        System.setProperty("log4j.configurationFile", ClassLoader.getSystemResource("log4j2.xml").getPath());
    }

    private static final Logger log = LogManager.getLogger();

    public static void main(String[] args) throws Exception{
        try {
            log.info("Bot starting!");
            Map<String, String> conf = loadConf();
            ApiContextInitializer.init();
            TelegramBotsApi botsApi = new TelegramBotsApi();
            botsApi.registerBot(new JavaBot(conf.get("javabot.token"), conf.get("javabot.username")));
            log.info("Bot started!");
        } catch (TelegramApiException e) {
            log.error(e);
        }
    }

    private static Map<String, String> loadConf() throws IOException {
        try {
            return DockerSecrets.load();
        } catch (DockerSecretLoadException e) {
            String path = ClassLoader.getSystemResource("application.properties").getPath();
            try (FileInputStream in = new FileInputStream(path)) {
                Properties p = new Properties();
                p.load(in);
                return p.entrySet().stream().collect(Collectors.toMap(q -> (String) q.getKey(), w -> (String) w.getValue()));
            }
        }
    }
}
