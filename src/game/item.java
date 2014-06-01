import java.io.Serializable;


public class item implements Serializable{
	private String name;
	private String description;
	
	public item(String nm, String dc)  {
		name = nm;
		description = dc;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
}
