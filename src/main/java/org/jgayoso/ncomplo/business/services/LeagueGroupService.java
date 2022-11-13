package org.jgayoso.ncomplo.business.services;

import org.jgayoso.ncomplo.business.entities.*;
import org.jgayoso.ncomplo.business.entities.repositories.LeagueGroupRepository;
import org.jgayoso.ncomplo.business.util.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class LeagueGroupService {

    @Autowired
    private LeagueGroupRepository leagueGroupRepository;
    @Autowired
    private LeagueService leagueService;

    public LeagueGroupService() {
        super();
    }

    @Transactional
    public LeagueGroup find(final Integer id) {
        return this.leagueGroupRepository.findOne(id);
    }

    @Transactional
    public List<LeagueGroup> findAll() {
        return IterableUtils.toList(this.leagueGroupRepository.findAll());
    }

    @Transactional
    public LeagueGroup save(final Integer id, final String name, final List<Integer> leagueIds) {

        final LeagueGroup leagueGroup = (id == null ? new LeagueGroup() : this.leagueGroupRepository.findOne(id));
        leagueGroup.setName(name);

        for (final League league : leagueGroup.getLeagues()) {
            if (!leagueIds.contains(league.getId())) {
                league.setLeagueGroup(null);
            }
        }

        leagueGroup.getLeagues().clear();
        for (final Integer leagueId : leagueIds) {
            final League league = this.leagueService.find(leagueId);
            leagueGroup.getLeagues().add(league);
            league.setLeagueGroup(leagueGroup);
        }

        if (id == null) {
            return this.leagueGroupRepository.save(leagueGroup);
        }

        return leagueGroup;
    }

    @Transactional
    public void delete(final Integer id) {
        this.leagueGroupRepository.delete(id);
    }

    @Transactional
    public LeagueGroup addLeague(Integer id, Integer leagueId) {
        LeagueGroup leagueGroup = this.leagueGroupRepository.findOne(id);
        if (leagueGroup == null) {
            throw new EntityNotFoundException("League group "+ id + " not found");
        }

        League league = this.leagueService.find(leagueId);
        if (league == null) {
            throw new EntityNotFoundException("League "+ leagueId + " not found");
        }

        leagueGroup.getLeagues().add(league);
        league.setLeagueGroup(leagueGroup);
        return this.leagueGroupRepository.save(leagueGroup);

    }

}
