package pwcg.mission.ground.building;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum PwcgBuilding
{
    CHURCH ("church", new BuildingSearchCriteria("church_", BuildingSearchMethod.SEARCH_BEGINS)),
    BRIDGE ("bridge", new BuildingSearchCriteria("bridge", BuildingSearchMethod.SEARCH_CONTAINS), new BuildingSearchCriteria("br_", BuildingSearchMethod.SEARCH_BEGINS)),
    RAILWAY ("railway facility", new BuildingSearchCriteria("rw", BuildingSearchMethod.SEARCH_BEGINS), new BuildingSearchCriteria("railroad", BuildingSearchMethod.SEARCH_CONTAINS)),
    HANGAR ("hangar", new BuildingSearchCriteria("hangar", BuildingSearchMethod.SEARCH_CONTAINS)),
    PORT_FACILITY ("port facility", new BuildingSearchCriteria("port_", BuildingSearchMethod.SEARCH_BEGINS)),
    INDUSTRIAL ("industrial facility", new BuildingSearchCriteria("industrial_", BuildingSearchMethod.SEARCH_BEGINS)),
    DEPOT ("depot facility", new BuildingSearchCriteria("warehouse", BuildingSearchMethod.SEARCH_CONTAINS)),
    FUEL ("fuel depot facility", new BuildingSearchCriteria("industrial_block_fuel", BuildingSearchMethod.SEARCH_BEGINS)),
    STATIC_VEHICLE ("vehicle", new BuildingSearchCriteria("static_", BuildingSearchMethod.SEARCH_BEGINS)),
    UNKNOWN ("some random building", new BuildingSearchCriteria("", BuildingSearchMethod.SEARCH_NONE));

    private String description;
    private List<BuildingSearchCriteria> searchCriteria = new ArrayList<>();
    
    PwcgBuilding (String description, BuildingSearchCriteria ... searchCriteriaArray)
    {
        this.description = description;
        for (BuildingSearchCriteria searchCriteriaElement : searchCriteriaArray)
        {
            this.searchCriteria.add(searchCriteriaElement);
        }
    }

    public String getDescription()
    {
        return description;
    }

    public List<BuildingSearchCriteria> getSearchCriteria()
    {
        return searchCriteria;
    }
    
    public static List<PwcgBuilding> getAllBuildings()
    {
        PwcgBuilding[] allBuildings = PwcgBuilding.class.getEnumConstants();
        return Arrays.asList(allBuildings);
    }
    
    
    public boolean matches (String buildingidentifier)
    {
        for (BuildingSearchCriteria buildingSearchCriteria : searchCriteria)
        {
            if (buildingSearchCriteria.getSearchMethod() == BuildingSearchMethod.SEARCH_BEGINS)
            {
                if (buildingidentifier.startsWith(buildingSearchCriteria.getSearchValue()))
                {
                    return true;
                }
            }
            else if (buildingSearchCriteria.getSearchMethod() == BuildingSearchMethod.SEARCH_CONTAINS)
            {
                if (buildingidentifier.contains(buildingSearchCriteria.getSearchValue()))
                {
                    return true;
                }
            }
        }
        
        return false;
    }
}
