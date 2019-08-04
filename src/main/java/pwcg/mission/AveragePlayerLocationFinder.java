package pwcg.mission;

import java.util.ArrayList;
import java.util.List;

import pwcg.campaign.Campaign;
import pwcg.campaign.api.IProductSpecificConfiguration;
import pwcg.campaign.api.Side;
import pwcg.campaign.context.FrontLinesForMap;
import pwcg.campaign.context.PWCGContextManager;
import pwcg.campaign.factory.ProductSpecificConfigurationFactory;
import pwcg.campaign.squadmember.SquadronMember;
import pwcg.campaign.squadron.Squadron;
import pwcg.core.exception.PWCGException;
import pwcg.core.location.Coordinate;
import pwcg.core.utils.MathUtils;

public class AveragePlayerLocationFinder
{
    private Campaign campaign;
    
    public AveragePlayerLocationFinder(Campaign campaign)
    {
        this.campaign = campaign;
    }
    
    public Coordinate findAveragePlayerLocation(MissionHumanParticipants participatingPlayers) throws PWCGException
    {
        List<Coordinate> playerLocations = new ArrayList<>();
        for (SquadronMember player : participatingPlayers.getAllParticipatingPlayers())
        {
            Squadron squadron = player.determineSquadron();
            Coordinate squadronLocation = squadron.determineCurrentAirfieldAnyMap(campaign.getDate()).getPosition();
            playerLocations.add(squadronLocation);
        }
        
        double xSum = 0.0;
        double zSum = 0.0;
        for (Coordinate playerLocation : playerLocations)
        {
            xSum += playerLocation.getXPos();
            zSum += playerLocation.getZPos();
        }
        double xAvg = xSum / playerLocations.size();
        double zAvg = zSum / playerLocations.size();
        
        Coordinate centralLocation = new Coordinate(xAvg, 0.0, zAvg);
        return adjustLocationForMapEdgeProximity(centralLocation);
    }

    private Coordinate adjustLocationForMapEdgeProximity(Coordinate centralLocation) throws PWCGException
    {
        FrontLinesForMap frontLinesForMap = PWCGContextManager.getInstance().getCurrentMap().getFrontLinesForMap(campaign.getDate());

        double distanceFromMapEdge = MathUtils.calcDist(centralLocation, frontLinesForMap.getFirstPositionForSide(Side.ALLIED).getPosition());
        IProductSpecificConfiguration productSpecific = ProductSpecificConfigurationFactory.createProductSpecificConfiguration();
        if (distanceFromMapEdge < productSpecific.getSmallMissionRadius())
        {
            double angleAwayFromEdge = MathUtils.calcAngle(frontLinesForMap.getFirstPositionForSide(Side.ALLIED).getPosition(), centralLocation);
            centralLocation = MathUtils.calcNextCoord(centralLocation, angleAwayFromEdge, productSpecific.getSmallMissionRadius());
        }
        
        double distanceFromOtherMapEdge = MathUtils.calcDist(centralLocation, frontLinesForMap.getLastPositionForSide(Side.ALLIED).getPosition());
        if (distanceFromOtherMapEdge < productSpecific.getSmallMissionRadius())
        {
            double angleAwayFromEdge = MathUtils.calcAngle(frontLinesForMap.getLastPositionForSide(Side.ALLIED).getPosition(), centralLocation);
            centralLocation = MathUtils.calcNextCoord(centralLocation, angleAwayFromEdge, productSpecific.getSmallMissionRadius());
        }
        return centralLocation;
    }
}
