package org.jgayoso.ncomplo.web.admin.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.jgayoso.ncomplo.business.entities.GameSide;
import org.jgayoso.ncomplo.business.services.CompetitionService;
import org.jgayoso.ncomplo.business.services.GameSideService;
import org.jgayoso.ncomplo.exceptions.CompetitionParserException;
import org.jgayoso.ncomplo.web.admin.beans.GameSideBean;
import org.jgayoso.ncomplo.web.admin.beans.LangBean;
import org.jgayoso.ncomplo.web.admin.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

@Controller
@RequestMapping("/admin/competition/{competitionId}/gameside")
public class GameSideController {

  private static final String VIEW_BASE = "admin/competition/gameside/";

  @Autowired private CompetitionService competitionService;

  @Autowired private GameSideService gameSideService;

  public GameSideController() {
    super();
  }

  @RequestMapping("/list")
  public String list(
      @PathVariable("competitionId") final Integer competitionId,
      final HttpServletRequest request,
      final ModelMap model) {

    final List<GameSide> gameSides =
        this.gameSideService.findAll(competitionId, RequestContextUtils.getLocale(request));

    model.addAttribute("allGameSides", gameSides);
    model.addAttribute("competition", this.competitionService.find(competitionId));

    return VIEW_BASE + "list";
  }

  @RequestMapping("/manage")
  public String manage(
      @RequestParam(value = "id", required = false) final Integer id,
      @PathVariable("competitionId") final Integer competitionId,
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
  public String save(
      final GameSideBean gameSideBean,
      final BindingResult bindingResult,
      @PathVariable("competitionId") final Integer competitionId) {

    this.gameSideService.save(
        gameSideBean.getId(),
        competitionId,
        gameSideBean.getName(),
        LangBean.mapFromList(gameSideBean.getNamesByLang()),
        gameSideBean.getCode());

    return "redirect:list";
  }

  @RequestMapping("/deleteAll")
  public String deleteAll(@RequestParam(value = "competitionId") final Integer competitionId) {

    this.gameSideService.deleteAll(competitionId);
    return "redirect:list";
  }

  @RequestMapping("/delete")
  public String delete(@RequestParam(value = "id") final Integer id) {

    this.gameSideService.delete(id);
    return "redirect:list";
  }

  @RequestMapping(method = RequestMethod.POST, value = "/upload")
  public String uploadGameSides(
      @RequestParam("file") final MultipartFile file,
      @RequestParam(value = "id") final Integer id,
      final HttpServletRequest request,
      final RedirectAttributes redirectAttributes) {

    final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth instanceof AnonymousAuthenticationToken) {
      return "error";
    }
    final String login = auth.getName();

    File competitionFile = null;
    try {
      competitionFile = FileUtils.convert("gameside", file, login);
      if (!competitionFile.exists() || competitionFile.length() == 0) {
        redirectAttributes.addFlashAttribute("error", "Empty file");
        return "redirect:list";
      }
      this.gameSideService.processFile(id, login, competitionFile);
    } catch (final IOException e) {
      redirectAttributes.addFlashAttribute("error", "Error processing game sides file");
      return "redirect:list";
    } catch (CompetitionParserException e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return "redirect:list";
    } finally {
      // delete file
      if (competitionFile != null) {
        competitionFile.delete();
      }
    }
    redirectAttributes.addFlashAttribute("message", "Game sides processed successfully");
    return "redirect:list";
  }
}
