package pwcg.mission.flight.balloonBust;

import java.util.ArrayList;
import java.util.List;

import pwcg.campaign.squadron.Squadron;
import pwcg.core.exception.PWCGException;
import pwcg.mission.flight.FlightBuildInformation;
import pwcg.mission.flight.FlightInformationFactory;
import pwcg.mission.flight.FlightTypes;
import pwcg.mission.flight.IFlight;
import pwcg.mission.flight.IFlightInformation;
import pwcg.mission.flight.balloondefense.BalloonDefenseFlight;
import pwcg.mission.ground.org.IGroundUnitCollection;
import pwcg.mission.target.ITargetDefinitionBuilder;
import pwcg.mission.target.TargetDefinition;
import pwcg.mission.target.TargetDefinitionBuilderFactory;

public class BalloonBustOpposingFlightBuilder
{
    private IFlightInformation playerFlightInformation;
    private IGroundUnitCollection balloonUnit;

    public BalloonBustOpposingFlightBuilder(IFlightInformation playerFlightInformation, IGroundUnitCollection balloonUnit)
    {
        this.playerFlightInformation = playerFlightInformation;
        this.balloonUnit = balloonUnit;
    }

    public List<IFlight> buildOpposingFlights() throws PWCGException
    {
        BalloonBustOpposingFlightSquadronChooser opposingSquadronChooser = new BalloonBustOpposingFlightSquadronChooser(playerFlightInformation);
        List<Squadron> opposingSquadrons = opposingSquadronChooser.getOpposingSquadrons();            
        return createOpposingFlights(opposingSquadrons);
    }
    
    private List<IFlight> createOpposingFlights(List<Squadron> opposingSquadrons) throws PWCGException
    {
        List<IFlight> opposingFlights = new ArrayList<>();
        for (Squadron squadron : opposingSquadrons)
        {
            IFlight opposingFlight = createOpposingFlight(squadron);
            if (opposingFlight != null)
            {
                opposingFlights.add(opposingFlight);
            }
        }
        return opposingFlights;
    }

    private IFlight createOpposingFlight(Squadron opposingSquadron) throws PWCGException
    {
        IFlight balloonBustOpposingFlight = null;
        String opposingFieldName = opposingSquadron.determineCurrentAirfieldName(playerFlightInformation.getCampaign().getDate());
        if (opposingFieldName != null)
        {
            balloonBustOpposingFlight = buildOpposingFlight(opposingSquadron);
        }
        return balloonBustOpposingFlight;
    }

    private IFlight buildOpposingFlight(Squadron opposingSquadron) throws PWCGException 
    {
        IFlightInformation opposingFlightInformation = buildOpposingFlightInformation(opposingSquadron);
        TargetDefinition opposingTargetDefinition = buildOpposingTargetDefintion(opposingFlightInformation);
                
        BalloonDefenseFlight opposingFlight = new BalloonDefenseFlight(opposingFlightInformation, opposingTargetDefinition);
        opposingFlight.createFlight();
        return opposingFlight;
    }

    private IFlightInformation buildOpposingFlightInformation(Squadron opposingSquadron) throws PWCGException
    {
        boolean isPlayerFlight = false;
        FlightBuildInformation flightBuildInformation = new FlightBuildInformation(this.playerFlightInformation.getMission(), opposingSquadron, isPlayerFlight);        
        IFlightInformation opposingFlightInformation = FlightInformationFactory.buildFlightInformation(flightBuildInformation, FlightTypes.BALLOON_DEFENSE);
        return opposingFlightInformation;
    }

    private TargetDefinition buildOpposingTargetDefintion(IFlightInformation opposingFlightInformation) throws PWCGException
    {
        ITargetDefinitionBuilder targetDefinitionBuilder = TargetDefinitionBuilderFactory.createFlightTargetDefinitionBuilder(opposingFlightInformation);
        TargetDefinition opposingTargetDefinition =  targetDefinitionBuilder.buildTargetDefinition();
        opposingTargetDefinition.setTargetPosition(balloonUnit.getPosition());
        return opposingTargetDefinition;
    }
}
