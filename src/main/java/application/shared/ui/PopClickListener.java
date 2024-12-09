package application.shared.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PopClickListener extends MouseAdapter {

    private PopupMenu pop;
    private boolean disabled;

    public PopClickListener(PopupMenu pop) {
        this.pop = pop;
    }


    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger())
            doPop(e);
    }

    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger())
            doPop(e);
    }

    private void doPop(MouseEvent e) {
        if (!disabled) {
            pop.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
