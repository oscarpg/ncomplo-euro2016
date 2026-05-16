package org.jgayoso.ncomplo.business.entities.repositories;

import org.jgayoso.ncomplo.business.entities.GameSideSource;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameSideSourceRepository extends PagingAndSortingRepository<GameSideSource, Integer> {
}
