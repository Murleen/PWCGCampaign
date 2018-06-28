package pwcg.gui.rofmap.debrief;

import java.awt.BorderLayout;

import pwcg.core.exception.PWCGException;

public abstract class AAREventPanel extends AARPanel
{
    private static final long serialVersionUID = 1L;

    protected boolean shouldDisplay = false;

    public AAREventPanel ()
    {
        super();
        this.setLayout(new BorderLayout());
    }

    public boolean isShouldDisplay()
    {
        return shouldDisplay;
    }

    abstract public void makePanel() throws PWCGException;
    abstract public void finished();
}
