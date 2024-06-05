package org.jgayoso.ncomplo.business.entities.repositories;

import java.util.List;
import org.jgayoso.ncomplo.business.entities.BetType;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BetTypeRepository extends PagingAndSortingRepository<BetType, Integer> {

  public List<BetType> findByCompetitionId(final Integer competitionId);

  public BetType findByCompetitionIdAndName(final Integer competitionId, final String name);
}
