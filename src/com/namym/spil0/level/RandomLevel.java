package com.namym.spil0.level;

import java.util.Random;

public class RandomLevel extends Level {
	
	private final Random random = new Random();

	public RandomLevel(int width, int height) {
		super(width, height); //Uanset hvad vi inds�tter her, bliver det kastet tilbage til Level
	}
	
	protected void generateLevel() {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				tiles[x + y * width] = random.nextInt(4);
			}
		}
	}

}
