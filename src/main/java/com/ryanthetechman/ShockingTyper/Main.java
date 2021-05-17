package com.ryanthetechman.ShockingTyper;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static int PORT = 4444;
    private static final int shockWait = 1000;

    private static final SerialHelper serial = new SerialHelper();
    private static WebsocketHelper webSocket = null;
    private static long lastTimeShocked = System.currentTimeMillis();
    public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {
        Options options = new Options();

        Option comport = new Option("c", "comport", true, "com port # for arduino");
        comport.setRequired(true);
        options.addOption(comport);

        Option webport = new Option("p", "port", true, "port number for websocket server");
        webport.setRequired(false);
        options.addOption(webport);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
            return;
        }

        try{
            String cp = cmd.getOptionValue("comport");
            if (cp != null) serial.setPort(Integer.parseInt(cp));
        } catch(NumberFormatException ignore){
            System.out.println("COM Port invalid. Must be a number!");
            System.exit(1);
            return;
        }

        try{
            String wp = cmd.getOptionValue("webport");
            if (wp != null) PORT = Integer.parseInt(wp);
        }
        catch(NumberFormatException ignore){
            System.out.println("Web Port invalid. Must be a number!");
            System.exit(1);
            return;
        }

        webSocket = new WebsocketHelper(PORT);

        webSocket.onMessageReceivedCallback.addListener((client, message) -> {
            //System.out.println("Received: " + client.getRemoteSocketAddress().getAddress().getHostAddress() + " : " + message);
            if (message.equals("shock")){
                TriggerShock();
            }
        });

        webSocket.onClientConnectCallback.addListener((client, handshake) -> {
            System.out.println("Shocker Connected");
        });

        webSocket.start();
        serial.connect();
    }

    private static void TriggerShock() {
        final long time = System.currentTimeMillis();
        if (lastTimeShocked < (time - shockWait)) {
            if (serial.isOpen()) {
                lastTimeShocked = System.currentTimeMillis();
                if (!serial.write(47)) LOGGER.error("Could not connect to Serial!");
                LOGGER.info("Shock Triggered!!!");
            } else LOGGER.error("Could not connect to Serial!");
        } else LOGGER.warn("Shock NOT Triggered. Too close to last shock! (" + String.format("%.2f", (shockWait - (time - lastTimeShocked))/1000f) + "s left)");
    }
}