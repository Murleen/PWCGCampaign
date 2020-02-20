package pwcg.mission.mcu.group;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pwcg.core.exception.PWCGException;
import pwcg.core.exception.PWCGIOException;
import pwcg.core.location.Coordinate;
import pwcg.core.utils.Logger;
import pwcg.mission.Mission;
import pwcg.mission.MissionBeginUnit;
import pwcg.mission.mcu.Coalition;
import pwcg.mission.mcu.effect.Effect;
import pwcg.mission.mcu.effect.EffectCommand;
import pwcg.mission.mcu.effect.Siren;

public class SirenGroup
{
    private MissionBeginUnit missionBeginUnit;

    private InOutCheckZone checkZone = new InOutCheckZone();

    private List<Siren> sirenEffects = new ArrayList<>();
    private EffectCommand startSirenEffectCommand = new EffectCommand(EffectCommand.START_EFFECT);
    private EffectCommand stopSirenEffectCommand = new EffectCommand(EffectCommand.STOP_EFFECT);

    private Coordinate position;

    public SirenGroup()
    {
    }

    public void buildSirenGroup(Mission mission, Coordinate sirenEffectPosition, Coalition enemyCoalition) throws PWCGException
    {
        this.position = sirenEffectPosition.copy();

        missionBeginUnit = new MissionBeginUnit(position);
        checkZone.setPosition(position);
        checkZone.setZone(10000);
        checkZone.triggerCheckZoneByCoalition(enemyCoalition);

        addSiren();

        setTargetAssociations();
        setObjectAssociations();
        setPositions();
        setNames();
    }

    private void addSiren() throws PWCGException
    {
        Siren sirenEffect = new Siren();

        sirenEffect.populateEntity();
        sirenEffects.add(sirenEffect);
    }

    private void setPositions()
    {
        startSirenEffectCommand.setPosition(position);
        stopSirenEffectCommand.setPosition(position);

        for (Effect sirenEffect : sirenEffects)
        {
            sirenEffect.setPosition(position);
        }
    }

    private void setNames()
    {
        startSirenEffectCommand.setName("start");
        stopSirenEffectCommand.setName("stop");

        for (Effect sirenEffect : sirenEffects)
        {
            sirenEffect.setName("sirenEffect");
        }
    }

    private void setTargetAssociations()
    {
        missionBeginUnit.linkToMissionBegin(checkZone.getEnableTimer().getIndex());

        checkZone.setInTarget(startSirenEffectCommand.getIndex());
        checkZone.setOutTarget(stopSirenEffectCommand.getIndex());
    }

    private void setObjectAssociations()
    {
        for (Effect sirenEffect : sirenEffects)
        {
            startSirenEffectCommand.setObject(sirenEffect.getLinkTrId());
            stopSirenEffectCommand.setObject(sirenEffect.getLinkTrId());
        }
    }

    public void write(BufferedWriter writer) throws PWCGException
    {
        try
        {
            writer.write("Group");
            writer.newLine();
            writer.write("{");
            writer.newLine();


            missionBeginUnit.write(writer);
            checkZone.write(writer);

            startSirenEffectCommand.write(writer);
            stopSirenEffectCommand.write(writer);

            for (Effect sirenEffect : sirenEffects)
            {
                sirenEffect.write(writer);
            }

            writer.write("}");
            writer.newLine();
        }
        catch (IOException e)
        {
            Logger.logException(e);
            throw new PWCGIOException(e.getMessage());
        }
    }

    public Coordinate getPosition()
    {
        return position;
    }
}
