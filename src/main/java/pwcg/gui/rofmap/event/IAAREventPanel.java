package pwcg.gui.rofmap.event;

import javax.swing.JPanel;

import pwcg.core.exception.PWCGException;

public interface IAAREventPanel
{
    boolean isShouldDisplay();
    void makePanel() throws PWCGException;
    void finished();
    JPanel getPanel();
}
