package org.jgayoso.ncomplo.business.services;

import java.util.*;
import java.util.Map.Entry;

import org.jgayoso.ncomplo.business.entities.*;
import org.jgayoso.ncomplo.business.entities.repositories.*;
import org.jgayoso.ncomplo.business.util.I18nNamedEntityComparator;
import org.jgayoso.ncomplo.business.util.IterableUtils;
import org.jgayoso.ncomplo.business.views.ScoreMatterBetView;
import org.jgayoso.ncomplo.business.views.TodayEventsView;
import org.jgayoso.ncomplo.business.views.TodayRoundGamesAndBetsView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
public class LeagueService {

	@Autowired
	private LeagueRepository leagueRepository;

	@Autowired
	private CompetitionRepository competitionRepository;

	@Autowired
	private GameRepository gameRepository;

	@Autowired
	private GameService gameService;
	
	@Autowired
	private BetTypeRepository betTypeRepository;

	@Autowired
	private BetRepository betRepository;
	@Autowired
	private InvitationRepository invitationRepository;
	
	@Autowired
	private EmailServiceFactory emailServiceFactory;

	public LeagueService() {
		super();
	}

	@Transactional
	public League find(final Integer id) {
		return this.leagueRepository.findOne(id);
	}

	@Transactional
	public List<League> findAll(final Locale locale) {
		final List<League> leagues = IterableUtils.toList(this.leagueRepository.findAll());
		Collections.sort(leagues, new I18nNamedEntityComparator(locale));
		return leagues;
	}

	@Transactional
	public List<League> findByCompetitionIdAndUser(final Integer competitionId, User user, final Locale locale) {

		final Date now = new Date();
		List<League> leagues = this.leagueRepository.findByCompetitionId(competitionId);
		List<League> userLeaguesForCompetition = new ArrayList<>();
		for (League league: leagues) {
			if (league.getParticipants().contains(user) && league.getBetsDeadLine().after(now)) {
				userLeaguesForCompetition.add(league);
			}
		}

		Collections.sort(userLeaguesForCompetition, new I18nNamedEntityComparator(locale));
		return userLeaguesForCompetition;

	}

	@Transactional
	public League save(final Integer id, final Integer competitionId, final String name,
			final Map<String, String> namesByLang, final String adminEmail, final boolean active,
			final Date betsDeadLine, final Map<Integer, Integer> betTypesByGame) {

		final Competition competition = this.competitionRepository.findOne(competitionId);

		final League league = (id == null ? new League() : this.leagueRepository.findOne(id));

		league.setCompetition(competition);
		league.setName(name);
		league.getNamesByLang().clear();
		league.getNamesByLang().putAll(namesByLang);
		league.setAdminEmail(adminEmail);
		league.setActive(active);
		league.getLeagueGames().clear();
		league.setBetsDeadLine(betsDeadLine);

		for (final Map.Entry<Integer, Integer> betTypesByGameEntry : betTypesByGame.entrySet()) {

			final Integer gameId = betTypesByGameEntry.getKey();
			final Integer betTypeId = betTypesByGameEntry.getValue();

			final Game game = this.gameRepository.findOne(gameId);
			final BetType betType = this.betTypeRepository.findOne(betTypeId);

			final LeagueGame leagueGame = new LeagueGame();
			leagueGame.setBetType(betType);
			leagueGame.setGame(game);
			leagueGame.setLeague(league);

			league.getLeagueGames().put(game, leagueGame);

		}

		if (id == null) {
			return this.leagueRepository.save(league);
		}
		return league;

	}

	@Transactional
	public void delete(final Integer leagueId) {
		final League league = this.leagueRepository.findOne(leagueId);
		if (league == null) {
			return;
		}
		invitationRepository.deleteByLeagueId(leagueId);
		this.leagueRepository.delete(leagueId);
	}

	@Transactional
	public void recomputeScores(final Integer leagueId) {

		final League league = this.leagueRepository.findOne(leagueId);

		for (final User participant : league.getParticipants()) {
			final List<Bet> bets = this.betRepository.findByLeagueIdAndUserLogin(league.getId(),
					participant.getLogin());
			for (final Bet bet : bets) {
				bet.evaluate();
			}
		}
	}
	
	@Transactional
	public void recomputeScoresForGames(final Integer leagueId, final Collection<Game> games) {

		final League league = this.leagueRepository.findOne(leagueId);

		for (final User participant : league.getParticipants()) {
			final List<Bet> bets = this.betRepository.findByLeagueIdAndUserLoginAndGameIn(league.getId(),
					participant.getLogin(), games);
			for (final Bet bet : bets) {
				bet.evaluate();
			}
		}
	}

	public TodayEventsView getTodayInformation(final Integer leagueId) {

		final League league = this.find(leagueId);
		final List<TodayRoundGamesAndBetsView> roundsInfo = new ArrayList<>();
		final List<Game> todayGames = this.gameService.findNextGames(leagueId);
		
		final Map<Round, List<Game>> gamesByRound = new HashMap<Round, List<Game>>();
		if (todayGames != null) {
			for (final Game game: todayGames){
				if (!gamesByRound.containsKey(game.getRound())) {
					gamesByRound.put(game.getRound(), new ArrayList<Game>());
				}
				gamesByRound.get(game.getRound()).add(game);
			}
		}

		Map<Game, LeagueGame> leagueGames = league.getLeagueGames();
		for (final Entry<Round, List<Game>> roundGamesEntry: gamesByRound.entrySet()) {
			final List<Game> games = roundGamesEntry.getValue();
			final BetType betType;
			if (leagueGames.containsKey(games.get(0))) {
				betType = leagueGames.get(games.get(0)).getBetType();
			} else {
				betType = games.get(0).getDefaultBetType();
			}
			TodayRoundGamesAndBetsView betView = null;
			if (betType.isScoreMatter()) {
				betView = this.processScoreMattersGames(leagueId, roundGamesEntry.getKey(), games);
			} else if (betType.isSidesMatter()) {
				betView = this.processSideMattersGames(leagueId, roundGamesEntry.getKey(), games);
			}
			if (betView != null) {
				roundsInfo.add(betView);
			}
		}
		
		if (!CollectionUtils.isEmpty(roundsInfo)) {
			Collections.sort(roundsInfo);
		}
		
		return new TodayEventsView(roundsInfo);
	}
	
	private TodayRoundGamesAndBetsView processScoreMattersGames(final Integer leagueId, final Round round, final List<Game> games) {
		final List<Bet> betsForGames = this.betRepository.findByScoreMatterTrueAndLeagueIdAndGameIn(leagueId, games);
		
		final Map<String, Map<Integer, ScoreMatterBetView>> bets = new HashMap<>();
    	if (CollectionUtils.isEmpty(betsForGames)) {
    		return new TodayRoundGamesAndBetsView(round, games, null, null, true);
    	}
		for (final Bet bet: betsForGames) {
			final String userLogin = bet.getUser().getLogin();
			if (!bets.containsKey(userLogin)) {
				bets.put(userLogin, new HashMap<Integer, ScoreMatterBetView>());
			}
			bets.get(userLogin).put(bet.getGame().getId(),
					new ScoreMatterBetView(userLogin, bet.getGame().getId(), bet.getScoreA(), bet.getScoreB()));
		}
    	return new TodayRoundGamesAndBetsView(round, games, bets, null, true);
	}
	
	private TodayRoundGamesAndBetsView processSideMattersGames(final Integer leagueId, final Round round, final List<Game> games) {
		
		final Collection<GameSide> gameSides = new HashSet<>();
    	for (final Game game: games) {
    		gameSides.add(game.getGameSideA());
    		gameSides.add(game.getGameSideB());
    	}
		
		final List<Bet> betsForRound = this.betRepository.findBySidesMatterTrueAndLeagueIdAndAndGameRound(leagueId, round);
		final Map<String, List<GameSide>> sideMatterBets = new HashMap<>();
		int maxNumber = 0;
    	if (CollectionUtils.isEmpty(betsForRound)) {
			final TodayRoundGamesAndBetsView betsView = new TodayRoundGamesAndBetsView(round, games, null, null, false);
			betsView.setMaxSideMattersBets(maxNumber);
			return betsView;
    	}
		for (final Bet bet: betsForRound) {
			final String userLogin = bet.getUser().getLogin();
			if (!sideMatterBets.containsKey(userLogin)) {
				sideMatterBets.put(userLogin, new ArrayList<GameSide>());
			}
			if (bet.getScoreA() == null || bet.getScoreB() == null) {
				continue;
			}
			if (bet.getScoreA().intValue() > bet.getScoreB().intValue() && gameSides.contains(bet.getGameSideA())) {
				sideMatterBets.get(userLogin).add(bet.getGameSideA());
			} else if (bet.getScoreA().intValue() < bet.getScoreB().intValue() && gameSides.contains(bet.getGameSideB())) {
				sideMatterBets.get(userLogin).add(bet.getGameSideB());
			}
		}
		for (final Entry<String, List<GameSide>> userBets : sideMatterBets.entrySet()) {
			if (userBets.getValue().size() > maxNumber) {
				maxNumber = userBets.getValue().size();
			}
    	}
    	final TodayRoundGamesAndBetsView betsView = new TodayRoundGamesAndBetsView(round, games, null, sideMatterBets, false);
    	betsView.setMaxSideMattersBets(maxNumber);
    	return betsView;
	}
	
	public void sendNotificationEmailToLeagueMembers(final Integer leagueId, final String subject, final String text) {
		final League league = this.leagueRepository.findOne(leagueId);

		EmailService emailService = emailServiceFactory.getEmailService();
		if (emailService == null) {
			return;
		}
		
		final Set<User> participants = league.getParticipants();
		if (CollectionUtils.isEmpty(participants)) {
			return;
		}
		
		final String[] destinations = new String[participants.size()];
		int i = 0;
		for (final User participant: participants) {
			destinations[i] = participant.getEmail();
			i++;
		}
		emailService.sendNotification(subject, destinations, text);
	}

}
