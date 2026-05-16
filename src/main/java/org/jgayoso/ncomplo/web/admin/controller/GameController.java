package org.jgayoso.ncomplo.web.admin.controller;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.jgayoso.ncomplo.business.entities.Game;
import org.jgayoso.ncomplo.business.entities.GameSideSource;
import org.jgayoso.ncomplo.business.services.BetTypeService;
import org.jgayoso.ncomplo.business.services.CompetitionGroupService;
import org.jgayoso.ncomplo.business.services.CompetitionService;
import org.jgayoso.ncomplo.business.services.GameService;
import org.jgayoso.ncomplo.business.services.GameSideService;
import org.jgayoso.ncomplo.business.services.RoundService;
import org.jgayoso.ncomplo.business.util.I18nUtils;
import org.jgayoso.ncomplo.web.admin.beans.GameBean;
import org.jgayoso.ncomplo.web.admin.beans.LangBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.support.RequestContextUtils;

@Controller
@RequestMapping("/admin/competition/{competitionId}/game")
public class GameController {

    private static final String VIEW_BASE = "admin/competition/game/";
    private static final SimpleDateFormat timeDateFormat = new SimpleDateFormat("HH:mm");

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private GameService gameService;

    @Autowired
    private RoundService roundService;

    @Autowired
    private GameSideService gameSideService;

    @Autowired
    private BetTypeService betTypeService;

    @Autowired
    private CompetitionGroupService competitionGroupService;

    public GameController() {
        super();
    }

    @InitBinder
    public void initDateBinder(final WebDataBinder dataBinder) {
        final SimpleDateFormat sdf = new SimpleDateFormat(I18nUtils.ISO_DATE_FORMAT);
        sdf.setLenient(false);
        dataBinder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, false));
    }

    @RequestMapping("/list")
    public String list(
            @PathVariable("competitionId") final Integer competitionId,
            final HttpServletRequest request,
            final ModelMap model) {

        final List<Game> games = this.gameService.findAll(competitionId, RequestContextUtils.getLocale(request));

        model.addAttribute("allGames", games);
        model.addAttribute("competition", this.competitionService.find(competitionId));

        return VIEW_BASE + "list";

    }

    @RequestMapping("/manage")
    public String manage(
            @RequestParam(value = "id", required = false) final Integer id,
            @PathVariable("competitionId") final Integer competitionId,
            final ModelMap model,
            final HttpServletRequest request) {

        final GameBean gameBean = new GameBean();

        if (id != null) {
            final Game game = this.gameService.find(id);
            gameBean.setId(game.getId());
            gameBean.setName(game.getName());
            gameBean.getNamesByLang().clear();
            gameBean.getNamesByLang().addAll(LangBean.listFromMap(game.getNamesByLang()));
            gameBean.setDate(game.getDate());
            final String time = timeDateFormat.format(game.getDate());
            gameBean.setTime(time);
            gameBean.setDefaultBetTypeId(game.getDefaultBetType().getId());
            gameBean.setRoundId(game.getRound().getId());
            gameBean.setOrder(game.getOrder());
            gameBean.setGameSideAId(
                    game.getGameSideA() == null ? null : game.getGameSideA().getId());
            gameBean.setGameSideBId(
                    game.getGameSideB() == null ? null : game.getGameSideB().getId());
            gameBean.setScoreA(game.getScoreA());
            gameBean.setScoreB(game.getScoreB());

            // Populate source fields
            if (game.getGameSideASource() != null) {
                final GameSideSource srcA = game.getGameSideASource();
                gameBean.setSideASourceType(srcA.getSourceType().name());
                if (srcA.getGroup() != null)
                    gameBean.setSideAGroupId(srcA.getGroup().getId());
                gameBean.setSideAPosition(srcA.getPosition());
                if (srcA.getSourceGame() != null)
                    gameBean.setSideASourceGameId(srcA.getSourceGame().getId());
            }
            if (game.getGameSideBSource() != null) {
                final GameSideSource srcB = game.getGameSideBSource();
                gameBean.setSideBSourceType(srcB.getSourceType().name());
                if (srcB.getGroup() != null)
                    gameBean.setSideBGroupId(srcB.getGroup().getId());
                gameBean.setSideBPosition(srcB.getPosition());
                if (srcB.getSourceGame() != null)
                    gameBean.setSideBSourceGameId(srcB.getSourceGame().getId());
            }
        }

        model.addAttribute("game", gameBean);
        model.addAttribute("competition", this.competitionService.find(competitionId));

        final Locale locale = RequestContextUtils.getLocale(request);
        model.addAttribute("allRounds", this.roundService.findAll(competitionId));
        model.addAttribute("allBetTypes", this.betTypeService.findAllOrderByName(competitionId, locale));
        model.addAttribute("allGameSides", this.gameSideService.findAll(competitionId, locale));
        model.addAttribute("allGroups", this.competitionGroupService.findAll(competitionId));
        model.addAttribute("allGames", this.gameService.findAll(competitionId, locale));

        return VIEW_BASE + "manage";

    }

    @RequestMapping("/save")
    public String save(
            final GameBean gameBean,
            final BindingResult bindingResult,
            @PathVariable("competitionId") final Integer competitionId) {

        if (StringUtils.isNotBlank(gameBean.getTime()) && gameBean.getDate() != null) {
            final LocalTime localTime = LocalTime.parse(gameBean.getTime(), DateTimeFormatter.ofPattern("HH:mm"));

            final Calendar cal = Calendar.getInstance();
            cal.setTime(gameBean.getDate());
            cal.set(Calendar.HOUR_OF_DAY, localTime.getHour());
            cal.set(Calendar.MINUTE, localTime.getMinute());
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            gameBean.setDate(cal.getTime());
        }

        this.gameService.save(
                gameBean.getId(),
                competitionId,
                gameBean.getDate(),
                gameBean.getName(),
                LangBean.mapFromList(gameBean.getNamesByLang()),
                gameBean.getDefaultBetTypeId(),
                gameBean.getRoundId(),
                gameBean.getOrder(),
                gameBean.getGameSideAId(),
                gameBean.getGameSideBId(),
                gameBean.getScoreA(),
                gameBean.getScoreB(),
                gameBean.getSideASourceType(),
                gameBean.getSideAGroupId(),
                gameBean.getSideAPosition(),
                gameBean.getSideASourceGameId(),
                gameBean.getSideBSourceType(),
                gameBean.getSideBGroupId(),
                gameBean.getSideBPosition(),
                gameBean.getSideBSourceGameId());

        return "redirect:list";

    }

    @RequestMapping("/deleteAll")
    public String deleteAll(@RequestParam(value = "competitionId") final Integer competitionId) {

        this.gameService.deleteAll(competitionId);
        return "redirect:list";

    }

    @RequestMapping("/delete")
    public String delete(
            @RequestParam(value = "id") final Integer id) {

        this.gameService.delete(id);
        return "redirect:list";

    }

}
