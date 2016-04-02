package com.ogarproject.ogar.server.physic;

/**
 * Created by Porama2 on 2/4/2016.
 */
public class TEST {
    public static void main(String args[]){
        MovementRecord rec = new MovementRecord(90,1);
        System.out.println(rec.toString());
        Vector vec = Calc.getVectorFromMovement(rec);
        System.out.println(vec.toString());
        rec = Calc.getMovementFromVector(vec);
        System.out.println(rec.toString());
    }
}
