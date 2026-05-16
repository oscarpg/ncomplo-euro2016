package org.jgayoso.ncomplo.web.admin.controller;

import org.jgayoso.ncomplo.business.entities.CompetitionGroup;
import org.jgayoso.ncomplo.business.entities.GameSide;
import org.jgayoso.ncomplo.business.services.CompetitionGroupService;
import org.jgayoso.ncomplo.business.services.CompetitionService;
import org.jgayoso.ncomplo.business.services.GameSideService;
import org.jgayoso.ncomplo.business.services.RoundService;
import org.jgayoso.ncomplo.business.util.I18nNamedEntityComparator;
import org.jgayoso.ncomplo.web.admin.beans.CompetitionGroupBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/admin/competition/{competitionId}/group")
public class CompetitionGroupController {

    private static final String VIEW_BASE = "admin/competition/group/";

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private CompetitionGroupService competitionGroupService;

    @Autowired
    private RoundService roundService;

    @Autowired
    private GameSideService gameSideService;

    public CompetitionGroupController() {
        super();
    }

    @RequestMapping("/list")
    public String list(
            @PathVariable("competitionId") final Integer competitionId,
            final HttpServletRequest request,
            final ModelMap model) {

        final List<CompetitionGroup> groups = this.competitionGroupService.findAll(competitionId);

        model.addAttribute("allGroups", groups);
        model.addAttribute("competition", this.competitionService.find(competitionId));

        return VIEW_BASE + "list";
    }

    @RequestMapping("/manage")
    public String manage(
            @RequestParam(value = "id", required = false) final Integer id,
            @PathVariable("competitionId") final Integer competitionId,
            final HttpServletRequest request,
            final ModelMap model) {

        final Locale locale = RequestContextUtils.getLocale(request);

        final CompetitionGroupBean groupBean = new CompetitionGroupBean();

        if (id != null) {
            final CompetitionGroup group = this.competitionGroupService.find(id);
            groupBean.setId(group.getId());
            groupBean.setName(group.getName());
            groupBean.setTeamsQualifying(group.getTeamsQualifying());
            if (group.getRound() != null) {
                groupBean.setRoundId(group.getRound().getId());
            }
            for (final GameSide team : group.getTeams()) {
                groupBean.getTeamIds().add(team.getId());
            }
        }

        final List<GameSide> allTeams = new ArrayList<>(this.gameSideService.findAll(competitionId, locale));
        Collections.sort(allTeams, new I18nNamedEntityComparator(locale));

        model.addAttribute("group", groupBean);
        model.addAttribute("competition", this.competitionService.find(competitionId));
        model.addAttribute("allRounds", this.roundService.findAll(competitionId));
        model.addAttribute("allTeams", allTeams);

        return VIEW_BASE + "manage";
    }

    @RequestMapping("/save")
    public String save(
            final CompetitionGroupBean groupBean,
            @PathVariable("competitionId") final Integer competitionId) {

        this.competitionGroupService.save(
                groupBean.getId(),
                competitionId,
                groupBean.getRoundId(),
                groupBean.getName(),
                groupBean.getTeamsQualifying(),
                groupBean.getTeamIds());

        return "redirect:list";
    }

    @RequestMapping("/delete")
    public String delete(
            @RequestParam(value = "id") final Integer id) {

        this.competitionGroupService.delete(id);
        return "redirect:list";
    }
}
