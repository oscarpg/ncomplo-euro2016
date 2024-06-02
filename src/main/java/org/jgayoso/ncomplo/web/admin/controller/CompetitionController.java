package org.jgayoso.ncomplo.web.admin.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jgayoso.ncomplo.business.entities.Competition;
import org.jgayoso.ncomplo.business.entities.CompetitionParserProperties;
import org.jgayoso.ncomplo.business.services.CompetitionService;
import org.jgayoso.ncomplo.web.admin.beans.CompetitionBean;
import org.jgayoso.ncomplo.web.admin.beans.LangBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.RequestContextUtils;

@Controller
@RequestMapping("/admin/competition")
public class CompetitionController {

    private static final String VIEW_BASE = "admin/competition/";
    
    
    @Autowired
    private CompetitionService competitionService;

    
    
    public CompetitionController() {
        super();
    }
    

    
    
    @RequestMapping("/list")
    public String list(final HttpServletRequest request, final ModelMap model) {
        final List<Competition> competitions =
                this.competitionService.findAll(RequestContextUtils.getLocale(request));
        model.addAttribute("allCompetitions", competitions);
        return VIEW_BASE + "list";
    }

    
    
    @RequestMapping("/manage")
    public String manage(
            @RequestParam(value="id",required=false)
            final Integer id,
            final ModelMap model) {
        
        final CompetitionBean competitionBean = new CompetitionBean();
        
        if (id != null) {
            
            final Competition competition = this.competitionService.find(id);
            
            competitionBean.setId(competition.getId());
            competitionBean.setName(competition.getName());
            competitionBean.setActive(competition.isActive());
			competitionBean.setUpdaterUri(competition.getUpdaterUri());
            competitionBean.getNamesByLang().clear();
            competitionBean.getNamesByLang().addAll(LangBean.listFromMap(competition.getNamesByLang()));

            if (competition.getCompetitionParserProperties() != null) {
                CompetitionParserProperties properties = competition.getCompetitionParserProperties();
                competitionBean.setTeamsSheetName(properties.getTeamsSheetName());
                competitionBean.setTeamsColumnName(properties.getTeamsColumnName());
                competitionBean.setTeamsStartIndex(properties.getTeamsStartIndex());
                competitionBean.setTeamsNumber(properties.getTeamsNumber());

                competitionBean.setGroupsName(properties.getGroupsName());
                competitionBean.setRoundOf16Name(properties.getRoundOf16Name());
                competitionBean.setQuarterFinalsName(properties.getQuarterFinalsName());
                competitionBean.setSemiFinalsName(properties.getSemiFinalsName());
                competitionBean.setFinalName(properties.getFinalName());

                competitionBean.setGamesSheetName(properties.getGamesSheetName());
                competitionBean.setGroupGamesColumnName(properties.getGroupGamesColumnName());
                competitionBean.setGroupGamesStartIndex(properties.getGroupGamesStartIndex());
                competitionBean.setGroupGamesNumber(properties.getGroupGamesNumber());
                competitionBean.setGroupsGamesHomeIndex(properties.getGroupGamesHomeIndex());
                competitionBean.setGroupsGamesAwayIndex(properties.getGroupGamesAwayIndex());
                competitionBean.setGroupsGamesDateIndex(properties.getGroupsGamesDateIndex());
                competitionBean.setGroupsGamesHourIndex(properties.getGroupsGamesHourIndex());
                competitionBean.setGroupsGamesDateFormat(properties.getGroupsGamesDateFormat());

                competitionBean.setPlayoffsGamesDateIndex(properties.getPlayoffsGamesDateIndex());
                competitionBean.setPlayoffsGamesHourIndex(properties.getPlayoffsGamesHourIndex());
                competitionBean.setPlayoffsGamesDateFormat(properties.getPlayoffsGamesDateFormat());

                competitionBean.setRoundOf16GamesColumnName(properties.getRoundOf16GamesColumnName());
                competitionBean.setRoundOf16GamesStartIndex(properties.getRoundOf16GamesStartIndex());
                competitionBean.setRoundOf16GamesJumpSize(properties.getRoundOf16GamesJumpSize());

                competitionBean.setQuarteFinalsGamesColumnName(properties.getQuarteFinalsGamesColumnName());
                competitionBean.setQuarteFinalsGamesStartIndex(properties.getQuarteFinalsGamesStartIndex());
                competitionBean.setQuarteFinalsGamesJumpSize(properties.getQuarteFinalsGamesJumpSize());

                competitionBean.setSemiFinalsGamesColumnName(properties.getSemiFinalsGamesColumnName());
                competitionBean.setSemiFinalsGamesStartIndex(properties.getSemiFinalsGamesStartIndex());
                competitionBean.setSemiFinalsGamesJumpSize(properties.getSemiFinalsGamesJumpSize());

                competitionBean.setFinalGamesColumnName(properties.getFinalGamesColumnName());
                competitionBean.setFinalGamesStartIndex(properties.getFinalGamesStartIndex());
            }
            
        }
        
        model.addAttribute("competition", competitionBean);
        
        return VIEW_BASE + "manage";
        
    }
    
    @RequestMapping("/save")
	public String save(final CompetitionBean competitionBean, final BindingResult bindingResult) {

        CompetitionParserProperties properties = null;

        if (competitionBean.isUpdateProperties()) {
            properties = new CompetitionParserProperties();
            properties.setTeamsSheetName(competitionBean.getTeamsSheetName());
            properties.setTeamsColumnName(competitionBean.getTeamsColumnName());
            properties.setTeamsStartIndex(competitionBean.getTeamsStartIndex());
            properties.setTeamsNumber(competitionBean.getTeamsNumber());

            properties.setGroupsName(competitionBean.getGroupsName());
            properties.setRoundOf16Name(competitionBean.getRoundOf16Name());
            properties.setQuarterFinalsName(competitionBean.getQuarterFinalsName());
            properties.setSemiFinalsName(competitionBean.getSemiFinalsName());
            properties.setFinalName(competitionBean.getFinalName());

            properties.setGamesSheetName(competitionBean.getGamesSheetName());
            properties.setGroupGamesColumnName(competitionBean.getGroupGamesColumnName());
            properties.setGroupGamesStartIndex(competitionBean.getGroupGamesStartIndex());
            properties.setGroupGamesNumber(competitionBean.getGroupGamesNumber());
            properties.setGroupGamesHomeIndex(competitionBean.getGroupsGamesHomeIndex());
            properties.setGroupGamesAwayIndex(competitionBean.getGroupsGamesAwayIndex());
            properties.setGroupsGamesDateIndex(competitionBean.getGroupsGamesDateIndex());
            properties.setGroupsGamesHourIndex(competitionBean.getGroupsGamesHourIndex());
            properties.setGroupsGamesDateFormat(competitionBean.getGroupsGamesDateFormat());

            properties.setPlayoffsGamesDateIndex(competitionBean.getPlayoffsGamesDateIndex());
            properties.setPlayoffsGamesHourIndex(competitionBean.getPlayoffsGamesHourIndex());
            properties.setPlayoffsGamesDateFormat(competitionBean.getPlayoffsGamesDateFormat());

            properties.setRoundOf16GamesColumnName(competitionBean.getRoundOf16GamesColumnName());
            properties.setRoundOf16GamesStartIndex(competitionBean.getRoundOf16GamesStartIndex());
            properties.setRoundOf16GamesJumpSize(competitionBean.getRoundOf16GamesJumpSize());

            properties.setQuarteFinalsGamesColumnName(competitionBean.getQuarteFinalsGamesColumnName());
            properties.setQuarteFinalsGamesStartIndex(competitionBean.getQuarteFinalsGamesStartIndex());
            properties.setQuarteFinalsGamesJumpSize(competitionBean.getQuarteFinalsGamesJumpSize());

            properties.setSemiFinalsGamesColumnName(competitionBean.getSemiFinalsGamesColumnName());
            properties.setSemiFinalsGamesStartIndex(competitionBean.getSemiFinalsGamesStartIndex());
            properties.setSemiFinalsGamesJumpSize(competitionBean.getSemiFinalsGamesJumpSize());

            properties.setFinalGamesColumnName(competitionBean.getFinalGamesColumnName());
            properties.setFinalGamesStartIndex(competitionBean.getFinalGamesStartIndex());
        }

		this.competitionService.save(competitionBean.getId(), competitionBean.getName(),
				LangBean.mapFromList(competitionBean.getNamesByLang()), competitionBean.isActive(),
				competitionBean.getUpdaterUri(), properties);
        
        return "redirect:list";
        
    }

    
    @RequestMapping("/delete")
    public String delete(
            @RequestParam(value="id")
            final Integer id) {

        this.competitionService.delete(id);
        return "redirect:list";
        
    }
    
}
