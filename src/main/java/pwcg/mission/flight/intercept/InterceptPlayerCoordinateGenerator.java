package pwcg.mission.flight.intercept;

import pwcg.core.exception.PWCGException;
import pwcg.mission.flight.FlightInformation;
import pwcg.mission.target.TargetDefinition;
import pwcg.mission.target.TargetBuilder;

public class InterceptPlayerCoordinateGenerator
{
    private FlightInformation flightInformation;

    public InterceptPlayerCoordinateGenerator(FlightInformation flightInformation)
    {
        this.flightInformation = flightInformation;
    }
    
    public TargetDefinition createTargetCoordinates() throws PWCGException
    {
        TargetBuilder targetDefinitionBuilder = new TargetBuilder(flightInformation);
        TargetDefinition targetDefinition = targetDefinitionBuilder.buildTargetDefinition();
        return targetDefinition;
    }
}
