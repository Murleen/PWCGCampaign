package pwcg.campaign.ww2.airfield;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pwcg.campaign.Campaign;
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
import pwcg.core.utils.MathUtils;
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
    public void addAirfieldObjects(Campaign campaign) throws PWCGException 
    {
        if (!(createCountry(campaign.getDate()).isNeutral()))
        {
	    	AirfieldObjectPlacer airfieldObjectPlacer = new AirfieldObjectPlacer(campaign, this);
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
    public PWCGLocation getPlanePosition() throws PWCGException
    {
		Runway runway = selectRunway();

		if (runway != null) {
			double runwayOrientation = MathUtils.calcAngle(runway.startPos, runway.endPos);

			PWCGLocation loc = new PWCGLocation();
			loc.setPosition(runway.startPos);
			loc.setOrientation(new Orientation(runwayOrientation));
			return loc;
		} else {
			return this;
		}
    }
        
	@Override
	public PWCGLocation getLandingLocation() throws PWCGException {
		return getPlanePosition();
	}

	// TODO: Select runway based on wind direction
	private Runway selectRunway()
	{
		if (runways.size() > 0)
			return runways.get(0);

		return null;
	}

	private String getChartPoint(int ptype, Coordinate point)
	{
		double xpos = point.getXPos() - getPosition().getXPos();
		double ypos = point.getZPos() - getPosition().getZPos();

		double angle = Math.toRadians(-this.getPlaneOrientation());

		double rxpos = Math.cos(angle) * xpos - Math.sin(angle) * ypos;
		double rypos = Math.cos(angle) * ypos + Math.sin(angle) * xpos;

		String pos;
		pos  = "      Point\n";
		pos += "      {\n";
		pos += "        Type = " + ptype + ";\n";
		pos += "        X = " + Coordinate.format(rxpos) + ";\n";
		pos += "        Y = " + Coordinate.format(rypos) + ";\n";
		pos += "      }\n";
		return pos;
	}

	public String getChart()
	{
		Runway runway = selectRunway();

		if (runway == null)
			return "";

		String chart;

		chart  = "    Chart\n";
		chart += "    {\n";
		chart += getChartPoint(0, runway.parkingLocation.getPosition());
		for (Coordinate pos : runway.taxiToStart)
			chart += getChartPoint(1, pos);
		chart += getChartPoint(2, runway.startPos);
		chart += getChartPoint(2, runway.endPos);
		for (Coordinate pos : runway.taxiFromEnd)
			chart += getChartPoint(1, pos);
		chart += getChartPoint(0, runway.parkingLocation.getPosition());
		chart += "    }\n";

		return chart;
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
