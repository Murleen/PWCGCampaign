package pwcg.campaign.personnel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pwcg.campaign.Campaign;
import pwcg.campaign.api.IAirfield;
import pwcg.campaign.api.ICountry;
import pwcg.campaign.api.Side;
import pwcg.campaign.context.PWCGContextManager;
import pwcg.campaign.plane.Role;
import pwcg.campaign.squadron.Squadron;
import pwcg.core.exception.PWCGException;
import pwcg.core.utils.RandomNumberGenerator;

public class EnemySquadronFinder
{
    private Campaign campaign;
    private List<Squadron> nearbyViableEnemySquadrons = new ArrayList<>();
    private List<Squadron> allViableEnemySquadrons = new ArrayList<>();
    private List<Squadron> allEnemySquadrons = new ArrayList<>();

    public EnemySquadronFinder (Campaign campaign)
    {
        this.campaign = campaign;
    }

    public Squadron getRandomEnemySquadron(Squadron squadron, Date date) throws PWCGException
    {
        List<Squadron> nearbyActiveEnemySquadrons = getNearbyEnemySquadronsForVictory(date, squadron);
        nearbyViableEnemySquadrons = getViableSquadrons(nearbyActiveEnemySquadrons);
        allEnemySquadrons = getAllActiveEnemySquadrons(squadron, date);
        allViableEnemySquadrons = getViableSquadrons(allEnemySquadrons);

        Squadron enemySquadron = null;
        if (nearbyViableEnemySquadrons.size() > 0)
        {
            enemySquadron = getEnemySquadron(nearbyViableEnemySquadrons);
        }
        else if (allViableEnemySquadrons.size() > 0)
        {
            enemySquadron = getEnemySquadron(allViableEnemySquadrons);
        }
        else if (allEnemySquadrons.size() > 0)
        {
            enemySquadron = getEnemySquadron(allEnemySquadrons);
        }
 
        return enemySquadron;
    }

    private Squadron getEnemySquadron(List<Squadron> possibleSquadrons) throws PWCGException
    {
        int index = RandomNumberGenerator.getRandom(possibleSquadrons.size());
        Squadron enemySquadron = possibleSquadrons.get(index);
        return enemySquadron;
    }

    private List<Squadron> getNearbyEnemySquadronsForVictory(Date date, Squadron squadron) throws PWCGException
    {
        List<Squadron> nearbyEnemySquadrons = new ArrayList<>();
                
        ICountry country = squadron.getCountry();
        Side enemySide = country.getSide().getOppositeSide();
        List<Role> acceptableRoles = createAcceptableRoledForVictory();
        IAirfield airfield = squadron.determineCurrentAirfieldAnyMap(date);
        if (airfield != null)
        {
            nearbyEnemySquadrons = PWCGContextManager.getInstance().getSquadronManager().getNearestSquadronsByRole(airfield.getPosition(), 1, 200000.0, acceptableRoles, enemySide, date);
        }
        
        List<Squadron> nearbyActiveEnemySquadrons = PWCGContextManager.getInstance().getSquadronManager().reduceToFlyable(nearbyEnemySquadrons, date);
        return nearbyActiveEnemySquadrons;
    }

    private List<Squadron> getAllActiveEnemySquadrons(Squadron squadron, Date date) throws PWCGException
    {
        ICountry country = squadron.getCountry();
        Side enemySide = country.getSide().getOppositeSide();
        return PWCGContextManager.getInstance().getSquadronManager().getAllActiveSquadronsForSide(enemySide, date);
    }

    private List<Squadron> getViableSquadrons(List<Squadron> possibleSquadrons) throws PWCGException
    {
        List<Squadron> viableSquadrons = new ArrayList<>();
        for (Squadron squadron : possibleSquadrons)
        {
            SquadronPersonnel squadronPersonnel = campaign.getPersonnelManager().getSquadronPersonnel(squadron.getSquadronId());
            if (squadronPersonnel != null)
            {
                if(squadronPersonnel.isSquadronViable())
                {
                    viableSquadrons.add(squadron);
                }
            }
        }
        return viableSquadrons;
    }

    private static List<Role> createAcceptableRoledForVictory()
    {
        List<Role> acceptableRoles = new ArrayList<Role>();
        acceptableRoles.add(Role.ROLE_FIGHTER);
        acceptableRoles.add(Role.ROLE_ATTACK);
        acceptableRoles.add(Role.ROLE_DIVE_BOMB);
        acceptableRoles.add(Role.ROLE_BOMB);
        acceptableRoles.add(Role.ROLE_RECON);
        return acceptableRoles;
    }

}
