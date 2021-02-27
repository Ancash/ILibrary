package de.ancash.ilibrary;
import de.ancash.ilibrary.sockets.Answer;
import de.ancash.ilibrary.sockets.ChatClient;
import de.ancash.ilibrary.sockets.InfoPacket;
import de.ancash.ilibrary.sockets.Request;
import de.ancash.ilibrary.sockets.TargetType;

public class TestClient extends ChatClient{

	public TestClient(String serverName, int serverPort, String plugin) {
		super(serverName, serverPort, plugin);
	}

	@Override
	public void onRequest(Request req) {
		System.out.println(req.getRequest());
	}

	@Override
	public void onAnswer(Answer ans) {
		System.out.println(ans.getAnswer());
		send(new Request("ALibrary", "Ping", TargetType.SERVER));
	}

	@Override
	public void onInfo(InfoPacket packet) {
		// TODO Auto-generated method stub
		
	}

}
