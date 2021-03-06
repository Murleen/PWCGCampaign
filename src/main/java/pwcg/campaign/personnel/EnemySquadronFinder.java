package pwcg.campaign.personnel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pwcg.campaign.Campaign;
import pwcg.campaign.api.IAirfield;
import pwcg.campaign.api.ICountry;
import pwcg.campaign.api.Side;
import pwcg.campaign.context.PWCGContext;
import pwcg.campaign.plane.Role;
import pwcg.campaign.squadron.Squadron;
import pwcg.core.exception.PWCGException;
import pwcg.core.utils.RandomNumberGenerator;

public class EnemySquadronFinder
{
    private Campaign campaign;

    public EnemySquadronFinder (Campaign campaign)
    {
        this.campaign = campaign;
    }

    public Squadron getRandomEnemyViableSquadronNotPlayerSquadron(Squadron squadron, Date date) throws PWCGException
    {
        List<Squadron> nearbyViableEnemySquadrons = getBestViableSquadrons(squadron, date);
        for (Squadron enemySquadron : nearbyViableEnemySquadrons)
        {
            SquadronPersonnel squadronPersonnel = campaign.getPersonnelManager().getSquadronPersonnel(squadron.getSquadronId());
            if (squadronPersonnel != null)
            {
                if (!squadronPersonnel.isPlayerSquadron())
                {
                    nearbyViableEnemySquadrons.add(enemySquadron);
                }
            }
        }
        
        Squadron enemySquadron = null;
        if (nearbyViableEnemySquadrons.size() > 0)
        {
            enemySquadron = getEnemySquadron(nearbyViableEnemySquadrons);
        }
        return enemySquadron;
    }

    public Squadron getRandomEnemyViableSquadron(Squadron squadron, Date date) throws PWCGException
    {
        List<Squadron> nearbyViableEnemySquadrons = getBestViableSquadrons(squadron, date);
        Squadron enemySquadron = null;
        if (nearbyViableEnemySquadrons.size() > 0)
        {
            enemySquadron = getEnemySquadron(nearbyViableEnemySquadrons);
        }

        return enemySquadron;
    }
    

    private List<Squadron> getBestViableSquadrons(Squadron squadron, Date date) throws PWCGException
    {
        List<Squadron> nearbyActiveEnemySquadrons = getNearbyEnemySquadronsForVictory(date, squadron);
        List<Squadron> nearbyViableEnemySquadrons = getViableSquadrons(nearbyActiveEnemySquadrons);
        List<Squadron> allEnemySquadrons = getAllActiveEnemySquadrons(squadron, date);
        List<Squadron> allViableEnemySquadrons = getViableSquadrons(allEnemySquadrons);

        if (nearbyViableEnemySquadrons.size() > 0)
        {
            return nearbyViableEnemySquadrons;
        }
        else if (allViableEnemySquadrons.size() > 0)
        {
            return allViableEnemySquadrons;
        }
 
        return new ArrayList<Squadron>();
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
            nearbyEnemySquadrons = PWCGContext.getInstance().getSquadronManager().getNearestSquadronsByRole(campaign, airfield.getPosition(), 1, 200000.0, acceptableRoles, enemySide, date);
        }
        
        List<Squadron> nearbyActiveEnemySquadrons = PWCGContext.getInstance().getSquadronManager().reduceToFlyable(nearbyEnemySquadrons, date);
        return nearbyActiveEnemySquadrons;
    }

    private List<Squadron> getAllActiveEnemySquadrons(Squadron squadron, Date date) throws PWCGException
    {
        ICountry country = squadron.getCountry();
        Side enemySide = country.getSide().getOppositeSide();
        return PWCGContext.getInstance().getSquadronManager().getActiveSquadronsForSide(enemySide, date);
    }

    private List<Squadron> getViableSquadrons(List<Squadron> possibleSquadrons) throws PWCGException
    {
        List<Squadron> viableSquadrons = new ArrayList<>();
        for (Squadron squadron : possibleSquadrons)
        {
            if (squadron.isSquadronViable(campaign))
            {
                viableSquadrons.add(squadron);
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
