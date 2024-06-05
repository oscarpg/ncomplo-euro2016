package org.jgayoso.ncomplo.business.entities.repositories;

import java.util.List;
import org.jgayoso.ncomplo.business.entities.Invitation;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface InvitationRepository extends PagingAndSortingRepository<Invitation, Integer> {

  public List<Invitation> findByLeagueId(final Integer leagueId);

  public List<Invitation> findByLeagueIdAndTokenIsNull(final Integer leagueId);

  public Invitation findByToken(final String token);

  public Invitation findByLeagueIdAndTokenIsNotNull(final Integer leagueId);

  public Invitation findByLeagueIdAndEmail(final Integer leagueId, final String email);

  public void deleteByLeagueId(final Integer leagueId);
}
