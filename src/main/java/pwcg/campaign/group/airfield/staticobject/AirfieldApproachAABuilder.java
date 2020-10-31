package pwcg.campaign.group.airfield.staticobject;

import java.util.ArrayList;
import java.util.List;

import pwcg.core.config.ConfigItemKeys;
import pwcg.core.config.ConfigManagerCampaign;
import pwcg.core.config.ConfigSimple;
import pwcg.core.constants.AiSkillLevel;
import pwcg.core.exception.PWCGException;
import pwcg.core.location.Coordinate;
import pwcg.core.utils.MathUtils;
import pwcg.mission.flight.IFlight;
import pwcg.mission.flight.waypoint.WaypointAction;
import pwcg.mission.flight.waypoint.missionpoint.MissionPoint;
import pwcg.mission.ground.GroundUnitSize;
import pwcg.mission.ground.builder.AAAUnitBuilder;
import pwcg.mission.ground.org.IGroundUnit;
import pwcg.mission.ground.org.IGroundUnitCollection;
import pwcg.mission.target.TargetDefinition;
import pwcg.mission.target.TargetType;

public class AirfieldApproachAABuilder
{
    private List<IGroundUnitCollection> airfieldApproachAA = new ArrayList<>();

    public List<IGroundUnitCollection> addAirfieldApproachAA(IFlight flight) throws PWCGException
    {
        MissionPoint approachPositionMissionPoint = flight.getWaypointPackage().getMissionPointByAction(WaypointAction.WP_ACTION_LANDING_APPROACH);
        MissionPoint landingPositionMissionPoint = flight.getWaypointPackage().getMissionPointByAction(WaypointAction.WP_ACTION_LANDING);
        
        if (approachPositionMissionPoint == null || landingPositionMissionPoint == null)
        {
            return airfieldApproachAA;
        }
        
        Coordinate approachPosition = approachPositionMissionPoint.getPosition().copy();
        Coordinate landingPosition = landingPositionMissionPoint.getPosition().copy();
        double angleOut = MathUtils.calcAngle(landingPosition, approachPosition);
        
        double angleLeft = MathUtils.adjustAngle(angleOut, 270);
        double angleRight = MathUtils.adjustAngle(angleOut, 90);
        Coordinate firstAAPoint = MathUtils.calcNextCoord(landingPosition, angleOut, 500);
        
        List<Coordinate> aaCoordinates = buildAirfieldAAPositions(flight, angleOut, angleLeft, angleRight, firstAAPoint);
        buildFlightPathAA(flight, aaCoordinates);

        return airfieldApproachAA;
    }

    private List<Coordinate> buildAirfieldAAPositions(IFlight flight, double angleOut, double angleLeft, double angleRight, Coordinate firstAAPoint) throws PWCGException
    {
        List<Coordinate> aaCoordinates = new ArrayList<>();
        if (shouldCreateApproachAA(flight))
        {
            int numPairs = getNumAAGunPairs(flight);
            for (int i = 0; i < numPairs; ++i)
            {
                Coordinate aaCenterCoordinate = MathUtils.calcNextCoord(firstAAPoint, angleOut, (i * 1000));
                Coordinate leftAAPoint = MathUtils.calcNextCoord(aaCenterCoordinate, angleLeft, 500);
                Coordinate rightAAPoint = MathUtils.calcNextCoord(aaCenterCoordinate, angleRight, 500);
                
                aaCoordinates.add(leftAAPoint);
                aaCoordinates.add(rightAAPoint);
            }
        }
        return aaCoordinates;
    }

    private boolean shouldCreateApproachAA(IFlight flight) throws PWCGException
    {
        if (flight.isPlayerFlight())
        {
            return true;
        }
        
        ConfigManagerCampaign configManager = flight.getCampaign().getCampaignConfigManager();
        String currentGroundSetting = configManager.getStringConfigParam(ConfigItemKeys.SimpleConfigAAKey);
        if (currentGroundSetting.equals(ConfigSimple.CONFIG_LEVEL_HIGH))
        {
            return true;
        }
        
        return false;
    }

    private int getNumAAGunPairs(IFlight flight) throws PWCGException
    {
        int numAAGunPairs = 2;
        ConfigManagerCampaign configManager = flight.getCampaign().getCampaignConfigManager();
        String currentAASetting = configManager.getStringConfigParam(ConfigItemKeys.SimpleConfigAAKey);
        if (currentAASetting.equals(ConfigSimple.CONFIG_LEVEL_LOW))
        {
            numAAGunPairs = 2;
        }
        else if (currentAASetting.equals(ConfigSimple.CONFIG_LEVEL_MED))
        {
            numAAGunPairs = 3;
        }
        else if (currentAASetting.equals(ConfigSimple.CONFIG_LEVEL_HIGH))
        {
            numAAGunPairs = 3;
        }
        return numAAGunPairs;
    }

    private void buildFlightPathAA(IFlight flight, List<Coordinate> aaCoordinates) throws PWCGException
    {
        for (Coordinate aaPoint : aaCoordinates)
        {            
            TargetDefinition targetDefinition = new TargetDefinition(TargetType.TARGET_ARTILLERY, aaPoint, flight.getSquadron().getCountry());
            AAAUnitBuilder groundUnitFactory = new AAAUnitBuilder(flight.getCampaign(), targetDefinition);

            IGroundUnitCollection aaa = groundUnitFactory.createAAAArtilleryBattery(GroundUnitSize.GROUND_UNIT_SIZE_TINY);
            if (aaa != null)
            {
                for (IGroundUnit aaGun : aaa.getGroundUnits())
                {
                    aaGun.setAiLevel(AiSkillLevel.COMMON);
                }
                airfieldApproachAA.add(aaa);
            }
        }
    }
}
