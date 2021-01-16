package pwcg.campaign.api;

import pwcg.core.exception.PWCGException;

public interface IStaticPlaneSelector
{
    IStaticPlane getStaticPlane(String planeName) throws PWCGException;

    boolean isStaticPlane(String planeType) throws PWCGException;
}