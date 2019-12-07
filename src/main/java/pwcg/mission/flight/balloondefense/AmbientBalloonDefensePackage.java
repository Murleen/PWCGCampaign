 package pwcg.mission.flight.balloondefense;

import java.util.ArrayList;
import java.util.List;

import pwcg.campaign.Campaign;
import pwcg.campaign.api.ICountry;
import pwcg.campaign.api.Side;
import pwcg.campaign.context.PWCGContext;
import pwcg.campaign.context.PositionsManager;
import pwcg.campaign.factory.CountryFactory;
import pwcg.campaign.squadron.Squadron;
import pwcg.core.exception.PWCGException;
import pwcg.core.location.Coordinate;
import pwcg.core.location.CoordinateBox;
import pwcg.core.utils.RandomNumberGenerator;
import pwcg.mission.Mission;
import pwcg.mission.ground.factory.BalloonUnitBuilder;
import pwcg.mission.ground.org.IGroundUnitCollection;
import pwcg.mission.target.TacticalTarget;
import pwcg.mission.target.TargetDefinition;
import pwcg.mission.target.TargetDefinitionBuilderGround;

public class AmbientBalloonDefensePackage
{
    public static final int BALLOON_SPAWN_TRIGGER_DISTANCE = 20000;

    private Campaign campaign;
    private Mission mission;

    public AmbientBalloonDefensePackage(Mission mission)
    {
        this.mission = mission;
        this.campaign = mission.getCampaign();
    }


	public IGroundUnitCollection createPackage (Side balloonSide, Coordinate referenceLocation) throws PWCGException 
	{
		Coordinate positionCoordinate = getNearestPositionCoordinate(balloonSide);
		Coordinate balloonPosition = getBalloonCoordinate(positionCoordinate, balloonSide);
		
		
		ICountry country = getAmbientBalloonCountry(positionCoordinate, balloonSide);
		IGroundUnitCollection balloonUnit = createBalloon (balloonPosition, country);

		return balloonUnit;
	}

	private ICountry getAmbientBalloonCountry(Coordinate positionCoordinate, Side balloonSide) throws PWCGException 
	{
	    ICountry country = CountryFactory.makeMapReferenceCountry(balloonSide);
	    
        Squadron squad = getBalloonSquadronForClosestCountry(balloonSide, positionCoordinate);
        if (squad != null)
        {
            Campaign campaign = PWCGContext.getInstance().getCampaign();
            country = squad.determineSquadronCountry(campaign.getDate());
        }
        
        return country;
	}

	private IGroundUnitCollection createBalloon (Coordinate balloonCoordinate, ICountry balloonCountry) throws PWCGException 
	{
		Side side = balloonCountry.getSide();
		Coordinate balloonPosition = getBalloonCoordinate(balloonCoordinate, side);
        
        TargetDefinitionBuilderGround targetDefinitionBuilder = new TargetDefinitionBuilderGround(campaign);
        boolean isPlayerTarget = false;
        TargetDefinition ambientBalloonTargetDefinition = targetDefinitionBuilder.buildTargetDefinitionAmbient(balloonCountry, TacticalTarget.TARGET_BALLOON, balloonPosition, isPlayerTarget);
        
        BalloonUnitBuilder groundUnitBuilderBalloonDefense = new BalloonUnitBuilder(campaign, ambientBalloonTargetDefinition);
        IGroundUnitCollection balloonUnit = groundUnitBuilderBalloonDefense.createBalloonUnit(balloonCountry);

		return balloonUnit;
	}

	private Coordinate getNearestPositionCoordinate(Side side) throws PWCGException 
	{
        CoordinateBox missionBorders = mission.getMissionBorders().expandBox(5000);
		PositionsManager positionsManager = new PositionsManager(mission.getCampaign().getDate());
	
		// Make the ambient balloon positions
		// Get the balloon position
		Coordinate balloonPosition = null;
        List<Coordinate> balloonPositions = positionsManager.getPositions(CountryFactory.makeMapReferenceCountry(side), missionBorders);
        int index = RandomNumberGenerator.getRandom(balloonPositions.size());
        balloonPosition = balloonPositions.get(index);
		
		return balloonPosition;
	}

	private Squadron getBalloonSquadronForClosestCountry(Side side, Coordinate balloonPosition) throws PWCGException 
	{		
		Squadron squad = null;
		List<Squadron> squadrons = new ArrayList<Squadron>();
		squadrons =  PWCGContext.getInstance().getSquadronManager().getNearestSquadronsBySide(campaign, 
		        balloonPosition, 1, 200000.0, side, campaign.getDate());
		if (squadrons.size() > 0)
		{
			squad = squadrons.get(0);
		}

		return squad;
	}

	private Coordinate getBalloonCoordinate(Coordinate referenceCoordinate, Side side) 
	{
		Coordinate targetWaypoint = null;
		PositionsManager positionsManager = new PositionsManager(campaign.getDate());
		targetWaypoint = positionsManager.getClosestDefinitePosition(side, referenceCoordinate.copy());
		return targetWaypoint;
	}

}
