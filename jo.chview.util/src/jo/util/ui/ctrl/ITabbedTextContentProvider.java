package jo.util.ui.ctrl;

public interface ITabbedTextContentProvider
{
    public String[] getPrimaryLabels();
    public String   getSecondaryText(String primaryText);
}
