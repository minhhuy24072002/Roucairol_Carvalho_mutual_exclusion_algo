import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;

public class NodeConfig implements Serializable {
	// Node attributes
	int id;
	ArrayList<Integer> neighbors;
	String status;
	int num_request_sent;
	int scalar_clock;
	String config_file_name;

	// Config attributes
	int num_nodes;
	int inter_request_delay;
	int cs_execution_time;
	int max_request;

	// Experimental attributes
	long num_messages_sent;
	long total_response_time;
	long total_execution_time;
	Instant start_time;
	Instant request_time;


	// HashMap to get from node id to Node object
	HashMap<Integer, Node> node_by_id;

	// ArrayList to store all nodes' info
	ArrayList<Node> nodes;

	// HashMap to get from node id to node's listening socket
	HashMap<Integer, Socket> channels;

	// HashMap to get from node id to ObjectOutputStream to the node
	HashMap<Integer, ObjectOutputStream> o_stream;

	// Node's Roucairol and Carvalho’s Protocol attributes
	ArrayList<Boolean> has_keys;
	ArrayList<Integer> request_queue;

	// Initialize node config
	public NodeConfig() {
		num_request_sent = 0;
		status = "IDLE";
		neighbors = new ArrayList<>();
		nodes = new ArrayList<Node>();
		node_by_id = new HashMap<Integer, Node>();
		channels = new HashMap<Integer, Socket>();
		o_stream = new HashMap<Integer, ObjectOutputStream>();
		has_keys = new ArrayList<Boolean>();
		request_queue = new ArrayList<Integer>();
	}
}
