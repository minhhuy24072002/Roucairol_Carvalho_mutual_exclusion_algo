import java.io.*;

//Print the global_snapshot to the output File
public class Output {
	NodeConfig node_config;

	public Output(NodeConfig node_config) {
		this.node_config = node_config;
	}

	public void printLogToFile() {
		synchronized (node_config) {
			try {
				File file = new File("log.out");
				FileWriter out_file;
				if (file.exists()) {
					out_file = new FileWriter(file, true);
				} else {
					out_file = new FileWriter(file);
				}

                BufferedReader input = new BufferedReader(new FileReader("log.out"));
                String last = "", line;
                while ((line = input.readLine()) != null) { 
                    last = line;
                }
                input.close();

				BufferedWriter buff_writer = new BufferedWriter(out_file);
                if (last.contains("enter critical section") == true) {
                    buff_writer.write("2 processes in critical section at the same time!\n");
                }
                buff_writer.write(node_config.id + " enter critical section\n");
                try {
                    Thread.sleep(Main.getExponentialRandom(node_config.cs_execution_time));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                buff_writer.write(node_config.id + " exit critical section\n");
				buff_writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
