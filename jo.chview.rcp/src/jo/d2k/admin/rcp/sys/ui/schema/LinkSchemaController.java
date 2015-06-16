package jo.d2k.admin.rcp.sys.ui.schema;

import java.awt.Desktop;
import java.net.URI;
import java.util.Map;

import jo.d2k.data.data.StarSchemaBean;
import jo.util.ui.utils.ControlUtils;
import jo.util.ui.utils.GridUtils;
import jo.util.utils.obj.StringUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class LinkSchemaController implements ISchemaController
{
    private static final String DELIM = "\ufeff";

    @Override
    public Control addUI(Composite parent, StarSchemaBean schema,
            boolean readOnly)
    {
        Control ctrl;
        if (readOnly)
        {
            GridUtils.makeLabel(parent, schema.getTitle()+":", "align=nw");
            ctrl = new Link(parent, SWT.NULL);
            GridUtils.setLayoutData(ctrl, "6x1 fill=h");
            ((Link)ctrl).addListener(SWT.Selection, new Listener() {                
                @Override
                public void handleEvent(Event ev)
                {
                    String url = (String)ev.widget.getData("url");
                    if (StringUtils.isTrivial(url))
                        return;
                    try
                    {
                        Desktop.getDesktop().browse(new URI(url));
                    }
                    catch (Exception e)
                    {
                    }
                }
            });
        }
        else
        {
            GridUtils.makeLabel(parent, schema.getTitle()+":", "align=nw 1x2");
            ctrl = GridUtils.makeText(parent, "", "5x1 fill=h");
            GridUtils.makeLabel(parent, "(text)", "");
            Text link = GridUtils.makeText(parent, "", "5x1 fill=h");
            GridUtils.makeLabel(parent, "(link)", "");
            ctrl.setData("link", link);
        }
        return ctrl;
    }

    @Override
    public void loadFromMetadata(Control ctrl, StarSchemaBean schema,
            Map<String, String> metadata)
    {
        String val = metadata.get(StarSchemaUIController.schemaToID(schema));
        String title;
        String url;
        if (val == null)
            return;
        int o = val.indexOf(DELIM);
        if (o < 0)
        {
            title = val;
            if (title.indexOf("://") > 0)
                url = title;
            else
                url = "";
        }
        else
        {
            title = val.substring(0, o);
            url = val.substring(o + 1);
        }
        ControlUtils.setText(ctrl, title);
        Text link = (Text)ctrl.getData("link");
        if (link != null)
            link.setText(url);
        else
            ctrl.setData("url", url);
    }

    @Override
    public void storeToMetadata(Control ctrl, StarSchemaBean schema,
            Map<String, String> metadata)
    {
        String val = ControlUtils.getText(ctrl);
        Text link = (Text)ctrl.getData("link");
        if (link != null)
            val += DELIM + link.getText();
        if (StringUtils.isTrivial(val))
            metadata.remove(StarSchemaUIController.schemaToID(schema));
        else
            metadata.put(StarSchemaUIController.schemaToID(schema), val);
    }

    @Override
    public String getName()
    {
        return "Link";
    }

    @Override
    public void addDefUI(Composite parent)
    {
        parent.setLayout(new GridLayout(2, false));
        GridUtils.makeLabel(parent, "Max Size:", "");
        Text textMaxSize = GridUtils.makeText(parent, SWT.NULL, "fill=h");
        parent.setData("textMaxSize", textMaxSize);
    }

    @Override
    public void loadFromDefPanel(StarSchemaBean schema, Composite parent)
    {
        schema.setSubType(new String[0]);
    }

    @Override
    public void storeToDefPanel(StarSchemaBean schema, Composite parent)
    {
    }
}
