package EventPack;

public class Delete_Mission implements EventSource{
	private int type;
	public Delete_Mission(){
		this.type = EventSource.EVENT_DELETE_MESSION;
	}
	public int getEventType() {
		return this.type;
	}

}
