package jo.util.ui.utils;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import jo.util.utils.DebugUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class FormUtils
{
    private static Map<Control, Map<String,Control>>  mComponentMap = new HashMap<Control, Map<String,Control>>();
    private static long     mUniqueID = System.currentTimeMillis();
    
    public static Label makeLabel(Composite parent, String name, String style)
    {
        Label l = new Label(parent, SWT.LEFT);
        if (name != null)
            l.setText(name);
        if (style != null)
            setLayoutData(l, style);
        else
            setLayoutData(l, "0,0,100,100");
        return l;
    }
    public static Text makeText(Composite parent, int ctrlStyle, String style)
    {
        Text l = new Text(parent, ctrlStyle);
        if (style != null)
            setLayoutData(l, style);
        else
            setLayoutData(l, "0,0,100,100");
        return l;
    }
    public static Text makeText(Composite parent, String text, String style)
    {
        Text l = new Text(parent, SWT.NULL);
        if (style != null)
            setLayoutData(l, style);
        else
            setLayoutData(l, "0,0,100,100");
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
            setLayoutData(l, "0,0,100,100");
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
            setLayoutData(l, "0,0,100,100");
        return l;
    }
    public static FormData makeFormData(Composite context, String style)
    {
        FormData ret = new FormData();
        StringTokenizer st = new StringTokenizer(style, ",");
        ret.left = makeFormAttachment(context, st.nextToken());
        ret.top = makeFormAttachment(context, st.nextToken());
        ret.right = makeFormAttachment(context, st.nextToken());
        ret.bottom = makeFormAttachment(context, st.nextToken());
        return ret;
    }
    
    public static FormAttachment makeFormAttachment(Composite context, String style)
    {
        if (style == null)
            return null;
        if (style.equals("-"))
            return null;
        Control control = findControl(context, style);
        if (control != null)
        {
            String key = control.toString();
            if (key.length() + 1 < style.length())
                style = style.substring(key.length()+1);
            else
                style = "";
            StringTokenizer st = new StringTokenizer(style, ":");
            if (!st.hasMoreTokens())
                return new FormAttachment(control);
            else
            {
                int offset = Integer.parseInt(st.nextToken());
                if (!st.hasMoreTokens())
                    return new FormAttachment(control, offset);
                else
                {
                    String alignment = st.nextToken();
                    if ("left".equalsIgnoreCase(alignment))
                        return new FormAttachment(control, offset, SWT.LEFT);
                    if ("right".equalsIgnoreCase(alignment))
                        return new FormAttachment(control, offset, SWT.RIGHT);
                    if ("top".equalsIgnoreCase(alignment))
                        return new FormAttachment(control, offset, SWT.TOP);
                    if ("bottom".equalsIgnoreCase(alignment))
                        return new FormAttachment(control, offset, SWT.BOTTOM);
                    if ("center".equalsIgnoreCase(alignment))
                        return new FormAttachment(control, offset, SWT.CENTER);
                    return new FormAttachment(control, offset);
                }
            }
        }
        else
        {
            try
            {
                StringTokenizer st = new StringTokenizer(style, ":");
                if (st.countTokens() == 0)
                    return null;
                int numerator = Integer.parseInt(st.nextToken());
                if (!st.hasMoreTokens())
                    return new FormAttachment(numerator);
                else
                {
                    int denominator = Integer.parseInt(st.nextToken());
                    if (!st.hasMoreTokens())
                        return new FormAttachment(numerator, denominator);
                    else
                    {
                        int offset = Integer.parseInt(st.nextToken());
                        return new FormAttachment(numerator, denominator, offset);
                    }
                }
            }
            catch (NumberFormatException e)
            {
                DebugUtils.error("FormUtils: "+"Problem trying to resolve '"+style+"'!");
                return null;
            }
        }
    }
    
    public static void setLayoutData(Control control, String style)
    {
        //DebugUtils.trace("FormUtils: "+control.toString()+"<-"+style);
        addControl(control);
        control.setLayoutData(makeFormData(control.getParent(), style));
    }

    private static Control findControl(Composite context, String ident)
    {
        //DebugUtils.trace("FormUtils: "+"Resolving "+ident);
        if (!mComponentMap.containsKey(context))
        {
            DebugUtils.error("FormUtils: "+"  Bad context!");
            return null;
        }
        Map<String,Control> map = mComponentMap.get(context);
        for (String key : map.keySet())
        {
            if (ident.startsWith(key))
            {
                Control c = map.get(key);
                if (c == null)
                    DebugUtils.error("FormUtils: "+"  Can't resolve!");
                //else
                //    DebugUtils.trace("FormUtils: "+"  Resolved -> "+c.toString());
                return c;
            }
        }
        return null;
    }
    
    private static void addControl(Control control)
    {
        //DebugUtils.trace("FormUtils: "+"Adding "+control.toString());
        Map<String,Control> map = mComponentMap.get(control.getParent());
        if (map == null)
        {
            map = new HashMap<String, Control>();
            mComponentMap.put(control.getParent(), map);
        }
        map.put(control.toString(), control);
        String unid = "UNID"+String.valueOf(mUniqueID++);
        control.setData("unid", unid);
        map.put(unid, control);
    }
    
    public static void clearContext(Composite context)
    {
        mComponentMap.remove(context);
    }
    
    public static void setLayoutData(Control control, Object left, Object top, Object right, Object bottom)
    {
        FormData data = new FormData();
        data.left = makeFormAttachment(left);
        data.top = makeFormAttachment(top);
        data.right = makeFormAttachment(right);
        data.bottom = makeFormAttachment(bottom);
        control.setLayoutData(data);
    }
    
    public static FormAttachment makeFormAttachment(Object anchor)
    {
        if (anchor == null)
            return null;
        if (anchor instanceof Control)
            return new FormAttachment((Control)anchor);
        if (anchor instanceof Number)
            return new FormAttachment(((Number)anchor).intValue());
        if (anchor instanceof String)
            try
            {
                return new FormAttachment(Integer.parseInt((String)anchor));
            }
            catch (Exception e){};
        return null;
    }
}
