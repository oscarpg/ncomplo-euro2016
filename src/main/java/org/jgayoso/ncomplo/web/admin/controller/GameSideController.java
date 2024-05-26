package org.jgayoso.ncomplo.web.admin.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jgayoso.ncomplo.business.entities.GameSide;
import org.jgayoso.ncomplo.business.services.CompetitionService;
import org.jgayoso.ncomplo.business.services.GameSideService;
import org.jgayoso.ncomplo.web.admin.beans.GameSideBean;
import org.jgayoso.ncomplo.web.admin.beans.LangBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.RequestContextUtils;

@Controller
@RequestMapping("/admin/competition/{competitionId}/gameside")
public class GameSideController {

    private static final String VIEW_BASE = "admin/competition/gameside/";
    
    
    @Autowired
    private CompetitionService competitionService;
    
    @Autowired
    private GameSideService gameSideService;
    
    public GameSideController() {
        super();
    }

    @RequestMapping("/list")
    public String list(
            @PathVariable("competitionId") final Integer competitionId, 
            final HttpServletRequest request, 
            final ModelMap model) {
        
        final List<GameSide> gameSides =
                this.gameSideService.findAll(competitionId,RequestContextUtils.getLocale(request));
        
        model.addAttribute("allGameSides", gameSides);
        model.addAttribute("competition", this.competitionService.find(competitionId));
        
        return VIEW_BASE + "list";
        
    }
    
    @RequestMapping("/manage")
    public String manage(
            @RequestParam(value="id",required=false)
            final Integer id,
            @PathVariable("competitionId")
            final Integer competitionId,
            final ModelMap model) {

        final GameSideBean gameSideBean = new GameSideBean();
        
        if (id != null) {
            final GameSide gameSide = this.gameSideService.find(id);
            gameSideBean.setId(gameSide.getId());
            gameSideBean.setName(gameSide.getName());
            gameSideBean.getNamesByLang().clear();
            gameSideBean.getNamesByLang().addAll(LangBean.listFromMap(gameSide.getNamesByLang()));
			gameSideBean.setCode(gameSide.getCode());
        }
        
        model.addAttribute("gameSide", gameSideBean);
        model.addAttribute("competition", this.competitionService.find(competitionId));
        
        return VIEW_BASE + "manage";
        
    }

    @RequestMapping("/save")
    public String save(final GameSideBean gameSideBean, final BindingResult bindingResult) {

        this.gameSideService.save(
                gameSideBean.getId(),
                gameSideBean.getName(),
                LangBean.mapFromList(gameSideBean.getNamesByLang()),
                gameSideBean.getCode());
        
        return "redirect:list";
        
    }

    
    
    @RequestMapping("/delete")
    public String delete(
            @RequestParam(value="id")
            final Integer id) {

        this.gameSideService.delete(id);
        return "redirect:list";
        
    }
    
    
}
