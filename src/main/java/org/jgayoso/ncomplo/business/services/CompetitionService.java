package org.jgayoso.ncomplo.business.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jgayoso.ncomplo.business.entities.*;
import org.jgayoso.ncomplo.business.entities.repositories.CompetitionParserPropertiesRepository;
import org.jgayoso.ncomplo.business.entities.repositories.CompetitionRepository;
import org.jgayoso.ncomplo.business.util.ExcelProcessor;
import org.jgayoso.ncomplo.business.util.I18nNamedEntityComparator;
import org.jgayoso.ncomplo.business.util.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
public class CompetitionService {

    private static final Logger logger = Logger.getLogger(CompetitionService.class);


    @Autowired
    private CompetitionRepository competitionRepository;
    @Autowired
    private CompetitionParserPropertiesRepository competitionParserPropertiesRepository;

    @Autowired
    private GameSideService gameSideService;

    @Autowired
    private GameService gameService;

    @Autowired
    private BetTypeService betTypeService;
    @Autowired
    private RoundService roundService;
    
    
    
    public CompetitionService() {
        super();
    }
    
    
    @Transactional
    public Competition find(final Integer id) {
        return this.competitionRepository.findOne(id);
    }
    
    
    @Transactional
    public List<Competition> findAll(final Locale locale) {
        final List<Competition> competitions = IterableUtils.toList(this.competitionRepository.findAll());
        Collections.sort(competitions, new I18nNamedEntityComparator(locale));
        return competitions;
    }

    
    @Transactional
    public Competition save(
            final Integer id,
            final String name, 
            final Map<String,String> namesByLang, 
			final boolean active, final String updaterUri,
            final String teamsSheetName, final String teamsColumnName, final Integer teamsStartIndex, final Integer teamsNumber) {
        
        final Competition competition =
                (id == null? new Competition() : this.competitionRepository.findOne(id));

        competition.setName(name);
        competition.getNamesByLang().clear();
        competition.getNamesByLang().putAll(namesByLang);
        competition.setActive(active);
		competition.setUpdaterUri(updaterUri);

        if (competition.getCompetitionParserProperties() == null) {
            competition.setCompetitionParserProperties(new CompetitionParserProperties());
        }
        CompetitionParserProperties competitionParserProperties = competition.getCompetitionParserProperties();
        competitionParserProperties.setTeamsSheetName(teamsSheetName);
        competitionParserProperties.setTeamsColumnName(teamsColumnName);
        competitionParserProperties.setTeamsStartIndex(teamsStartIndex);
        competitionParserProperties.setTeamsNumber(teamsNumber);
        competitionParserProperties.setCompetition(competition);

        competitionParserPropertiesRepository.save(competitionParserProperties);
        competition.setCompetitionParserProperties(competitionParserProperties);

        if (id == null) {
            return this.competitionRepository.save(competition);
        }
        return competition;
        
    }
    
    @Transactional
    public void delete(final Integer competitionId) {
        this.competitionRepository.delete(competitionId);
    }


}
