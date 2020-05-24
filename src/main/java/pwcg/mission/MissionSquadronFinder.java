package pwcg.mission;

import java.util.ArrayList;
import java.util.List;

import pwcg.campaign.Campaign;
import pwcg.campaign.api.Side;
import pwcg.campaign.context.FrontLinesForMap;
import pwcg.campaign.context.PWCGContext;
import pwcg.campaign.context.PWCGMap;
import pwcg.campaign.personnel.SquadronMemberFilter;
import pwcg.campaign.plane.Role;
import pwcg.campaign.squadmember.SquadronMembers;
import pwcg.campaign.squadron.Squadron;
import pwcg.core.config.ConfigItemKeys;
import pwcg.core.config.ConfigManagerCampaign;
import pwcg.core.exception.PWCGException;
import pwcg.core.location.Coordinate;
import pwcg.core.location.CoordinateBox;
import pwcg.core.utils.RandomNumberGenerator;

public class MissionSquadronFinder
{
    private List<Squadron> alliedSquads = new ArrayList<Squadron>();
    private List<Squadron> axisSquads = new ArrayList<Squadron>();
    private Campaign campaign;
    private Mission mission;

    public MissionSquadronFinder(Campaign campaign, Mission mission)
    {
        this.campaign = campaign;
        this.mission = mission;
    }

    public void findAiSquadronsForMission() throws PWCGException
    {
        if (mission.getMissionProfile() == MissionProfile.DAY_TACTICAL_MISSION)
        {
            findSquadronsForTacticalMission();
        }
        else if (mission.getMissionProfile() == MissionProfile.NIGHT_TACTICAL_MISSION)
        {
            findSquadronsForTacticalMission();
        }
        else if (mission.getMissionProfile() == MissionProfile.DAY_STRATEGIC_MISSION)
        {
            findSquadronsForStrategicMission();
        }
        else if (mission.getMissionProfile() == MissionProfile.NIGHT_STRATEGIC_MISSION)
        {
            findSquadronsForNightStrategicMission();
        }
        else
        {
            throw new PWCGException("No profile for mission");
        }
    }

    private void findSquadronsForTacticalMission() throws PWCGException
    {
        ConfigManagerCampaign configManager = campaign.getCampaignConfigManager();

        int initialSquadronSearchRadiusKey = configManager.getIntConfigParam(ConfigItemKeys.InitialSquadronSearchRadiusKey);

        PWCGMap map = PWCGContext.getInstance().getCurrentMap();
        FrontLinesForMap frontLinesForMap = map.getFrontLinesForMap(campaign.getDate());

        CoordinateBox missionBorders = mission.getMissionBorders().expandBox(5000);
        Coordinate closestAlliedFrontPosition = frontLinesForMap.findClosestFrontCoordinateForSide(missionBorders.getCenter(), Side.ALLIED);
        alliedSquads = PWCGContext.getInstance().getSquadronManager().getNearestSquadronsBySide(campaign, closestAlliedFrontPosition, 5,
                        initialSquadronSearchRadiusKey, Side.ALLIED, campaign.getDate());

        Coordinate closestAxisFrontPosition = frontLinesForMap.findClosestFrontCoordinateForSide(missionBorders.getCenter(), Side.AXIS);
        axisSquads = PWCGContext.getInstance().getSquadronManager().getNearestSquadronsBySide(campaign, closestAxisFrontPosition, 5,
                        initialSquadronSearchRadiusKey, Side.AXIS, campaign.getDate());
        determineSquadronAvailability();
    }

    private void findSquadronsForStrategicMission() throws PWCGException
    {
        alliedSquads.clear();
        axisSquads.clear();

        Campaign campaign = PWCGContext.getInstance().getCampaign();
        ConfigManagerCampaign configManager = campaign.getCampaignConfigManager();

        List<Role> acceptableRoles = new ArrayList<Role>();
        acceptableRoles.clear();
        acceptableRoles.add(Role.ROLE_FIGHTER);

        FrontLinesForMap frontLinesForMap = PWCGContext.getInstance().getCurrentMap().getFrontLinesForMap(campaign.getDate());
        int initialSquadronSearchRadiusKey = configManager.getIntConfigParam(ConfigItemKeys.InitialSquadronSearchRadiusKey);

        CoordinateBox missionBorders = mission.getMissionBorders().expandBox(5000);
        Coordinate closestAlliedFrontPosition = frontLinesForMap.findClosestFrontCoordinateForSide(missionBorders.getCenter(), Side.ALLIED);
        List<Squadron> otherAlliedSquads = PWCGContext.getInstance().getSquadronManager().getNearestSquadronsByRole(campaign,
                        closestAlliedFrontPosition, 3, initialSquadronSearchRadiusKey, acceptableRoles, Side.ALLIED, campaign.getDate());

        Coordinate closestAxisFrontPosition = frontLinesForMap.findClosestFrontCoordinateForSide(missionBorders.getCenter(), Side.AXIS);
        List<Squadron> otherAxisSquads = PWCGContext.getInstance().getSquadronManager().getNearestSquadronsByRole(campaign,
                        closestAxisFrontPosition, 3, initialSquadronSearchRadiusKey, acceptableRoles, Side.AXIS, campaign.getDate());

        alliedSquads.addAll(otherAlliedSquads);
        axisSquads.addAll(otherAxisSquads);
        determineSquadronAvailability();
    }

    private void findSquadronsForNightStrategicMission()
    {
        alliedSquads.clear();
        axisSquads.clear();
    }

    private void determineSquadronAvailability() throws PWCGException
    {
        alliedSquads = filterSquadrons(alliedSquads);
        axisSquads = filterSquadrons(axisSquads);
    }

    private List<Squadron> filterSquadrons(List<Squadron> squadronsToBeEvaluated) throws PWCGException
    {
        List<Squadron> acceptedSquadrons = new ArrayList<>();
        for (Squadron squadron : squadronsToBeEvaluated)
        {
            if (Squadron.isPlayerSquadron(campaign, squadron.getSquadronId()))
            {
                continue;
            }

            if (isSquadronPersonnelDepleted(squadron))
            {
                continue;
            }

            if (!shouldFlyDayNight(squadron))
            {
                continue;
            }

            acceptedSquadrons.add(squadron);
        }

        return acceptedSquadrons;
    }

    private boolean shouldFlyDayNight(Squadron squadron) throws PWCGException
    {
        
        if ((squadron.getNightOdds(campaign.getDate()) <= 0) && (mission.isNightMission()))
        {
            return false;
        }
        else if ((squadron.getNightOdds(campaign.getDate()) >= 100) && (!mission.isNightMission()))
        {
            return false;
        }
        else if (mission.isNightMission())
        {
            int roll = RandomNumberGenerator.getRandom(100);
            if (roll >= squadron.getNightOdds(campaign.getDate()))
            {
                return false;
            }
        }
        
        return true;
    }
    
    private boolean isSquadronPersonnelDepleted(Squadron squadron) throws PWCGException
    {
        SquadronMembers squadronMembers = SquadronMemberFilter.filterActiveAIAndPlayerAndAcesNoWounded(campaign.getPersonnelManager()
                        .getSquadronPersonnel(squadron.getSquadronId()).getSquadronMembersWithAces().getSquadronMemberCollection(),
                        campaign.getDate());
        int numSquadronMembers = squadronMembers.getSquadronMemberList().size();
        if (numSquadronMembers < 6)
        {
            return true;
        }

        if (numSquadronMembers > 10)
        {
            return false;
        }

        int diceRoll = RandomNumberGenerator.getRandom(10);
        if (numSquadronMembers < diceRoll)
        {
            return true;
        }

        return false;
    }

    public List<Squadron> getAlliedSquads()
    {
        return alliedSquads;
    }

    public List<Squadron> getAxisSquads()
    {
        return axisSquads;
    }
}
