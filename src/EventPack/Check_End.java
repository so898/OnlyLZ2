package EventPack;

public class Check_End implements EventSource{
	private int type;
	public Check_End(){
		this.type = EventSource.EVENT_END;
	}
	public int getEventType() {
		return this.type;
	}
}
