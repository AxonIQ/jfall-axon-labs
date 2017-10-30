package io.axoniq.labs.chat;

import org.h2.tools.Server;
import org.jgroups.stack.GossipRouter;

public class Servers {

    public static void main(String[] args) throws Exception {
        Server server = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
        server.start();
        System.out.println("Database running on port 9092");

        GossipRouter router = new GossipRouter("127.0.0.1", 12001);
        router.start();
        System.out.println("Gossip Router started on port 12001");

        System.out.println("Press any key to shut down");
        System.in.read();

        System.out.println("Stopping database.");
        server.stop();

        System.out.println("Stopping Gossip router");
        router.stop();
    }
}
