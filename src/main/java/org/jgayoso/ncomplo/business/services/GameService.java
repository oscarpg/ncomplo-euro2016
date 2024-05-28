package org.jgayoso.ncomplo.business.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jgayoso.ncomplo.business.entities.*;
import org.jgayoso.ncomplo.business.entities.Game.GameComparator;
import org.jgayoso.ncomplo.business.entities.repositories.BetTypeRepository;
import org.jgayoso.ncomplo.business.entities.repositories.CompetitionRepository;
import org.jgayoso.ncomplo.business.entities.repositories.GameRepository;
import org.jgayoso.ncomplo.business.entities.repositories.GameSideRepository;
import org.jgayoso.ncomplo.business.entities.repositories.RoundRepository;
import org.jgayoso.ncomplo.business.util.ExcelProcessor;
import org.jgayoso.ncomplo.business.util.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;



@Service
public class GameService {

    private static final Logger logger = Logger.getLogger(GameService.class);
    
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
    private GameSideService gameSideService;

    @Autowired
    private BetTypeService betTypeService;
    @Autowired
    private RoundService roundService;
    
    @Autowired
    private LeagueService leagueService;
    
    SimpleDateFormat hourFormatter = new SimpleDateFormat("HH:mm");

    public static final String GAMES_SHEET_NAME = "UEFA EURO 2024";

    public static final String ROUND_OF_16_GAMES_COLUMN = "EV";
    public static final int GAMES_ROUND16_START_INDEX = 9;

    public static final String QUARTER_GAMES_COLUMN = "FC";
    public static final int GAMES_QUARTER_START_INDEX = 11;

    public static final String SEMIS_GAMES_COLUMN = "FJ";
    public static final int GAMES_SEMIS_START_INDEX = 15;

    public static final String FINAL_GAMES_COLUMN = "FQ";
    public static final int GAMES_FINAL_INDEX = 22;

    public static final String GROUPS_NAME = "Groups";
    public static final String ROUND_OF_16_NAME = "Round of 16";
    public static final String QUARTER_FINALS_NAME = "Quarter Finals";
    public static final String SEMIFINALS_NAME = "Semifinals";
    public static final String FINAL_NAME = "Final";

    public GameService() {
        super();
    }

    @Transactional
    public Game find(final Integer id) {
        return this.gameRepository.findOne(id);
    }
    
    
    @Transactional
    public List<Game> findAll(final Integer competitionId, final Locale locale) {
        final List<Game> rounds = 
                IterableUtils.toList(this.gameRepository.findByCompetitionId(competitionId));
        Collections.sort(rounds, new GameComparator(locale));
        return rounds;
    }

    
    @Transactional
    public Game save(
            final Integer id,
            final Integer competitionId,
            final Date date,
            final String name,
            final Map<String,String> namesByLang,
            final Integer defaultBetTypeId,
            final Integer roundId,
            final Integer order,
            final Integer gameSideAId,
            final Integer gameSideBId,
            final Integer scoreA,
            final Integer scoreB) {

        final Competition competition = 
                this.competitionRepository.findOne(competitionId);

        final Game game =
                (id == null? new Game() : this.gameRepository.findOne(id));
        
        final GameSide gameSideA = 
                (gameSideAId == null? null : this.gameSideRepository.findOne(gameSideAId));
        final GameSide gameSideB = 
                (gameSideBId == null? null : this.gameSideRepository.findOne(gameSideBId));
        
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
        
        if (id == null) {
            competition.getGames().add(game);
            return this.gameRepository.save(game);
        }
        
        return game;
        
    }

    @Transactional
    public void deleteAll(Integer competitionId) {
        final Competition competition = this.competitionRepository.findOne(competitionId);
        for (Game game: new HashSet<>(competition.getGames())) {
            competition.getGames().remove(game);
        }
    }
    
    @Transactional
    public void delete(final Integer gameId) {
        
        final Game game = 
                this.gameRepository.findOne(gameId);
        final Competition competition = game.getCompetition();
        
        competition.getGames().remove(game);
        
    }
    
    public List<Game> findNextGames(final Integer leagueId) {
    	final League league = this.leagueService.find(leagueId);
    	final Date today = new Date();
    	final Date todayMorning = DateUtils.truncate(today, Calendar.DATE);
    	final Date todayEvening = DateUtils.addSeconds(DateUtils.addMinutes(DateUtils.addHours(todayMorning, 23), 59), 59);
    	final List<Game> todayGames = this.gameRepository.findByCompetitionAndDateBetweenOrderByDate(league.getCompetition(), todayMorning, todayEvening);
    	if (!CollectionUtils.isEmpty(todayGames)) {
    		//Collections.sort(todayGames, new GameOrderComparator());
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

    @Transactional
    public void processFile(Integer competitionId, String login, File competitionFile) {
        Competition competition = this.competitionRepository.findOne(competitionId);
        if (competition == null) {
            logger.error("Not possible to processFile, competition not found");
            return;
        }

        logger.info("User " + login + " generating games for competition " + competitionId);

        try (FileInputStream fis = new FileInputStream(competitionFile); XSSFWorkbook book = new XSSFWorkbook(fis)) {
            FormulaEvaluator evaluator = book.getCreationHelper().createFormulaEvaluator();

            final XSSFSheet games = book.getSheet(GAMES_SHEET_NAME);

            Iterable<GameSide> gameSides = gameSideRepository.findAll();
            Map<String, GameSide> gameSideMap = new HashMap<>();
            gameSides.forEach(gameSide -> gameSideMap.put(gameSide.getName(), gameSide));

            BetType groupsBetType = betTypeService.findByName(competitionId, GROUPS_NAME);
            Round groupsRound = roundService.findByCompetitionIdAndName(competitionId, GROUPS_NAME);
            int gameIndex = ExcelProcessor.processGroupGames(competitionId, games, evaluator, groupsBetType, groupsRound, gameSideMap, this);

            // Round of 16
            BetType roundOf16BetType = betTypeService.findByName(competitionId, ROUND_OF_16_NAME);
            Round roundOf16Round = roundService.findByCompetitionIdAndName(competitionId, ROUND_OF_16_NAME);
            gameIndex = ExcelProcessor.processPlayoffGames(competitionId, games, evaluator, roundOf16BetType,
                    roundOf16Round, gameIndex, 8, 4, ROUND_OF_16_GAMES_COLUMN, GAMES_ROUND16_START_INDEX, this);

            // Quarter Finals
            BetType quarterFinalsBetType = betTypeService.findByName(competitionId, QUARTER_FINALS_NAME);
            Round quarterFinalsRound = roundService.findByCompetitionIdAndName(competitionId, QUARTER_FINALS_NAME);
            gameIndex = ExcelProcessor.processPlayoffGames(competitionId, games, evaluator, quarterFinalsBetType,
                    quarterFinalsRound, gameIndex, 4, 8, QUARTER_GAMES_COLUMN, GAMES_QUARTER_START_INDEX, this);


            // Semifinals
            BetType semifinalsBetType = betTypeService.findByName(competitionId, SEMIFINALS_NAME);
            Round semifinalsRound = roundService.findByCompetitionIdAndName(competitionId, SEMIFINALS_NAME);
            gameIndex = ExcelProcessor.processPlayoffGames(competitionId, games, evaluator, semifinalsBetType,
                    semifinalsRound, gameIndex, 2, 16, SEMIS_GAMES_COLUMN, GAMES_SEMIS_START_INDEX, this);

            // Final
            BetType finalBetType = betTypeService.findByName(competitionId, FINAL_NAME);
            Round finalRound = roundService.findByCompetitionIdAndName(competitionId, FINAL_NAME);
            ExcelProcessor.processPlayoffGames(competitionId, games, evaluator, finalBetType,
                    finalRound, gameIndex, 1, 16, FINAL_GAMES_COLUMN, GAMES_FINAL_INDEX, this);


        } catch (IOException e) {
            logger.error("Not possible to processFile, IOException", e);
        }

    }
    
}
