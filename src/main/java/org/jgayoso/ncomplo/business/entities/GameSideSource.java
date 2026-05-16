package org.jgayoso.ncomplo.business.entities;

import javax.persistence.*;

/**
 * Describes where a game's participant (side A or side B) comes from in a
 * tournament bracket.
 *
 * Three source types are supported:
 *
 *  GROUP_POSITION  →  "Nth place of a given CompetitionGroup"
 *                     e.g. 1st of Group A  → label "A1"
 *
 *  GAME_WINNER     →  "Winner of a given Game"
 *                     e.g. Winner of game #33 → label "W33"
 *
 *  GAME_LOSER      →  "Loser of a given Game" (used for 3rd-place match)
 *                     e.g. Loser of game #35  → label "L35"
 */
@Entity
@Table(name = "GAME_SIDE_SOURCE")
public class GameSideSource {

    public enum SourceType {
        GROUP_POSITION,
        GAME_WINNER,
        GAME_LOSER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "SOURCE_TYPE", nullable = false, length = 20)
    private SourceType sourceType;

    /** For GROUP_POSITION: the group to look the position up in. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID", nullable = true)
    private CompetitionGroup group;

    /** For GROUP_POSITION: 1 = 1st place, 2 = 2nd place, etc. */
    @Column(name = "POSITION", nullable = true)
    private Integer position;

    /** For GAME_WINNER / GAME_LOSER: the source game. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SOURCE_GAME_ID", nullable = true)
    private Game sourceGame;

    public GameSideSource() {
        super();
    }

    // -----------------------------------------------------------------------
    // Convenience factory methods
    // -----------------------------------------------------------------------

    public static GameSideSource groupPosition(final CompetitionGroup group, final int position) {
        final GameSideSource s = new GameSideSource();
        s.sourceType = SourceType.GROUP_POSITION;
        s.group = group;
        s.position = position;
        return s;
    }

    public static GameSideSource gameWinner(final Game sourceGame) {
        final GameSideSource s = new GameSideSource();
        s.sourceType = SourceType.GAME_WINNER;
        s.sourceGame = sourceGame;
        return s;
    }

    public static GameSideSource gameLoser(final Game sourceGame) {
        final GameSideSource s = new GameSideSource();
        s.sourceType = SourceType.GAME_LOSER;
        s.sourceGame = sourceGame;
        return s;
    }

    // -----------------------------------------------------------------------
    // Display label (e.g. "A1", "W33", "L35")
    // -----------------------------------------------------------------------

    public String getDisplayLabel() {
        if (sourceType == null) return "";
        switch (sourceType) {
            case GROUP_POSITION:
                if (group != null && position != null) {
                    return group.getName() + position;
                }
                return "?";
            case GAME_WINNER:
                return sourceGame != null ? "W" + sourceGame.getOrder() : "W?";
            case GAME_LOSER:
                return sourceGame != null ? "L" + sourceGame.getOrder() : "L?";
            default:
                return "";
        }
    }

    // -----------------------------------------------------------------------
    // Getters / Setters
    // -----------------------------------------------------------------------

    public Integer getId() {
        return this.id;
    }

    public SourceType getSourceType() {
        return this.sourceType;
    }

    public void setSourceType(final SourceType sourceType) {
        this.sourceType = sourceType;
    }

    public CompetitionGroup getGroup() {
        return this.group;
    }

    public void setGroup(final CompetitionGroup group) {
        this.group = group;
    }

    public Integer getPosition() {
        return this.position;
    }

    public void setPosition(final Integer position) {
        this.position = position;
    }

    public Game getSourceGame() {
        return this.sourceGame;
    }

    public void setSourceGame(final Game sourceGame) {
        this.sourceGame = sourceGame;
    }
}
