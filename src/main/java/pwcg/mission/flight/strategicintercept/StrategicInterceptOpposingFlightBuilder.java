package pwcg.mission.flight.strategicintercept;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pwcg.campaign.squadron.Squadron;
import pwcg.core.exception.PWCGException;
import pwcg.mission.flight.FlightBuildInformation;
import pwcg.mission.flight.FlightInformationFactory;
import pwcg.mission.flight.FlightTypes;
import pwcg.mission.flight.IFlight;
import pwcg.mission.flight.IFlightInformation;
import pwcg.mission.flight.bomb.BombingFlight;
import pwcg.mission.flight.escort.VirtualEscortFlightBuilder;
import pwcg.mission.target.ITargetDefinitionBuilder;
import pwcg.mission.target.TargetDefinition;
import pwcg.mission.target.TargetDefinitionBuilderFactory;

public class StrategicInterceptOpposingFlightBuilder
{
    private IFlightInformation playerFlightInformation;
    private TargetDefinition playerTargetDefinition;
    private StrategicInterceptOpposingFlightSquadronChooser opposingFlightSquadronChooser;

    public StrategicInterceptOpposingFlightBuilder(IFlightInformation playerFlightInformation, TargetDefinition playerTargetDefinition)
    {
        this.playerFlightInformation = playerFlightInformation;
        this.playerTargetDefinition = playerTargetDefinition;
        opposingFlightSquadronChooser = new StrategicInterceptOpposingFlightSquadronChooser(playerFlightInformation);
    }

    public List<IFlight> buildOpposingFlights() throws PWCGException
    {
        List<IFlight> opposingFlights = new ArrayList<>();
        
        List<Squadron> opposingBomberSquadrons = opposingFlightSquadronChooser.getOpposingBomberSquadron();
        Collections.shuffle(opposingBomberSquadrons); 
        if (opposingBomberSquadrons.size() > 0)
        {
            IFlight opposingBomberFlight = createBomberFlight(opposingBomberSquadrons.get(0));
            if (opposingBomberFlight != null)
            {
                opposingFlights.add(opposingBomberFlight);

                IFlight escortForAiFlight = createOpposingEscortFlights(opposingBomberFlight);
                if (escortForAiFlight != null)
                {
                    opposingFlights.add(escortForAiFlight);
                }
            }
        }
        return opposingFlights;
    }
    
    private IFlight createBomberFlight(Squadron opposingBomberSquadron) throws PWCGException
    {
        IFlightInformation opposingFlightInformation = buildOpposingFlightInformation(opposingBomberSquadron);
        TargetDefinition opposingTargetDefinition = buildOpposingTargetDefintion(opposingFlightInformation);    
        IFlight opposingFlight = new BombingFlight(opposingFlightInformation, opposingTargetDefinition);
        opposingFlight.createFlight();
        return opposingFlight;
    }

    private IFlightInformation buildOpposingFlightInformation(Squadron opposingSquadron) throws PWCGException
    {
        boolean isPlayerFlight = false;
        FlightBuildInformation flightBuildInformation = new FlightBuildInformation(this.playerFlightInformation.getMission(), opposingSquadron, isPlayerFlight);
        IFlightInformation opposingFlightInformation = FlightInformationFactory.buildFlightInformation(flightBuildInformation, FlightTypes.STRATEGIC_BOMB);
        return opposingFlightInformation;
    }

    private TargetDefinition buildOpposingTargetDefintion(IFlightInformation opposingFlightInformation) throws PWCGException
    {
        ITargetDefinitionBuilder targetDefinitionBuilder = TargetDefinitionBuilderFactory.createFlightTargetDefinitionBuilder(opposingFlightInformation);
        TargetDefinition opposingTargetDefinition =  targetDefinitionBuilder.buildTargetDefinition();
        opposingTargetDefinition.setTargetPosition(playerTargetDefinition.getTargetPosition());
        return opposingTargetDefinition;
    }

    private IFlight createOpposingEscortFlights(IFlight escortedFlight) throws PWCGException
    {
        VirtualEscortFlightBuilder virtualEscortFlightBuilder = new VirtualEscortFlightBuilder();
        IFlight escortForAiFlight = virtualEscortFlightBuilder.createVirtualEscortFlight(escortedFlight);
        return escortForAiFlight;
    }
}
