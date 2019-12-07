package pwcg.mission.flight.ferry;

import pwcg.campaign.api.IAirfield;
import pwcg.campaign.context.PWCGContext;
import pwcg.core.exception.PWCGException;
import pwcg.mission.MissionBeginUnit;
import pwcg.mission.flight.Flight;
import pwcg.mission.flight.FlightInformation;
import pwcg.mission.flight.IFlightPackage;

public class FerryPackage implements IFlightPackage

{private FlightInformation flightInformation;

    public FerryPackage(FlightInformation flightInformation)
    {
        this.flightInformation = flightInformation;
    }

	public Flight createPackage () throws PWCGException 
	{
	    IAirfield fromAirfield = PWCGContext.getInstance().getCurrentMap().getAirfieldManager().getAirfield(flightInformation.getCampaign().getSquadronMoveEvent().getLastAirfield());
        MissionBeginUnit missionBeginUnit = new MissionBeginUnit(fromAirfield.getPosition().copy());        
		FerryFlight ferry = new FerryFlight (flightInformation, missionBeginUnit);
		ferry.createUnitMission();
		return ferry;
	}
}
