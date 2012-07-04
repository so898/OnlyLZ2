package EventPack;

public class Proxy_Success implements EventSource{
	private int type;
	public Proxy_Success(){
		this.type = EventSource.EVENT_PROXY_SUCCESS;
	}
	public int getEventType() {
		return this.type;
	}
}
