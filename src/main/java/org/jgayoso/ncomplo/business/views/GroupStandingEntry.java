package org.jgayoso.ncomplo.business.views;

import org.jgayoso.ncomplo.business.entities.GameSide;

/**
 * Represents a single team's row in a group phase standing table.
 * Points are computed as: win=3, draw=1, loss=0.
 */
public class GroupStandingEntry {

    private final GameSide team;
    private int played;
    private int won;
    private int drawn;
    private int lost;
    private int goalsFor;
    private int goalsAgainst;
    private boolean qualifies;

    public GroupStandingEntry(final GameSide team) {
        this.team = team;
    }

    public GameSide getTeam() {
        return this.team;
    }

    public int getPlayed() {
        return this.played;
    }

    public int getWon() {
        return this.won;
    }

    public int getDrawn() {
        return this.drawn;
    }

    public int getLost() {
        return this.lost;
    }

    public int getGoalsFor() {
        return this.goalsFor;
    }

    public int getGoalsAgainst() {
        return this.goalsAgainst;
    }

    public int getGoalDifference() {
        return this.goalsFor - this.goalsAgainst;
    }

    public int getPoints() {
        return (this.won * 3) + this.drawn;
    }

    public boolean isQualifies() {
        return this.qualifies;
    }

    public void setQualifies(final boolean qualifies) {
        this.qualifies = qualifies;
    }

    /** Called when this team wins a game. */
    public void recordWin(final int goalsFor, final int goalsAgainst) {
        this.played++;
        this.won++;
        this.goalsFor += goalsFor;
        this.goalsAgainst += goalsAgainst;
    }

    /** Called when this team draws a game. */
    public void recordDraw(final int goalsFor, final int goalsAgainst) {
        this.played++;
        this.drawn++;
        this.goalsFor += goalsFor;
        this.goalsAgainst += goalsAgainst;
    }

    /** Called when this team loses a game. */
    public void recordLoss(final int goalsFor, final int goalsAgainst) {
        this.played++;
        this.lost++;
        this.goalsFor += goalsFor;
        this.goalsAgainst += goalsAgainst;
    }
}
