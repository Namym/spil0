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
	private static final long serialVersionUID = 1L; // Sæt dette ind for at få en fejl til at gå væk

	public static int width = 300; // width, height og scale vil blive brugt til skærmstørrelsen
	public static int height = width / 16 * 9;
	public static int scale = 3;
	public static String title = "Spil";

	private Thread thread; // En process inde i en process.
	private JFrame frame; // JFrame er et vindue
	private Keyboard key;
	private boolean running = false; // Indikere om spillet kører

	private Screen screen;

	private BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); // Det er et billede med en buffer																						
	private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData(); // Laver vores image om til et arrays med integers

	public Game() { // Koden her inde vil kun køre en gang, når man starter den
		Dimension size = new Dimension(width * scale, height * scale);
		setPreferredSize(size); // Sætter størrelsen på vores canvas

		screen = new Screen(width, height); // Henter skærmen
		frame = new JFrame(); // Henter frames
		key = new Keyboard(); // Henter keys

		addKeyListener(key); // Lytter efter om knapper bliver trykket

	}

	public synchronized void start() { // syncrhonized sørger for at alt kører som det skal, ingen overlap afinformationer
		running = true;
		thread = new Thread(this, "Display"); // this binder det her til vores Game objekt
		thread.start();
	}

	public synchronized void stop() {
		running = false;
		try { // Prøv at gøre det nedenunder
			thread.join();
		} catch (InterruptedException e) { // Hvis det ovenover ikke virker, så luk ned
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
				update(); // Kan også kaldes tick. Opdaterer logikken i spillet
				updates++;
				delta--;
			}
			render(); // Viser billderne på skærmen
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
		if (key.right) x++; //HØJRE

	}

	public void render() {
		BufferStrategy bs = getBufferStrategy(); // Vi laver en buffer strategy ved navn bs
		if (bs == null) { // Hvis bs ikke findes
			createBufferStrategy(3); // Lav bs. 3 fordi det er godt at have flere buffere, så cpu kan lave noget
			return;
		}

		screen.clear(); // Ryder skærmen
		screen.render(x, y); // Render billedet på skærmen

		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = screen.pixels[i]; // Går igennem alle værdier i pixels[], og sætter dem lig screen.pixels
		}

		Graphics g = bs.getDrawGraphics(); // Laver en forbindelse mellem Graphics og buffer
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null); // tegner vores skærm. 0, 0 er det øverste venstre hjørne.
		g.dispose(); // Fjerner det grafik data v har. Hver gang vi har lavet en frame, vil vi af med den
		bs.show(); // Viser den næste buffer, som er blevet beregnet
	}

	public static void main(String[] args) { // Det er her programmet starter
		Game game = new Game(); // Starter en ny instance af Game
		game.frame.setResizable(false); // Vinduets størrelse kan ikke ændres (skal gøres før pack)
		game.frame.setTitle(Game.title); // Sætter titlen på spillet
		game.frame.add(game); // Tager spillet og sætter det ind på vores frame. Fylder vinduet med noget
		game.frame.pack(); // Gør at vores frame er den rigtige størrelse
		game.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Når du trykker luk, bliver processen lukket
		game.frame.setLocationRelativeTo(null); // Sætter vinduet midt i skærmen
		game.frame.setVisible(true); // Viser vores vindue

		game.start();
	}

}
