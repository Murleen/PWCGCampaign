package pwcg.mission;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

import pwcg.campaign.Campaign;
import pwcg.campaign.CampaignMode;
import pwcg.campaign.api.IAirfield;
import pwcg.campaign.api.Side;
import pwcg.campaign.context.PWCGContext;
import pwcg.core.exception.PWCGException;
import pwcg.core.exception.PWCGIOException;
import pwcg.mission.mcu.McuIcon;

public class MissionAirfieldIconBuilder
{
    private ArrayList<McuIcon> airfieldIcons = new ArrayList<McuIcon>();

    public void createWaypointIcons(Campaign campaign, Mission mission) throws PWCGException 
    {        
        if (campaign.getCampaignData().getCampaignMode() == CampaignMode.CAMPAIGN_MODE_COMPETITIVE)
        {
            createAirfieldIconsForSide(campaign, mission, Side.ALLIED);
            createAirfieldIconsForSide(campaign, mission, Side.AXIS);
        }
        else
        {
            Side side = mission.getMissionFlightBuilder().getReferencePlayerFlight().getSquadron().getCountry().getSide();
            createAirfieldIconsForSide(campaign, mission, side);
        }
    }

    private void createAirfieldIconsForSide(Campaign campaign, Mission mission, Side side) throws PWCGException
    {
        List<IAirfield> airfields = PWCGContext.getInstance().getCurrentMap().getAirfieldManager().getAirFieldsForSide(campaign.getDate(), side);
		for (IAirfield airfield : airfields)
		{
	        McuIcon airfieldIcon = new McuIcon(airfield, airfield.getCountry(campaign.getDate()).getSide());
            airfieldIcons.add(airfieldIcon);
		}
    }

	public void write(BufferedWriter writer) throws PWCGIOException
	{
		for (McuIcon icon : airfieldIcons)
		{
			icon.write(writer);
		}
	}

}
