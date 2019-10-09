package pwcg.product.fc.airfield;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pwcg.campaign.Campaign;
import pwcg.campaign.api.IAirfield;
import pwcg.campaign.api.IStaticPlane;
import pwcg.campaign.context.PWCGContext;
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

public class FCAirfield extends FixedPosition implements IAirfield, Cloneable
{
    private List<Runway> runways = new ArrayList<>();
	private AirfieldObjects airfieldObjects = new AirfieldObjects();

	public FCAirfield ()
	{
		deleteAfterDeath = 0;
	}

	@Override
    public FCAirfield copy()
	{
		FCAirfield clone = new FCAirfield();
		
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

    public List<Runway> getAllRunways()
    {
        return runways;
    }

    private PWCGLocation getRunwayStart() throws PWCGException
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
	public PWCGLocation getTakeoffLocation() throws PWCGException {
		return getRunwayStart();
	}

	@Override
	public PWCGLocation getLandingLocation() throws PWCGException {
		return getRunwayStart();
	}

	@Override
	public PWCGLocation getParkingLocation() throws PWCGException {
		Runway runway = selectRunway();

		if (runway == null)
			return this;

		return runway.parkingLocation;
	}

	@Override
	public PWCGLocation getFakeAirfieldLocation() throws PWCGException {
		Runway runway = selectRunway();

		PWCGLocation loc = new PWCGLocation();
		Coordinate pos = new Coordinate();
		pos.setXPos((runway.startPos.getXPos() + runway.endPos.getXPos()) / 2.0);
		pos.setYPos((runway.startPos.getYPos() + runway.endPos.getYPos()) / 2.0);
		pos.setZPos((runway.startPos.getZPos() + runway.endPos.getZPos()) / 2.0);
		loc.setPosition(pos);
        double runwayOrientation = MathUtils.calcAngle(runway.startPos, runway.endPos);
        // BoX seems to like the runway orientation to be an odd integer
        runwayOrientation = Math.rint(runwayOrientation);
        if ((runwayOrientation % 2) == 0)
            runwayOrientation = MathUtils.adjustAngle(runwayOrientation, 1.0);
        loc.setOrientation(new Orientation(runwayOrientation));
		return loc;
	}

	private Runway selectRunway() throws PWCGException
	{
		// Note: wind direction is as specified in mission file, i.e. heading wind blows to
		// Adjust by 180 degrees to give standard direction
		double windDirection = MathUtils.adjustAngle(PWCGContext.getInstance().getCurrentMap().getMapWeather().getWindDirection(), 180);

		double bestOffset = 1000.0;
		Runway bestRunway = null;
		boolean invertRunway = false;

		for (Runway r : runways) {
			double offset = MathUtils.calcNumberOfDegrees(windDirection, r.getHeading());
			boolean invert = false;

			if (offset > 90.0) {
				offset = 180 - offset;
				invert = true;
			}

			if (offset < bestOffset) {
				bestOffset = offset;
				bestRunway = r;
				invertRunway = invert;
			}
		}

		if (invertRunway)
			return bestRunway.invert();
		else
			return bestRunway.copy();
	}

	private String getChartPoint(int ptype, Coordinate point) throws PWCGException
	{
		double xpos = point.getXPos() - getFakeAirfieldLocation().getPosition().getXPos();
		double ypos = point.getZPos() - getFakeAirfieldLocation().getPosition().getZPos();

		double angle = Math.toRadians(-getFakeAirfieldLocation().getOrientation().getyOri());

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

	public String getChart() throws PWCGException
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

	@Override
	public boolean isNearRunwayOrTaxiway(Coordinate pos) throws PWCGException {
		if (runways.size() == 0)
		{
			double runwayOrientation = getTakeoffLocation().getOrientation().getyOri();
			Coordinate startOfRunway = getTakeoffLocation().getPosition();
			Coordinate endOfRunway = MathUtils.calcNextCoord(getTakeoffLocation().getPosition(), runwayOrientation, 2000.0);

			return MathUtils.distFromLine(startOfRunway, endOfRunway, pos) < 120;
		} else {
			for (Runway r : runways)
			{
				Coordinate extendedRunwayStart = MathUtils.calcNextCoord(r.startPos, MathUtils.adjustAngle(r.getHeading(), 180), 300);
				Coordinate extendedRunwayEnd = MathUtils.calcNextCoord(r.endPos, r.getHeading(), 300);
				if (MathUtils.distFromLine(extendedRunwayStart, extendedRunwayEnd, pos) < 120)
					return true;

				Coordinate prevPoint = r.parkingLocation.getPosition();
				for (Coordinate p : r.taxiToStart) {
					if (MathUtils.distFromLine(prevPoint, p, pos) < 50)
						return true;
					prevPoint = p;
				}
				if (MathUtils.distFromLine(prevPoint, r.startPos, pos) < 50)
					return true;

				prevPoint = r.endPos;
				for (Coordinate p : r.taxiFromEnd) {
					if (MathUtils.distFromLine(prevPoint, p, pos) < 50)
						return true;
					prevPoint = p;
				}
				if (MathUtils.distFromLine(prevPoint, r.parkingLocation.getPosition(), pos) < 50)
					return true;

				double parkingOrientation = MathUtils.adjustAngle(r.parkingLocation.getOrientation().getyOri(), 90);
				Coordinate parkingEnd = MathUtils.calcNextCoord(r.parkingLocation.getPosition(), parkingOrientation, 300);
				if (MathUtils.distFromLine(r.parkingLocation.getPosition(), parkingEnd, pos) < 80)
					return true;
			}
		}

		return false;
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

		public double getHeading() throws PWCGException
		{
			return MathUtils.calcAngle(startPos, endPos);
		}

		public Runway invert()
		{
			Runway inv = new Runway();

			inv.startPos = endPos;
			inv.endPos = startPos;
			inv.parkingLocation = parkingLocation;

			for (Coordinate t : taxiFromEnd)
				inv.taxiToStart.add(0, t.copy());
			for (Coordinate t : taxiToStart)
				inv.taxiFromEnd.add(0, t.copy());

			return inv;
		}
	}

	static public class AirfieldDescriptor extends PWCGLocation
	{
		public List<Runway> runways = new ArrayList<>();
	}
}
