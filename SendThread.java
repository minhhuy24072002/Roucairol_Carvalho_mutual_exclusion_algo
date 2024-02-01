import java.io.*;

public class SendThread extends Thread {
    NodeConfig node_config;
    String message_type;
    int destination_id;

    public SendThread(NodeConfig node_config, String message_type, int destination_id) {
        this.node_config = node_config;
        this.message_type = message_type;
        this.destination_id = destination_id;
    }

    void sendRequest() throws Exception {
        synchronized (node_config) {
            if (destination_id == -1) {
                for (int neighbor_id : node_config.neighbors) {
                    if (node_config.has_keys.get(neighbor_id) == true) {
                        continue;
                    }
                    // Create new message
                    Message message = new Message();
                    message.str = "RequestMessage";
                    // node_config.scalar_clock++;
                    message.scalar_clock = node_config.scalar_clock;
                    message.node_id = node_config.id;
                    node_config.num_messages_sent++;
                    // Send request to the neighbor
                    try {
                        ObjectOutputStream oos = node_config.o_stream.get(neighbor_id);
                        System.out.println("Sent CS request from node " + node_config.id + " to node " + neighbor_id
                                + ": Request Message " + node_config.num_request_sent + " at timestamp "
                                + node_config.scalar_clock);

                        oos.writeObject(message);
                        oos.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                // Create new message
                Message message = new Message();
                message.str = "RequestMessage";
                // node_config.scalar_clock++;
                message.scalar_clock = node_config.scalar_clock;
                message.node_id = node_config.id;
                node_config.num_messages_sent++;
                // Send request to the neighbor
                try {
                    ObjectOutputStream oos = node_config.o_stream.get(this.destination_id);
                    System.out.println("Sent CS request from node " + node_config.id + " to node " + this.destination_id
                            + ": Request Message " + node_config.num_request_sent + " at timestamp "
                            + node_config.scalar_clock);

                    oos.writeObject(message);
                    oos.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Increment num_request_sent
                // node_config.num_request_sent++;
            }
        }
    }

    void sendRelease() throws Exception {
        synchronized (node_config) {
            System.out.println("Hello");
            if (destination_id == -1) {
                for (int neighbor_id : node_config.request_queue) {
                    // Create new message
                    Message message = new Message();
                    message.str = "ReleaseMessage";
                    // node_config.scalar_clock++;
                    node_config.has_keys.set(neighbor_id, false);
                    message.scalar_clock = node_config.scalar_clock;
                    message.node_id = node_config.id;
                    node_config.num_messages_sent++;
                    // Send release to the neighbor
                    try {
                        ObjectOutputStream oos = node_config.o_stream.get(neighbor_id);
                        System.out.println("Sent CS release from node " + node_config.id + " to node " + neighbor_id
                                + ": Release Message " + node_config.num_request_sent + " at timestamp "
                                + node_config.scalar_clock);

                        oos.writeObject(message);
                        oos.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                node_config.request_queue.clear();
            } else {
                // Create new message
                Message message = new Message();
                message.str = "ReleaseMessage";
                // node_config.scalar_clock++;
                node_config.has_keys.set(destination_id, false);
                message.scalar_clock = node_config.scalar_clock;
                message.node_id = node_config.id;
                node_config.num_messages_sent++;
                // Send release to the neighbor
                try {
                    ObjectOutputStream oos = node_config.o_stream.get(destination_id);
                    System.out.println("Sent CS release from node " + node_config.id + " to node " + destination_id
                            + ": Release Message " + node_config.num_request_sent + " at timestamp "
                            + node_config.scalar_clock);

                    oos.writeObject(message);
                    oos.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < node_config.request_queue.size(); i++) {
                    if (node_config.request_queue.get(i) == destination_id) {
                        node_config.request_queue.remove(i);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void run() {
        if (message_type.equals("RequestMessage")) {
            try {
                this.sendRequest();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (message_type.equals("ReleaseMessage")) {
            try {
                this.sendRelease();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (message_type.equals("ReleaseRequestMessage")) {
            try {
                this.sendRelease();
                this.sendRequest();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
