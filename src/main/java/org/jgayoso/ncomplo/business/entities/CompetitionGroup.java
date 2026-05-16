package org.jgayoso.ncomplo.business.entities;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "COMPETITION_GROUP")
public class CompetitionGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "NAME", nullable = false, length = 100)
    private String name;

    @ManyToOne
    @JoinColumn(name = "COMPETITION_ID", nullable = false)
    private Competition competition;

    /**
     * The round that represents the group stage (e.g. the "Groups" round).
     * Games in this round for teams belonging to this group are used to
     * compute the group standing table.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROUND_ID", nullable = true)
    private Round round;

    /**
     * How many teams from this group advance to the next phase.
     */
    @Column(name = "TEAMS_QUALIFYING", nullable = false)
    private int teamsQualifying = 2;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "COMPETITION_GROUP_TEAM",
        joinColumns = @JoinColumn(name = "GROUP_ID"),
        inverseJoinColumns = @JoinColumn(name = "GAME_SIDE_ID")
    )
    private final Set<GameSide> teams = new LinkedHashSet<>();

    public CompetitionGroup() {
        super();
    }

    public Integer getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Competition getCompetition() {
        return this.competition;
    }

    public void setCompetition(final Competition competition) {
        this.competition = competition;
    }

    public Round getRound() {
        return this.round;
    }

    public void setRound(final Round round) {
        this.round = round;
    }

    public int getTeamsQualifying() {
        return this.teamsQualifying;
    }

    public void setTeamsQualifying(final int teamsQualifying) {
        this.teamsQualifying = teamsQualifying;
    }

    public Set<GameSide> getTeams() {
        return this.teams;
    }

    @Override
    public String toString() {
        return "CompetitionGroup{name='" + name + "'}";
    }
}
