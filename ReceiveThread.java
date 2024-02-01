import java.io.*;
import java.net.*;

public class ReceiveThread extends Thread {
    final Socket connection_socket;
    private NodeConfig node_config;

    public ReceiveThread(Socket connection_socket, NodeConfig node_config) {
        this.connection_socket = connection_socket;
        this.node_config = node_config;
    }

    @Override
    public void run() {
        // Input stream
        ObjectInputStream input_from_client = null;
        try {
            input_from_client = new ObjectInputStream(connection_socket.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                // System.out.println("Node " + node_config.id + " is waiting");
                Message message = (Message) input_from_client.readObject();
                synchronized (node_config) {
                    System.out.println("Node " + node_config.id + " received " + message.str
                            + " from node " + message.node_id + " at timestamp " + message.scalar_clock);

                    if (message.str.contains("RequestMessage")) {
                        System.out.println("Node " + node_config.id + " status: " + node_config.status);
                        if (node_config.status.contains("IDLE")) {
                            new SendThread(node_config, "ReleaseMessage", message.node_id).sendRelease();;
                        } else if (node_config.status.contains("EXECUTING")) {
                            node_config.request_queue.add(message.node_id);
                        } else if (node_config.status.contains("REQUESTING")) {
                            if (message.scalar_clock < node_config.scalar_clock ||
                                    (message.scalar_clock == node_config.scalar_clock
                                            && message.node_id < node_config.id)) {
                                new SendThread(node_config, "ReleaseMessage", message.node_id).sendRelease();
                                new SendThread(node_config, "RequestMessage", message.node_id).sendRequest();
                                // t.start();
                                // System.out.println("Node " + node_config.id + " is waiting for " +
                                // t.getName());
                                // t.join();
                                // // try {
                                // // Thread.sleep(500);
                                // // } catch (Exception e) {
                                // // e.printStackTrace();
                                // // }
                                // new SendThread(node_config, "RequestMessage", message.node_id).start();
                            } else {
                                node_config.request_queue.add(message.node_id);
                            }
                        }
                    } else if (message.str.contains("ReleaseMessage")) {
                        node_config.has_keys.set(message.node_id, true);

                        int key_count = 0;
                        for (int i = 0; i < node_config.num_nodes; i++) {
                            if (node_config.has_keys.get(i) == true) {
                                key_count++;
                            }
                        }

                        if (key_count == node_config.num_nodes) {
                            new CS_Operation_Thread(node_config, "ExecuteCS").start();
                        }
                    }

                    // node_config.scalar_clock = Math.max(node_config.scalar_clock,
                    // message.scalar_clock) + 1;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}