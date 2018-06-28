package pwcg.dev.utils;

import java.util.List;
import java.util.TreeMap;

import pwcg.campaign.api.IAirfield;
import pwcg.campaign.context.PWCGContextManager;
import pwcg.campaign.context.PWCGMap.FrontMapIdentifier;
import pwcg.campaign.squadron.Squadron;
import pwcg.core.exception.PWCGException;
import pwcg.core.utils.DateUtils;
import pwcg.core.utils.Logger;

public class SquadronMapFinder 
{
	static public void main (String[] args)
	{
        UserDir.setUserDir();

        try
		{
			SquadronMapFinder finder = new SquadronMapFinder();
			finder.squadronIsOnMap();
		}
		catch (Exception e)
		{
			 Logger.logException(e);
		}
	}

    
    private void squadronIsOnMap() throws PWCGException  
    {     
        PWCGContextManager.setRoF(false);
        PWCGContextManager.getInstance().changeContext(FrontMapIdentifier.KUBAN_MAP);
        
        TreeMap<Integer, String> airfieldsOnMapSorted = new TreeMap<>();
        
        List<Squadron> allSq =  PWCGContextManager.getInstance().getSquadronManager().getAllSquadrons();
        for (Squadron squadron : allSq)
        {
            IAirfield airfield = squadron.determineCurrentAirfieldCurrentMap(DateUtils.getDateYYYYMMDD("19430801"));
            if (airfield != null)
            {
                airfieldsOnMapSorted.put(squadron.getSquadronId(), airfield.getName());
            }
        }

        for (int squadronId : airfieldsOnMapSorted.keySet())
        {
            System.out.println(squadronId);
        }
    }
}
