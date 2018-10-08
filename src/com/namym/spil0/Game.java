package com.namym.spil0;

import java.awt.Canvas;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

import com.namym.spil0.graphics.*;
import com.namym.spil0.input.Keyboard;

public class Game extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L; // S�t dette ind for at f� en fejl til at g� v�k

	public static int width = 300; // width, height og scale vil blive brugt til sk�rmst�rrelsen
	public static int height = width / 16 * 9;
	public static int scale = 3;
	public static String title = "Spil";

	private Thread thread; // En process inde i en process.
	private JFrame frame; // JFrame er et vindue
	private Keyboard key;
	private boolean running = false; // Indikere om spillet k�rer

	private Screen screen;

	private BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); // Det er et billede med en buffer																						
	private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData(); // Laver vores image om til et arrays med integers

	public Game() { // Koden her inde vil kun k�re en gang, n�r man starter den
		Dimension size = new Dimension(width * scale, height * scale);
		setPreferredSize(size); // S�tter st�rrelsen p� vores canvas

		screen = new Screen(width, height); // Henter sk�rmen
		frame = new JFrame(); // Henter frames
		key = new Keyboard(); // Henter keys

		addKeyListener(key); // Lytter efter om knapper bliver trykket

	}

	public synchronized void start() { // syncrhonized s�rger for at alt k�rer som det skal, ingen overlap afinformationer
		running = true;
		thread = new Thread(this, "Display"); // this binder det her til vores Game objekt
		thread.start();
	}

	public synchronized void stop() {
		running = false;
		try { // Pr�v at g�re det nedenunder
			thread.join();
		} catch (InterruptedException e) { // Hvis det ovenover ikke virker, s� luk ned
			e.printStackTrace();
		}
	}

	public void run() {
		long lastTime = System.nanoTime(); // Systemtiden i nanosekunder
		long timer = System.currentTimeMillis();
		final double ns = 1_000_000_000.0 / 60.0;
		double delta = 0;
		int frames = 0;
		int updates = 0;
		requestFocus();
		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns; // Ligger forskellen mellem de to tider til delta
			lastTime = now;
			while (delta >= 1) { // Dette vil ske 60 gange i sekundet
				update(); // Kan ogs� kaldes tick. Opdaterer logikken i spillet
				updates++;
				delta--;
			}
			render(); // Viser billderne p� sk�rmen
			frames++;

			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				System.out.println(updates + " ups, " + frames + " fps"); // Printer updates og frames i konsollen
				frame.setTitle(title + "  |  " + updates + " ups, " + frames + " fps"); // Printer updates og frames i titlen
				updates = 0;
				frames = 0;
			}
		}
		stop();
	}

	int x = 0, y = 0;

	public void update() {
		key.update();
		if (key.up) y--; //OP
		if (key.down) y++; //NED
		if (key.left) x--; //VENSTRE
		if (key.right) x++; //H�JRE

	}

	public void render() {
		BufferStrategy bs = getBufferStrategy(); // Vi laver en buffer strategy ved navn bs
		if (bs == null) { // Hvis bs ikke findes
			createBufferStrategy(3); // Lav bs. 3 fordi det er godt at have flere buffere, s� cpu kan lave noget
			return;
		}

		screen.clear(); // Ryder sk�rmen
		screen.render(x, y); // Render billedet p� sk�rmen

		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = screen.pixels[i]; // G�r igennem alle v�rdier i pixels[], og s�tter dem lig screen.pixels
		}

		Graphics g = bs.getDrawGraphics(); // Laver en forbindelse mellem Graphics og buffer
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null); // tegner vores sk�rm. 0, 0 er det �verste venstre hj�rne.
		g.dispose(); // Fjerner det grafik data v har. Hver gang vi har lavet en frame, vil vi af med den
		bs.show(); // Viser den n�ste buffer, som er blevet beregnet
	}

	public static void main(String[] args) { // Det er her programmet starter
		Game game = new Game(); // Starter en ny instance af Game
		game.frame.setResizable(false); // Vinduets st�rrelse kan ikke �ndres (skal g�res f�r pack)
		game.frame.setTitle(Game.title); // S�tter titlen p� spillet
		game.frame.add(game); // Tager spillet og s�tter det ind p� vores frame. Fylder vinduet med noget
		game.frame.pack(); // G�r at vores frame er den rigtige st�rrelse
		game.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // N�r du trykker luk, bliver processen lukket
		game.frame.setLocationRelativeTo(null); // S�tter vinduet midt i sk�rmen
		game.frame.setVisible(true); // Viser vores vindue

		game.start();
	}

}
