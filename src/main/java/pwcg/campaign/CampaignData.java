package pwcg.campaign;

import java.util.Date;

import pwcg.campaign.squadmember.SerialNumber;
import pwcg.campaign.squadmember.SquadronMemberStatus;

public class CampaignData
{

	private Date date = null;
	private String name = "";
	private int campaignStatus = SquadronMemberStatus.STATUS_ACTIVE;
	private int squadId = -1;
    private boolean isGreatAce = false;
    private SerialNumber serialNumber = new SerialNumber();

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

	public int getCampaignStatus()
	{
		return campaignStatus;
	}

	public void setCampaignStatus(int campaignStatus)
	{
		this.campaignStatus = campaignStatus;
	}

	public int getSquadId()
	{
		return squadId;
	}

	public void setSquadId(int squadId)
	{
		this.squadId = squadId;
	}

	public boolean isGreatAce()
	{
		return isGreatAce;
	}

	public void setGreatAce(boolean isGreatAce)
	{
		this.isGreatAce = isGreatAce;
	}

    public SerialNumber getSerialNumber()
    {
        return serialNumber;
    }

    public void setSerialNumber(SerialNumber serialNumber)
    {
        this.serialNumber = serialNumber;
    }
	
	
}
