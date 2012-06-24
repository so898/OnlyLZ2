package EventPack;
public interface EventSource {  
    public final int EVENT_END = 1;
    public final int EVENT_DELETE_MESSION = 2;
    public final int EVENT_MISSION_END = 3;
    public int getEventType();  
}  