/*
 * Created on May 15, 2005
 *
 */
package jo.util.ui.utils;

import java.util.Collection;
import java.util.StringTokenizer;

import jo.util.ui.ctrl.CButton;
import jo.util.ui.ctrl.ComboUtils;
import jo.util.ui.ctrl.ListUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

/**
 * @author Jo
 *
 */
public class GridUtils
{
    public static Label makeLabel(Composite parent)
    {
        return makeLabel(parent, (String)null, null);
    }
    public static Label makeLabel(Composite parent, String name, String style)
    {
        Label l = new Label(parent, SWT.LEFT);
        if (name != null)
            l.setText(name);
        if (style != null)
            setLayoutData(l, style);
        else
            setLayoutData(l, "fill=h");
        return l;
    }
    public static Label makeLabel(Composite parent, Image icon, String style)
    {
        Label l = new Label(parent, SWT.LEFT);
        if (icon != null)
            l.setImage(icon);
        if (style != null)
            setLayoutData(l, style);
        else
            setLayoutData(l, "fill=h");
        return l;
    }
    public static Text makeText(Composite parent, int ctrlStyle, String style)
    {
        Text l = new Text(parent, ctrlStyle);
        if (style != null)
            setLayoutData(l, style);
        else
            setLayoutData(l, "fill=h");
        return l;
    }
    public static Text makeText(Composite parent, String text, String style)
    {
        Text l = new Text(parent, SWT.NULL);
        if (style != null)
            setLayoutData(l, style);
        else
            setLayoutData(l, "fill=h");
        if (text != null)
            l.setText(text);
        return l;
    }
    public static Button makeButton(Composite parent, String name, String style)
    {
        Button l = new Button(parent, SWT.PUSH);
        if (name != null)
            l.setText(name);
        if (style != null)
            setLayoutData(l, style);
        else
            setLayoutData(l, "fill=h");
        return l;
    }
    public static Button makeButton(Composite parent, Image image, String style)
    {
        Button l = new Button(parent, SWT.PUSH);
        if (image != null)
            l.setImage(image);
        if (style != null)
            setLayoutData(l, style);
        else
            setLayoutData(l, "fill=h");
        return l;
    }
    public static Button makeButton(Composite parent, Image image, String text, String style)
    {
        Button l = new Button(parent, SWT.PUSH|SWT.LEFT);
        if (image != null)
            l.setImage(image);
        if (text != null)
            l.setText(text);
        if (style != null)
            setLayoutData(l, style);
        else
            setLayoutData(l, "fill=h");
        return l;
    }
    public static Button makeCheck(Composite parent, String name, String style)
    {
        Button l = new Button(parent, SWT.CHECK);
        if (name != null)
            l.setText(name);
        if (style != null)
            setLayoutData(l, style);
        else
            setLayoutData(l, "fill=h");
        return l;
    }
    public static Button makeRadio(Composite parent, String name, String style)
    {
        Button l = new Button(parent, SWT.RADIO);
        if (name != null)
            l.setText(name);
        if (style != null)
            setLayoutData(l, style);
        else
            setLayoutData(l, "fill=h");
        return l;
    }
    public static Composite makeComposite(Composite parent, int ctrlStyle, String style)
    {
        Composite l = new Composite(parent, ctrlStyle);
        if (style != null)
            setLayoutData(l, style);
        else
            setLayoutData(l, "fill=h");
        return l;
    }
    public static CButton makeCButton(Composite parent, Image image, String text, String style)
    {
        CButton l = new CButton(parent, SWT.PUSH|SWT.LEFT);
        if (image != null)
            l.setImage(image);
        if (text != null)
            l.setText(text);
        if (style != null)
            setLayoutData(l, style);
        else
            setLayoutData(l, "fill=h");
        return l;
    }
    public static Combo makeCombo(Composite parent, String[] elements, String style)
    {
        Combo c = new Combo(parent, SWT.DROP_DOWN|SWT.READ_ONLY);
        if (style != null)
            setLayoutData(c, style);
        else
            setLayoutData(c, "fill=h");
        ComboUtils.addAll(c, elements);
        return c;
    }
    public static Combo makeCombo(Composite parent, Collection<?> elements, String style)
    {
        Combo c = new Combo(parent, SWT.DROP_DOWN|SWT.READ_ONLY);
        if (style != null)
            setLayoutData(c, style);
        else
            setLayoutData(c, "fill=h");
        ComboUtils.addAll(c, elements);
        return c;
    }
    public static List makeList(Composite parent, String[] elements, String style)
    {
        List c = new List(parent, SWT.NULL);
        if (style != null)
            setLayoutData(c, style);
        else
            setLayoutData(c, "fill=h");
        if (elements != null)
            ListUtils.addAll(c, elements);
        return c;
    }
    public static List makeList(Composite parent, int ctrlStyle, String style)
    {
        List c = new List(parent, ctrlStyle);
        if (style != null)
            setLayoutData(c, style);
        else
            setLayoutData(c, "fill=h");
        return c;
    }
    public static GridData makeGridData(String style)
    {
        GridData ret = new GridData();
        if (style == null)
            style = "fill=h";
        for (StringTokenizer st = new StringTokenizer(style); st.hasMoreTokens(); )
        {
            String token = st.nextToken();
            if (token.startsWith("fill="))
            {
                token = token.substring(5);
                if (token.indexOf('h') >= 0)
                {
                    ret.grabExcessHorizontalSpace = true;
                    ret.horizontalAlignment = GridData.FILL;
                }
                if (token.indexOf('H') >= 0)
                {
                    ret.horizontalAlignment = GridData.FILL;
                }
                if (token.indexOf('v') >= 0)
                {
                    ret.grabExcessVerticalSpace = true;
                    ret.verticalAlignment = GridData.FILL;
                }
                if (token.indexOf('V') >= 0)
                {
                    ret.verticalAlignment = GridData.FILL;
                }
            }
            else if (token.startsWith("align="))
            {
                if (token.endsWith("=center"))
                {
                    ret.verticalAlignment = GridData.CENTER;
                    ret.horizontalAlignment = GridData.CENTER;
                }
                else if (token.endsWith("=north") || token.endsWith("=n"))
                {
                    ret.verticalAlignment = GridData.BEGINNING;
                    ret.horizontalAlignment = GridData.CENTER;
                }
                else if (token.endsWith("=northeast") || token.endsWith("=ne"))
                {
                    ret.verticalAlignment = GridData.BEGINNING;
                    ret.horizontalAlignment = GridData.END;
                }
                else if (token.endsWith("=east") || token.endsWith("=e"))
                {
                    ret.verticalAlignment = GridData.CENTER;
                    ret.horizontalAlignment = GridData.END;
                }
                else if (token.endsWith("=southeast") || token.endsWith("=se"))
                {
                    ret.verticalAlignment = GridData.END;
                    ret.horizontalAlignment = GridData.END;
                }
                else if (token.endsWith("=south") || token.endsWith("=s"))
                {
                    ret.verticalAlignment = GridData.END;
                    ret.horizontalAlignment = GridData.CENTER;
                }
                else if (token.endsWith("=southwest") || token.endsWith("=sw"))
                {
                    ret.verticalAlignment = GridData.END;
                    ret.horizontalAlignment = GridData.BEGINNING;
                }
                else if (token.endsWith("=west") || token.endsWith("=w"))
                {
                    ret.verticalAlignment = GridData.CENTER;
                    ret.horizontalAlignment = GridData.BEGINNING;
                }
                else if (token.endsWith("=northwest") || token.endsWith("=nw"))
                {
                    ret.verticalAlignment = GridData.BEGINNING;
                    ret.horizontalAlignment = GridData.BEGINNING;
                }
            }
            else if (token.startsWith("size="))
            {
                token = token.substring(5);
                int o = token.indexOf("x");
                if (o >= 0)
                {
                    if (!token.startsWith("*"))
                        ret.widthHint = Integer.parseInt(token.substring(0, o));
                    ret.heightHint = Integer.parseInt(token.substring(o+1));
                }
                else
                    ret.widthHint = Integer.parseInt(token);
            }
            else // span=
            {
                int off = token.indexOf('x');
                if (off < 0)
                    ret.horizontalSpan = Integer.parseInt(token);
                else if (off == 0)
                    ret.horizontalSpan = Integer.parseInt(token.substring(1));
                else
                {
                    ret.horizontalSpan = Integer.parseInt(token.substring(0, off));
                    ret.verticalSpan = Integer.parseInt(token.substring(off+1));
                }
            }
        }
        return ret;
    }

    public static void setLayoutData(Control control, String style)
    {
        control.setLayoutData(makeGridData(style));
    }
    
    public static String toString(GridData gd)
    {
        StringBuffer sb = new StringBuffer();
        if ((gd.horizontalSpan != 1) || (gd.verticalSpan != 1))
            sb.append(" "+gd.horizontalSpan+"x"+gd.verticalSpan);
        if ((gd.horizontalAlignment == GridData.FILL) || (gd.verticalAlignment == GridData.FILL))
        {
            sb.append(" fill=");
            if (gd.horizontalAlignment == GridData.FILL)
                sb.append("h");
            if (gd.verticalAlignment == GridData.FILL)
                sb.append("v");
        }
        if ((gd.widthHint >= 0) || (gd.heightHint >= 0))
        {
            sb.append(" size=");
            if (gd.widthHint >= -0)
                sb.append(String.valueOf(gd.widthHint));
            if (gd.heightHint >= 0)
                sb.append(String.valueOf(gd.heightHint));
        }
        return sb.toString().trim();
    }
}
