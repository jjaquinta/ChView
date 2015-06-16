package jo.util.ui.utils;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;

public class ControlUtils
{
    public static void setText(Control ctrl, String txt)
    {
        if (ctrl == null)
            return;
        if (txt == null)
            txt = "";
        if (ctrl instanceof Text)
            ((Text)ctrl).setText(txt);
        else if (ctrl instanceof Button)
            ((Button)ctrl).setText(txt);
        else if (ctrl instanceof Link)
            ((Link)ctrl).setText("<a>"+txt+"</a>");
        else
            throw new IllegalArgumentException("Unsupported control: "+ctrl.getClass().getName());
    }
    
    public static String getText(Control ctrl)
    {
        if (ctrl == null)
            return "";
        if (ctrl instanceof Text)
            return ((Text)ctrl).getText();
        else if (ctrl instanceof Button)
            return ((Button)ctrl).getText();
        else if (ctrl instanceof Link)
        {
            String txt = ((Link)ctrl).getText();
            if (txt.startsWith("<a>"))
                txt = txt.substring(3);
            if (txt.endsWith("</a>"))
                txt = txt.substring(0, txt.length() - 4);
            return txt;
        }
        else
            throw new IllegalArgumentException("Unsupported control: "+ctrl.getClass().getName());
    }

    public static void setEnabled(Control ctrl, boolean enabled)
    {
        if (ctrl instanceof Composite)
        {
            for (Control c : ((Composite)ctrl).getChildren())
                setEnabled(c, enabled);
        }
        else
            ctrl.setEnabled(enabled);
    }
}
