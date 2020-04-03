package pwcg.mission.flight.escort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pwcg.campaign.context.PWCGContext;
import pwcg.campaign.plane.Role;
import pwcg.campaign.squadron.Squadron;
import pwcg.core.exception.PWCGException;
import pwcg.core.utils.RandomNumberGenerator;
import pwcg.mission.flight.FlightBuildInformation;
import pwcg.mission.flight.FlightInformationFactory;
import pwcg.mission.flight.FlightTypes;
import pwcg.mission.flight.IFlight;
import pwcg.mission.flight.IFlightInformation;

public class VirtualEscortFlightBuilder
{
    public IFlight createVirtualEscortFlight(IFlight escortedFlight) throws PWCGException 
    {
        List<Role> fighterRole = new ArrayList<Role>(Arrays.asList(Role.ROLE_FIGHTER));
        List<Squadron> friendlyFighterSquadrons = PWCGContext.getInstance().getSquadronManager().getNearestSquadronsByRole(
                escortedFlight.getCampaign(),
                escortedFlight.getFlightHomePosition(),
                1,
                50000.0,
                fighterRole,
                escortedFlight.getSquadron().determineSquadronCountry(escortedFlight.getCampaign().getDate()).getSide(), 
                escortedFlight.getCampaign().getDate());
        
        if (friendlyFighterSquadrons != null && friendlyFighterSquadrons.size() > 0)
        {
            IFlightInformation escortFlightInformation = createFlightInformation(escortedFlight, friendlyFighterSquadrons);            
            VirtualEscortFlight virtualEscortFlight = new VirtualEscortFlight(escortFlightInformation, escortedFlight);
            virtualEscortFlight.createFlight();
            return virtualEscortFlight;
        }
        
        return null;
    }

    private IFlightInformation createFlightInformation(IFlight escortedFlight, List<Squadron> friendlyFighterSquadrons) throws PWCGException
    {
        int index = RandomNumberGenerator.getRandom(friendlyFighterSquadrons.size());
        Squadron escortFighterSquadron = friendlyFighterSquadrons.get(index);
        
        boolean isPlayerFlight = false;
        FlightBuildInformation flightBuildInformation = new FlightBuildInformation(escortedFlight.getMission(), escortFighterSquadron, isPlayerFlight);
        
        IFlightInformation escortFlightInformation = FlightInformationFactory.buildFlightInformation(flightBuildInformation, FlightTypes.ESCORT);
        return escortFlightInformation;
    }

}
