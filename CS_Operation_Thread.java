import java.io.*;
import java.time.*;

public class CS_Operation_Thread extends Thread {
    NodeConfig node_config;
    String operation;

    public CS_Operation_Thread(NodeConfig node_config, String operation) {
        this.node_config = node_config;
        this.operation = operation;
    }

    @Override
    public void run() {
        synchronized (node_config) {
            if (operation.equals("EnterCS")) {
                enterCS(node_config);
            } else if (operation.equals("ExecuteCS")) {
                executeCS(node_config);
            } else if (operation.equals("ExitCS")) {
                try {
                    exitCS(node_config);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void enterCS(NodeConfig node_config) {
        synchronized (node_config) {
            node_config.request_time = Instant.now();
            System.out.println("Node " + node_config.id + " is requesting for critical section at timestamp "
                    + node_config.scalar_clock);
            System.out.println(
                    "Node " + node_config.id + " is requesting for critical section at " + node_config.request_time);
            node_config.status = "REQUESTING";
            // Increment num_request_sent
            node_config.num_request_sent++;

            int key_count = 0;
            for (int i = 0; i < node_config.num_nodes; i++) {
                System.out.println("Node " + node_config.id + " has key " + i + " " + node_config.has_keys.get(i));
                if (node_config.has_keys.get(i) == true) {
                    key_count++;
                }
            }
            if (key_count == node_config.num_nodes) {
                new CS_Operation_Thread(node_config, "ExecuteCS").start();
                return;
            }

            try {
                new SendThread(node_config, "RequestMessage", -1).sendRequest();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void executeCS(NodeConfig node_config) {
        synchronized (node_config) {
            node_config.status = "EXECUTING";
            node_config.scalar_clock++;
            System.out.println(
                    "Node " + node_config.id + " is in critical section at timestamp " + node_config.scalar_clock);
            new Output(node_config).printLogToFile();
            new CS_Operation_Thread(node_config, "ExitCS").start();
        }
    }

    public static void exitCS(NodeConfig node_config) throws Exception {
        synchronized (node_config) {
            System.out.println(
                    "Node " + node_config.id + " is exiting critical section at timestamp " + node_config.scalar_clock);
            node_config.status = "IDLE";
            System.out.println("Node " + node_config.id + " is exiting critical section at " + Instant.now()
                    + " since its request time " + node_config.request_time);
            node_config.total_response_time += Instant.now().toEpochMilli() - node_config.request_time.toEpochMilli();
            new SendThread(node_config, "ReleaseMessage", -1).sendRelease();
            try {
                Thread.sleep(Main.getExponentialRandom(node_config.inter_request_delay));
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (node_config.num_request_sent < node_config.max_request) {
                new CS_Operation_Thread(node_config, "EnterCS").start();
            } else {
                node_config.total_execution_time = Instant.now().toEpochMilli() - node_config.start_time.toEpochMilli();
                File file = new File("log-" + node_config.id + ".out");
                FileWriter out_file;
                out_file = new FileWriter(file);
                BufferedWriter buff_writer = new BufferedWriter(out_file);
                buff_writer.write("Node " + node_config.id + " has total request " + node_config.max_request + "\n");
                buff_writer.write(
                        "Node " + node_config.id + " has total messages sent " + node_config.num_messages_sent + "\n");
                buff_writer.write("Node " + node_config.id + " has total response time "
                        + node_config.total_response_time + " ms\n");
                buff_writer.write("Node " + node_config.id + " has total execution time "
                        + node_config.total_execution_time + " ms\n");
                buff_writer.close();
            }
        }
    }
}
