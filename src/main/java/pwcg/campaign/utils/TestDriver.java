package pwcg.campaign.utils;

import java.util.List;

import pwcg.campaign.plane.Role;
import pwcg.campaign.squadmember.SquadronMember;
import pwcg.campaign.target.TacticalTarget;
import pwcg.core.exception.PWCGException;
import pwcg.mission.flight.FlightTypes;

public class TestDriver
{
    private static TestDriver testDriver = new TestDriver();
    
    private boolean enabled = false;
    private boolean createPlayerOnly = true;
    private boolean writeCampaignFile = true;
    private boolean useTestFlightType = false;
    
    private Role currentRole = Role.ROLE_NONE;
    
    private TestFlightType testFighterFlightType = new TestFlightType();
    private TestFlightType testReconFlightType = new TestFlightType();
    private TestFlightType testAttackFlightType = new TestFlightType();
    private TestFlightType testDiveBombFlightType = new TestFlightType();
    private TestFlightType testBombFlightType = new TestFlightType();
    private TestFlightType testStrategicBombFlightType = new TestFlightType();
    
    private TacticalTarget testPlayerTacticalTargetType = TacticalTarget.TARGET_TROOP_CONCENTRATION;
    private TacticalTarget testAITacticalTargetType = TacticalTarget.TARGET_ASSAULT;

    private List<SquadronMember>assignedSquadMembers = null;

    private TestDriver()
    {
        testFighterFlightType.playerFlightType = FlightTypes.OFFENSIVE;
        testFighterFlightType.aiFlightType = FlightTypes.PATROL;
    }
    
    public static TestDriver getInstance()
    {
        return testDriver;
    }

    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
    

    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * @return the createPlayerOnly
     */
    public boolean isCreatePlayerOnly()
    {
        if (enabled)
        {
            return this.createPlayerOnly;
        }
            
        return false;
    }

    /**
     * @param createPlayerOnly the createPlayerOnly to set
     */
    public void setCreatePlayerOnly(boolean createPlayerOnly)
    {
        this.createPlayerOnly = createPlayerOnly;
    }

    /**
     * @return
     */
    public boolean isWriteCampaignFile()
    {
        if (enabled)
        {
            return writeCampaignFile;
        }
            
        return false;
    }

    /**
     * @param writeCampaignFile
     */
    public void setWriteCampaignFile(boolean writeCampaignFile)
    {
        this.writeCampaignFile = writeCampaignFile;
    }
    
    /**
     * @return
     */
    public boolean isUseTestFlightType()
    {
        return useTestFlightType;
    }

    /**
     * @param useTestFlightType
     */
    public void setUseTestFlightType(boolean useTestFlightType)
    {
        this.useTestFlightType = useTestFlightType;
    }


    /**
     * @param role
     * @return
     * @throws PWCGException
     */
    private TestFlightType getTestFlightTypeForRole() throws PWCGException
    {
        if (currentRole == Role.ROLE_FIGHTER)
        {
            return testFighterFlightType;
        }
        else if (currentRole == Role.ROLE_BOMB)
        {
            return testBombFlightType;
        }
        else if (currentRole == Role.ROLE_STRAT_BOMB)
        {
            return testStrategicBombFlightType;
        }
        else if (currentRole == Role.ROLE_RECON)
        {
            return testReconFlightType;
        }
        else if (currentRole == Role.ROLE_DIVE_BOMB)
        {
            return testDiveBombFlightType;
        }
        else if (currentRole == Role.ROLE_ATTACK)
        {
            return testAttackFlightType;
        }
        else
        {
            throw new PWCGException ("Invalid role for test flight: " + currentRole);
        }
    }


    /**
     * @param playerFlight
     * @param role
     * @return
     * @throws PWCGException
     */
    public FlightTypes getTestFlightType(boolean playerFlight) throws PWCGException
    {
        if (enabled)
        {
            if (useTestFlightType)
            {
                TestFlightType testFlightType = getTestFlightTypeForRole();
                if (playerFlight)
                {
                    return testFlightType.playerFlightType;
                }
                else
                {
                    return testFlightType.aiFlightType;
                }
            }
        }
        
        return FlightTypes.ANY;
    }

    /**
     * @param playerFlight
     * @param role
     * @param newTestFlightType
     * @throws PWCGException 
     */
    public void setTestFlightType(boolean playerFlight, Role role, FlightTypes newTestFlightType) throws PWCGException
    {
        if (enabled)
        {
            if (useTestFlightType)
            {
                currentRole = role;
                
                TestFlightType testFlightType = getTestFlightTypeForRole();
                if (playerFlight)
                {
                    testFlightType.playerFlightType = newTestFlightType;
                }
                else
                {
                    testFlightType.aiFlightType = newTestFlightType;
                }
            }
        }
    }



    /**
     * @param playerFlight
     * @param role
     * @return
     * @throws PWCGException
     */
    public TacticalTarget getTestTacticalTargetType(boolean playerFlight) throws PWCGException
    {
        if (enabled)
        {
            if (useTestFlightType)
            {
                if (playerFlight)
                {
                    return this.testPlayerTacticalTargetType;
                }
                else
                {
                    return this.testAITacticalTargetType;
                }
            }
        }
        
        return TacticalTarget.TARGET_NONE;
    }

    /**
     * @return the assignedSquadMembers
     */
    public List<SquadronMember> getAssignedSquadMembers()
    {
        return this.assignedSquadMembers;
    }

    /**
     * @param assignedSquadMembers the assignedSquadMembers to set
     */
    public void setAssignedSquadMembers(List<SquadronMember> assignedSquadMembers)
    {
        this.assignedSquadMembers = assignedSquadMembers;
    }

    /**
     * @author Patrick Wilson
     *
     */
    private class TestFlightType
    {
        private FlightTypes playerFlightType = FlightTypes.ANY;
        private FlightTypes aiFlightType = FlightTypes.ANY;
    }
}
