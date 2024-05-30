package com.mygdx.game;

import java.util.Comparator;

class Run {
      private String shipType, score,shotsFired, shotsMissed, shotsDodged, killCount, date;

    public Run(String shipType, String score, String shotsFired, String shotsMissed, String shotsDodged,String killCount, String date) {
        this.shipType = shipType;
        this.score = score;
        this.shotsFired = shotsFired;
        this.shotsMissed = shotsMissed;
        this.shotsDodged = shotsDodged;
        this.killCount = killCount;
        this.date = date;
    }

    public int getScore() {
        return Integer.parseInt(score);
    }

    public String getScoreString()
    {
        return score;
    }

    public String getShipType() {
        return shipType;
    }

    public String getShotsFired() {
        return shotsFired;
    }

    public String getShotsMissed() {
        return shotsMissed;
    }

    public String getShotsDodged() {
        return shotsDodged;
    }

    public String getKillCount() {
        return killCount;
    }

    public String getDate() {
        return date;
    }
}

class sortByScore implements Comparator<Run> {
    // Used for sorting in descending order
    public int compare(Run a, Run b)
    {
        return b.getScore() - a.getScore();
    }
}
