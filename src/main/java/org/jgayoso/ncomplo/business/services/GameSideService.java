package org.jgayoso.ncomplo.business.services;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.jgayoso.ncomplo.business.entities.Competition;
import org.jgayoso.ncomplo.business.entities.GameSide;
import org.jgayoso.ncomplo.business.entities.repositories.CompetitionRepository;
import org.jgayoso.ncomplo.business.entities.repositories.GameSideRepository;
import org.jgayoso.ncomplo.business.util.I18nNamedEntityComparator;
import org.jgayoso.ncomplo.business.util.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class GameSideService {
    
    
    @Autowired
    private CompetitionRepository competitionRepository;

   
    @Autowired
    private GameSideRepository gameSideRepository;
    
    
    
    public GameSideService() {
        super();
    }
    
    
    @Transactional
    public GameSide find(final Integer id) {
        return this.gameSideRepository.findOne(id);
    }
    
    
    @Transactional
    public List<GameSide> findAll(final Integer competitionId, final Locale locale) {
        final List<GameSide> gameSides = 
                IterableUtils.toList(this.gameSideRepository.findByCompetitionId(competitionId));
        Collections.sort(gameSides, new I18nNamedEntityComparator(locale));
        return gameSides;
    }
    
    @Transactional
    public GameSide save(
            final Integer id,
            final String name,
            final Map<String,String> namesByLang,
            final String code) {

        final GameSide gameSide =
                (id == null? new GameSide() : this.gameSideRepository.findOne(id));

        gameSide.setName(name);
        gameSide.getNamesByLang().clear();
        gameSide.getNamesByLang().putAll(namesByLang);
        gameSide.setCode(code);
        
        if (id == null) {
            return this.gameSideRepository.save(gameSide);
        }
        return gameSide;
    }

    @Transactional
    public GameSide addCompetition(final Integer id, final Competition competition) {

        final GameSide gameSide = this.gameSideRepository.findOne(id);
        if (gameSide == null) {
            return null;
        }

        gameSide.getCompetitions().add(competition);
        return this.gameSideRepository.save(gameSide);
    }

    @Transactional
    public GameSide removeCompetition(final Integer id, final Competition competition) {

        final GameSide gameSide = this.gameSideRepository.findOne(id);
        if (gameSide == null) {
            return null;
        }

        gameSide.getCompetitions().remove(competition);
        return this.gameSideRepository.save(gameSide);
    }

    @Transactional
    public void delete(final Integer gameSideId) {
        
        final GameSide gameSide = this.gameSideRepository.findOne(gameSideId);
        if (CollectionUtils.isNotEmpty(gameSide.getCompetitions())) {
            return;
        }

        this.gameSideRepository.delete(gameSideId);
        

        
    }

    
}
