/*
 * Created on Jun 27, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package jo.util.ui.viewers;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.TableColumn;

public class TableSortSelectionListener implements SelectionListener {
    private final TableViewer mViewer;
    private final TableColumn mColumn;
    private final InvertableSorter mSorter;
    private final boolean mKeepDirection;
    private InvertableSorter mCurrentSorter;
 
    /**
     * The constructor of this listener.
     * 
     * @param viewer
     *            the tableviewer this listener belongs to
     * @param column
     *            the column this listener is responsible for
     * @param sorter
     *            the sorter this listener uses
     * @param defaultDirection
     *            the default sorting direction of this Listener. Possible
     *            values are {@link SWT.UP} and {@link SWT.DOWN}
     * @param keepDirection
     *            if true, the listener will remember the last sorting direction
     *            of the associated column and restore it when the column is
     *            reselected. If false, the listener will use the default soting
     *            direction
     */
    public TableSortSelectionListener(TableViewer viewer, TableColumn column,
            AbstractInvertableTableSorter sorter, int defaultDirection,
            boolean keepDirection) {
        mViewer = viewer;
        mColumn = column;
        mKeepDirection = keepDirection;
        mSorter = (defaultDirection == SWT.UP) ? sorter : sorter.getInverseSorter();
        mCurrentSorter = this.mSorter;
 
        mColumn.addSelectionListener(this);
    }
 
    /**
     * Chooses the colum of this listener for sorting of the table. Mainly used
     * when first initialising the table.
     */
    public void chooseColumnForSorting() {
        mViewer.getTable().setSortColumn(mColumn);
        mViewer.getTable().setSortDirection(mCurrentSorter.getSortDirection());
        mViewer.setSorter(mCurrentSorter);
    }
 
    public void widgetSelected(SelectionEvent e) {
        InvertableSorter newSorter;
        if (mViewer.getTable().getSortColumn() == mColumn) {
            newSorter = ((InvertableSorter) mViewer.getSorter())
                    .getInverseSorter();
        } else {
            if (mKeepDirection) {
                newSorter = mCurrentSorter;
            } else {
                newSorter = mSorter;
            }
        }
 
        mCurrentSorter = newSorter;
        chooseColumnForSorting();
    }
 
    public void widgetDefaultSelected(SelectionEvent e) {
        widgetSelected(e);
    }
}
