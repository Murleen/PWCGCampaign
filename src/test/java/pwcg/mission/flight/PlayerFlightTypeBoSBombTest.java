package pwcg.mission.flight;

import org.junit.Before;
import org.junit.Test;

import pwcg.campaign.Campaign;
import pwcg.campaign.context.PWCGContextManager;
import pwcg.campaign.target.TacticalTarget;
import pwcg.campaign.target.TargetCategory;
import pwcg.campaign.target.TargetDefinition;
import pwcg.core.exception.PWCGException;
import pwcg.mission.Mission;
import pwcg.mission.flight.bomb.BombingFlight;
import pwcg.mission.flight.bomb.StrategicBombingFlight;
import pwcg.mission.flight.plane.PlaneMCU;
import pwcg.testutils.CampaignCache;
import pwcg.testutils.CampaignCacheBoS;

public class PlayerFlightTypeBoSBombTest
{
    Mission mission;
    Campaign campaign;

    @Before
    public void fighterFlightTests() throws PWCGException
    {
        PWCGContextManager.setRoF(false);
        campaign = CampaignCache.makeCampaign(CampaignCacheBoS.KG53_PROFILE);
    }

    @Test
    public void bombFlightTest() throws PWCGException
    {
        mission = new Mission();
        mission.initialize(campaign);
        mission.generate(FlightTypes.BOMB);
        BombingFlight flight = (BombingFlight) mission.getMissionFlightBuilder().getPlayerFlight();
        flight.finalizeFlight();

        GroundAttackFlightValidator groundAttackFlightValidator = new GroundAttackFlightValidator();
        groundAttackFlightValidator.validateGroundAttackFlight(flight);
        validateTargetDefinition(flight.getTargetDefinition());
        assert (flight.getFlightType() == FlightTypes.BOMB);
        for (PlaneMCU plane : flight.getPlanes())
        {
            assert(plane.getPlanePayload().getSelectedPayloadId() > 0);
        }
    }

    @Test
    public void lowAltBombFlightTest() throws PWCGException
    {
        mission = new Mission();
        mission.initialize(campaign);
        mission.generate(FlightTypes.LOW_ALT_BOMB);
        BombingFlight flight = (BombingFlight) mission.getMissionFlightBuilder().getPlayerFlight();
        flight.finalizeFlight();

        GroundAttackFlightValidator groundAttackFlightValidator = new GroundAttackFlightValidator();
        groundAttackFlightValidator.validateGroundAttackFlight(flight);
        validateTargetDefinition(flight.getTargetDefinition());
        assert (flight.getFlightType() == FlightTypes.LOW_ALT_BOMB);
        for (PlaneMCU plane : flight.getPlanes())
        {
            assert(plane.getPlanePayload().getSelectedPayloadId() > 0);
        }
    }

    @Test
    public void strategicBombFlightTest() throws PWCGException
    {
        mission = new Mission();
        mission.initialize(campaign);
        mission.generate(FlightTypes.STRATEGIC_BOMB);
        StrategicBombingFlight flight = (StrategicBombingFlight) mission.getMissionFlightBuilder().getPlayerFlight();
        flight.finalizeFlight();

        GroundAttackFlightValidator groundAttackFlightValidator = new GroundAttackFlightValidator();
        groundAttackFlightValidator.validateGroundAttackFlight(flight);
        validateTargetDefinition(flight.getTargetDefinition());
        assert (flight.getFlightType() == FlightTypes.STRATEGIC_BOMB);
        for (PlaneMCU plane : flight.getPlanes())
        {
            assert(plane.getPlanePayload().getSelectedPayloadId() > 0);
        }
    }

    public void validateTargetDefinition(TargetDefinition targetDefinition)
    {
        assert (targetDefinition.getAttackingCountry() != null);
        assert (targetDefinition.getTargetCountry() != null);
        assert (targetDefinition.getTargetGeneralPosition() != null);
        assert (targetDefinition.getTargetCategory() != TargetCategory.TARGET_CATEGORY_NONE);
        assert (targetDefinition.getTargetType() != TacticalTarget.TARGET_NONE);
    }
}
