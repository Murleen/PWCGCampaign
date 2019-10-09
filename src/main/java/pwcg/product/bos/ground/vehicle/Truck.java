package pwcg.product.bos.ground.vehicle;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

import pwcg.campaign.api.ICountry;
import pwcg.campaign.api.Side;
import pwcg.campaign.context.Country;
import pwcg.campaign.utils.IndexGenerator;
import pwcg.core.exception.PWCGException;
import pwcg.core.exception.PWCGIOException;
import pwcg.core.location.Coordinate;
import pwcg.core.location.Orientation;
import pwcg.mission.ground.vehicle.VehicleDefinition;
import pwcg.mission.mcu.McuTREntity;

class Truck extends Vehicle
{
    private TruckType truckType;

    public enum TruckType
    {
        TRUCK_CARGO,
        TRUCK_AAA,
        CAR;
    }
    
    private static final List<VehicleDefinition> germanTrucks = new ArrayList<VehicleDefinition>() 
    {
        private static final long serialVersionUID = 1L;
        {
            add(new VehicleDefinition("vehicles\\", "vehicles\\ford\\", "ford-g917", Country.GERMANY));
            add(new VehicleDefinition("vehicles\\", "vehicles\\ford\\", "ford-g917", Country.GERMANY));
            add(new VehicleDefinition("vehicles\\", "vehicles\\ford\\", "ford-g917", Country.GERMANY));
            add(new VehicleDefinition("vehicles\\", "vehicles\\opel\\", "opel-blitz", Country.GERMANY));
            add(new VehicleDefinition("vehicles\\", "vehicles\\opel\\", "opel-blitz", Country.GERMANY));
            add(new VehicleDefinition("vehicles\\", "vehicles\\opel\\", "opel-blitz", Country.GERMANY));
            add(new VehicleDefinition("vehicles\\", "vehicles\\opel\\", "opel-blitz", Country.GERMANY));
            add(new VehicleDefinition("vehicles\\", "vehicles\\opel\\", "opel-blitz", Country.GERMANY));
            add(new VehicleDefinition("vehicles\\", "vehicles\\sdkfz251\\", "sdkfz251-1c", Country.GERMANY));
            add(new VehicleDefinition("vehicles\\", "vehicles\\sdkfz251\\", "sdkfz251-1c", Country.GERMANY));
            add(new VehicleDefinition("vehicles\\", "vehicles\\sdkfz251\\", "sdkfz251-szf", Country.GERMANY));
        }
    };
    
    private static final List<VehicleDefinition> germanAAATrucks = new ArrayList<VehicleDefinition>() 
    {
        private static final long serialVersionUID = 1L;
        {
            add(new VehicleDefinition("vehicles\\", "vehicles\\sdkfz10-flak38\\", "sdkfz10-flak38", Country.GERMANY));
        }
    };
    
    private static final List<VehicleDefinition> germanCars = new ArrayList<VehicleDefinition>() 
    {
        private static final long serialVersionUID = 1L;
        {
            add(new VehicleDefinition("vehicles\\", "vehicles\\horch\\", "horch830", Country.GERMANY));
        }
    };

    private static final List<VehicleDefinition> russianTrucks = new ArrayList<VehicleDefinition>() 
    {
        private static final long serialVersionUID = 1L;
        {
            add(new VehicleDefinition("vehicles\\", "vehicles\\gaz\\", "gaz-aa", Country.RUSSIA));
            add(new VehicleDefinition("vehicles\\", "vehicles\\zis\\", "zis5", Country.RUSSIA));
            add(new VehicleDefinition("vehicles\\", "vehicles\\zis\\", "bm13", Country.RUSSIA));
        }
    };
    
    private static final List<VehicleDefinition> russianAAATrucks = new ArrayList<VehicleDefinition>() 
    {
        private static final long serialVersionUID = 1L;
        {
            add(new VehicleDefinition("vehicles\\", "vehicles\\gaz\\", "gaz-aa-m4-aa", Country.RUSSIA));
            add(new VehicleDefinition("vehicles\\", "vehicles\\zis\\", "zis5-72k", Country.RUSSIA));
        }
    };
    
    private static final List<VehicleDefinition> russianCars = new ArrayList<VehicleDefinition>() 
    {
        private static final long serialVersionUID = 1L;
        {
            add(new VehicleDefinition("vehicles\\", "vehicles\\gaz\\", "gaz-m", Country.GERMANY));
        }
    };

    public Truck()
    {
        super();
        this.truckType = TruckType.TRUCK_CARGO;
    }

    public Truck(TruckType truckType)
	{
        super();
	    this.truckType = truckType;
	}

    @Override
    public List<VehicleDefinition> getAllVehicleDefinitions()
    {
        List<VehicleDefinition> allvehicleDefinitions = new ArrayList<>();
        allvehicleDefinitions.addAll(germanTrucks);
        allvehicleDefinitions.addAll(germanAAATrucks);
        allvehicleDefinitions.addAll(germanCars);
        allvehicleDefinitions.addAll(russianTrucks);
        allvehicleDefinitions.addAll(russianAAATrucks);
        allvehicleDefinitions.addAll(russianCars);
        return allvehicleDefinitions;
    }

    @Override
    public void makeRandomVehicleFromSet(ICountry country) throws PWCGException
	{		
		this.country = country;
        
		List<VehicleDefinition> vehicleSet = null;
		if (truckType == TruckType.TRUCK_CARGO)
        {
            displayName = "Truck";
            vehicleSet = germanTrucks;          
            if (country.getSideNoNeutral() == Side.ALLIED)
            {
                vehicleSet = russianTrucks;                       
            }
        }
        else if (truckType == TruckType.TRUCK_AAA)
        {
            displayName = "AAA Truck";
            vehicleSet = germanAAATrucks;          
            if (country.getSideNoNeutral() == Side.ALLIED)
            {
                vehicleSet = russianAAATrucks;                       
            }
        }
        else if (truckType == TruckType.CAR)
        {
            displayName = "AAA Truck";
            vehicleSet = germanCars;          
            if (country.getSideNoNeutral() == Side.ALLIED)
            {
                vehicleSet = russianCars;                       
            }
        }

		makeRandomVehicleInstance(vehicleSet);
	}

    public Truck copy () 
	{
		Truck truck = new Truck(this.truckType);
		
		truck.index = IndexGenerator.getInstance().getNextIndex();
		
		truck.vehicleType = this.vehicleType;
		truck.displayName = this.displayName;
		truck.linkTrId = this.linkTrId;
		truck.script = this.script;
		truck.model = this.model;
		truck.Desc = this.Desc;
		truck.aiLevel = this.aiLevel;
		truck.numberInFormation = this.numberInFormation;
		truck.vulnerable = this.vulnerable;
		truck.engageable = this.engageable;
		truck.limitAmmo = this.limitAmmo;
		truck.damageReport = this.damageReport;
		truck.country = this.country;
		truck.damageThreshold = this.damageThreshold; 
		
		truck.position = new Coordinate();
		truck.orientation = new Orientation();
		
		truck.entity = new McuTREntity();
		
		truck.populateEntity();
		
		return truck;
	}
	
	public void write(BufferedWriter writer) throws PWCGIOException
	{
		super.write(writer);
	}
	
	public void setOrientation (Orientation orient)
	{
		super.setOrientation(orient);
	}

	public void setPosition (Coordinate coord)
	{
		super.setPosition(coord);
	}
}
