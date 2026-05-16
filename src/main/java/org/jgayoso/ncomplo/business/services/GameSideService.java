package org.jgayoso.ncomplo.business.services;

import org.jgayoso.ncomplo.business.entities.Competition;
import org.jgayoso.ncomplo.business.entities.GameSide;
import org.jgayoso.ncomplo.business.entities.repositories.CompetitionRepository;
import org.jgayoso.ncomplo.business.entities.repositories.GameSideRepository;
import org.jgayoso.ncomplo.business.util.I18nNamedEntityComparator;
import org.jgayoso.ncomplo.business.util.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

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
        final List<GameSide> gameSides = IterableUtils
                .toList(this.gameSideRepository.findByCompetitionId(competitionId));
        Collections.sort(gameSides, new I18nNamedEntityComparator(locale));
        return gameSides;
    }

    @Transactional
    public GameSide save(
            final Integer id,
            final Integer competitionId,
            final String name,
            final Map<String, String> namesByLang,
            final String code) {

        final Competition competition = this.competitionRepository.findOne(competitionId);

        final GameSide gameSide = (id == null ? new GameSide() : this.gameSideRepository.findOne(id));

        gameSide.setCompetition(competition);
        gameSide.setName(name);
        gameSide.getNamesByLang().clear();
        gameSide.getNamesByLang().putAll(namesByLang);
        gameSide.setCode(code);

        if (id == null) {
            competition.getGameSides().add(gameSide);
            return this.gameSideRepository.save(gameSide);
        }
        return gameSide;

    }

    @Transactional
    public void deleteAll(final Integer competitionId) {

        Competition competition = this.competitionRepository.findOne(competitionId);

        for (GameSide gameSide : new HashSet<>(competition.getGameSides())) {
            competition.getGameSides().remove(gameSide);
        }

    }

    @Transactional
    public void delete(final Integer gameSideId) {

        final GameSide gameSide = this.gameSideRepository.findOne(gameSideId);
        final Competition competition = gameSide.getCompetition();

        competition.getGameSides().remove(gameSide);

    }

}
