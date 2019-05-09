package pwcg.product.bos.map.prokhorovka;

import pwcg.campaign.context.FrontParameters;

public class ProkhorovkaFrontParameters extends FrontParameters
{
    static private double PROKHOROVKA_XMIN = 30000;
    static private double PROKHOROVKA_XMAX = 166400;

    static private double PROKHOROVKA_ZMIN = 30000;
    static private double PROKHOROVKA_ZMAX = 166400;
    
    public ProkhorovkaFrontParameters ()
    {
        xMin = PROKHOROVKA_XMIN;
        xMax = PROKHOROVKA_XMAX;
        zMin = PROKHOROVKA_ZMIN;
        zMax = PROKHOROVKA_ZMAX;
    }
}
