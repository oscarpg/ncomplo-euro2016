package org.jgayoso.ncomplo.business.tasks;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.jgayoso.ncomplo.business.entities.Bet;
import org.jgayoso.ncomplo.business.entities.BetType;
import org.jgayoso.ncomplo.business.entities.League;
import org.jgayoso.ncomplo.business.entities.User;
import org.jgayoso.ncomplo.business.entities.repositories.LeagueRepository;
import org.jgayoso.ncomplo.business.services.BetService;
import org.jgayoso.ncomplo.business.services.EmailServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Component
@Transactional
public class UserBetsReviewer {

    private static final Logger logger = Logger.getLogger(UserBetsReviewer.class);

    @Autowired
    private LeagueRepository leagueRepository;
    @Autowired
    private BetService betService;
    @Autowired
    private EmailServiceFactory emailServiceFactory;

    @Scheduled(cron = "0 0 19 * * *")
    public void reviewUserBets() {
        logger.debug("Executing reviewUserBets cron ");
        Iterable<League> leagues = leagueRepository.findAll();

        leagues.forEach(this::reviewLeaguesBets);

    }

    private void reviewLeaguesBets(League league) {
        if (!league.isActive()) {
            return;
        }

        Instant now = new Date().toInstant();
        Instant twoHours = now.plus( 2 , ChronoUnit.HOURS );

        Instant leagueDeadTime = league.getBetsDeadLine().toInstant();

        List<User> usersWithInvalidBets = new ArrayList<>();
        if (leagueDeadTime.isAfter(now) && leagueDeadTime.isBefore(twoHours)) {
            // League dead time is in 2 hours
            league.getParticipants().forEach(user -> {
                boolean invalid = reviewLeaguesBets(league, user);
                if (invalid) {
                    usersWithInvalidBets.add(user);
                }
            });
        }

        if (!usersWithInvalidBets.isEmpty()) {
            try {
                emailServiceFactory.getEmailService().sendInvalidBetsWarningToLeagueAdmin(usersWithInvalidBets, league);
            } catch (IOException e) {
                logger.error("Error sending warning to league admin", e);
            }
        }

    }

    private boolean reviewLeaguesBets(League league, User user) {

        boolean invalidBets = false;
        List<Bet> bets =  betService.findByLeagueIdAndUserLogin(league.getId(), user.getLogin(), Locale.getDefault());
        if (CollectionUtils.isEmpty(bets)) {
            invalidBets = true;
        } else {
            for (Bet bet: bets) {
                if (isInvalid(bet)) {
                    invalidBets = true;
                    break;
                }
            }
        }

        if (invalidBets) {
            try {
                emailServiceFactory.getEmailService().sendInvalidBetsWarning(user, league);
                Thread.sleep(1000);
            } catch (IOException e) {
                logger.error("Error sending warning", e);
            } catch (InterruptedException e) {
                logger.error("InterruptedException sending warnings", e);
            }
        }

        return invalidBets;


    }

    private boolean isInvalid(Bet bet) {
        BetType betType = bet.getGame().getDefaultBetType();
        return betType.isSidesMatter() && (
                bet.getGameSideA() == null || bet.getGameSideB() == null
                || bet.getScoreA() == null || bet.getScoreB() == null
                || bet.getScoreA().equals(bet.getScoreB()));
    }
}
