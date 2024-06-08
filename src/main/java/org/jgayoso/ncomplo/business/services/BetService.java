package org.jgayoso.ncomplo.business.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jgayoso.ncomplo.business.entities.*;
import org.jgayoso.ncomplo.business.entities.Bet.BetComparator;
import org.jgayoso.ncomplo.business.entities.repositories.BetRepository;
import org.jgayoso.ncomplo.business.entities.repositories.GameRepository;
import org.jgayoso.ncomplo.business.entities.repositories.GameSideRepository;
import org.jgayoso.ncomplo.business.entities.repositories.LeagueRepository;
import org.jgayoso.ncomplo.business.entities.repositories.UserRepository;
import org.jgayoso.ncomplo.business.util.ExcelProcessor;
import org.jgayoso.ncomplo.business.views.BetView;
import org.jgayoso.ncomplo.exceptions.InternalErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;



@Service
public class BetService {
    
    
    @Autowired
    private LeagueRepository leagueRepository;
    
    
    @Autowired
    private LeagueService leagueService;
    
    @Autowired
    private BetRepository betRepository;
    
    @Autowired
    private GameRepository gameRepository;
    
    @Autowired
    private GameSideRepository gameSideRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    private final String groupsFirstColumnName = "E";
    private final String secondRoundColumnName = "AZ";
    private final String quarterFinalsColumnName = "BG";
    private final String semisColumnName = "BN";
    private final String finalColumnName = "BU";
    
    public BetService() {
        super();
    }
    
    
    public Bet find(final Integer id) {
        return this.betRepository.findOne(id);
    }
    
    public List<Bet> findByLeagueIdAndUserLogin(
            final Integer leagueId, final String login, final Locale locale) {
        final List<Bet> bets =
                this.betRepository.findByLeagueIdAndUserLogin(leagueId, login);
        Collections.sort(bets, new BetComparator(locale));
        return bets;
    }

    public List<Bet> findByLeagueId(
            final Integer leagueId, final Locale locale) {
        final List<Bet> bets =
                this.betRepository.findByLeagueId(leagueId);
        Collections.sort(bets, new BetComparator(locale));
        return bets;
    }
    
    @Transactional
    public List<String> processBetsFile(final File betsFile, final String login, final Integer leagueId, final boolean updateAllLeagues, final Locale locale)
            throws IOException {

        try (FileInputStream fis = new FileInputStream(betsFile); XSSFWorkbook book = new XSSFWorkbook(fis);) {
            final League requestLeague = this.leagueService.find(leagueId);

            final XSSFSheet sheet = book.getSheet(requestLeague.getCompetition().getCompetitionParserProperties().getGamesSheetName());

            List<League> leaguesToUpdate;
            if (updateAllLeagues) {
                final User user = this.userRepository.findOne(login);
                if (user == null) {
                    throw new InternalErrorException("User " + login + " not found");
                }
                leaguesToUpdate = this.leagueService.findByCompetitionIdAndUser(requestLeague.getCompetition().getId(), user, locale);
            } else {
                leaguesToUpdate = Collections.singletonList(requestLeague);
            }

            List<String> invalidBets = null;
            for (League league: leaguesToUpdate) {
                invalidBets = updateLeagueBets(login, locale, sheet, league);
            }
            
            return invalidBets;
        }
    }

    private List<String> updateLeagueBets(String login, Locale locale, XSSFSheet sheet, League league) {
        Integer leagueId = league.getId();

        final Collection<LeagueGame> leagueGames = league.getLeagueGames().values();

        final List<GameSide> gameSides = this.gameSideRepository.findByCompetitionId(league.getCompetition().getId());
        final Map<String, GameSide> gameSidesByName = new HashMap<>();
        for (final GameSide gameSide : gameSides) {
            gameSidesByName.put(gameSide.getName(), gameSide);
            for (final String name : gameSide.getNamesByLang().values()) {
                gameSidesByName.put(name, gameSide);
            }
        }

        final List<Bet> bets =
                this.findByLeagueIdAndUserLogin(leagueId, login, locale);

        final Map<Integer, BetView> betViewssByGameId = new HashMap<Integer, BetView>();
        final Map<Integer, Game> gamesByOrder = new HashMap<Integer, Game>();
        final Map<Integer, Integer> betIdsByGameId = new HashMap<Integer, Integer>();

        if (!CollectionUtils.isEmpty(bets)) {
            for (final Bet bet : bets) {
                betIdsByGameId.put(bet.getGame().getId(), bet.getId());
            }
        }

        for (final LeagueGame leagueGame : leagueGames) {
            final BetView betView = new BetView();
            final Game game = leagueGame.getGame();
            betView.setBetTypeId(leagueGame.getBetType().getId());
            betView.setGameId(game.getId());
            if (game.getGameSideA() != null) {
                betView.setGameSideAId(game.getGameSideA().getId());
            }
            if (game.getGameSideB() != null) {
                betView.setGameSideBId(game.getGameSideB().getId());
            }
            betViewssByGameId.put(game.getId(), betView);
            gamesByOrder.put(game.getOrder(), game);
        }

        final Competition competition = league.getCompetition();
        // Groups games
        int matchNumber = ExcelProcessor.processGroupGamesBets(leagueId, login, competition, sheet, gamesByOrder,
                betIdsByGameId, betViewssByGameId, this);

        List<String> invalidBets = new ArrayList<>();
        // Round of 16
        CompetitionParserProperties competitionParserProperties = competition.getCompetitionParserProperties();
        List<BetView> betViews = ExcelProcessor.processPlayOffGamesBets(leagueId, login, sheet, matchNumber,
                competitionParserProperties.getRoundOf16GamesColumnName(), competitionParserProperties.getRoundOf16GamesStartIndex(), 8,
                competitionParserProperties.getRoundOf16GamesJumpSize(),gamesByOrder, betIdsByGameId, betViewssByGameId, gameSidesByName, this);
        getInvalidBetsMessages(invalidBets, betViews);

        // Quarter final round
        betViews = ExcelProcessor.processPlayOffGamesBets(leagueId, login, sheet, matchNumber + 8,
                competitionParserProperties.getQuarteFinalsGamesColumnName(), competitionParserProperties.getQuarteFinalsGamesStartIndex(), 4,
                competitionParserProperties.getQuarteFinalsGamesJumpSize(),gamesByOrder, betIdsByGameId, betViewssByGameId, gameSidesByName, this);
        getInvalidBetsMessages(invalidBets, betViews);

        // Semifinal round
        betViews = ExcelProcessor.processPlayOffGamesBets(leagueId, login, sheet, matchNumber + 12,
                competitionParserProperties.getSemiFinalsGamesColumnName(), competitionParserProperties.getSemiFinalsGamesStartIndex(), 2,
                competitionParserProperties.getSemiFinalsGamesJumpSize(),gamesByOrder, betIdsByGameId, betViewssByGameId, gameSidesByName, this);
        getInvalidBetsMessages(invalidBets, betViews);

        // Final
        betViews = ExcelProcessor.processPlayOffGamesBets(leagueId, login, sheet, matchNumber + 14,
                competitionParserProperties.getFinalGamesColumnName(), competitionParserProperties.getFinalGamesStartIndex(), 1,
                0, gamesByOrder, betIdsByGameId, betViewssByGameId, gameSidesByName, this);
        getInvalidBetsMessages(invalidBets, betViews);
        return invalidBets;
    }

    private void getInvalidBetsMessages(List<String> invalidBets, List<BetView> betViews) {
        for (BetView betView: betViews) {
            if (StringUtils.isNotEmpty(betView.getInvalidMessage())) {
                invalidBets.add(betView.getInvalidMessage());
            }
        }
    }


    @Transactional
    public Bet save(
            final Integer id,
            final Integer leagueId,
            final String login,
            final Integer gameId,
            final Integer gameSideAId,
            final Integer gameSideBId,
            final Integer scoreA,
            final Integer scoreB) {

        final League league = 
                this.leagueRepository.findOne(leagueId);
        final User user = 
                this.userRepository.findOne(login);
        final Game game = 
                this.gameRepository.findOne(gameId);
        
        final GameSide gameSideA = 
                (gameSideAId == null? null : this.gameSideRepository.findOne(gameSideAId));
        final GameSide gameSideB = 
                (gameSideBId == null? null : this.gameSideRepository.findOne(gameSideBId));
        
        final Bet bet =
                (id == null? new Bet() : this.betRepository.findOne(id));

        bet.setLeague(league);
        bet.setGame(game);
        bet.setUser(user);
        bet.setGameSideA(gameSideA);
        bet.setGameSideB(gameSideB);
        bet.setScoreA(scoreA);
        bet.setScoreB(scoreB);
        
        if (id == null) {
            return this.betRepository.save(bet);
        }
        return bet;
        
    }
    

    
    @Transactional
    public void delete(final Integer betId) {
        this.betRepository.delete(betId);
    }

    
    
    
}
