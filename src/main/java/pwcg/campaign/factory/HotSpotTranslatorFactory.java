package pwcg.campaign.factory;

import pwcg.campaign.api.IHotSpotTranslator;
import pwcg.campaign.group.airfield.hotspot.AirfieldHotSpotTranslator;
import pwcg.mission.Mission;

public class HotSpotTranslatorFactory
{
    public static IHotSpotTranslator createHotSpotTranslatorFactory(Mission mission)
    {
        return new AirfieldHotSpotTranslator(mission);
    }
}
