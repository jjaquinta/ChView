/*
 * Created on Nov 1, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package jo.util.ui.ctrl;

import java.util.Collection;
import java.util.Iterator;

import jo.util.utils.obj.StringUtils;

import org.eclipse.swt.widgets.Combo;

public class ComboUtils
{
    public static void addAll(Combo combo, String[] texts)
    {
        for (int i = 0; i < texts.length; i++)
            if (texts[i] != null)
                combo.add(texts[i]);
    }
    public static void addAll(Combo combo, Collection<?> texts)
    {
        for (Iterator<?> i = texts.iterator(); i.hasNext(); )
        {
            String txt = i.next().toString();
            if (!StringUtils.isTrivial(txt))
                combo.add(txt);
        }
    }
	public static int select(Combo combo, String sel) 
	{
		if (sel != null)
			for (int i = 0; i < combo.getItemCount(); i++)
				if (sel.equals(combo.getItem(i)))
				{
					combo.select(i);
					return i;
				}
		combo.select(-1);
		return -1;
	}
}
