/*
 * Created on Oct 25, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package jo.util.ui.ctrl;

import jo.util.ui.act.GenericAction;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class ActionBar extends Composite
{
    public ActionBar(Composite parent, int style)
    {
        super(parent, style);
        if ((style&SWT.VERTICAL) != 0)
            setLayout(new FillLayout(SWT.VERTICAL));
        else
            setLayout(new FillLayout(SWT.HORIZONTAL));
    }
    
    public ActionButton add(GenericAction action)
    {
        ActionButton button = new ActionButton(this, SWT.NULL, action);
        return button;
    }
    
    public Button add(String name)
    {
        Button button = new Button(this, SWT.NULL);
        button.setText(name);
        return button;
    }
}
