package pwcg.mission.ground.unittypes.infantry;

import java.io.BufferedWriter;
import java.util.List;

import pwcg.campaign.api.Side;
import pwcg.core.exception.PWCGException;
import pwcg.core.location.Coordinate;
import pwcg.mission.flight.IFlight;
import pwcg.mission.ground.GroundUnitInformation;
import pwcg.mission.ground.GroundUnitSize;
import pwcg.mission.ground.org.GroundUnitNumberCalculator;
import pwcg.mission.mcu.McuFlare;
import pwcg.mission.mcu.group.FlareSequence;

public class GroundMachineGunFlareUnit extends GroundMachineGunUnit
{
    private FlareSequence flares = new FlareSequence();
    private IFlight triggeringFlight;
    
    public GroundMachineGunFlareUnit(GroundUnitInformation pwcgGroundUnitInformation, IFlight triggeringFlight)
    {
        super(pwcgGroundUnitInformation);
        this.triggeringFlight = triggeringFlight;
    }   

    @Override
    public void createGroundUnit() throws PWCGException
    {
        super.createSpawnTimer();
        List<Coordinate> vehicleStartPositions = createVehicleStartPositions();
        super.createVehicles(vehicleStartPositions);
        addAspects();
        createFlares();
    }

    protected int calcNumUnits() throws PWCGException
    {
        if (pwcgGroundUnitInformation.getUnitSize() == GroundUnitSize.GROUND_UNIT_SIZE_TINY)
        {
            return GroundUnitNumberCalculator.calcNumUnits(1, 1);
        }
        else if (pwcgGroundUnitInformation.getUnitSize() == GroundUnitSize.GROUND_UNIT_SIZE_LOW)
        {
            return GroundUnitNumberCalculator.calcNumUnits(1, 1);
        }
        else if (pwcgGroundUnitInformation.getUnitSize() == GroundUnitSize.GROUND_UNIT_SIZE_MEDIUM)
        {
            return GroundUnitNumberCalculator.calcNumUnits(2, 2);
        }
        else if (pwcgGroundUnitInformation.getUnitSize() == GroundUnitSize.GROUND_UNIT_SIZE_HIGH)
        {
            return GroundUnitNumberCalculator.calcNumUnits(2, 2);
        }
        
        throw new PWCGException ("No unit size provided for ground unit");
    }

    protected void addAspects() throws PWCGException
    {
        super.addDirectFireAspect();
    }

    public void createFlares() throws PWCGException 
    {
        int flareColor = McuFlare.FLARE_COLOR_RED;        
        if (pwcgGroundUnitInformation.getCountry().getSide() == Side.ALLIED)
        {
            flareColor = McuFlare.FLARE_COLOR_GREEN;
        }
        
        flares = new FlareSequence();
        flares.setFlare(triggeringFlight, pwcgGroundUnitInformation.getPosition().copy(), flareColor, super.getVehicles().get(0).getEntity().getIndex());
    }

    @Override
    public void write(BufferedWriter writer) throws PWCGException 
    {       
        super.write(writer);
        flares.write(writer);
    }

    public FlareSequence getFlares()
    {
        return flares;
    }
}	
