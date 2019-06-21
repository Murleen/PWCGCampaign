package pwcg.campaign.target;

import java.util.Date;

import pwcg.campaign.api.ICountry;
import pwcg.campaign.squadron.Squadron;
import pwcg.core.location.Coordinate;
import pwcg.core.location.Orientation;

public class TargetDefinition
{
    private TacticalTarget targetType = TacticalTarget.TARGET_NONE;
    private boolean isPlayerTarget = false;
    private Coordinate targetPosition;
    private Orientation targetOrientation;
    private Squadron attackingSquadron;
    private ICountry attackingCountry;
    private ICountry targetCountry;
    private Date date;
    private String targetName = "";
    private int preferredRadius;
    private int maximumRadius;

    public TargetDefinition()
    {
    }
    
    public TargetCategory getTargetCategory()
    {
        return targetType.getTargetCategory();
    }

    public TacticalTarget getTargetType()
    {
        return targetType;
    }

    public void setTargetType(TacticalTarget targetType)
    {
        this.targetType = targetType;
    }

    public Coordinate getTargetPosition()
    {
        return targetPosition;
    }

    public void setTargetPosition(Coordinate targetLocation)
    {
        this.targetPosition = targetLocation;
    }

    public Orientation getTargetOrientation()
    {
        return targetOrientation;
    }

    public void setTargetOrientation(Orientation targetOrientation)
    {
        this.targetOrientation = targetOrientation;
    }

    public ICountry getAttackingCountry()
    {
        return attackingCountry;
    }

    public void setAttackingCountry(ICountry attackingCountry)
    {
        this.attackingCountry = attackingCountry;
    }

    public ICountry getTargetCountry()
    {
        return targetCountry;
    }

    public void setTargetCountry(ICountry targetCountry)
    {
        this.targetCountry = targetCountry;
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public boolean isPlayerTarget()
    {
        return isPlayerTarget;
    }

    public void setPlayerTarget(boolean isPlayerTarget)
    {
        this.isPlayerTarget = isPlayerTarget;
    }

    public String getTargetName()
    {
        return targetName;
    }

    public void setTargetName(String targetName)
    {
        this.targetName = targetName;
    }

    public int getPreferredRadius()
    {
        return preferredRadius;
    }

    public void setPreferredRadius(int preferredRadius)
    {
        this.preferredRadius = preferredRadius;
    }

    public int getMaximumRadius()
    {
        return maximumRadius;
    }

    public void setMaximumRadius(int maximumRadius)
    {
        this.maximumRadius = maximumRadius;
    }

    public Squadron getAttackingSquadron()
    {
        return attackingSquadron;
    }

    public void setAttackingSquadron(Squadron attackingSquadron)
    {
        this.attackingSquadron = attackingSquadron;
    }
}
