package io.axoniq.labs.chat;

import org.h2.tools.Server;

public class DatabaseServer {

    public static void main(String[] args) throws Exception {
        Server server = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
        server.start();

        System.out.println("Database running on port 9092. Press a key to stop");
        System.in.read();

        System.out.println("Stopping database.");
        server.stop();
    }
}
