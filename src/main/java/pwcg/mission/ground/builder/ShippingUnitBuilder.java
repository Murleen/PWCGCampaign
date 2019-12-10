package pwcg.mission.ground.builder;

import pwcg.campaign.Campaign;
import pwcg.core.exception.PWCGException;
import pwcg.core.location.Coordinate;
import pwcg.core.utils.MathUtils;
import pwcg.core.utils.RandomNumberGenerator;
import pwcg.mission.ground.GroundUnitInformation;
import pwcg.mission.ground.GroundUnitInformationFactory;
import pwcg.mission.ground.org.GroundUnitCollection;
import pwcg.mission.ground.org.GroundUnitCollectionData;
import pwcg.mission.ground.org.GroundUnitCollectionType;
import pwcg.mission.ground.org.IGroundUnit;
import pwcg.mission.ground.org.IGroundUnitCollection;
import pwcg.mission.ground.unittypes.transport.ShipCargoConvoyUnit;
import pwcg.mission.ground.unittypes.transport.ShipSubmarineConvoyUnit;
import pwcg.mission.ground.unittypes.transport.ShipWarshipConvoyUnit;
import pwcg.mission.ground.vehicle.VehicleClass;
import pwcg.mission.mcu.Coalition;
import pwcg.mission.target.TacticalTarget;
import pwcg.mission.target.TargetDefinition;

public class ShippingUnitBuilder
{
    private Campaign campaign;
    private TargetDefinition targetDefinition;
    
    public ShippingUnitBuilder (Campaign campaign, TargetDefinition targetDefinition)
    {
        this.campaign = campaign;
        this.targetDefinition  = targetDefinition;
    }

    public IGroundUnitCollection createShippingUnit (VehicleClass shipType) throws PWCGException 
    {
        IGroundUnitCollection shipConvoyUnit = generateConvoy(shipType);
        return shipConvoyUnit;
    }


    private IGroundUnitCollection generateConvoy(VehicleClass shipType) throws PWCGException 
    {
        GroundUnitInformation groundUnitInformation = createGroundUnitInformationForUnit();
        
        IGroundUnit shipGroup = null;
        if (shipType == VehicleClass.ShipCargo)
        {
            shipGroup = new ShipCargoConvoyUnit(groundUnitInformation);
            shipGroup.createGroundUnit();
        }
        else if (shipType == VehicleClass.ShipWarship)
        {
            shipGroup = new ShipWarshipConvoyUnit(groundUnitInformation);
            shipGroup.createGroundUnit();
        }
        else if (shipType == VehicleClass.Submarine)
        {
            shipGroup = new ShipSubmarineConvoyUnit(groundUnitInformation);
            shipGroup.createGroundUnit();
        }
        else
        {
            throw new PWCGException("Invalid vehicle type for ship builder " + shipType);
        }
        
        GroundUnitCollectionData groundUnitCollectionData = new GroundUnitCollectionData(
                GroundUnitCollectionType.TRANSPORT_GROUND_UNIT_COLLECTION, 
                "Ships", 
                TacticalTarget.TARGET_SHIPPING,
                Coalition.getCoalitionsForSide(groundUnitInformation.getCountry().getSide().getOppositeSide()));

        IGroundUnitCollection groundUnitCollection = new GroundUnitCollection (groundUnitCollectionData);
        groundUnitCollection.addGroundUnit(shipGroup);
        groundUnitCollection.finishGroundUnitCollection();
        return groundUnitCollection;
    }

    private GroundUnitInformation createGroundUnitInformationForUnit() throws PWCGException
    {
        GroundUnitInformation groundUnitInformation = GroundUnitInformationFactory.buildGroundUnitInformation(campaign, targetDefinition);
        int angle = RandomNumberGenerator.getRandom(360);
        Coordinate destination = MathUtils.calcNextCoord(targetDefinition.getTargetPosition(), angle, 50000);
        groundUnitInformation.setDestination(destination);
        return groundUnitInformation;
    }

}
