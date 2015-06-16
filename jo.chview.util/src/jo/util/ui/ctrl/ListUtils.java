/*
 * Created on Nov 1, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package jo.util.ui.ctrl;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.swt.widgets.List;

public class ListUtils
{
    public static void addAll(List list, String[] texts)
    {
        for (int i = 0; i < texts.length; i++)
            if (texts[i] != null)
                list.add(texts[i]);
    }

    public static void addAll(List list, Collection<?> texts)
    {
        for (Iterator<?> i = texts.iterator(); i.hasNext(); )
            list.add(i.next().toString());
    }
}
