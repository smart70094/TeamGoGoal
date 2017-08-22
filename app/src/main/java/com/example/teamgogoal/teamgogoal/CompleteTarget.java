package com.example.teamgogoal.teamgogoal;

/**
 * Created by Gary on 2017/7/12.
 */

class CompleteTarget {
    private String tid;
    private String target;
    private String planet;

    public CompleteTarget(String tid, String target, String planet) {
        this.tid = tid;
        this.target = target;
        this.planet = planet;
    }
    public String getTid(){return tid;}

    public String getTarget() {
        return target;
    }

    public String getPlanet() {
        return planet;
    }


}
