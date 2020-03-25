package pwcg.mission.ground.unittypes.artillery;

import java.util.ArrayList;
import java.util.List;

import pwcg.core.exception.PWCGException;
import pwcg.core.location.Coordinate;
import pwcg.core.utils.MathUtils;
import pwcg.mission.ground.GroundUnitInformation;
import pwcg.mission.ground.GroundUnitSize;
import pwcg.mission.ground.org.GroundAspectAreaFire;
import pwcg.mission.ground.org.GroundAspectFactory;
import pwcg.mission.ground.org.GroundUnit;
import pwcg.mission.ground.org.GroundUnitNumberCalculator;
import pwcg.mission.ground.org.IGroundAspect;
import pwcg.mission.ground.vehicle.VehicleClass;
import pwcg.mission.mcu.AttackAreaType;

public class GroundArtilleryBattery extends GroundUnit
{
    static private int ARTY_ATTACK_AREA_RADIUS = 500;

    public GroundArtilleryBattery(GroundUnitInformation pwcgGroundUnitInformation)
    {
        super(VehicleClass.ArtilleryHowitzer, pwcgGroundUnitInformation);
    }   

    @Override
    protected List<Coordinate> createSpawnerLocations() throws PWCGException 
    {
        List<Coordinate> spawnerLocations = new ArrayList<>();

        int numArtillery = calcNumUnits();

        double startLocationOrientation = MathUtils.adjustAngle (pwcgGroundUnitInformation.getOrientation().getyOri(), 270);             
        double gunSpacing = 30.0;
        Coordinate gunCoords = MathUtils.calcNextCoord(pwcgGroundUnitInformation.getPosition(), startLocationOrientation, ((numArtillery * gunSpacing) / 2));       
        
        // Direction in which subsequent units will be placed
        double placementOrientation = MathUtils.adjustAngle (pwcgGroundUnitInformation.getOrientation().getyOri(), 90.0);        

        for (int i = 0; i < numArtillery; ++i)
        {   
            gunCoords = MathUtils.calcNextCoord(gunCoords, placementOrientation, gunSpacing);
            spawnerLocations.add(gunCoords);
        }
        return spawnerLocations;
    }

    protected int calcNumUnits() throws PWCGException
    {
        if (pwcgGroundUnitInformation.getUnitSize() == GroundUnitSize.GROUND_UNIT_SIZE_TINY)
        {
            return GroundUnitNumberCalculator.calcNumUnits(1, 1);
        }
        else if (pwcgGroundUnitInformation.getUnitSize() == GroundUnitSize.GROUND_UNIT_SIZE_LOW)
        {
            return GroundUnitNumberCalculator.calcNumUnits(2, 4);
        }
        else if (pwcgGroundUnitInformation.getUnitSize() == GroundUnitSize.GROUND_UNIT_SIZE_MEDIUM)
        {
            return GroundUnitNumberCalculator.calcNumUnits(3, 6);
        }
        else if (pwcgGroundUnitInformation.getUnitSize() == GroundUnitSize.GROUND_UNIT_SIZE_HIGH)
        {
            return GroundUnitNumberCalculator.calcNumUnits(4, 8);
        }
        
        throw new PWCGException ("No unit size provided for ground unit");
    }

    @Override
    protected void addAspects() throws PWCGException
    {
        IGroundAspect areaFire = GroundAspectFactory.createGroundAspectAreaFire(pwcgGroundUnitInformation, pwcgGroundUnitInformation.getDestination(), vehicle, AttackAreaType.INDIRECT, ARTY_ATTACK_AREA_RADIUS);
        this.addGroundElement(areaFire);        
    }

    public void setTargetPosition(Coordinate targetPosition)
    {
        for (IGroundAspect groundElement : this.getGroundElements())
        {
            if (groundElement instanceof GroundAspectAreaFire)
            {
                GroundAspectAreaFire areaFireElement = (GroundAspectAreaFire)groundElement;
                areaFireElement.setTargetPosition(targetPosition);
            }
        }
        
    }
}	

