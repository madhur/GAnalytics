package in.co.madhur.ganalyticsdashclock.API;

public class GType
{
	
	protected String Id;
	protected String Name;
	
	public GType(String Id, String Name)
	{
		this.Id=Id;
		this.Name=Name;
	}
	
	public GType()
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
