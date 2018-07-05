package pwcg.campaign.ww2.airfield;

import java.io.BufferedWriter;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import pwcg.campaign.api.IAirfield;
import pwcg.campaign.api.IStaticPlane;
import pwcg.campaign.group.FixedPosition;
import pwcg.campaign.group.airfield.AirfieldObjectPlacer;
import pwcg.campaign.group.airfield.AirfieldObjects;
import pwcg.core.exception.PWCGException;
import pwcg.core.location.Coordinate;
import pwcg.core.location.Orientation;
import pwcg.core.location.PWCGLocation;
import pwcg.core.utils.DateUtils;
import pwcg.mission.ground.unittypes.GroundUnitSpawning;
import pwcg.mission.ground.vehicle.IVehicle;

public class BoSAirfield extends FixedPosition implements IAirfield, Cloneable
{
    private List<Runway> runways = new ArrayList<>();
	private AirfieldObjects airfieldObjects = new AirfieldObjects();

	public BoSAirfield ()
	{
		deleteAfterDeath = 0;
	}

	@Override
    public BoSAirfield copy()
	{
		BoSAirfield clone = new BoSAirfield();
		
	    clone.airfieldObjects = new AirfieldObjects();

        for (Runway r : runways)
            clone.runways.add(r.copy());

		super.clone(clone);
        
		return clone;
	}

	@Override
    public void write(BufferedWriter writer) throws PWCGException
	{
        // Write any vehicles on the airfield
        for (IVehicle airfieldObject : airfieldObjects.getAirfieldObjects())
        {
            airfieldObject.write(writer);
        }
        
        // Write airfield AAA
        for (GroundUnitSpawning airfieldAAA : airfieldObjects.getAaaForAirfield())
        {
            airfieldAAA.write(writer);
        }
        
        // Write any static planes
        for (IStaticPlane staticPlane : airfieldObjects.getStaticPlanes())
        {
            staticPlane.write(writer);
        }
	}

	@Override
    public String toString()
	{
		StringBuffer output = new StringBuffer("");
		output.append("Airfield\n");
		output.append("{\n");
		
		output.append("  Name = \"" + name + "\";");
		output.append("  Index = " + index + ";\n");
		output.append("  XPos = " + position.getXPos() + ";\n");
		output.append("  YPos = " + position.getYPos() + ";\n");
		output.append("  ZPos = " + position.getZPos() + ";\n");
		output.append("  YOri = " + orientation.getyOri() + ";\n");
		
		output.append("  Country = " + determineCountry().getCountryName() + ";\n");

		output.append("}\n");
		output.append("\n");
		output.append("\n");
		
		return output.toString();
	}

    @Override
    public void initializeAirfieldFromLocation(PWCGLocation planePosition)
    {
        this.position = planePosition.getPosition().copy();
        this.orientation = planePosition.getOrientation().copy();
        this.name = planePosition.getName();
    }

    public void initializeAirfieldFromDescriptor(AirfieldDescriptor desc)
    {
        this.position = desc.getPosition().copy();
        this.orientation = desc.getOrientation().copy();
        this.name = desc.getName();
        for (Runway r : desc.runways)
            this.runways.add(r.copy());
    }

    public double getPlaneOrientation() 
    {
        // BoS does not use airfield orientation
        return orientation.getyOri();
    }

    @Override
    public void addAirfieldObjects(Date date) throws PWCGException 
    {
        if (!(createCountry(date).isNeutral()))
        {
	    	AirfieldObjectPlacer airfieldObjectPlacer = new AirfieldObjectPlacer(this, date);
	    	airfieldObjects = airfieldObjectPlacer.createAirfieldObjectsWithEmptySpace();
        }
    }

    @Override
    public boolean isGroup()
    {
        // BoS airfields are not real fields
        return false;
    }

    @Override
    public void setAAA(GroundUnitSpawning aaa)
    {
        // BoS airfields do not have AAA
    }

    @Override
    public Date getStartDate()
    {
        try
        {
            return DateUtils.getBeginningOfGame();
        }
        catch (PWCGException e)
        {
            e.printStackTrace();
        }
        
        return null;
    }

    @Override
    public PWCGLocation getPlanePosition()
    {
        return this;
    }
        
	static public class Runway
	{
		public Coordinate startPos;
		public Coordinate endPos;
		public PWCGLocation parkingLocation;

		public List<Coordinate> taxiToStart = new ArrayList<>();
		public List<Coordinate> taxiFromEnd = new ArrayList<>();

		public Runway copy()
		{
			Runway clone = new Runway();

			clone.startPos = startPos;
			clone.endPos = endPos;
			clone.parkingLocation = parkingLocation;

			for (Coordinate t : taxiToStart)
				clone.taxiToStart.add(t.copy());
			for (Coordinate t : taxiFromEnd)
				clone.taxiFromEnd.add(t.copy());

			return clone;
		}
	}

	static public class AirfieldDescriptor extends PWCGLocation
	{
		public List<Runway> runways = new ArrayList<>();
	}
}
