//Thread to start chandy lamport protocol

public class RC_Thread extends Thread {

	NodeConfig node_config;
	String operation;

	public RC_Thread(NodeConfig node_config, String operation) {
		this.node_config = node_config;
		this.operation = operation;
	}

	public void run() {
		if (operation.equals("EnterCS")) {
			startRequest(node_config);
		} else if (operation.equals("ExitCS")) {
			leaveCS(node_config);
		}
	}

	public static void startRequest(NodeConfig node_config) {
		new SendThread(node_config, "RequestMessage", -1).start();
	}

	public static void leaveCS(NodeConfig node_config) {
		Thread t = new SendThread(node_config, "ReleaseMessage", -1);
		t.start();
		try {
			t.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
