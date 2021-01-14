package pwcg.mission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import pwcg.campaign.Campaign;
import pwcg.campaign.api.Side;
import pwcg.campaign.context.PWCGContext;
import pwcg.campaign.plane.Role;
import pwcg.campaign.squadron.Squadron;
import pwcg.core.exception.PWCGException;
import pwcg.core.utils.RandomNumberGenerator;
import pwcg.mission.flight.FlightInformation;

public class OpposingSquadronChooser
{
    private FlightInformation playerFlightInformation;
    private List<Role> opposingRoles = new ArrayList<>();
    private int numberOfOpposingFlights = 1;

    public OpposingSquadronChooser(FlightInformation playerFlightInformation, List<Role> opposingRoles, int numberOfOpposingFlights)
    {
        this.playerFlightInformation = playerFlightInformation;
        this.opposingRoles = opposingRoles;
        this.numberOfOpposingFlights = numberOfOpposingFlights;
    }

    public List<Squadron> getOpposingSquadrons() throws PWCGException
    {
        List<Squadron> viableOpposingSquads = getViableOpposingSquadrons();        
        if (viableOpposingSquads.size() <= numberOfOpposingFlights)
        {
            return viableOpposingSquads;
        }
        else
        {
            return selectOpposingSquadrons(viableOpposingSquads);            
        }
    }

    private List<Squadron> selectOpposingSquadrons(List<Squadron> viableOpposingSquads)
    {
        Map<Integer, Squadron> selectedOpposingSquads = new HashMap<>();
        HashSet<Integer> alreadyPicked = new HashSet<>();
        while (selectedOpposingSquads.size() < numberOfOpposingFlights)
        {
            int index= RandomNumberGenerator.getRandom(viableOpposingSquads.size());
            Squadron opposingSquadron = viableOpposingSquads.get(index);
            if (!selectedOpposingSquads.containsKey(opposingSquadron.getSquadronId()))
            {
                selectedOpposingSquads.put(opposingSquadron.getSquadronId(), opposingSquadron);
                alreadyPicked.add(index);
            }
        }
        return new ArrayList<>(selectedOpposingSquads.values());
    }

    private List<Squadron> getViableOpposingSquadrons() throws PWCGException
    {        
        Campaign campaign = playerFlightInformation.getCampaign();
        Side enemySide = playerFlightInformation.getSquadron().determineEnemySide();
        List<Squadron> viableOpposingSquads = PWCGContext.getInstance().getSquadronManager().getViableAiSquadronsForCurrentMapAndSideAndRole(campaign, opposingRoles, enemySide);
        return viableOpposingSquads;
    }
}
