package jo.d2k.admin.rcp.viz.chview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jo.chview.rcp.logic.StarSchemaControllerLogic;
import jo.d2k.admin.rcp.sys.ui.schema.ISchemaController;
import jo.d2k.data.data.ChViewContextBean;
import jo.d2k.data.data.FilterConditionBean;
import jo.d2k.data.data.StarColumn;
import jo.d2k.data.data.StarSchemaBean;
import jo.d2k.data.logic.StarColumnLogic;
import jo.d2k.data.logic.StarSchemaLogic;
import jo.d2k.data.logic.schema.StarSchemaComparatorLogic;
import jo.util.ui.ctrl.ComboUtils;
import jo.util.ui.utils.ControlUtils;
import jo.util.ui.utils.GridUtils;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class FilterConditionPanel extends Composite
{
    private FilterConditionBean mCondition;
    
    private ComboViewer   mColumn;
    private Combo   mOption;
    private Composite   mArgs;

    public FilterConditionPanel(Composite parent, int style)
    {
        super(parent, style);
        setLayout(new GridLayout(2, false));
        GridUtils.makeLabel(this, "Column:", "");
        mColumn = new ComboViewer(this);
        GridUtils.setLayoutData(mColumn.getControl(), "fill=h");
        mColumn.setContentProvider(new ArrayContentProvider());
        mColumn.setLabelProvider(new LabelProvider());
        mColumn.setInput(StarColumnLogic.getAllColumns());
        GridUtils.makeLabel(this, "Condition:", "");
        mOption = GridUtils.makeCombo(this, (String[])null, "fill=h");
        mArgs = GridUtils.makeComposite(this, SWT.NULL, "2x1 fill=hv");
        mArgs.setLayout(new GridLayout(7, false));
        
        mColumn.addSelectionChangedListener(new ISelectionChangedListener() {            
            @Override
            public void selectionChanged(SelectionChangedEvent event)
            {
                updateOption();
            }
        });
        mOption.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                updateArgs();
            }
        });
        mColumn.setSelection(new StructuredSelection(StarColumnLogic.getColumn(ChViewContextBean.NAME)));
    }
    
    private void updateOption()
    {
        IStructuredSelection colSel = (IStructuredSelection)mColumn.getSelection();
        StarColumn col = (StarColumn)colSel.getFirstElement();
        mOption.removeAll();
        ComboUtils.addAll(mOption, col.getComparator().getOptions());
        mOption.select(col.getComparator().getDefaultOption());
        updateArgs();
    }
    
    private void updateArgs()
    {
        IStructuredSelection colSel = (IStructuredSelection)mColumn.getSelection();
        StarColumn col = (StarColumn)colSel.getFirstElement();
        int opt = mOption.getSelectionIndex();
        ControlUtils.removeAll(mArgs);
        if (col.getComparator().isArgFor(opt))
        {
            int type = StarSchemaComparatorLogic.getType(col.getComparator());
            StarSchemaBean schema;
            if (col.getType() == StarColumn.TYPE_EXTRA)
                schema = StarSchemaLogic.getSchema(col.getID());
            else
            {
                schema = new StarSchemaBean();
                schema.setMetadataID(col.getID());
                schema.setTitle(col.getTitle());
                schema.setType(type);
            }
            ISchemaController controller = StarSchemaControllerLogic.getController(type);
            Control ctrl = controller.addUI(mArgs, schema, false);
            Map<String, String> metadata = new HashMap<String, String>();
            if (mCondition != null)
                metadata.put(schema.getMetadataID(), (String)mCondition.getArgument());
            controller.loadFromMetadata(ctrl, schema, metadata);
            mArgs.setData("controller", controller);
            mArgs.setData("ctrl", ctrl);
            mArgs.setData("schema", schema);
            mArgs.layout();
        }
    }

    public FilterConditionBean getCondition()
    {
        if (mCondition == null)
            mCondition = new FilterConditionBean();
        IStructuredSelection colSel = (IStructuredSelection)mColumn.getSelection();
        StarColumn col = (StarColumn)colSel.getFirstElement();
        mCondition.setID(col.getID());
        mCondition.setOption(mOption.getSelectionIndex());
        if (col.getComparator().isArgFor(mCondition.getOption()))
        {
            ISchemaController controller = (ISchemaController)mArgs.getData("controller");
            Control ctrl = (Control)mArgs.getData("ctrl");
            StarSchemaBean schema = (StarSchemaBean)mArgs.getData("schema");
            Map<String, String> metadata = new HashMap<String, String>();
            controller.storeToMetadata(ctrl, schema, metadata);
            mCondition.setArgument(metadata.get(schema.getMetadataID()));
        }
        else if (col.getType() == StarColumn.TYPE_PSEUDO)
            mCondition.setArgument(new ArrayList<FilterConditionBean>());
        return mCondition;
    }

    public void setCondition(FilterConditionBean condition)
    {
        mCondition = condition;
        if (mCondition != null)
        {
            mColumn.setSelection(new StructuredSelection(StarColumnLogic.getColumn(mCondition.getID())));
            mOption.select(mCondition.getOption());
        }
    }

}
