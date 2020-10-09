package pwcg.mission.ground.org;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

import pwcg.campaign.api.Side;
import pwcg.campaign.utils.IndexGenerator;
import pwcg.core.exception.PWCGException;
import pwcg.core.location.Coordinate;
import pwcg.core.utils.PWCGLogger;
import pwcg.mission.Mission;
import pwcg.mission.mcu.group.MissionBeginCheckZoneBase;
import pwcg.mission.mcu.group.MissionBeginSelfDeactivatingCheckZone;
import pwcg.mission.target.TargetType;

public class GroundUnitCollection implements IGroundUnitCollection
{
    private final static int GROUND_UNIT_SPAWN_DISTANCE = 5000;

    private GroundUnitCollectionData groundUnitCollectionData;
    private int index = IndexGenerator.getInstance().getNextIndex();
    private MissionBeginCheckZoneBase missionBeginUnit;
    private IGroundUnit primaryGroundUnit;
    private List<IGroundUnit> groundUnits = new ArrayList<>();
    private String groundUnitName;

    public GroundUnitCollection(String groundUnitName, GroundUnitCollectionData groundUnitCollectionData)
    {
        this.groundUnitName = groundUnitName;
        this.groundUnitCollectionData = groundUnitCollectionData;
    }

    public Coordinate getTargetCoordinatesFromGroundUnits(Side side) throws PWCGException
    {
        GroundUnitCollectionTargetFinder groundUnitCollectionTargetFinder = new GroundUnitCollectionTargetFinder(this);
        IGroundUnit targetUnit = groundUnitCollectionTargetFinder.findTargetUnit(side);
        Coordinate targetCoordinates = targetUnit.getPosition();
        return targetCoordinates;
    }

    public void merge(IGroundUnitCollection relatedGroundCollection)
    {
        groundUnits.addAll(relatedGroundCollection.getGroundUnits());
    }

    public void finishGroundUnitCollection() throws PWCGException
    {
        createCheckZone();
        createTargetAssociations();
    }

    public List<IGroundUnit> getGroundUnitsForSide(Side side) throws PWCGException
    {
        List<IGroundUnit> groundUnitsForSide = new ArrayList<>();
        for (IGroundUnit groundUnit : groundUnits)
        {
            if (groundUnit.getCountry().getSide() == side)
            {
                groundUnitsForSide.add(groundUnit);
            }
        }
        return groundUnitsForSide;
    }

    public List<IGroundUnit> getInterestingGroundUnitsForSide(Side side) throws PWCGException
    {
        List<IGroundUnit> groundUnitsForSide = new ArrayList<>();
        for (IGroundUnit groundUnit : groundUnits)
        {
            if (groundUnit.getCountry().getSide() == side)
            {
                if (groundUnit.getGroundUnitType() != GroundUnitType.AAA_UNIT && groundUnit.getGroundUnitType() != GroundUnitType.STATIC_UNIT)
                {
                    groundUnitsForSide.add(groundUnit);
                }
            }
        }
        
        if (groundUnitsForSide.size() == 0)
        {
            groundUnitsForSide = getGroundUnitsForSide(side);
        }
        
        return groundUnitsForSide;
    }

    public int getUnitCount()
    {
        int unitCount = 0;
        for (IGroundUnit groundUnit : groundUnits)
        {
            unitCount += groundUnit.getUnitCount();
        }
        return unitCount;
    }

    private void createCheckZone() throws PWCGException
    {
        missionBeginUnit = new MissionBeginSelfDeactivatingCheckZone("Check Zone " + groundUnitName, getPosition(), GROUND_UNIT_SPAWN_DISTANCE);
        missionBeginUnit.setCheckZoneCoalitions(groundUnitCollectionData.getTriggerCoalitions());
    }

    private void createTargetAssociations()
    {
        for (IGroundUnit groundUnit : groundUnits)
        {
            missionBeginUnit.linkCheckZoneTarget(groundUnit.getEntryPoint());
        }
    }

    @Override
    public List<IGroundUnit> getGroundUnits()
    {
        return groundUnits;
    }

    @Override
    public Coordinate getPosition() throws PWCGException
    {
        GroundUnitCollectionTargetFinder groundUnitCollectionTargetFinder = new GroundUnitCollectionTargetFinder(this);
        IGroundUnit targetUnit = groundUnitCollectionTargetFinder.findTargetUnit();
        Coordinate targetCoordinates = targetUnit.getPosition();
        return targetCoordinates;
    }

    @Override
    public Coordinate getPosition(Side side) throws PWCGException
    {
        GroundUnitCollectionTargetFinder groundUnitCollectionTargetFinder = new GroundUnitCollectionTargetFinder(this);
        IGroundUnit targetUnit = groundUnitCollectionTargetFinder.findTargetUnit(side);
        Coordinate targetCoordinates = targetUnit.getPosition();
        return targetCoordinates;
    }

    @Override
    public TargetType getTargetType()
    {
        return groundUnitCollectionData.getTargetType();
    }

    @Override
    public void write(BufferedWriter writer) throws PWCGException
    {
        try
        {
            writer.write("Group");
            writer.newLine();
            writer.write("{");
            writer.newLine();

            writer.write("  Name = \"" + groundUnitCollectionData.getName() + "\";");
            writer.newLine();
            writer.write("  Index = " + index + ";");
            writer.newLine();
            writer.write("  Desc = \"" + groundUnitCollectionData.getName() + "\";");
            writer.newLine();

            missionBeginUnit.write(writer);
            for (IGroundUnit groundUnit : groundUnits)
            {
                groundUnit.write(writer);
            }

            writer.write("}");
            writer.newLine();
        }
        catch (Exception e)
        {
            PWCGLogger.logException(e);
            throw new PWCGException(e.getMessage());
        }
    }

    @Override
    public void validate() throws PWCGException
    {
        missionBeginUnit.validate();
        for (IGroundUnit groundUnit : groundUnits)
        {
            int entryPoint = groundUnit.getEntryPoint();
            missionBeginUnit.validateTarget(entryPoint);
            groundUnit.validate();
        }
    }

    @Override
    public GroundUnitCollectionType getGroundUnitCollectionType()
    {
        return groundUnitCollectionData.getGroundUnitCollectionType();
    }

    @Override
    public void addGroundUnit(IGroundUnit groundUnit)
    {
        groundUnits.add(groundUnit);
    }

    @Override
    public IGroundUnit getPrimaryGroundUnit()
    {
        if (primaryGroundUnit == null)
        {
            return groundUnits.get(0);
        }
        else
        {
            return primaryGroundUnit;
        }
    }

    @Override
    public void setPrimaryGroundUnit(IGroundUnit groundUnit)
    {
        primaryGroundUnit = groundUnit;
    }

    @Override
    public void triggerGroundUnitCollection(Mission mission) throws PWCGException
    {
        missionBeginUnit.triggerMissionBeginCheckZone(mission);
    }
}
