package pwcg.mission.ground.vehicle;

import java.util.Date;

import pwcg.campaign.api.ICountry;
import pwcg.core.exception.PWCGException;

public class VehicleFactory
{
    public static IVehicle createVehicle(ICountry country, Date date, VehicleClass vehicleClass) throws PWCGException
    {
        VehicleRequestDefinition requestDefinition = new VehicleRequestDefinition(country.getCountry(), date, vehicleClass);
        IVehicleDefinition vehicleDefinition = VehicleDefinitionManager.getInstance().getVehicleDefinitionForRequest(requestDefinition);
        IVehicle vehicle = new Vehicle(vehicleDefinition);
        vehicle.makeVehicleFromDefinition(country);
        return vehicle;
    }

    public static ITrainLocomotive createLocomotive(ICountry country, Date date) throws PWCGException
    {
        VehicleRequestDefinition requestDefinition = new VehicleRequestDefinition(country.getCountry(), date, VehicleClass.TrainLocomotive);
        IVehicleDefinition vehicleDefinition = VehicleDefinitionManager.getInstance().getVehicleDefinitionForRequest(requestDefinition);
        ITrainLocomotive locomotive = new TrainLocomotive(vehicleDefinition);
        locomotive.makeVehicleFromDefinition(country);
        return locomotive;
    }
}
