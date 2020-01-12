package pwcg.mission;

import java.util.ArrayList;
import java.util.List;

import pwcg.campaign.Campaign;
import pwcg.campaign.factory.PWCGFlightFactoryFactory;
import pwcg.campaign.squadron.Squadron;
import pwcg.campaign.utils.TestDriver;
import pwcg.core.exception.PWCGException;
import pwcg.mission.flight.IFlight;
import pwcg.mission.flight.FlightTypes;
import pwcg.mission.flight.factory.FlightFactory;
import pwcg.mission.flight.factory.IFlightTypeFactory;
import pwcg.mission.flight.factory.NightFlightTypeConverter;

public class AiFlightBuilder
{
    private Campaign campaign;
    private Mission mission;

    AiFlightBuilder (Campaign campaign, Mission mission)
    {
        this.campaign = campaign;
        this.mission = mission;
    }
    
    private List<IFlight> missionFlights = new ArrayList<IFlight>();

    public List<IFlight> createAiFlights() throws PWCGException 
    {
        TestDriver testDriver = TestDriver.getInstance();
        if (testDriver.isCreatePlayerOnly())
        {
            return missionFlights;
        }
        
        List<Squadron> aiSquadronsForMission = determineParticipatingSquadrons();
        for (Squadron squadron : aiSquadronsForMission)
        {
            FlightTypes flightType = determineFlightType(squadron);
            flightType = NightFlightTypeConverter.getFlightType(mission, flightType);

            IFlight flight = buildFlight(flightType, squadron);
            if (flight != null)
            {
                missionFlights.add(flight);
            }
        }
        return missionFlights;
    }
    
    private List<Squadron> determineParticipatingSquadrons() throws PWCGException
    {
        AiSquadronIncluder aiSquadronIncluder = new AiSquadronIncluder(mission);
        List<Squadron> aiSquadronsForMission = aiSquadronIncluder.decideSquadronsForMission();
        
        return aiSquadronsForMission;
    }    

    private FlightTypes determineFlightType(Squadron squadron) throws PWCGException 
    {
        IFlightTypeFactory flightTypeFactory = PWCGFlightFactoryFactory.createFlightFactory(campaign);
        boolean isPlayerFlight = false;
        FlightTypes flightType = flightTypeFactory.getFlightType(squadron, isPlayerFlight);
        return flightType;
    }

    private IFlight buildFlight(FlightTypes flightType, Squadron squadron) throws PWCGException
    {
        FlightFactory flightFactory = new FlightFactory(campaign);
        boolean isPlayerFlight = false;
        IFlight flight = flightFactory.buildFlight(mission, squadron, flightType, isPlayerFlight);
        return flight;        
    }

}
