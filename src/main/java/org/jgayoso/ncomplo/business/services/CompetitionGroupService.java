package org.jgayoso.ncomplo.business.services;

import org.jgayoso.ncomplo.business.entities.Competition;
import org.jgayoso.ncomplo.business.entities.CompetitionGroup;
import org.jgayoso.ncomplo.business.entities.GameSide;
import org.jgayoso.ncomplo.business.entities.repositories.CompetitionGroupRepository;
import org.jgayoso.ncomplo.business.entities.repositories.CompetitionRepository;
import org.jgayoso.ncomplo.business.entities.repositories.GameSideRepository;
import org.jgayoso.ncomplo.business.entities.repositories.RoundRepository;
import org.jgayoso.ncomplo.business.util.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CompetitionGroupService {

    @Autowired
    private CompetitionGroupRepository competitionGroupRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private RoundRepository roundRepository;

    @Autowired
    private GameSideRepository gameSideRepository;

    public CompetitionGroupService() {
        super();
    }

    @Transactional
    public CompetitionGroup find(final Integer id) {
        return this.competitionGroupRepository.findOne(id);
    }

    @Transactional
    public List<CompetitionGroup> findAll(final Integer competitionId) {
        return IterableUtils.toList(this.competitionGroupRepository.findByCompetitionId(competitionId));
    }

    @Transactional
    public CompetitionGroup save(
            final Integer id,
            final Integer competitionId,
            final Integer roundId,
            final String name,
            final int teamsQualifying,
            final List<Integer> teamIds) {

        final Competition competition = this.competitionRepository.findOne(competitionId);

        final CompetitionGroup group = (id == null ? new CompetitionGroup()
                : this.competitionGroupRepository.findOne(id));

        group.setCompetition(competition);
        group.setName(name);
        group.setTeamsQualifying(teamsQualifying);

        if (roundId != null) {
            group.setRound(this.roundRepository.findOne(roundId));
        } else {
            group.setRound(null);
        }

        group.getTeams().clear();
        if (teamIds != null) {
            for (final Integer teamId : teamIds) {
                final GameSide side = this.gameSideRepository.findOne(teamId);
                if (side != null) {
                    group.getTeams().add(side);
                }
            }
        }

        if (id == null) {
            competition.getGroups().add(group);
            return this.competitionGroupRepository.save(group);
        }
        return group;
    }

    @Transactional
    public void delete(final Integer groupId) {
        final CompetitionGroup group = this.competitionGroupRepository.findOne(groupId);
        final Competition competition = group.getCompetition();
        competition.getGroups().remove(group);
    }
}
