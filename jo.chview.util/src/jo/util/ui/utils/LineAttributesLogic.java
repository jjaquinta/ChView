package jo.util.ui.utils;

import java.util.StringTokenizer;

import jo.util.utils.obj.FloatUtils;
import jo.util.utils.obj.IntegerUtils;
import jo.util.utils.obj.StringUtils;

import org.eclipse.swt.graphics.LineAttributes;

public class LineAttributesLogic
{
    public static final String[] CAP_LABELS = {
        "Flat", "Round", "Square",
    };
    public static final String[] JOIN_LABELS = {
        "Miter", "Round", "Bevel",
    };
    public static final String[] STYLE_LABELS = {
        "Solid", "Dash", "Dot", "Dash-Dot", "Dash-Dot-Dot",
    };
    
    public static LineAttributes fromString(Object obj)
    {
        if (obj == null)
            return new LineAttributes(1f);
        else if (obj instanceof LineAttributes)
            return (LineAttributes)obj;
        else
            return fromString(obj.toString());
    }
    
    public static LineAttributes fromString(String txt)
    {
        LineAttributes attr = new LineAttributes(1f);
        if (StringUtils.isTrivial(txt))
            return attr;
        StringTokenizer st = new StringTokenizer(txt, ":");
        if (st.countTokens() != 7)
            throw new IllegalArgumentException("Unexpected format for LineAttributes '"+txt+"'");
        attr.cap = IntegerUtils.parseInt(st.nextToken());
        String dash = st.nextToken();
        if ("null".equals(dash))
            attr.dash = null;
        else
        {
            StringTokenizer st2 = new StringTokenizer(dash, "|");
            int len = IntegerUtils.parseInt(st2.nextToken());
            attr.dash = new float[len];
            for (int i = 0; i < len; i++)
                attr.dash[i] = FloatUtils.parseFloat(st2.nextToken());
        }
        attr.dashOffset = FloatUtils.parseFloat(st.nextToken());
        attr.join = IntegerUtils.parseInt(st.nextToken());
        attr.miterLimit = FloatUtils.parseFloat(st.nextToken());
        attr.style = IntegerUtils.parseInt(st.nextToken());
        attr.width = FloatUtils.parseFloat(st.nextToken());
        return attr;
    }
    
    public static String toString(LineAttributes attr)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(attr.cap);
        sb.append(":");
        if (attr.dash == null)
            sb.append("null");
        else
        {
            sb.append(attr.dash.length);
            for (float f : attr.dash)
            {
                sb.append("|");
                sb.append(f);
            }
        }
        sb.append(":");
        sb.append(attr.dashOffset);
        sb.append(":");
        sb.append(attr.join);
        sb.append(":");
        sb.append(attr.miterLimit);
        sb.append(":");
        sb.append(attr.style);
        sb.append(":");
        sb.append(attr.width);
        return sb.toString();
    }
}
