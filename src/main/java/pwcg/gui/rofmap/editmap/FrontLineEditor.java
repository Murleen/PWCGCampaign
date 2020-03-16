package pwcg.gui.rofmap.editmap;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import pwcg.campaign.context.FrontLinePoint;
import pwcg.core.exception.PWCGException;
import pwcg.core.location.Coordinate;
import pwcg.core.utils.PWCGLogger;
import pwcg.core.utils.MathUtils;
import pwcg.core.utils.PositionFinder;
import pwcg.gui.rofmap.MapPanelBase;

public class FrontLineEditor
{
    private static final int LOCATION_INDEX_NOT_FOUND = -1;
    
    private List<FrontLinePoint> userFrontLines = new ArrayList<>();
    private MapPanelBase mapPanel = null;
    
    private FrontLinePoint frontLinePointToEdit = null;
    
    public FrontLineEditor(MapPanelBase mapPanel)
    {
        this.mapPanel = mapPanel;
    }

    public void createFrontPoint(MouseEvent e)
    {
        try
        {
            Point point = new Point();
            point.x = e.getX();
            point.y = e.getY();
            Coordinate coordinate = mapPanel.pointToCoordinate(point);

            FrontLinePoint frontFrontLinePoint = makeFrontLinePoint(coordinate, FrontLinePoint.ALLIED_FRONT_LINE);

            userFrontLines.add(frontFrontLinePoint);
        }
        catch (Exception exp)
        {
            PWCGLogger.logException(exp);
        }
    }
    
    public void selectFrontPointToMove(MouseEvent e)
    {
        int selectedFrontLineIndex = selectNearestFrontPointToMouseClick(e, 3000.0);
        if (selectedFrontLineIndex != LOCATION_INDEX_NOT_FOUND)
        {
            frontLinePointToEdit = userFrontLines.get(selectedFrontLineIndex);
        }
        else
        {
            frontLinePointToEdit = null;
        }
    }

    public void releaseFrontPointToMove(MouseEvent e)
    {
        try
        {
            if (frontLinePointToEdit != null)
            {
                Point point = new Point();
                point.x = e.getX();
                point.y = e.getY();
                Coordinate releaseCoordinate = mapPanel.pointToCoordinate(point);
                
                frontLinePointToEdit.setPosition(releaseCoordinate);
                
                frontLinePointToEdit = null;
            }
        }
        catch (Exception exp)
        {
            PWCGLogger.logException(exp);
        }
    }

    public void deletePoint(MouseEvent e)
    {
        int selectedFrontLineIndex = selectNearestFrontPointToMouseClick(e, 3000.0);
        if (selectedFrontLineIndex != LOCATION_INDEX_NOT_FOUND)
        {
            userFrontLines.remove(selectedFrontLineIndex);
        }
    }

    public void addFrontPointToLines(MouseEvent e) throws PWCGException
    {
        int selectedFrontPointIndex = selectNearestFrontPointToMouseClick(e, 20000.0);
        if (selectedFrontPointIndex != LOCATION_INDEX_NOT_FOUND)
        {
            if (selectedFrontPointIndex == 0)
            {
                addFrontPoint(0, 1);
            }
            else if (selectedFrontPointIndex == (userFrontLines.size()-1))
            {
                addFrontPoint((userFrontLines.size()-2), (userFrontLines.size()-1));
            }
            else
            {
                int closestFrontPointIndex = selectNearestFrontPointIndexToOtherFrontPoint (selectedFrontPointIndex);
                addFrontPoint(selectedFrontPointIndex, closestFrontPointIndex);
            }
        }        
    }   

    private void addFrontPoint(int selectedFrontPointRightIndex, int selectedFrontPointLeftIndex) throws PWCGException
    {
        FrontLinePoint selectedFrontPointRight = userFrontLines.get(selectedFrontPointRightIndex);
        FrontLinePoint selectedFrontPointLeft = userFrontLines.get(selectedFrontPointLeftIndex);
        
        double angle = MathUtils.calcAngle(selectedFrontPointRight.getPosition(), selectedFrontPointLeft.getPosition());
        double distance = MathUtils.calcDist(selectedFrontPointRight.getPosition(), selectedFrontPointLeft.getPosition());
        Coordinate frontCoordinate = MathUtils.calcNextCoord(selectedFrontPointRight.getPosition(), angle, distance / 2);

        FrontLinePoint newFrontFrontLinePoint = makeFrontLinePoint(frontCoordinate, selectedFrontPointRight.getName());
        userFrontLines.add(selectedFrontPointRightIndex, newFrontFrontLinePoint);
    }

    public void setFromMap(List<FrontLinePoint> frontLines)
    {
        userFrontLines.clear();
        for (FrontLinePoint location : frontLines)
        {
            userFrontLines.add(location);
        }        
    }

    public List<FrontLinePoint> getUserCreatedFrontLines()
    {
        return userFrontLines;
    }


    public void addAdditionalFrontLinePoints(List<FrontLinePoint> additionalFrontLinePoints)
    {
        userFrontLines.addAll(additionalFrontLinePoints);
    }

    private int selectNearestFrontPointToMouseClick(MouseEvent e, double radius)
    {
        try
        {
            int closestFrontLinePointIndex = LOCATION_INDEX_NOT_FOUND;

            Point point = new Point();
            point.x = e.getX();
            point.y = e.getY();
            Coordinate clickCoordinate = mapPanel.pointToCoordinate(point);

            double closestDistance = PositionFinder.ABSURDLY_LARGE_DISTANCE;
            for (int i = 0; i < userFrontLines.size(); ++i)
            {
                FrontLinePoint location = userFrontLines.get(i);
                double distance = MathUtils.calcDist(location.getPosition(), clickCoordinate);
                if (distance < radius)
                {
                    if (distance < closestDistance)
                    {
                        closestDistance = distance;
                        closestFrontLinePointIndex = i;
                    }
                }
            }
            
            return closestFrontLinePointIndex;
        }
        catch (Exception exp)
        {
            PWCGLogger.logException(exp);
        }
        
        return LOCATION_INDEX_NOT_FOUND;
    }
    

    private int selectNearestFrontPointIndexToOtherFrontPoint (int selectedFrontPointIndex)
    {
        if (selectedFrontPointIndex == 0)
        {
            return 1;
        }
        else if (selectedFrontPointIndex == (userFrontLines.size()-1))
        {
            return (userFrontLines.size()-2);
        }
        else
        {
            FrontLinePoint selectedFrontPoint = userFrontLines.get(selectedFrontPointIndex);
            FrontLinePoint selectedFrontPointRight = userFrontLines.get(selectedFrontPointIndex-1);
            FrontLinePoint selectedFrontPointLeft = userFrontLines.get(selectedFrontPointIndex+1);
            double distanceRight = MathUtils.calcDist(selectedFrontPoint.getPosition(), selectedFrontPointRight.getPosition());
            double distanceLeft = MathUtils.calcDist(selectedFrontPoint.getPosition(), selectedFrontPointLeft.getPosition());
            if (distanceRight < distanceLeft)
            {
                return (selectedFrontPointIndex-1);
            }
            else
            {
                return (selectedFrontPointIndex+1);
            }
        }
    }

    private FrontLinePoint makeFrontLinePoint(Coordinate coordinate, String name)
    {
        FrontLinePoint frontFrontLinePoint = new FrontLinePoint();
        frontFrontLinePoint.setPosition(coordinate);
        frontFrontLinePoint.setName(name);
        return frontFrontLinePoint;
    }

}
