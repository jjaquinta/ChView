package chuck.terran.admin.ui;

import java.util.ArrayList;

import jo.d2k.data.data.StarColumn;
import jo.d2k.data.logic.StarColumnLogic;
import jo.util.ui.utils.GridUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;

public class StarColumnsPanel extends Composite
{
    private java.util.List<StarColumn>  mUsedColumns;
    private java.util.List<StarColumn>  mUnusedColumns;
    
    private List    mCtrlUsedColumns;
    private List    mCtrlUnusedColumns;
    private Button  mAdd;
    private Button  mDel;
    private Button  mUp;
    private Button  mDown;

    public StarColumnsPanel(Composite parent, int style)
    {
        super(parent, style);
        setLayout(new GridLayout(3, false));
        mCtrlUsedColumns = new List(this, SWT.MULTI|SWT.H_SCROLL|SWT.V_SCROLL);
        GridUtils.setLayoutData(mCtrlUsedColumns, "1x5 fill=hv");
        mAdd = GridUtils.makeButton(this, "<<<", "");
        mCtrlUnusedColumns = new List(this, SWT.MULTI|SWT.H_SCROLL|SWT.V_SCROLL);
        GridUtils.setLayoutData(mCtrlUnusedColumns, "1x5 fill=hv");
        mDel = GridUtils.makeButton(this, ">>>", "");
        mUp = GridUtils.makeButton(this, "^", "");
        mDown = GridUtils.makeButton(this, "v", "");
        GridUtils.makeLabel(this, "", "fill=v");
        
        setColumns(StarColumnLogic.getDefaultColumns());
        mAdd.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doAdd();
            }
        });
        mDel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doDel();
            }
        });
        mUp.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doUp();
            }
        });
        mDown.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doDown();
            }
        });
    }
    
    private void updateLists()
    {
        mCtrlUsedColumns.removeAll();
        for (StarColumn col : mUsedColumns)
            mCtrlUsedColumns.add(col.getTitle());
        mCtrlUnusedColumns.removeAll();
        for (StarColumn col : mUnusedColumns)
            mCtrlUnusedColumns.add(col.getTitle());
    }
    
    private void doAdd()
    {
        int[] sel = mCtrlUnusedColumns.getSelectionIndices();
        java.util.List<StarColumn> selected = new ArrayList<StarColumn>();
        for (int s : sel)
            selected.add(mUnusedColumns.get(s));
        mUnusedColumns.removeAll(selected);
        mUsedColumns.addAll(selected);
        updateLists();
    }
    
    private void doDel()
    {
        int[] sel = mCtrlUsedColumns.getSelectionIndices();
        java.util.List<StarColumn> selected = new ArrayList<StarColumn>();
        for (int s : sel)
            selected.add(mUsedColumns.get(s));
        mUsedColumns.removeAll(selected);
        mUnusedColumns.addAll(selected);
        updateLists();
    }
    
    private void doUp()
    {
        int sel = mCtrlUsedColumns.getSelectionIndex();
        if (sel <= 0)
            return;
        StarColumn col = mUsedColumns.get(sel);
        mUsedColumns.remove(col);
        mCtrlUsedColumns.remove(sel);
        mUsedColumns.add(sel - 1, col);
        mCtrlUsedColumns.add(col.getTitle(), sel - 1);
        mCtrlUsedColumns.select(sel - 1);
    }
    
    private void doDown()
    {
        int sel = mCtrlUsedColumns.getSelectionIndex();
        if ((sel <= 0) || (sel >= mCtrlUsedColumns.getItemCount() - 1))
            return;
        StarColumn col = mUsedColumns.get(sel);
        mUsedColumns.remove(col);
        mCtrlUsedColumns.remove(sel);
        mUsedColumns.add(sel + 1, col);
        mCtrlUsedColumns.add(col.getTitle(), sel + 1);
        mCtrlUsedColumns.select(sel + 1);
    }

    public java.util.List<StarColumn> getColumns()
    {
        return mUsedColumns;
    }

    public void setColumns(java.util.List<StarColumn> usedColumns)
    {
        mUsedColumns = usedColumns;
        mUnusedColumns = StarColumnLogic.getUnusedColumns(mUsedColumns);
        updateLists();
    }

}
