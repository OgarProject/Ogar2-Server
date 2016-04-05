package com.ogarproject.ogar.server.physic;

import java.util.Random;

/**
 * Created by Porama2 on 2/4/2016.
 */
public class TEST {
    public static void main(String args[]) throws InterruptedException {
        while (true) {
            Random r = new Random();
            MovementData rec = new MovementData(r.nextInt(360), r.nextInt(100) / 10.0);
            System.out.println(rec.toString());
            Vector vec = Calc.getVectorFromMovement(rec);
            System.out.println(vec.toString());
            rec = Calc.getMovementFromVector(vec);
            System.out.println(rec.toString());
            System.out.println("============================================");
            Thread.sleep(2500);
        }
    }
}
