package pwcg.campaign;

import java.util.Date;

import pwcg.campaign.context.PWCGMap.FrontMapIdentifier;
import pwcg.campaign.squadmember.SerialNumber;

public class CampaignData
{

	private Date date = null;
	private String name = "";
    private boolean isCoop = false;
    private SerialNumber serialNumber = new SerialNumber();
    private FrontMapIdentifier campaignMap;

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		this.date = date;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
    public boolean isCoop()
    {
        return isCoop;
    }

    public void setCoop(boolean isCoop)
    {
        this.isCoop = isCoop;
    }

    public SerialNumber getSerialNumber()
    {
        return serialNumber;
    }

    public void setSerialNumber(SerialNumber serialNumber)
    {
        this.serialNumber = serialNumber;
    }

	public FrontMapIdentifier getCampaignMap() 
	{
		return campaignMap;
	}

	public void setCampaignMap(FrontMapIdentifier campaignMap) 
	{
		this.campaignMap = campaignMap;
	}
	
	
}
