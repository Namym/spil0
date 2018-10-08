package com.namym.spil0.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keyboard implements KeyListener {
	
	private boolean[] keys = new boolean[120]; //120, pga. de forskellige tasters værdi W = 87, osv.
	public boolean up, down, left, right; //Alle de her ting er booleans
	
	public void update() {
		up = keys[KeyEvent.VK_UP] || keys[KeyEvent.VK_W]; //Op = UP eller W
		down = keys[KeyEvent.VK_DOWN] || keys[KeyEvent.VK_S]; //Ned = DOWN eller S
		left = keys[KeyEvent.VK_LEFT] || keys[KeyEvent.VK_A]; //Venstre = LEFT eller A
		right = keys[KeyEvent.VK_RIGHT] || keys[KeyEvent.VK_D]; //Højre = RIGHT eller D
	}
	

	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode()] = true; //Sætter den knap vi har trykket til true
	}

	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false; //Samme som den ovenover, bare omvendt
	}

	public void keyTyped(KeyEvent e) {
		
	}

}
