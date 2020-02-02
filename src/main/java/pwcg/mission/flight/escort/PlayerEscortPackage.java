package pwcg.mission.flight.escort;

import pwcg.core.exception.PWCGException;
import pwcg.core.exception.PWCGMissionGenerationException;
import pwcg.mission.flight.IFlight;
import pwcg.mission.flight.IFlightInformation;
import pwcg.mission.flight.IFlightPackage;

public class PlayerEscortPackage implements IFlightPackage
{
    private IFlightInformation playerFlightInformation;    
    public PlayerEscortPackage(IFlightInformation playerFlightInformation)
    {
        this.playerFlightInformation = playerFlightInformation;
    }

    public IFlight createPackage () throws PWCGException 
    {
	    if(!playerFlightInformation.isPlayerFlight())
        {
	        throw new PWCGMissionGenerationException ("Attempt to create non player escort package");
        }
	    
	    PlayerEscortedFlightBuilder escortedFlightBuilder = new PlayerEscortedFlightBuilder(playerFlightInformation);
	    PlayerEscortedFlight escortedFlight = escortedFlightBuilder.createEscortedFlight();

		PlayerIsEscortFlight playerEscort = new PlayerIsEscortFlight(playerFlightInformation, escortedFlight);
		playerEscort.createFlight();
		
		PlayerEscortFlightConnector connector = new PlayerEscortFlightConnector(playerEscort, escortedFlight);
		connector.connectEscortAndEscortedFlight();
		
		playerEscort.getLinkedFlights().addLinkedFlight(escortedFlight);
		
		return playerEscort;
	}
}
