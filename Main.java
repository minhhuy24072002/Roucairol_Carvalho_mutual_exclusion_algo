import java.io.*;
import java.time.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        NodeConfig node_config = ReadConfigFile(args[1]);
        node_config.id = Integer.parseInt(args[0]);
        node_config.config_file_name = args[1];

        for (int i = 0; i < node_config.num_nodes; i++) {
            if (i >= node_config.id) {
                node_config.has_keys.add(true);
            } else {
                node_config.has_keys.add(false);
            }
        }

        for (int i = 0; i < node_config.nodes.size(); i++) {
            node_config.node_by_id.put(node_config.nodes.get(i).node_id, node_config.nodes.get(i));
        }

        // Create a server socket
        TCPServer server = new TCPServer(node_config);
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        new TCPClient(node_config, node_config.id);

        node_config.scalar_clock = 0;

        // if (node_config.id == 0) {
        // node_config.active = true;
        // new Chandy_Lamport_Thread(node_config).start();
        // new SendThread(node_config).start();
        // } else {
        // try {
        // Thread.sleep(1000);
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // }
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        node_config.start_time = Instant.now();
        new CS_Operation_Thread(node_config, "EnterCS").start();

        // try {
        // Thread.sleep(1000);
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        server.listen();
    }

    private static NodeConfig ReadConfigFile(String file_name) throws Exception {
        NodeConfig node_config = new NodeConfig();
        int node_count = 0, section = 0;
        // Keeps track of current node

        String file_path = file_name;

        String line = null;
        try {
            BufferedReader buff = new BufferedReader(new FileReader(file_path));

            while ((line = buff.readLine()) != null) {
                if (line.length() == 0 || line.startsWith("#"))
                    continue;
                // Ignore comments and consider only those lines which are not comments
                String[] config_input;
                if (line.contains("#")) {
                    String[] config_input_comment = line.split("#.*$"); // Ignore text after # symbol
                    config_input = config_input_comment[0].split("\\s+");
                } else {
                    config_input = line.split("\\s+");
                }

                if (section == 0 && config_input.length == 4) {
                    node_config.num_nodes = Integer.parseInt(config_input[0]);
                    node_config.inter_request_delay = Integer.parseInt(config_input[1]);
                    node_config.cs_execution_time = Integer.parseInt(config_input[2]);
                    node_config.max_request = Integer.parseInt(config_input[3]);

                    section++;
                } else if (section == 1 && node_count < node_config.num_nodes) {
                    System.out.println(config_input[0] + " " + config_input[1] + " " + config_input[2]);
                    node_config.nodes.add(new Node(Integer.parseInt(config_input[0]), config_input[1],
                            Integer.parseInt(config_input[2])));
                    node_count++;
                    if (node_count == node_config.num_nodes) {
                        section = 2;
                    }
                }
            }
            buff.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return node_config;
    }

    public static int getExponentialRandom(int mean) {
        Random random = new Random();
        double lambda = 1.0 / mean;
        return (int) (-Math.log(1.0 - random.nextDouble()) / lambda);
    }
}