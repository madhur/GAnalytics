package in.co.madhur.ganalyticsdashclock;

public class GProfile
{
	private String Id;
	private String Name;
	
	public GProfile(String Id, String Name)
	{
		this.Id=Id;
		this.Name=Name;
	}
	
	public GProfile()
	{
		
	}

	
	public String getName()
	{
		return Name;
	}
	public void setName(String name)
	{
		Name = name;
	}
	public String getId()
	{
		return Id;
	}
	public void setId(String id)
	{
		Id = id;
	}
	
	@Override
	public String toString()
	{
		return Name;
	}

}
