package org.jgayoso.ncomplo.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jgayoso.ncomplo.business.entities.Bet;
import org.jgayoso.ncomplo.business.entities.Bet.BetComparator;
import org.jgayoso.ncomplo.business.entities.League;
import org.jgayoso.ncomplo.business.entities.Round;
import org.jgayoso.ncomplo.business.entities.User;
import org.jgayoso.ncomplo.business.services.BetService;
import org.jgayoso.ncomplo.business.services.LeagueService;
import org.jgayoso.ncomplo.business.services.ScoreboardService;
import org.jgayoso.ncomplo.business.services.UserService;
import org.jgayoso.ncomplo.business.util.I18nNamedEntityComparator;
import org.jgayoso.ncomplo.business.views.ScoreboardEntry;
import org.jgayoso.ncomplo.business.views.TodayEventsView;
import org.jgayoso.ncomplo.exceptions.InternalErrorException;
import org.jgayoso.ncomplo.web.beans.LeagueSelectorBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

@Controller
public class ScoreboardController {

	@Autowired
	private UserService userService;

	@Autowired
	private BetService betService;

	@Autowired
	private LeagueService leagueService;

	@Autowired
	private ScoreboardService scoreboardService;

	public ScoreboardController() {
		super();
	}

	@RequestMapping({ "/", "/scoreboard" })
	public String scoreboard(final HttpServletRequest request) {

		final Locale locale = RequestContextUtils.getLocale(request);

		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth instanceof AnonymousAuthenticationToken) {
			/* The user is not logged in */
			return "login";
		}
		/* The user is logged in */
		final String login = auth.getName();

		final User user = this.userService.find(login);

		final List<League> userLeagues = new ArrayList<>(user.getLeagues());
		final List<League> activeUserLeagues = new ArrayList<>();
		for (final League league : userLeagues) {
			if (league.isActive()) {
				activeUserLeagues.add(league);
			}
		}
		Collections.sort(activeUserLeagues, new I18nNamedEntityComparator(locale));

		if (activeUserLeagues.size() == 0) {
			if (user.isAdmin()) {
				return "redirect:/admin";
			}
			return "redirect:/userWithNoLeagues";
		}

		return "redirect:/scoreboard/" + activeUserLeagues.get(0).getId();

	}

	@RequestMapping("/userWithNoLeagues")
	public String userWithNoLeagues(final HttpServletRequest request,
			final ModelMap model) {
		return "userWithNoLeagues";
	}

	@RequestMapping("/scoreboard/{leagueId}")
	public String scoreboardByLeague(@PathVariable("leagueId") final Integer leagueId, final HttpServletRequest request,
			final ModelMap model) {

		return computeScoreboard(leagueId, null, request, model);

	}

	@RequestMapping("/scoreboard/{leagueId}/{roundId}")
	public String scoreboardByLeagueAndRound(@PathVariable("leagueId") final Integer leagueId,
			@PathVariable("roundId") final Integer roundId, final HttpServletRequest request, final ModelMap model) {

		return computeScoreboard(leagueId, roundId, request, model);

	}

	private String computeScoreboard(final Integer leagueId, final Integer roundId, final HttpServletRequest request,
			final ModelMap model) {

		final Locale locale = RequestContextUtils.getLocale(request);

		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth instanceof AnonymousAuthenticationToken) {
			/* The user is not logged in */
			return "login";
		}
		/* The user is logged in */
		final String login = auth.getName();

		final User user = this.userService.find(login);

		final List<League> userLeagues = new ArrayList<>(user.getLeagues());
		final List<League> activeUserLeagues = new ArrayList<>();
		for (final League league : userLeagues) {
			if (league.isActive()) {
				activeUserLeagues.add(league);
			}
		}
		Collections.sort(activeUserLeagues, new I18nNamedEntityComparator(locale));

		final LeagueSelectorBean leagueSelectorBean = new LeagueSelectorBean();
		leagueSelectorBean.setLeagueId(leagueId);
		leagueSelectorBean.setRoundId(roundId);

		final League league = this.leagueService.find(leagueId);

		final List<Round> rounds = new ArrayList<>(league.getCompetition().getRounds());
		Collections.sort(rounds);

		final List<ScoreboardEntry> scoreboardEntries = this.scoreboardService.computeScoreboard(leagueId, roundId,
				locale);
		
		final TodayEventsView todayEvents = this.leagueService.getTodayInformation(leagueId);
		model.addAttribute("todayEvents", todayEvents);

		model.addAttribute("scoreboardEntries", scoreboardEntries);
		model.addAttribute("user", user);
		model.addAttribute("league", league);
		model.addAttribute("leagueSelector", leagueSelectorBean);
		model.addAttribute("allLeagues", activeUserLeagues);
		model.addAttribute("allRounds", rounds);
		model.addAttribute("showLeagueSelector", Boolean.valueOf(activeUserLeagues.size() > 1));
		model.addAttribute("betsAllowed", league.getBetsDeadLine().after(new Date()));

		return "scoreboard";

	}

	@RequestMapping({ "/selectScoreboard" })
	public String selectScoreboard(final LeagueSelectorBean leagueSelectorBean) {
		final Integer leagueId = leagueSelectorBean.getLeagueId();
		final Integer roundId = leagueSelectorBean.getRoundId();
		if (roundId != null) {
			return "redirect:/scoreboard/" + leagueId + "/" + roundId;
		}
		return "redirect:/scoreboard/" + leagueId;
	}

	@RequestMapping({ "/bets/{leagueId}/{login}" })
	public String bets(@PathVariable("leagueId") final Integer leagueId, @PathVariable("login") final String login,
			final HttpServletRequest request, final ModelMap model) {

		final Locale locale = RequestContextUtils.getLocale(request);

		final User user = this.userService.find(login);
		final List<Bet> betsForUser = this.betService.findByLeagueIdAndUserLogin(leagueId, login, locale);
		Collections.sort(betsForUser, new BetComparator(locale));

		final League league = this.leagueService.find(leagueId);

		final List<ScoreboardEntry> scoreboardEntries = this.scoreboardService.computeScoreboard(leagueId, null,
				locale);

		model.addAttribute("scoreboardEntries", scoreboardEntries);

		model.addAttribute("user", user);
		model.addAttribute("league", league);
		model.addAttribute("allBets", betsForUser);

		return "bets";

	}

	@RequestMapping(value = { "/downloadbets/{leagueId}/{login}" })
	public void downloadbets(@PathVariable("leagueId") final Integer leagueId, @PathVariable("login") final String login,
			 final HttpServletRequest request, HttpServletResponse response, final RedirectAttributes redirectAttributes) throws IOException {

		final Locale locale = RequestContextUtils.getLocale(request);

		final User user = this.userService.find(login);
		League league = this.leagueService.find(leagueId);
		if (!league.getParticipants().contains(user)) {
			redirectAttributes.addFlashAttribute("error", "Invalid league");
			return;
		}
		final String separator = ";";
		final String endLine = "\n";
		final List<Bet> bets = this.betService.findByLeagueId(leagueId, locale);
		Collections.sort(bets, new Bet.BetByLoginComparator(locale));
		StringBuilder csvContent = new StringBuilder("login").append(separator).append("#game").append(separator)
				.append("team1").append(separator).append("team2").append(separator).append("score1").append(separator).append("score2").append(endLine);
		for (Bet bet: bets) {
			csvContent.append(bet.getUser().getName()).append(separator).append(bet.getGame().getOrder()).append(separator)
					.append(bet.getGameSideA() == null ? "null" : bet.getGameSideA().getName(locale)).append(separator)
					.append(bet.getGameSideB() == null ? "null" : bet.getGameSideB().getName(locale)).append(separator)
					.append(bet.getScoreA()).append(separator).append(bet.getScoreB()).append(endLine);
		}

		response.setContentType("text/plain; charset=utf-8");
		response.getWriter().print(csvContent);

	}

}
