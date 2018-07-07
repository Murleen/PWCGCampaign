package pwcg.campaign.ww2.airfield;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pwcg.campaign.api.IAirfield;
import pwcg.campaign.api.IAirfieldConfiguration;
import pwcg.campaign.context.PWCGContextManager;
import pwcg.campaign.io.json.AirfieldDescriptorIOJson;
import pwcg.core.exception.PWCGException;

public class BoSAirfieldConfiguration implements IAirfieldConfiguration
{
    public Map<String, IAirfield> configure (String mapName) throws PWCGException
    {
        Map<String, IAirfield> airfields = new TreeMap<String, IAirfield>();
        
        airfields.clear();

        String pwcgInputDir = PWCGContextManager.getInstance().getDirectoryManager().getPwcgInputDir() + mapName + "\\";
        AirfieldDescriptorSet airfieldDescriptors = AirfieldDescriptorIOJson.readJson(pwcgInputDir, AIRFIELD_LOCATION_FILE_NAME);
        for (BoSAirfield.AirfieldDescriptor desc : airfieldDescriptors.locations)
        {
            BoSAirfield field = new BoSAirfield();
            field.initializeAirfieldFromDescriptor(desc);
            airfields.put(desc.getName(), field);
        }

        return airfields;
    }

    static public class AirfieldDescriptorSet
    {
        String locationSetName = "";
        List <BoSAirfield.AirfieldDescriptor> locations = new ArrayList<>();
    }
}