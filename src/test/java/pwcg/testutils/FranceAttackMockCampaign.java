package pwcg.testutils;

import java.util.Date;

import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import pwcg.campaign.Campaign;
import pwcg.campaign.api.ICountry;
import pwcg.campaign.context.Country;
import pwcg.campaign.context.PWCGContextManager;
import pwcg.campaign.context.PWCGMap.FrontMapIdentifier;
import pwcg.campaign.factory.CountryFactory;
import pwcg.campaign.squadron.Squadron;
import pwcg.core.config.ConfigItemKeys;
import pwcg.core.config.ConfigManagerCampaign;
import pwcg.core.config.ConfigSimple;
import pwcg.core.exception.PWCGException;
import pwcg.core.location.Coordinate;
import pwcg.core.location.CoordinateBox;
import pwcg.core.utils.DateUtils;
import pwcg.mission.Mission;
import pwcg.mission.MissionBeginUnitCheckZone;
import pwcg.mission.MissionFlightBuilder;
import pwcg.mission.MissionGroundUnitResourceManager;
import pwcg.mission.mcu.Coalition;

public class FranceAttackMockCampaign
{
    @Mock
    protected Campaign campaign;

    @Mock
    protected Mission mission;

    @Mock
    protected MissionFlightBuilder missionFlightBuilder;

    @Mock
    protected ConfigManagerCampaign configManager;

    protected ICountry country = CountryFactory.makeCountryByCountry(Country.GERMANY);
    protected MissionBeginUnitCheckZone missionBeginUnit;
    protected Coordinate myTestPosition = new Coordinate (100000, 0, 100000);
    protected Coordinate mytargetLocation = new Coordinate (100000, 0, 150000);
    
    protected Date date;
    protected MissionGroundUnitResourceManager missionGroundUnitResourceManager = new MissionGroundUnitResourceManager();
    protected CoordinateBox missionBorders;
    protected Squadron squadron;

    public void mockCampaignSetup() throws PWCGException
    {
        PWCGContextManager.setRoF(true);
        PWCGContextManager.getInstance().changeContext(FrontMapIdentifier.FRANCE_MAP);
        PWCGContextManager.getInstance().setCampaign(campaign);

        date = DateUtils.getDateYYYYMMDD("19180501");
        
        squadron = PWCGContextManager.getInstance().getSquadronManager().getSquadron(501115);

        Mockito.when(campaign.getCampaignConfigManager()).thenReturn(configManager);
        Mockito.when(campaign.getDate()).thenReturn(date);
        Mockito.when(configManager.getIntConfigParam(ConfigItemKeys.MaxGroundTargetDistanceKey)).thenReturn(50000);
        Mockito.when(configManager.getStringConfigParam(ConfigItemKeys.SimpleConfigGroundKey)).thenReturn(ConfigSimple.CONFIG_LEVEL_MED);
        Mockito.when(mission.getMissionGroundUnitManager()).thenReturn(missionGroundUnitResourceManager);
        
        
        missionBorders = CoordinateBox.coordinateBoxFromCenter(myTestPosition, 100000);
        Mockito.when(mission.getMissionFlightBuilder()).thenReturn(missionFlightBuilder);
        Mockito.when(missionFlightBuilder.isInFlightPath(Matchers.any())).thenReturn(true);
        Mockito.when(missionFlightBuilder.getMissionBorders(Matchers.<Integer>any())).thenReturn(missionBorders);

        missionBeginUnit = new MissionBeginUnitCheckZone(myTestPosition, 10000);
        missionBeginUnit.getSelfDeactivatingCheckZone().getCheckZone().triggerCheckZoneByPlaneCoalition(Coalition.COALITION_ALLIED);
    }

}
