
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseEvent;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class InputListener implements MouseListener, MouseMotionListener{

	private boolean[] buttons;
	private int mouseX;
	private int mouseY;
	
	public InputListener(){
		buttons = new boolean[2];
	}
	
	public boolean mouseDown(int n){//only 0 or 1 for Left and Right
		if(buttons[n]){
			return true;
		}else{
			return false;
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1){
			buttons[0] = true;
		}else if(e.getButton() == MouseEvent.BUTTON3){
			buttons[1] = true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1){
			buttons[0] = false;
		}else if(e.getButton() == MouseEvent.BUTTON3){
			buttons[1] = false;
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		Point mouse = MouseInfo.getPointerInfo().getLocation();
		mouseX = e.getX();
		mouseY = e.getY();
	}
	
	public int getMouseX(){
		return mouseX;
	}
	
	public int getMouseY(){
		return mouseY;
	}
}
