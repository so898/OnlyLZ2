package EventPack;

public class Proxy_Fail implements EventSource{
	private int type;
	public Proxy_Fail(){
		this.type = EventSource.EVENT_PROXY_FAIL;
	}
	public int getEventType() {
		return this.type;
	}
}