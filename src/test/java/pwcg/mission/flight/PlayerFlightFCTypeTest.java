package pwcg.mission.flight;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import pwcg.campaign.Campaign;
import pwcg.campaign.context.PWCGContext;
import pwcg.campaign.context.PWCGProduct;
import pwcg.core.exception.PWCGException;
import pwcg.mission.Mission;
import pwcg.mission.MissionGenerator;
import pwcg.mission.flight.attack.GroundAttackFlight;
import pwcg.mission.flight.balloonBust.BalloonBustFlight;
import pwcg.mission.flight.balloondefense.BalloonDefenseFlight;
import pwcg.mission.flight.bomb.BombingFlight;
import pwcg.mission.flight.escort.PlayerIsEscortFlight;
import pwcg.mission.flight.intercept.InterceptFlight;
import pwcg.mission.flight.offensive.OffensiveFlight;
import pwcg.mission.flight.patrol.PatrolFlight;
import pwcg.mission.flight.validate.EscortForPlayerValidator;
import pwcg.mission.flight.validate.GroundAttackFlightValidator;
import pwcg.mission.flight.validate.GroundUnitValidator;
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

@RunWith(Parameterized.class)
public class PlayerFlightFCTypeTest 
{    
    @Parameterized.Parameters
    public static List<Object[]> data() 
    {
        return Arrays.asList(new Object[1][0]);
    }
    
    @Before
    public void setup() throws PWCGException
    {
        PWCGContext.setProduct(PWCGProduct.FC);
    }

	@Test
	public void groundAttackFlightTest() throws PWCGException
	{
		Campaign campaign = CampaignCache.makeCampaign(SquadronTestProfile.RFC_2_PROFILE);

        MissionGenerator missionGenerator = new MissionGenerator(campaign);
        Mission mission = missionGenerator.makeMissionFromFlightType(TestParticipatingHumanBuilder.buildTestParticipatingHumans(campaign), FlightTypes.GROUND_ATTACK);
        GroundAttackFlight flight = (GroundAttackFlight) mission.getMissionFlightBuilder().getPlayerFlights().get(0);
        mission.finalizeMission();
        MissionPoint targetMissionPoint = flight.getWaypointPackage().getMissionPointByAction(WaypointAction.WP_ACTION_INGRESS);
        assert (targetMissionPoint != null);
        PlaneRtbValidator.verifyPlaneRtbDisabled(mission);

		GroundAttackFlightValidator groundAttackFlightValidator = new GroundAttackFlightValidator();
		groundAttackFlightValidator.validateGroundAttackFlight(flight);
        validateTargetDefinition(flight.getFlightInformation().getTargetDefinition());
        assert(flight.getFlightType() == FlightTypes.GROUND_ATTACK);
        
        GroundUnitValidator groundUnitValidator = new GroundUnitValidator();
        groundUnitValidator.validateGroundUnitsForMission(mission);
        EscortForPlayerValidator playerEscortedFlightValidator = new EscortForPlayerValidator(flight);
        playerEscortedFlightValidator.validateEscortForPlayer();
        PositionEvaluator.evaluateAiFlight(mission);
        
        VirtualWaypointPackageValidator virtualWaypointPackageValidator = new VirtualWaypointPackageValidator(mission);
        virtualWaypointPackageValidator.validate();
	}
	
	@Test
	public void bombFlightTest() throws PWCGException
	{
        Campaign campaign = CampaignCache.makeCampaign(SquadronTestProfile.RFC_2_PROFILE);
		
        MissionGenerator missionGenerator = new MissionGenerator(campaign);
        Mission mission = missionGenerator.makeMissionFromFlightType(TestParticipatingHumanBuilder.buildTestParticipatingHumans(campaign), FlightTypes.BOMB);
        BombingFlight flight = (BombingFlight) mission.getMissionFlightBuilder().getPlayerFlights().get(0);
        mission.finalizeMission();
        MissionPoint targetMissionPoint = flight.getWaypointPackage().getMissionPointByAction(WaypointAction.WP_ACTION_INGRESS);
        assert (targetMissionPoint != null);

		GroundAttackFlightValidator groundAttackFlightValidator = new GroundAttackFlightValidator();
		groundAttackFlightValidator.validateGroundAttackFlight(flight);
        validateTargetDefinition(flight.getFlightInformation().getTargetDefinition());
        assert(flight.getFlightType() == FlightTypes.BOMB);
        PlaneRtbValidator.verifyPlaneRtbDisabled(mission);

        GroundUnitValidator groundUnitValidator = new GroundUnitValidator();
        groundUnitValidator.validateGroundUnitsForMission(mission);
        EscortForPlayerValidator playerEscortedFlightValidator = new EscortForPlayerValidator(flight);
        playerEscortedFlightValidator.validateEscortForPlayer();
        PositionEvaluator.evaluateAiFlight(mission);
        
        VirtualWaypointPackageValidator virtualWaypointPackageValidator = new VirtualWaypointPackageValidator(mission);
        virtualWaypointPackageValidator.validate();
	}

	@Test
	public void patrolFlightTest() throws PWCGException
	{
        Campaign campaign = CampaignCache.makeCampaign(SquadronTestProfile.ESC_103_PROFILE);

        MissionGenerator missionGenerator = new MissionGenerator(campaign);
        Mission mission = missionGenerator.makeMissionFromFlightType(TestParticipatingHumanBuilder.buildTestParticipatingHumans(campaign), FlightTypes.PATROL);
        PatrolFlight flight = (PatrolFlight) mission.getMissionFlightBuilder().getPlayerFlights().get(0);
        mission.finalizeMission();
        MissionPoint targetMissionPoint = flight.getWaypointPackage().getMissionPointByAction(WaypointAction.WP_ACTION_INGRESS);
        assert (targetMissionPoint != null);
        PlaneRtbValidator.verifyPlaneRtbDisabled(mission);

        PositionEvaluator.evaluateAiFlight(mission);
		PatrolFlightValidator patrolFlightValidator = new PatrolFlightValidator();
		patrolFlightValidator.validatePatrolFlight(flight);
        assert(flight.getFlightType() == FlightTypes.PATROL);
        
        VirtualWaypointPackageValidator virtualWaypointPackageValidator = new VirtualWaypointPackageValidator(mission);
        virtualWaypointPackageValidator.validate();
	}

    @Test
    public void balloonBustFlightTest() throws PWCGException
    {
        Campaign campaign = CampaignCache.makeCampaign(SquadronTestProfile.JASTA_11_PROFILE);
        MissionGenerator missionGenerator = new MissionGenerator(campaign);
        Mission mission = missionGenerator.makeMissionFromFlightType(TestParticipatingHumanBuilder.buildTestParticipatingHumans(campaign), FlightTypes.BALLOON_BUST);
        BalloonBustFlight flight = (BalloonBustFlight) mission.getMissionFlightBuilder().getPlayerFlights().get(0);
        mission.finalizeMission();
        MissionPoint targetMissionPoint = flight.getWaypointPackage().getMissionPointByAction(WaypointAction.WP_ACTION_INGRESS);
        assert (targetMissionPoint != null);
        PlaneRtbValidator.verifyPlaneRtbDisabled(mission);

        assert(flight.getFlightType() == FlightTypes.BALLOON_BUST);
        
        GroundUnitValidator groundUnitValidator = new GroundUnitValidator();
        groundUnitValidator.validateGroundUnitsForMission(mission);
        PositionEvaluator.evaluateAiFlight(mission);
        
        VirtualWaypointPackageValidator virtualWaypointPackageValidator = new VirtualWaypointPackageValidator(mission);
        virtualWaypointPackageValidator.validate();
    }

    @Test
    public void balloonDefenseFlightTest() throws PWCGException
    {
        Campaign campaign = CampaignCache.makeCampaign(SquadronTestProfile.JASTA_11_PROFILE);
        MissionGenerator missionGenerator = new MissionGenerator(campaign);
        Mission mission = missionGenerator.makeMissionFromFlightType(TestParticipatingHumanBuilder.buildTestParticipatingHumans(campaign), FlightTypes.BALLOON_DEFENSE);
        BalloonDefenseFlight flight = (BalloonDefenseFlight) mission.getMissionFlightBuilder().getPlayerFlights().get(0);
        mission.finalizeMission();
        MissionPoint targetMissionPoint = flight.getWaypointPackage().getMissionPointByAction(WaypointAction.WP_ACTION_INGRESS);
        assert (targetMissionPoint != null);
        PlaneRtbValidator.verifyPlaneRtbDisabled(mission);

        assert(flight.getFlightType() == FlightTypes.BALLOON_DEFENSE);
        
        GroundUnitValidator groundUnitValidator = new GroundUnitValidator();
        groundUnitValidator.validateGroundUnitsForMission(mission);
        PositionEvaluator.evaluateAiFlight(mission);
        
        VirtualWaypointPackageValidator virtualWaypointPackageValidator = new VirtualWaypointPackageValidator(mission);
        virtualWaypointPackageValidator.validate();
    }

	@Test
	public void interceptFlightTest() throws PWCGException
	{
        Campaign campaign = CampaignCache.makeCampaign(SquadronTestProfile.JASTA_11_PROFILE);
        MissionGenerator missionGenerator = new MissionGenerator(campaign);
        Mission mission = missionGenerator.makeMissionFromFlightType(TestParticipatingHumanBuilder.buildTestParticipatingHumans(campaign), FlightTypes.INTERCEPT);
        InterceptFlight flight = (InterceptFlight) mission.getMissionFlightBuilder().getPlayerFlights().get(0);
        mission.finalizeMission();
        MissionPoint targetMissionPoint = flight.getWaypointPackage().getMissionPointByAction(WaypointAction.WP_ACTION_INGRESS);
        assert (targetMissionPoint != null);
        PlaneRtbValidator.verifyPlaneRtbDisabled(mission);

		PatrolFlightValidator patrolFlightValidator = new PatrolFlightValidator();
		patrolFlightValidator.validatePatrolFlight(flight);
        assert(flight.getFlightType() == FlightTypes.INTERCEPT);
        PositionEvaluator.evaluateAiFlight(mission);
        
        VirtualWaypointPackageValidator virtualWaypointPackageValidator = new VirtualWaypointPackageValidator(mission);
        virtualWaypointPackageValidator.validate();
	}

	@Test
	public void offensiveFlightTest() throws PWCGException
	{
        Campaign campaign = CampaignCache.makeCampaign(SquadronTestProfile.ESC_103_PROFILE);

        MissionGenerator missionGenerator = new MissionGenerator(campaign);
        Mission mission = missionGenerator.makeMissionFromFlightType(TestParticipatingHumanBuilder.buildTestParticipatingHumans(campaign), FlightTypes.OFFENSIVE);
        OffensiveFlight flight = (OffensiveFlight) mission.getMissionFlightBuilder().getPlayerFlights().get(0);
        mission.finalizeMission();
        MissionPoint targetMissionPoint = flight.getWaypointPackage().getMissionPointByAction(WaypointAction.WP_ACTION_INGRESS);
        assert (targetMissionPoint != null);
        PlaneRtbValidator.verifyPlaneRtbDisabled(mission);

		PatrolFlightValidator patrolFlightValidator = new PatrolFlightValidator();
		patrolFlightValidator.validatePatrolFlight(flight);
        assert(flight.getFlightType() == FlightTypes.OFFENSIVE);
        PositionEvaluator.evaluateAiFlight(mission);
        
        VirtualWaypointPackageValidator virtualWaypointPackageValidator = new VirtualWaypointPackageValidator(mission);
        virtualWaypointPackageValidator.validate();
	}

	@Test
	public void escortFlightTest() throws PWCGException
	{
        Campaign campaign = CampaignCache.makeCampaign(SquadronTestProfile.ESC_103_PROFILE);
        
        MissionGenerator missionGenerator = new MissionGenerator(campaign);
        Mission mission = missionGenerator.makeMissionFromFlightType(TestParticipatingHumanBuilder.buildTestParticipatingHumans(campaign), FlightTypes.ESCORT);
        PlayerIsEscortFlight flight = (PlayerIsEscortFlight) mission.getMissionFlightBuilder().getPlayerFlights().get(0);
        mission.finalizeMission();
        MissionPoint targetMissionPoint = flight.getWaypointPackage().getMissionPointByAction(WaypointAction.WP_ACTION_INGRESS);
        assert (targetMissionPoint != null);
        PlaneRtbValidator.verifyPlaneRtbDisabled(mission);

		PlayerEscortFlightValidator escortFlightValidator = new PlayerEscortFlightValidator(flight);
		escortFlightValidator.validateEscortFlight();
        assert(flight.getFlightType() == FlightTypes.ESCORT);
        PositionEvaluator.evaluateAiFlight(mission);
        
        VirtualWaypointPackageValidator virtualWaypointPackageValidator = new VirtualWaypointPackageValidator(mission);
        virtualWaypointPackageValidator.validate();
	}
	
	public void validateTargetDefinition(TargetDefinition targetDefinition)
	{
        assert (targetDefinition.getAttackingCountry() != null);
        assert (targetDefinition.getTargetCountry() != null);
        assert (targetDefinition.getTargetCategory() != TargetCategory.TARGET_CATEGORY_NONE);
        assert (targetDefinition.getTargetType() != TargetType.TARGET_NONE);
	}
}
