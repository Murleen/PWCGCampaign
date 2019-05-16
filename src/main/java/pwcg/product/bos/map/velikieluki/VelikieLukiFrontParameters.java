package pwcg.product.bos.map.velikieluki;

import pwcg.campaign.context.FrontParameters;

public class VelikieLukiFrontParameters extends FrontParameters
{
    static private double VELIKIE_LUKI_XMIN = 0;
    static private double VELIKIE_LUKI_XMAX = 124800;

    static private double VELIKIE_LUKI_ZMIN = 0;
    static private double VELIKIE_LUKI_ZMAX = 166400;
    
    public VelikieLukiFrontParameters ()
    {
        xMin = VELIKIE_LUKI_XMIN;
        xMax = VELIKIE_LUKI_XMAX;
        zMin = VELIKIE_LUKI_ZMIN;
        zMax = VELIKIE_LUKI_ZMAX;
    }
}
