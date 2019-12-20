package pwcg.campaign.api;

import pwcg.mission.flight.FlightTypes;
import pwcg.mission.flight.initialposition.TakeoffFormation;
import pwcg.mission.ground.BattleSize;

public interface IProductSpecificConfiguration
{
    boolean useWaypointGoal();
    boolean usePlaneDir();
    boolean useFlagDir();
    boolean useCallSign();
    boolean usePosition1();
    TakeoffFormation getTakeoffFormation();
    int getAdditionalInitialTargetRadius(FlightTypes flightType);
    int getAdditionalMaxTargetRadius(FlightTypes flightType);
    int getInitialFrontLineRadius();
    int geNeutralZone();
    int getInterceptRadius();
    int getInterceptCrossDiameterDistance();
    int getInterceptCreepLegDistance();
    int getInterceptCreepCrossDistance();
    int getInterceptInnerLoopDistance();
    int getInterceptLoopAngle();
    int getClimbDistance();
    int getBombApproachDistance();
    int getBombFinalApproachDistance();
    int getMinClimbWPAlt();
    int getLargeMissionRadius();
    int getMediumMissionRadius();
    int getSmallMissionRadius();
    int getVerySmallMissionRadius();
    int getNumAssaultSegments(BattleSize battleSize);
    int getCloseToFrontDistance();
    int getMaxDistanceForVirtualFlightFromPlayerBox();
    int getMaxDistanceForVirtualFlightAirStart();
    int getInitialWaypointAltitude();
    int getGroundAttackIngressDistance();
    int getAircraftSpacingHorizontal();
    int getAircraftSpacingVertical();
    int getTakeoffSpacing();
    int getRendezvousDistanceFromFront();
    int getAdditionalAltitudeForEscort();
    int getScrambleOpposeMinDistance();
    int getScrambleOpposeMaxDistance();
}
