package org.jgayoso.ncomplo.business.services;

import org.jgayoso.ncomplo.business.entities.CompetitionGroup;
import org.jgayoso.ncomplo.business.entities.Game;
import org.jgayoso.ncomplo.business.entities.GameSide;
import org.jgayoso.ncomplo.business.entities.Round;
import org.jgayoso.ncomplo.business.views.GroupStandingEntry;
import org.jgayoso.ncomplo.web.admin.beans.BetBean;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Computes group phase standing tables.
 *
 * The standings are computed from game results.
 * When userBets is provided (non-null), the scores are taken from the user's
 * bets rather than the real game results, so the table shows the "simulated"
 * classification based on what the user predicted.
 *
 * Tiebreaking order (standard football):
 *   1. Points (3W + 1D)
 *   2. Goal difference (GF - GA)
 *   3. Goals for
 *   4. Head-to-head points between tied teams
 *   5. Head-to-head goal difference
 */
@Service
public class GroupStandingsService {

    /**
     * Compute standings for a single group.
     *
     * @param group    the competition group
     * @param allGames all games in the competition
     * @param userBets map of gameId -> BetBean; if null, real game scores are used
     * @return ordered list of standing entries, qualifying teams marked
     */
    public List<GroupStandingEntry> computeStandings(
            final CompetitionGroup group,
            final Collection<Game> allGames,
            final Map<Integer, BetBean> userBets) {

        final Round groupRound = group.getRound();
        final Set<GameSide> groupTeams = group.getTeams();

        // Build a map of team -> entry
        final Map<Integer, GroupStandingEntry> entries = new LinkedHashMap<>();
        for (final GameSide team : groupTeams) {
            entries.put(team.getId(), new GroupStandingEntry(team));
        }

        // Collect the games relevant to this group: in the group round, and
        // where both sides belong to this group
        final Set<Integer> teamIds = new HashSet<>();
        for (final GameSide t : groupTeams) {
            teamIds.add(t.getId());
        }

        for (final Game game : allGames) {

            // Filter: must belong to the group's round
            if (groupRound != null && !groupRound.getId().equals(game.getRound().getId())) {
                continue;
            }

            final GameSide sideA = game.getGameSideA();
            final GameSide sideB = game.getGameSideB();

            if (sideA == null || sideB == null) {
                continue;
            }

            // Both teams must be in this group
            if (!teamIds.contains(sideA.getId()) || !teamIds.contains(sideB.getId())) {
                continue;
            }

            // Determine the scores to use
            final Integer scoreA;
            final Integer scoreB;

            if (userBets != null) {
                final BetBean bet = userBets.get(game.getId());
                if (bet == null || bet.getScoreA() == null || bet.getScoreB() == null) {
                    continue; // no bet entered yet, skip
                }
                scoreA = bet.getScoreA();
                scoreB = bet.getScoreB();
            } else {
                if (game.getScoreA() == null || game.getScoreB() == null) {
                    continue; // game not played yet
                }
                scoreA = game.getScoreA();
                scoreB = game.getScoreB();
            }

            final GroupStandingEntry entryA = entries.get(sideA.getId());
            final GroupStandingEntry entryB = entries.get(sideB.getId());

            if (entryA == null || entryB == null) {
                continue;
            }

            if (scoreA > scoreB) {
                entryA.recordWin(scoreA, scoreB);
                entryB.recordLoss(scoreB, scoreA);
            } else if (scoreA < scoreB) {
                entryB.recordWin(scoreB, scoreA);
                entryA.recordLoss(scoreA, scoreB);
            } else {
                entryA.recordDraw(scoreA, scoreB);
                entryB.recordDraw(scoreB, scoreA);
            }
        }

        final List<GroupStandingEntry> standing = new ArrayList<>(entries.values());

        // Sort with tiebreaking
        standing.sort(new GroupStandingComparator(standing));

        // Mark qualifying teams
        final int teamsQualifying = group.getTeamsQualifying();
        for (int i = 0; i < standing.size(); i++) {
            standing.get(i).setQualifies(i < teamsQualifying);
        }

        return standing;
    }

    /**
     * Comparator applying standard football tiebreaking rules:
     * 1. Points (descending)
     * 2. Goal difference (descending)
     * 3. Goals for (descending)
     * (Head-to-head would require a second pass and is omitted for simplicity)
     */
    private static class GroupStandingComparator implements Comparator<GroupStandingEntry> {

        GroupStandingComparator(final List<GroupStandingEntry> all) {
            // reserved for future head-to-head computation
        }

        @Override
        public int compare(final GroupStandingEntry a, final GroupStandingEntry b) {
            // 1. Points descending
            int cmp = Integer.compare(b.getPoints(), a.getPoints());
            if (cmp != 0) return cmp;

            // 2. Goal difference descending
            cmp = Integer.compare(b.getGoalDifference(), a.getGoalDifference());
            if (cmp != 0) return cmp;

            // 3. Goals for descending
            cmp = Integer.compare(b.getGoalsFor(), a.getGoalsFor());
            if (cmp != 0) return cmp;

            // 4. Alphabetical by team name (stable fallback)
            return a.getTeam().getName().compareTo(b.getTeam().getName());
        }
    }
}
