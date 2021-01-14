package pwcg.mission.flight.balloondefense;

import java.util.ArrayList;
import java.util.List;

import pwcg.campaign.squadron.Squadron;
import pwcg.core.exception.PWCGException;
import pwcg.core.location.Coordinate;
import pwcg.mission.flight.FlightBuildInformation;
import pwcg.mission.flight.FlightInformationFactory;
import pwcg.mission.flight.FlightTypes;
import pwcg.mission.flight.IFlight;
import pwcg.mission.flight.FlightInformation;
import pwcg.mission.flight.balloonBust.BalloonBustFlight;
import pwcg.mission.target.TargetDefinition;
import pwcg.mission.target.TargetType;

public class BalloonDefenseOpposingFlightBuilder
{
    private FlightInformation playerFlightInformation;
    private Coordinate balloonUnitPosition;

    public BalloonDefenseOpposingFlightBuilder(FlightInformation playerFlightInformation, Coordinate balloonUnitPosition)
    {
        this.playerFlightInformation = playerFlightInformation;
        this.balloonUnitPosition = balloonUnitPosition;
    }

    public List<IFlight> buildOpposingFlights() throws PWCGException
    {
        BalloonDefenseOpposingFlightSquadronChooser opposingSquadronChooser = new BalloonDefenseOpposingFlightSquadronChooser(playerFlightInformation);
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
        IFlight opposingBalloonBustFlight = null;
        String opposingFieldName = opposingSquadron.determineCurrentAirfieldName(playerFlightInformation.getCampaign().getDate());
        if (opposingFieldName != null)
        {
            opposingBalloonBustFlight = buildOpposingFlight(opposingSquadron);
        }
        return opposingBalloonBustFlight;
    }

    private IFlight buildOpposingFlight(Squadron opposingSquadron) throws PWCGException 
    {
        FlightInformation opposingFlightInformation = buildOpposingFlightInformation(opposingSquadron);
        TargetDefinition opposingTargetDefinition = buildOpposingTargetDefintion(opposingFlightInformation);

        BalloonBustFlight opposingFlight = new BalloonBustFlight(opposingFlightInformation, opposingTargetDefinition);
        opposingFlight.createFlight();
        return opposingFlight;
    }

    private FlightInformation buildOpposingFlightInformation(Squadron opposingSquadron) throws PWCGException
    {
        boolean isPlayerFlight = false;
        FlightBuildInformation flightBuildInformation = new FlightBuildInformation(this.playerFlightInformation.getMission(), opposingSquadron, isPlayerFlight);        
        FlightInformation opposingFlightInformation = FlightInformationFactory.buildFlightInformation(flightBuildInformation, FlightTypes.BALLOON_BUST);
        return opposingFlightInformation;
    }

    private TargetDefinition buildOpposingTargetDefintion(FlightInformation opposingFlightInformation) throws PWCGException
    {
        TargetDefinition opposingTargetDefinition =  new TargetDefinition(TargetType.TARGET_AIR, balloonUnitPosition, opposingFlightInformation.getCountry());
        return opposingTargetDefinition;
    }
}
