package pwcg.mission.flight;

import org.junit.Before;
import org.junit.Test;

import pwcg.campaign.Campaign;
import pwcg.campaign.context.PWCGContext;
import pwcg.campaign.context.PWCGMap.FrontMapIdentifier;
import pwcg.campaign.context.PWCGProduct;
import pwcg.core.exception.PWCGException;
import pwcg.mission.Mission;
import pwcg.mission.MissionGenerator;
import pwcg.mission.flight.escort.PlayerIsEscortFlight;
import pwcg.mission.flight.intercept.InterceptFlight;
import pwcg.mission.flight.offensive.OffensiveFlight;
import pwcg.mission.flight.patrol.PatrolFlight;
import pwcg.mission.flight.scramble.PlayerScrambleFlight;
import pwcg.mission.flight.strategicintercept.StrategicInterceptFlight;
import pwcg.mission.flight.validate.EscortForPlayerValidator;
import pwcg.mission.flight.validate.FlightActivateValidator;
import pwcg.mission.flight.validate.PatrolFlightValidator;
import pwcg.mission.flight.validate.PlaneRtbValidator;
import pwcg.mission.flight.validate.PlayerEscortFlightValidator;
import pwcg.mission.flight.validate.PositionEvaluator;
import pwcg.mission.flight.validate.VirtualWaypointPackageValidator;
import pwcg.mission.flight.waypoint.WaypointAction;
import pwcg.mission.flight.waypoint.missionpoint.MissionPoint;
import pwcg.mission.target.TargetCategory;
import pwcg.mission.target.TargetDefinition;
import pwcg.mission.target.TargetType;
import pwcg.testutils.CampaignCache;
import pwcg.testutils.SquadronTestProfile;
import pwcg.testutils.TestParticipatingHumanBuilder;

public class PlayerFlightTypeBoSFighterTest 
{    
    Campaign campaign;
    
    @Before
    public void setup() throws PWCGException
    {
        PWCGContext.setProduct(PWCGProduct.BOS);
        PWCGContext.getInstance().changeContext(FrontMapIdentifier.MOSCOW_MAP);
        campaign = CampaignCache.makeCampaign(SquadronTestProfile.JG_51_PROFILE_MOSCOW);
    }

	@Test
	public void patrolFlightTest() throws PWCGException
	{
        MissionGenerator missionGenerator = new MissionGenerator(campaign);
        Mission mission = missionGenerator.makeMissionFromFlightType(TestParticipatingHumanBuilder.buildTestParticipatingHumans(campaign), FlightTypes.PATROL);
        PatrolFlight flight = (PatrolFlight) mission.getMissionFlightBuilder().getPlayerFlights().get(0);
        mission.finalizeMission();
        MissionPoint targetMissionPoint = flight.getWaypointPackage().getMissionPointByAction(WaypointAction.WP_ACTION_INGRESS);
        assert (targetMissionPoint != null);
        PlaneRtbValidator.verifyPlaneRtbEnabled(mission);

		PatrolFlightValidator patrolFlightValidator = new PatrolFlightValidator();
		patrolFlightValidator.validatePatrolFlight(flight);
        assert(flight.getFlightType() == FlightTypes.PATROL);
        FlightActivateValidator.validate(flight);
        EscortForPlayerValidator playerEscortedFlightValidator = new EscortForPlayerValidator(flight);
        playerEscortedFlightValidator.validateNoEscortForPlayer();
        PositionEvaluator.evaluateAiFlight(mission);
        
        VirtualWaypointPackageValidator virtualWaypointPackageValidator = new VirtualWaypointPackageValidator(mission);
        virtualWaypointPackageValidator.validate();

        assert(mission.getMissionFlightBuilder().getAiFlights().size() > 2);
	}

    @Test
    public void interceptFlightTest() throws PWCGException
    {
        MissionGenerator missionGenerator = new MissionGenerator(campaign);
        Mission mission = missionGenerator.makeMissionFromFlightType(TestParticipatingHumanBuilder.buildTestParticipatingHumans(campaign), FlightTypes.INTERCEPT);
        InterceptFlight flight = (InterceptFlight) mission.getMissionFlightBuilder().getPlayerFlights().get(0);
        mission.finalizeMission();
        MissionPoint targetMissionPoint = flight.getWaypointPackage().getMissionPointByAction(WaypointAction.WP_ACTION_INGRESS);
        assert (targetMissionPoint != null);
        PlaneRtbValidator.verifyPlaneRtbEnabled(mission);

        PatrolFlightValidator patrolFlightValidator = new PatrolFlightValidator();
        patrolFlightValidator.validatePatrolFlight(flight);
        assert(flight.getFlightType() == FlightTypes.INTERCEPT);
        FlightActivateValidator.validate(flight);
        EscortForPlayerValidator playerEscortedFlightValidator = new EscortForPlayerValidator(flight);
        playerEscortedFlightValidator.validateNoEscortForPlayer();
        PositionEvaluator.evaluateAiFlight(mission);
        
        VirtualWaypointPackageValidator virtualWaypointPackageValidator = new VirtualWaypointPackageValidator(mission);
        virtualWaypointPackageValidator.validate();

        assert(mission.getMissionFlightBuilder().getAiFlights().size() > 2);
    }

    @Test
    public void strategicInterceptFlightTest() throws PWCGException
    {
        MissionGenerator missionGenerator = new MissionGenerator(campaign);
        Mission mission = missionGenerator.makeMissionFromFlightType(TestParticipatingHumanBuilder.buildTestParticipatingHumans(campaign), FlightTypes.STRATEGIC_INTERCEPT);
        StrategicInterceptFlight flight = (StrategicInterceptFlight) mission.getMissionFlightBuilder().getPlayerFlights().get(0);
        mission.finalizeMission();
        MissionPoint targetMissionPoint = flight.getWaypointPackage().getMissionPointByAction(WaypointAction.WP_ACTION_INGRESS);
        assert (targetMissionPoint != null);
        PlaneRtbValidator.verifyPlaneRtbEnabled(mission);

        PatrolFlightValidator patrolFlightValidator = new PatrolFlightValidator();
        patrolFlightValidator.validatePatrolFlight(flight);
        assert(flight.getFlightType() == FlightTypes.STRATEGIC_INTERCEPT);
        FlightActivateValidator.validate(flight);
        EscortForPlayerValidator playerEscortedFlightValidator = new EscortForPlayerValidator(flight);
        playerEscortedFlightValidator.validateNoEscortForPlayer();
        PositionEvaluator.evaluateAiFlight(mission);
        
        VirtualWaypointPackageValidator virtualWaypointPackageValidator = new VirtualWaypointPackageValidator(mission);
        virtualWaypointPackageValidator.validate();
        
        assert(mission.getMissionFlightBuilder().getAiFlights().size() == 0);
    }

	@Test
	public void offensiveFlightTest() throws PWCGException
	{
        MissionGenerator missionGenerator = new MissionGenerator(campaign);
        Mission mission = missionGenerator.makeMissionFromFlightType(TestParticipatingHumanBuilder.buildTestParticipatingHumans(campaign), FlightTypes.OFFENSIVE);
        OffensiveFlight flight = (OffensiveFlight) mission.getMissionFlightBuilder().getPlayerFlights().get(0);
        mission.finalizeMission();
        MissionPoint targetMissionPoint = flight.getWaypointPackage().getMissionPointByAction(WaypointAction.WP_ACTION_INGRESS);
        assert (targetMissionPoint != null);
        PlaneRtbValidator.verifyPlaneRtbEnabled(mission);

		PatrolFlightValidator patrolFlightValidator = new PatrolFlightValidator();
		patrolFlightValidator.validatePatrolFlight(flight);
        assert(flight.getFlightType() == FlightTypes.OFFENSIVE);
        FlightActivateValidator.validate(flight);
        EscortForPlayerValidator playerEscortedFlightValidator = new EscortForPlayerValidator(flight);
        playerEscortedFlightValidator.validateNoEscortForPlayer();
        PositionEvaluator.evaluateAiFlight(mission);
        
        VirtualWaypointPackageValidator virtualWaypointPackageValidator = new VirtualWaypointPackageValidator(mission);
        virtualWaypointPackageValidator.validate();

        assert(mission.getMissionFlightBuilder().getAiFlights().size() > 2);
	}

	@Test
	public void escortFlightTest() throws PWCGException
	{
        MissionGenerator missionGenerator = new MissionGenerator(campaign);
        Mission mission = missionGenerator.makeMissionFromFlightType(TestParticipatingHumanBuilder.buildTestParticipatingHumans(campaign), FlightTypes.ESCORT);
        PlayerIsEscortFlight flight = (PlayerIsEscortFlight) mission.getMissionFlightBuilder().getPlayerFlights().get(0);
        mission.finalizeMission();
        MissionPoint targetMissionPoint = flight.getWaypointPackage().getMissionPointByAction(WaypointAction.WP_ACTION_INGRESS);
        assert (targetMissionPoint != null);
        PlaneRtbValidator.verifyPlaneRtbEnabled(mission);

		PlayerEscortFlightValidator escortFlightValidator = new PlayerEscortFlightValidator(flight);
		escortFlightValidator.validateEscortFlight();
        assert(flight.getFlightType() == FlightTypes.ESCORT);
        FlightActivateValidator.validate(flight);
        EscortForPlayerValidator playerEscortedFlightValidator = new EscortForPlayerValidator(flight);
        playerEscortedFlightValidator.validateNoEscortForPlayer();
        PositionEvaluator.evaluateAiFlight(mission);
        
        VirtualWaypointPackageValidator virtualWaypointPackageValidator = new VirtualWaypointPackageValidator(mission);
        virtualWaypointPackageValidator.validate();

        assert(mission.getMissionFlightBuilder().getAiFlights().size() > 2);
	}

    @Test
    public void scrambleFlightTest() throws PWCGException
    {
        MissionGenerator missionGenerator = new MissionGenerator(campaign);
        Mission mission = missionGenerator.makeMissionFromFlightType(TestParticipatingHumanBuilder.buildTestParticipatingHumans(campaign), FlightTypes.SCRAMBLE);
        PlayerScrambleFlight flight = (PlayerScrambleFlight) mission.getMissionFlightBuilder().getPlayerFlights().get(0);
        mission.finalizeMission();
        MissionPoint targetMissionPoint = flight.getWaypointPackage().getMissionPointByAction(WaypointAction.WP_ACTION_INGRESS);
        assert (targetMissionPoint != null);
        PlaneRtbValidator.verifyPlaneRtbEnabled(mission);

        PatrolFlightValidator patrolFlightValidator = new PatrolFlightValidator();
        patrolFlightValidator.validatePatrolFlight(flight);
        assert(flight.getFlightType() == FlightTypes.SCRAMBLE);        
        FlightActivateValidator.validate(flight);
        EscortForPlayerValidator playerEscortedFlightValidator = new EscortForPlayerValidator(flight);
        playerEscortedFlightValidator.validateNoEscortForPlayer();
        PositionEvaluator.evaluateAiFlight(mission);
        
        VirtualWaypointPackageValidator virtualWaypointPackageValidator = new VirtualWaypointPackageValidator(mission);
        virtualWaypointPackageValidator.validate();

        assert(mission.getMissionFlightBuilder().getAiFlights().size() > 2);
    }

    public void validateTargetDefinition(TargetDefinition targetDefinition)
	{
        assert (targetDefinition.getAttackingCountry() != null);
        assert (targetDefinition.getTargetCountry() != null);
        assert (targetDefinition.getTargetCategory() != TargetCategory.TARGET_CATEGORY_NONE);
        assert (targetDefinition.getTargetType() != TargetType.TARGET_NONE);
	}
}
