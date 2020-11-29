package pwcg.gui.rofmap.brief.builder;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import pwcg.campaign.Campaign;
import pwcg.campaign.context.PWCGContext;
import pwcg.campaign.context.PWCGProduct;
import pwcg.core.exception.PWCGException;
import pwcg.gui.rofmap.brief.model.BriefingData;
import pwcg.gui.rofmap.brief.model.BriefingFlight;
import pwcg.gui.rofmap.brief.model.BriefingFlightParameters;
import pwcg.gui.rofmap.brief.model.BriefingMapPoint;
import pwcg.mission.Mission;
import pwcg.mission.MissionGenerator;
import pwcg.mission.MissionProfile;
import pwcg.mission.flight.FlightTypes;
import pwcg.mission.flight.IFlight;
import pwcg.testutils.CampaignCache;
import pwcg.testutils.SquadronTestProfile;
import pwcg.testutils.TestMissionBuilderUtility;

public class BriefingDataBuilderTest
{
    private Campaign campaign;
    private Mission mission;

    @Before
    public void setup() throws PWCGException
    {
        PWCGContext.setProduct(PWCGProduct.BOS);
        campaign = CampaignCache.makeCampaign(SquadronTestProfile.KG53_PROFILE);

        MissionGenerator missionGenerator = new MissionGenerator(campaign);
        mission = missionGenerator.makeTestSingleMissionFromFlightType(TestMissionBuilderUtility.buildTestParticipatingHumans(campaign), FlightTypes.GROUND_ATTACK, MissionProfile.DAY_TACTICAL_MISSION);
    }

    @Test
    public void testBriefingBuilder() throws PWCGException
    {
        BriefingDataBuilder briefingDataBuilder = new BriefingDataBuilder(mission);
        BriefingData briefingData = briefingDataBuilder.buildBriefingData();
        assert(briefingData != null);
        assert(briefingData.getActiveBriefingFlight() != null);
        assert(briefingData.getActiveBriefingFlight().getBriefingFlightParameters().getBriefingMapMapPoints().size() > 4);
        assert(briefingData.getActiveBriefingFlight().getBriefingAssignmentData().getCrews().size() > 0);
        
        IFlight flight = briefingData.getSelectedFlight();
        assert (flight.getSquadron().getSquadronId() == SquadronTestProfile.KG53_PROFILE.getSquadronId());

        BriefingFlight briefingFlight = briefingData.getActiveBriefingFlight();
        assert (briefingFlight.getSquadronId() == SquadronTestProfile.KG53_PROFILE.getSquadronId());
        
        BriefingFlightParameters briefingFlightParameters = briefingFlight.getBriefingFlightParameters();
        List<BriefingMapPoint>  briefingMapMapPoints = briefingFlightParameters.getBriefingMapMapPoints();
        for (BriefingMapPoint briefingMapMapPoint : briefingMapMapPoints)
        {
            assert(briefingMapMapPoint.getDesc() != null);
            assert(!briefingMapMapPoint.getDesc().isEmpty());
        }

    }
}
