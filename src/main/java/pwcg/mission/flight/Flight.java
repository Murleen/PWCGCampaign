package pwcg.mission.flight;

import java.io.BufferedWriter;

import pwcg.campaign.Campaign;
import pwcg.campaign.squadron.Squadron;
import pwcg.core.exception.PWCGException;
import pwcg.core.location.Coordinate;
import pwcg.mission.Mission;
import pwcg.mission.MissionSkinGenerator;
import pwcg.mission.flight.plane.PlaneMcu;
import pwcg.mission.flight.waypoint.IVirtualWaypointPackage;
import pwcg.mission.flight.waypoint.IWaypointPackage;
import pwcg.mission.flight.waypoint.VirtualWaypointPackage;
import pwcg.mission.flight.waypoint.WaypointPackage;
import pwcg.mission.target.TargetDefinition;

public abstract class Flight implements IFlight
{
    private IFlightInformation flightInformation;
    private FlightPlanes flightPlanes;
    private ILinkedFlights linkedFlights = new LinkedFlights();
    private FlightPlayerContact flightPlayerContact = new FlightPlayerContact();
    private IWaypointPackage waypointPackage;
    private VirtualWaypointPackage virtualWaypointPackage;
    private TargetDefinition targetDefinition;

    public Flight(IFlightInformation flightInformation, TargetDefinition targetDefinition)
    {
        this.flightInformation = flightInformation;
        this.targetDefinition = targetDefinition;
    }
    
    public void initialize(IFlight flight) throws PWCGException
    {
        this.flightPlanes = new FlightPlanes(flight);
        this.waypointPackage = new WaypointPackage(flight);
        this.virtualWaypointPackage = new VirtualWaypointPackage(flight);
    }

    public void write(BufferedWriter writer) throws PWCGException 
    {
        flightPlanes.write(writer);
        if (flightInformation.isVirtual())
        {
            virtualWaypointPackage.write(writer);
        }
        else
        {
            waypointPackage.write(writer);
        }
        
        writeLinkedFlights(writer);
    }

    private void writeLinkedFlights(BufferedWriter writer) throws PWCGException
    {
        for (IFlight flight : linkedFlights.getLinkedFlights())
        {
            flight.write(writer);
            for (IFlight linkedFlight : flight.getLinkedFlights().getLinkedFlights())
            {
                linkedFlight.write(writer);
            }
        }
    }

    public IFlightInformation getFlightInformation()
    {
        return flightInformation;
    }

    public IFlightPlanes getFlightPlanes()
    {
        return flightPlanes;
    }

    public IWaypointPackage getWaypointPackage()
    {
        return waypointPackage;
    }

    public ILinkedFlights getLinkedFlights()
    {
        return linkedFlights;
    }

    public Coordinate getFlightHomePosition() throws PWCGException
    {
        return flightInformation.getFlightHomePosition();
    }

    public Campaign getCampaign()
    {
        return flightInformation.getCampaign();
    }

    public IFlightPlayerContact getFlightPlayerContact()
    {
        return flightPlayerContact;
    }

    @Override
    public void finalizeFlight() throws PWCGException
    {
        flightPlanes.finalize();
        PlaneMcu flightLeader = flightPlanes.getFlightLeader();
        waypointPackage.finalize(flightLeader);
        
        for (IFlight linkedFlight : linkedFlights.getLinkedFlights())
        {
            linkedFlight.finalizeFlight();
        }

        if (flightInformation.isVirtual())
        {
            virtualWaypointPackage.buildVirtualWaypoints();
            virtualWaypointPackage.addDelayForPlayerDelay(flightInformation.getMission());
        }        

        MissionSkinGenerator skinGenerator = new MissionSkinGenerator();
        skinGenerator.assignSkinsForFlight(this);
    }

    public IVirtualWaypointPackage getVirtualWaypointPackage()
    {
        return virtualWaypointPackage;
    }
    
    @Override
    public void overrideFlightCruisingSpeedForEscort(int cruisingSpeed)
    {
        flightInformation.setCruisingSpeed(cruisingSpeed);
    }

    @Override
    public int getFlightCruisingSpeed()
    {
        if (flightInformation.getFlightCruisingSpeed() > 0)
        {
            return flightInformation.getFlightCruisingSpeed();
        }
        else
        {
            return flightPlanes.getFlightCruisingSpeed();
        }
    }

    @Override
    public Mission getMission()
    {
        return flightInformation.getMission();
    }

    @Override
    public int getFlightId()
    {
        return flightInformation.getFlightId();
    }

    @Override
    public Squadron getSquadron()
    {
        return flightInformation.getSquadron();
    }

    @Override
    public FlightTypes getFlightType()
    {
        return flightInformation.getFlightType();
    }

    @Override
    public boolean isPlayerFlight()
    {
        return flightInformation.isPlayerFlight();
    }

    @Override
    public boolean isFlightHasFighterPlanes()
    {
        return flightPlanes.isFlightHasFighterPlanes();
    }

    @Override
    public double getClosestContactWithPlayerDistance()
    {
        return flightPlayerContact.getClosestContactWithPlayerDistance();
    }

    @Override
    public TargetDefinition getTargetDefinition()
    {
        return targetDefinition;
    }    
}
