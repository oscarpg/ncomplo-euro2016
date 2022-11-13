package org.jgayoso.ncomplo.business.entities.repositories;

import org.jgayoso.ncomplo.business.entities.LeagueGroup;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LeagueGroupRepository
        extends PagingAndSortingRepository<LeagueGroup,Integer> {
    
    // No methods to add
    
}
    