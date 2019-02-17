package pwcg.campaign.resupply.depo;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pwcg.campaign.plane.PlaneArchType;
import pwcg.campaign.squadron.Squadron;
import pwcg.core.exception.PWCGException;

public class EquipmentReplacementWeightUsage
{
    private Date campaignDate;

    public EquipmentReplacementWeightUsage (Date campaignDate)
    {
        this.campaignDate = campaignDate;
    }

    public Map<String, Integer> getAircraftUsageByArchType(List<Squadron> squadronsForService) throws PWCGException
    {
        Map<String, Integer> aircraftUsageByArchType = new HashMap<>();
        
        for (Squadron squadron : squadronsForService)
        {
            List<PlaneArchType> currentAircraftArchTypes = squadron.determineCurrentAircraftArchTypes(campaignDate);
            for (PlaneArchType planeArchType : currentAircraftArchTypes)
            {
                if (!aircraftUsageByArchType.containsKey(planeArchType.getPlaneArchTypeName()))
                {
                    aircraftUsageByArchType.put(planeArchType.getPlaneArchTypeName(), new Integer(0)); 
                }
                
                Integer numUsagesOfArchtype = aircraftUsageByArchType.get(planeArchType.getPlaneArchTypeName());
                ++numUsagesOfArchtype;
                aircraftUsageByArchType.put(planeArchType.getPlaneArchTypeName(), numUsagesOfArchtype);
            }
        }
        
        return aircraftUsageByArchType;
    }
}
