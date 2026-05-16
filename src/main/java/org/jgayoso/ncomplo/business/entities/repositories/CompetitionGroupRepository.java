package org.jgayoso.ncomplo.business.entities.repositories;

import org.jgayoso.ncomplo.business.entities.CompetitionGroup;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompetitionGroupRepository extends PagingAndSortingRepository<CompetitionGroup, Integer> {

    List<CompetitionGroup> findByCompetitionId(Integer competitionId);

}
