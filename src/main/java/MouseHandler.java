import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MouseHandler implements MouseListener {
    public int mouseX, mouseY;
    public boolean leftMouseClicked,  rightMouseClicked;
    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        if (e.getButton() == MouseEvent.BUTTON1) {
            //System.out.println("MOUSE PRESSED");
            leftMouseClicked = true;
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            rightMouseClicked = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            //System.out.println("MOUSE RELEASED");
            leftMouseClicked = false;
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            rightMouseClicked = false;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
