package EventPack;

public class TypeEvent implements EventSource{
	private int type;
	public TypeEvent(int i){
		switch(i){
			case 1:
				this.type = EventSource.EVENT_END;
				System.out.println("Here");
			case 2:
				this.type = EventSource.EVENT_DELETE_MESSION;
			default:
				return ;
		}
	}
	public int getEventType() {
		return this.type;
	}
}
