package org.jgayoso.ncomplo.business.util;

import com.neovisionaries.i18n.CountryCode;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jgayoso.ncomplo.business.entities.BetType;
import org.jgayoso.ncomplo.business.entities.Game;
import org.jgayoso.ncomplo.business.entities.GameSide;
import org.jgayoso.ncomplo.business.entities.Round;
import org.jgayoso.ncomplo.business.services.GameService;
import org.jgayoso.ncomplo.business.services.GameSideService;
import org.jgayoso.ncomplo.business.views.BetView;

import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.*;

public class ExcelProcessor {

    private static final Logger logger = Logger.getLogger(ExcelProcessor.class);

    private static final String TEAMS_SHEET_NAME = "Settings";
    private static final String TEAMS_COLUMN = "B";
    private static final int TEAMS_START_INDEX = 15;
    private static final int NUMBER_OF_TEAMS = 24;

    private static final String GROUP_GAMES_COLUMN = "A";
    public static final String GROUPS_NAME = "Groups";
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd,yyyy");
    public static final SimpleDateFormat PLAYOFF_DATE_FORMAT = new SimpleDateFormat("MMM dd, yyyy   HH:ss");
//    Jul 1, 2024   21:00

    public static BetView processGroupsGameBet(final XSSFSheet sheet, final int rowIndex, final int matchNumber,
                                               final String columnName, final Map<Integer, Game> gamesByOrder,
                                         final Map<Integer, BetView> betsByGameId) {

        final CellReference cellReference = new CellReference(columnName + rowIndex);
        final Row row = sheet.getRow(cellReference.getRow());
        final Cell homeResultCell = row.getCell(cellReference.getCol() + 1);
        final Cell awayResultCell = row.getCell(cellReference.getCol() + 2);

        final int homeResult = Double.valueOf(homeResultCell.getNumericCellValue()).intValue();
        final int awayResult = Double.valueOf(awayResultCell.getNumericCellValue()).intValue();

        final Game game = gamesByOrder.get(Integer.valueOf(matchNumber));
        final BetView betView = betsByGameId.get(game.getId());
        betView.setScoreA(Integer.valueOf(homeResult));
        betView.setScoreB(Integer.valueOf(awayResult));
        return betView;
    }

    public static BetView processPlayOffGameBet(final XSSFSheet sheet, final int rowIndex, final int matchNumber,
                                          final String columnName, final Map<Integer, Game> gamesByOrder,
                                          final Map<Integer, BetView> betsByGameId,
                                          final Map<String, GameSide> gameSidesByName) {

        final CellReference homeCellReference = new CellReference(columnName + rowIndex);
        final Row homeRow = sheet.getRow(homeCellReference.getRow());
        final Cell homeTeamCell = homeRow.getCell(homeCellReference.getCol());
        final Cell homeResultCell = homeRow.getCell(homeCellReference.getCol() + 1);
        final Cell extraTimeHomeResultCell = homeRow.getCell(homeCellReference.getCol() + 2);
        final Cell penaltiesHomeResultCell = homeRow.getCell(homeCellReference.getCol() + 3);
        final String homeTeamName = homeTeamCell.getStringCellValue();
        int homeResult = Double.valueOf(homeResultCell.getNumericCellValue()).intValue();
        final int extraTimeHomeResult = Double.valueOf(extraTimeHomeResultCell.getNumericCellValue()).intValue();
        final int penaltiesHomeResult = Double.valueOf(penaltiesHomeResultCell.getNumericCellValue()).intValue();

        final CellReference awayCellReference = new CellReference(columnName + (rowIndex + 1));
        final Row awayRow = sheet.getRow(awayCellReference.getRow());
        final Cell awayTeamCell = awayRow.getCell(awayCellReference.getCol());
        final Cell awayResultCell = awayRow.getCell(awayCellReference.getCol() + 1);
        final Cell extraTimeAwayResultCell = awayRow.getCell(homeCellReference.getCol() + 2);
        final Cell penaltiesAwayResultCell = awayRow.getCell(homeCellReference.getCol() + 3);
        final String awayTeamName = awayTeamCell.getStringCellValue();
        int awayResult = Double.valueOf(awayResultCell.getNumericCellValue()).intValue();
        final int extraTimeAwayResult = Double.valueOf(extraTimeAwayResultCell.getNumericCellValue()).intValue();
        final int penaltiesAwayResult = Double.valueOf(penaltiesAwayResultCell.getNumericCellValue()).intValue();

        final Integer gameSideAId =
                gameSidesByName.get(homeTeamName) == null ? null : gameSidesByName.get(homeTeamName).getId();
        final Integer gameSideBId =
                gameSidesByName.get(awayTeamName) == null ? null : gameSidesByName.get(awayTeamName).getId();

        final Game game = gamesByOrder.get(Integer.valueOf(matchNumber));
        final BetView betView = betsByGameId.get(game.getId());
        betView.setGameSideAId(gameSideAId);
        betView.setGameSideBId(gameSideBId);

        if (homeResult == awayResult) {
            // We need to parse the extra time and maybe penalties results
            if (extraTimeHomeResult == extraTimeAwayResult) {
                // Penalties
                homeResult = penaltiesHomeResult > penaltiesAwayResult ? 1 : 0;
                awayResult = homeResult == 1 ? 0 : 1;
            }
        }

        betView.setScoreA(Integer.valueOf(homeResult));
        betView.setScoreB(Integer.valueOf(awayResult));
        return betView;
    }

    public static Map<String, GameSide> processCompetitionGameSides(Integer competitionId, XSSFWorkbook book,
                                                                    GameSideService gameSideService) {
        final XSSFSheet teams = book.getSheet(TEAMS_SHEET_NAME);
        final String teamsColumnName = TEAMS_COLUMN;
        final int teamsStartIndex = TEAMS_START_INDEX;
        final int numberOfTeams = NUMBER_OF_TEAMS;

        Map<String, GameSide> gamesByName = new HashMap<>(NUMBER_OF_TEAMS);

        for (int i = 0; i < numberOfTeams; i++) {
            final CellReference cellReference = new CellReference(teamsColumnName + (teamsStartIndex + i));
            final Row row = teams.getRow(cellReference.getRow());
            final String teamName = row.getCell(cellReference.getCol()).getStringCellValue();
            String countryCode = StringUtils.substring(teamName, 0, 5);
            List<CountryCode> codes = CountryCode.findByName(teamName);
            if (CollectionUtils.isNotEmpty(codes)) {
                countryCode = codes.get(0).getAlpha2();
            }

            logger.debug("Creating team " + teamName);
            GameSide newGameSide = gameSideService.save(null, competitionId, teamName, new HashMap<>(), countryCode);
            gamesByName.put(teamName, newGameSide);
        }
        return gamesByName;
    }

    public static int processPlayoffGames(Integer competitionId, XSSFSheet sheet, FormulaEvaluator evaluator,
                                          BetType betType, Round round, int gameOrder, int numberOfGames, int jumpSize,
                                          String gamesColumnName, Integer gamesStartIndex,
                                          GameService gameService) {

        List<Date> matchDates = new ArrayList<>(numberOfGames);
        for (int i = 0; i < numberOfGames; i++) {
            final CellReference cellReference = new CellReference(gamesColumnName + (gamesStartIndex + i * jumpSize));
            final Row row = sheet.getRow(cellReference.getRow());
            Date matchDate = getPlayoffGameDate(cellReference, row, evaluator);
            matchDates.add(matchDate);
        }

        Collections.sort(matchDates);
        int i = 0;
        for (Date finalDate: matchDates) {
            int gameIndex = i + 1;
            String gameName = numberOfGames == 1 ? round.getName() : round.getName() + " - " + gameIndex;
            logger.debug("Creating game " + gameName);
            gameService.save(null, competitionId, finalDate, gameName, new HashMap<>(),
                    betType.getId(), round.getId(), gameOrder, null, null, null, null);
            i++;
            gameOrder++;
        }
        return gameOrder;
    }


    public static int processGroupGames(Integer competitionId, XSSFSheet sheet, FormulaEvaluator evaluator,
                                         BetType groupsBetType, Round groupsRound,
                                         Map<String, GameSide> gamesByName, GameService gameService) {

        // Groups games
        int matchNumber = 1;
        for (int rowIndex = 10; rowIndex < 46; rowIndex++) {
            final CellReference cellReference = new CellReference(GROUP_GAMES_COLUMN + rowIndex);
            final Row row = sheet.getRow(cellReference.getRow());
            Cell homeGameSideNameCell = row.getCell(cellReference.getCol() + 4);
            Cell awayGameSideNameCell = row.getCell(cellReference.getCol() + 7);

            evaluator.evaluateInCell(homeGameSideNameCell);
            evaluator.evaluateInCell(awayGameSideNameCell);
            String homeGameSideName = homeGameSideNameCell.getStringCellValue();
            String awayGameSideName = awayGameSideNameCell.getStringCellValue();

            GameSide home = gamesByName.get(homeGameSideName);
            GameSide away = gamesByName.get(awayGameSideName);

            Date finalDate = getGameDate(cellReference, row, evaluator);

            String gameName = GROUPS_NAME + " - " + matchNumber;
            logger.debug("Creating game " + gameName);
            gameService.save(null, competitionId, finalDate, gameName, new HashMap<>(),
                    groupsBetType.getId(), groupsRound.getId(), matchNumber, home.getId(), away.getId(), null, null);
            matchNumber++;
        }

        return matchNumber;
    }

    private static Date getGameDate(CellReference cellReference, Row row, FormulaEvaluator evaluator) {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        try {
            Cell dateCell = row.getCell(cellReference.getCol() + 2);
            Cell hourCell = row.getCell(cellReference.getCol() + 3);

            evaluator.evaluateInCell(dateCell);
            evaluator.evaluateInCell(hourCell);

            date = DATE_FORMAT.parse(dateCell.getStringCellValue());
            calendar.setTime(date);

            Calendar timeCalendar = Calendar.getInstance();
            timeCalendar.setTime(hourCell.getDateCellValue());

            calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));

        } catch (Exception e) {
            logger.error("Error parsing game date");
        }
        // Get the final Date object
        return calendar.getTime();
    }

    private static Date getPlayoffGameDate(CellReference cellReference, Row row, FormulaEvaluator evaluator) {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        try {
            Cell dateCell = row.getCell(cellReference.getCol());
            evaluator.evaluateInCell(dateCell);
            date = PLAYOFF_DATE_FORMAT.parse(dateCell.getStringCellValue());
            calendar.setTime(date);

        } catch (Exception e) {
            logger.error("Error parsing game date");
        }
        // Get the final Date object
        return calendar.getTime();
    }



}
