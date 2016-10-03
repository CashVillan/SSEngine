package socket;

public class SocketMessage {

	String stream = null;
	String type = null;
	String target = null;
	String info = null;
	
	public SocketMessage(String message) {
		this.stream = message;
			
		String[] data = message.split(";/");
		
		if(data.length >= 3) {
			this.type = data[0];
			this.target = data[1];
			
			String rawInfo = "";
			for(int x = 2; x < data.length; x++) {
				if(rawInfo.equals("")) {
					rawInfo = data[x];
				} else {
					rawInfo = rawInfo + ";/" + data[x];
				}
			}
			this.info = rawInfo;	
		}
	}
	
	public SocketMessage(String type, String target, String info) {
		this.type = type;
		this.target = target;
		this.info = info;
		
		this.stream = type + ";/" + target + ";/" + info;
	}
	
	public String toString() {
		return this.stream;
	}
	
	public String getType() {
		return this.type;
	}
	
	public String getTarget() {
		return this.target;
	}
	
	public String getInfo() {
		return this.info;
	}
}
