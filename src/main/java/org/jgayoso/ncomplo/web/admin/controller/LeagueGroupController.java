package org.jgayoso.ncomplo.web.admin.controller;

import org.jgayoso.ncomplo.business.entities.League;
import org.jgayoso.ncomplo.business.entities.LeagueGroup;
import org.jgayoso.ncomplo.business.services.LeagueGroupService;
import org.jgayoso.ncomplo.business.services.LeagueService;
import org.jgayoso.ncomplo.web.admin.beans.LeagueGroupBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/admin/leagueGroup")
public class LeagueGroupController {

    private static final String VIEW_BASE = "admin/leagueGroup/";

    @Autowired
    private LeagueGroupService leagueGroupService;

    @Autowired
    private LeagueService leagueService;

    public LeagueGroupController() {
        super();
    }

    @RequestMapping("/list")
    public String list(final HttpServletRequest request, final ModelMap model) {
        final List<LeagueGroup> lGroups = this.leagueGroupService.findAll();
        model.addAttribute("allLeagueGroups", lGroups);
        return VIEW_BASE + "list";
    }

    @RequestMapping("/manage")
    public String manage(
            @RequestParam(value="leagueGroupId",required=false)
            final Integer leagueGroupId,
            final HttpServletRequest request,
            final ModelMap model) {

        final LeagueGroupBean leagueGroupBean = new LeagueGroupBean();

        if (leagueGroupId != null) {

            final LeagueGroup leagueGroup = this.leagueGroupService.find(leagueGroupId);

            leagueGroupBean.setId(leagueGroup.getId());
            leagueGroupBean.setName(leagueGroup.getName());

            final Integer[] leagueIds = new Integer[leagueGroup.getLeagues().size()];
            int i = 0;
            for (final League league : leagueGroup.getLeagues()) {
                leagueIds[i++] = league.getId();
            }
            leagueGroupBean.setLeagueIds(leagueIds);

        }

        model.addAttribute("leagueGroup", leagueGroupBean);
        model.addAttribute("allLeagues", this.leagueService.findAll(RequestContextUtils.getLocale(request)));

        return VIEW_BASE + "manage";

    }

    @RequestMapping("/save")
    public String save(
            final LeagueGroupBean leagueGroupBean,
            @SuppressWarnings("unused") final BindingResult bindingResult) {

        this.leagueGroupService.save(
                leagueGroupBean.getId(),
                leagueGroupBean.getName(),
                (leagueGroupBean.getLeagueIds() != null ?
                        Arrays.asList(leagueGroupBean.getLeagueIds()) : new ArrayList<>()));

        return "redirect:list";

    }

    @RequestMapping("/delete")
    public String delete(
            @RequestParam(value="leagueGroupId", required=true) final Integer leagueGroupId) {

        this.leagueGroupService.delete(leagueGroupId);
        return "redirect:list";

    }
}
