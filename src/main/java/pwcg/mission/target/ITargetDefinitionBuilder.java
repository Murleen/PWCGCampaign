package pwcg.mission.target;

import pwcg.core.exception.PWCGException;

public interface ITargetDefinitionBuilder
{
    public TargetDefinition buildTargetDefinition () throws PWCGException;
}
