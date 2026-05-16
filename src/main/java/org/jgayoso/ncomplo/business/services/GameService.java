package org.jgayoso.ncomplo.business.services;

import org.jgayoso.ncomplo.business.entities.*;
import org.jgayoso.ncomplo.business.entities.Game.GameComparator;
import org.jgayoso.ncomplo.business.entities.GameSideSource.SourceType;
import org.jgayoso.ncomplo.business.entities.repositories.BetTypeRepository;
import org.jgayoso.ncomplo.business.entities.repositories.CompetitionGroupRepository;
import org.jgayoso.ncomplo.business.entities.repositories.CompetitionRepository;
import org.jgayoso.ncomplo.business.entities.repositories.GameRepository;
import org.jgayoso.ncomplo.business.entities.repositories.GameSideRepository;
import org.jgayoso.ncomplo.business.entities.repositories.GameSideSourceRepository;
import org.jgayoso.ncomplo.business.entities.repositories.RoundRepository;
import org.jgayoso.ncomplo.business.util.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;

@Service
public class GameService {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private BetTypeRepository betTypeRepository;

    @Autowired
    private RoundRepository roundRepository;

    @Autowired
    private GameSideRepository gameSideRepository;

    @Autowired
    private GameSideSourceRepository gameSideSourceRepository;

    @Autowired
    private CompetitionGroupRepository competitionGroupRepository;

    @Autowired
    private LeagueService leagueService;

    public GameService() {
        super();
    }

    @Transactional
    public Game find(final Integer id) {
        return this.gameRepository.findOne(id);
    }

    @Transactional
    public List<Game> findAll(final Integer competitionId, final Locale locale) {
        final List<Game> rounds = IterableUtils.toList(this.gameRepository.findByCompetitionId(competitionId));
        Collections.sort(rounds, new GameComparator(locale));
        return rounds;
    }

    @Transactional
    public Game save(
            final Integer id,
            final Integer competitionId,
            final Date date,
            final String name,
            final Map<String, String> namesByLang,
            final Integer defaultBetTypeId,
            final Integer roundId,
            final Integer order,
            final Integer gameSideAId,
            final Integer gameSideBId,
            final Integer scoreA,
            final Integer scoreB,
            final String sideASourceType,
            final Integer sideAGroupId,
            final Integer sideAPosition,
            final Integer sideASourceGameId,
            final String sideBSourceType,
            final Integer sideBGroupId,
            final Integer sideBPosition,
            final Integer sideBSourceGameId) {

        final Competition competition = this.competitionRepository.findOne(competitionId);

        final Game game = (id == null ? new Game() : this.gameRepository.findOne(id));

        final GameSide gameSideA = (gameSideAId == null ? null : this.gameSideRepository.findOne(gameSideAId));
        final GameSide gameSideB = (gameSideBId == null ? null : this.gameSideRepository.findOne(gameSideBId));

        game.setCompetition(competition);
        game.setDate(date);
        game.setName(name);
        game.getNamesByLang().clear();
        game.getNamesByLang().putAll(namesByLang);
        game.setDefaultBetType(this.betTypeRepository.findOne(defaultBetTypeId));
        game.setRound(this.roundRepository.findOne(roundId));
        game.setOrder(order);
        game.setGameSideA(gameSideA);
        game.setGameSideB(gameSideB);
        game.setScoreA(scoreA);
        game.setScoreB(scoreB);

        // Bracket source for Side A
        game.setGameSideASource(buildSource(sideASourceType, sideAGroupId, sideAPosition, sideASourceGameId));
        // Bracket source for Side B
        game.setGameSideBSource(buildSource(sideBSourceType, sideBGroupId, sideBPosition, sideBSourceGameId));

        if (id == null) {
            competition.getGames().add(game);
            return this.gameRepository.save(game);
        }

        return game;

    }

    private GameSideSource buildSource(
            final String sourceType,
            final Integer groupId,
            final Integer position,
            final Integer sourceGameId) {

        if (sourceType == null || sourceType.isBlank()) {
            return null;
        }
        final SourceType type = SourceType.valueOf(sourceType);
        switch (type) {
            case GROUP_POSITION: {
                final CompetitionGroup group = (groupId != null ? this.competitionGroupRepository.findOne(groupId) : null);
                return GameSideSource.groupPosition(group, position != null ? position : 1);
            }
            case GAME_WINNER: {
                final Game src = (sourceGameId != null ? this.gameRepository.findOne(sourceGameId) : null);
                return GameSideSource.gameWinner(src);
            }
            case GAME_LOSER: {
                final Game src = (sourceGameId != null ? this.gameRepository.findOne(sourceGameId) : null);
                return GameSideSource.gameLoser(src);
            }
            default:
                return null;
        }
    }

    @Transactional
    public void deleteAll(Integer competitionId) {
        final Competition competition = this.competitionRepository.findOne(competitionId);
        for (Game game : new HashSet<>(competition.getGames())) {
            competition.getGames().remove(game);
        }
    }

    @Transactional
    public void delete(final Integer gameId) {

        final Game game = this.gameRepository.findOne(gameId);
        final Competition competition = game.getCompetition();

        competition.getGames().remove(game);

    }

    public List<Game> findNextGames(final Integer leagueId) {
        final League league = this.leagueService.find(leagueId);
        final Date today = new Date();
        final Date todayMorning = DateUtils.truncate(today, Calendar.DATE);
        final Date todayEvening = DateUtils.addSeconds(DateUtils.addMinutes(DateUtils.addHours(todayMorning, 23), 59),
                59);
        final List<Game> todayGames = this.gameRepository
                .findByCompetitionAndDateBetweenOrderByDate(league.getCompetition(), todayMorning, todayEvening);
        if (!CollectionUtils.isEmpty(todayGames)) {
            // Collections.sort(todayGames, new GameOrderComparator());
            return todayGames;
        }

        final Date nextWeek = DateUtils.addDays(todayMorning, 7);
        final List<Game> games = this.gameRepository.findByCompetitionAndDateBetweenOrderByDate(league.getCompetition(),
                todayMorning, nextWeek);
        if (CollectionUtils.isEmpty(games)) {
            return null;
        }
        final Date date = games.get(0).getDate();
        final List<Game> gamesToReturn = new ArrayList<>();
        for (final Game game : games) {
            if (DateUtils.isSameDay(date, game.getDate())) {
                gamesToReturn.add(game);
            } else {
                break;
            }
        }

        return gamesToReturn;
    }

}
