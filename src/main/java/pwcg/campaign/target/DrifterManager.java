package pwcg.campaign.target;

import pwcg.campaign.context.PWCGDirectoryManager;
import pwcg.campaign.io.json.LocationIOJson;
import pwcg.core.exception.PWCGException;
import pwcg.core.location.LocationSet;

public class DrifterManager
{
	public static String BARGE_FILE_NAME = "BargePositions";
	
    private LocationSet bargePositions = new LocationSet(BARGE_FILE_NAME);

    public DrifterManager()
	{
	}

    public void configure(String mapName) throws PWCGException
	{
		String pwcgInputDir = PWCGDirectoryManager.getInstance().getPwcgInputDir() + mapName + "\\";
    	bargePositions = LocationIOJson.readJson(pwcgInputDir, BARGE_FILE_NAME);
	}

    public LocationSet getBargePositions()
    {
        return bargePositions;
    }
}
