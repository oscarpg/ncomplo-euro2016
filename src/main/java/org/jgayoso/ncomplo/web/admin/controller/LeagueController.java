package org.jgayoso.ncomplo.web.admin.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jgayoso.ncomplo.business.entities.BetType;
import org.jgayoso.ncomplo.business.entities.Competition;
import org.jgayoso.ncomplo.business.entities.Game;
import org.jgayoso.ncomplo.business.entities.Game.GameComparator;
import org.jgayoso.ncomplo.business.entities.Invitation;
import org.jgayoso.ncomplo.business.entities.League;
import org.jgayoso.ncomplo.business.entities.LeagueGame;
import org.jgayoso.ncomplo.business.services.BetTypeService;
import org.jgayoso.ncomplo.business.services.CompetitionService;
import org.jgayoso.ncomplo.business.services.InvitationService;
import org.jgayoso.ncomplo.business.services.LeagueService;
import org.jgayoso.ncomplo.web.admin.beans.InvitationBean;
import org.jgayoso.ncomplo.web.admin.beans.LangBean;
import org.jgayoso.ncomplo.web.admin.beans.LeagueBean;
import org.jgayoso.ncomplo.web.admin.beans.NotificationBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

@Controller
@RequestMapping("/admin/league")
public class LeagueController {

	private static final String VIEW_BASE = "admin/league/";
	private static final String DEFAULT_TIME_ZONE = "+0200";
	private static final String DEFAULT_TIME_ZONE_ID = "Europe/Madrid";
	private static final String DATE_FORMAT_PATTERN = "dd-MM-yyyy HH:mm";
	private static final String DATE_FORMAT_PATTERN_WITH_TIMEZONE = "dd-MM-yyyy HH:mm Z";
	@Autowired
	private CompetitionService competitionService;

	@Autowired
	private LeagueService leagueService;
	
	@Autowired
	private InvitationService invitationService;

	@Autowired
	private BetTypeService betTypeService;

	private final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);

	private final SimpleDateFormat dateFormatWithTimeZone = new SimpleDateFormat(DATE_FORMAT_PATTERN_WITH_TIMEZONE);


	public LeagueController() {
		super();
	}

	@RequestMapping("/list")
	public String list(final HttpServletRequest request, final ModelMap model) {

		final List<League> leagues = this.leagueService.findAll(RequestContextUtils.getLocale(request));
		final List<Competition> competitions = this.competitionService.findAll(RequestContextUtils.getLocale(request));

		model.addAttribute("allLeagues", leagues);
		model.addAttribute("allCompetitions", competitions);

		return VIEW_BASE + "list";

	}

	@RequestMapping("/manage")
	public String manage(@RequestParam(value = "id", required = false) final Integer id,
			@RequestParam(value = "competitionId", required = false) final Integer competitionId, final ModelMap model,
			final HttpServletRequest request) {

		final Locale locale = RequestContextUtils.getLocale(request);

		final League league = (id == null ? null : this.leagueService.find(id));
		final Integer leagueCompetitionId = (league == null ? competitionId : league.getCompetition().getId());
		if (leagueCompetitionId == null) {
			return "redirect:/admin";
		}

		final Competition competition = this.competitionService.find(leagueCompetitionId);

		final List<Game> allGamesForCompetition = new ArrayList<>(competition.getGames());
		Collections.sort(allGamesForCompetition, new GameComparator(locale));

		final LeagueBean leagueBean = new LeagueBean();
		leagueBean.setCompetitionId(leagueCompetitionId);

		Date firstGameDate = null;
		for (final Game game : allGamesForCompetition) {
			// Initialize default values for game bet types
			final BetType defaultBetType = game.getDefaultBetType();
			leagueBean.getBetTypesByGame().put(game.getId(), defaultBetType.getId());
			if (firstGameDate == null || (game.getDate() != null && firstGameDate.after(game.getDate()))) {
				firstGameDate = game.getDate();
			}
		}

		if (league != null) {
			leagueBean.setId(league.getId());
			leagueBean.setName(league.getName());
			leagueBean.getNamesByLang().clear();
			leagueBean.getNamesByLang().addAll(LangBean.listFromMap(league.getNamesByLang()));
			leagueBean.setAdminEmail(league.getAdminEmail());
			leagueBean.setActive(league.isActive());
			this.dateFormat.setTimeZone(TimeZone.getTimeZone(DEFAULT_TIME_ZONE_ID));
			leagueBean.setDate(this.dateFormat.format(league.getBetsDeadLine()));

			for (final LeagueGame leagueGame : league.getLeagueGames().values()) {
				leagueBean.getBetTypesByGame().put(leagueGame.getGame().getId(), leagueGame.getBetType().getId());
			}
		} else if (firstGameDate != null) {
			this.dateFormat.setTimeZone(TimeZone.getTimeZone(DEFAULT_TIME_ZONE_ID));
			leagueBean.setDate(this.dateFormat.format(DateUtils.addHours(firstGameDate, -1)));
		}

		model.addAttribute("league", leagueBean);
		model.addAttribute("competition", competition);
		model.addAttribute("allGames", allGamesForCompetition);
		model.addAttribute("allBetTypes", this.betTypeService.findAllOrderByName(leagueCompetitionId, locale));

		return VIEW_BASE + "manage";

	}

	@RequestMapping("/save")
	public String save(@Valid final LeagueBean leagueBean, final BindingResult result) {

		try {
			Date date;
			try {
				date = this.dateFormatWithTimeZone.parse(leagueBean.getDate());
			} catch (ParseException e) {
				date = this.dateFormatWithTimeZone.parse(leagueBean.getDate() + " " + DEFAULT_TIME_ZONE);
			}
			this.leagueService.save(leagueBean.getId(), leagueBean.getCompetitionId(), leagueBean.getName(),
					LangBean.mapFromList(leagueBean.getNamesByLang()), leagueBean.getAdminEmail(), leagueBean.isActive(),
					date, leagueBean.getBetTypesByGame());
		} catch (final ParseException e) {
			result.rejectValue("date", "bets.date.format.error", "Invalid bets deadline date");
			return VIEW_BASE + "manage";
		}

		return "redirect:list";

	}

	@RequestMapping("/delete")
	public String delete(@RequestParam(value = "id") final Integer id) {

		this.leagueService.delete(id);
		return "redirect:list";

	}

	@RequestMapping("/recompute")
	public String manage(@RequestParam(value = "id", required = true) final Integer leagueId,
						 @RequestParam(value = "scoreboard", required = false) final Boolean toScoreboard) {

		this.leagueService.recomputeScores(leagueId);

		if (toScoreboard) {
			return "redirect:/scoreboard/"+leagueId;
		}
		return "redirect:list";

	}
	
	@RequestMapping("/invite")
	public String invite(@RequestParam(value = "id", required = true) final Integer leagueId,
			final HttpServletRequest request, final ModelMap model) {
		
		final List<Invitation> invitationsSent = this.invitationService.findByLeagueId(leagueId);
		
		final InvitationBean bean = new InvitationBean();
		bean.setLeagueId(leagueId);
		
		model.addAttribute("invitation", bean);
		model.addAttribute("leagueId", leagueId);
		model.addAttribute("invitations", invitationsSent);
		return VIEW_BASE + "invite";
	}
	
	@RequestMapping("/doInvite")
	public String doInvite(final InvitationBean bean, final HttpServletRequest request,
			final RedirectAttributes redirectAttributes) {
		final Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
			/* The user is not logged in */
			return "login";
		}
		/* The user is logged in */
		final String adminLogin = auth.getName();
		
		if (StringUtils.isBlank(bean.getName()) || StringUtils.isBlank(bean.getEmail())) {
			redirectAttributes.addFlashAttribute("error", "Mandatory name and email fields");
        	return VIEW_BASE + "invite";
		}
		
		this.invitationService.sendInvitations(bean.getLeagueId(), adminLogin, bean.getName(), bean.getEmail(), request.getLocale());
		return "redirect:list";

	}
	
	@RequestMapping("/inviteGroup")
	public String inviteGroup(@RequestParam(value = "id", required = true) final Integer leagueId,
			final ModelMap model) {
		
		final Invitation existingInvitation = this.invitationService.findGroupByLeagueId(leagueId);
		model.addAttribute("leagueId", leagueId);
		model.addAttribute("invitation", existingInvitation);
		
		final InvitationBean bean = new InvitationBean();
		bean.setLeagueId(leagueId);
		model.addAttribute("invitationBean", bean);
		
		return VIEW_BASE + "inviteGroup";
	}
	
	@RequestMapping("/doInviteGroup")
	public String doInvite(final InvitationBean bean, final HttpServletRequest request, final ModelMap model) {
		final Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
			/* The user is not logged in */
			return "login";
		}
		/* The user is logged in */
		final String adminLogin = auth.getName();
		final Integer leagueId = bean.getLeagueId();
			
		this.invitationService.generateInvitationGroup(leagueId, adminLogin);
		return "redirect:inviteGroup?id="+leagueId;
	}
	
	
	@RequestMapping("/notification")
	public String notification(@RequestParam(value = "id", required = true) final Integer leagueId, final ModelMap model, final RedirectAttributes redirectAttributes) {
		final Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
			/* The user is not logged in */
			return "login";
		}
        
        final League league = this.leagueService.find(leagueId);
        if (league == null) {
        	redirectAttributes.addFlashAttribute("error", "League doesn't exist");
        	return VIEW_BASE + "notification";
        }
        
		model.addAttribute("notificationBean", new NotificationBean(leagueId));
		return VIEW_BASE + "notification";
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/doNotification")
	public String sendNotification(
			final NotificationBean notificationBean,
			final RedirectAttributes redirectAttributes) {
		
		final Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
			/* The user is not logged in */
			return "login";
		}
        
        final League league = this.leagueService.find(notificationBean.getLeagueId());
        if (league == null) {
        	redirectAttributes.addFlashAttribute("error", "League doesn't exist");
        	return VIEW_BASE + "notification";
        }
		
		this.leagueService.sendNotificationEmailToLeagueMembers(notificationBean.getLeagueId(), notificationBean.getSubject(),
				notificationBean.getText());
		
		redirectAttributes.addFlashAttribute("message", "Notifications sent");
		return "redirect:list";
	}
	
}
