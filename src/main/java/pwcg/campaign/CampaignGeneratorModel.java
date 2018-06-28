package pwcg.campaign;

import java.util.Date;

public class CampaignGeneratorModel
{
    private ArmedService service;
    private String playerName;
    private String playerRank;
    private String squadronName;
    private Date campaignDate;
    private String playerRegion;

    public ArmedService getService()
    {
        return service;
    }

    public void setService(ArmedService service)
    {
        this.service = service;
    }

    public String getPlayerName()
    {
        return playerName;
    }

    public void setPlayerName(String playerName)
    {
        this.playerName = playerName;
    }

    public String getPlayerRank()
    {
        return playerRank;
    }

    public void setPlayerRank(String playerRank)
    {
        this.playerRank = playerRank;
    }

    public String getSquadronName()
    {
        return squadronName;
    }

    public void setSquadronName(String squadronName)
    {
        this.squadronName = squadronName;
    }

    public Date getCampaignDate()
    {
        return campaignDate;
    }

    public void setCampaignDate(Date campaignDate)
    {
        this.campaignDate = campaignDate;
    }

    public String getPlayerRegion()
    {
        return playerRegion;
    }

    public void setPlayerRegion(String playerRegion)
    {
        this.playerRegion = playerRegion;
    }
}
