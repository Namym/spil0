package com.namym.spil0.graphics;

import java.util.Random;

public class Screen {

	private int width, height;
	public int[] pixels;
	public final int MAP_SIZE = 64;
	public final int MAP_SIZE_MASK = MAP_SIZE - 1;
	
	public int[] tiles = new int[MAP_SIZE * MAP_SIZE]; // Størrelsen på vores tiles

	private Random random = new Random();

	public Screen(int width, int height) {
		this.width = width;
		this.height = height;
		pixels = new int[width * height];

		for (int i = 0; i < MAP_SIZE * MAP_SIZE; i++) { // For loop
			tiles[i] = random.nextInt(0xffffff); // Giver hver vores tile en tilfældig farve. Dens max er hvid.
		}
	}

	public void clear() { // Denne metode går alle pixels igennem, og slukker for dem
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = 0;

		}
	}

	public void render(int xOffset, int yOffset) { // Hvert loop styrer en pixel på skærmen
		for (int y = 0; y < height; y++) { // Så længe y er mindre en højden, så kører loopet.
			int yp = y + yOffset;
			if (yp < 0 || yp >= height) continue;
			for (int x = 0; x < width; x++) { // Samme som ovenover. //For hver y-pixel, kører hele rækken med x.
				int xp = x + xOffset;
				if (xp < 0 || xp >= width) continue;
				pixels[xp + yp * width] = Sprite.grass.pixels[(x & 15) + (y & 15) * Sprite.grass.SIZE]; // Vi specificerer hvilken pixel der skal gøres noget ved. x giver os x-koordinaten, og y * width giver os  y-koordinaten
			}
		}
	}
}
