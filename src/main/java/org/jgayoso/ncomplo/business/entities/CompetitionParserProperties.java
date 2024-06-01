package org.jgayoso.ncomplo.business.entities;

import javax.persistence.*;

@Entity
@Table(name="COMPETITION_PARSER_PROPERTIES")
public class CompetitionParserProperties {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @OneToOne
    @JoinColumn(name="COMPETITION")
    private Competition competition;

    @Column(name="TEAMS_SHEET_NAME")
    private String teamsSheetName;
    @Column(name="GAMES_SHEET_NAME")
    private String gamesSheetName;

    @Column(name="TEAMS_COLUMN_NAME")
    private String teamsColumnName;
    @Column(name="TEAMS_START_INDEX")
    private Integer teamsStartIndex;
    @Column(name="TEAMS_NUMBER")
    private Integer teamsNumber;

    @Column(name="GROUPS_GAMES_COLUMN_NAME")
    private String groupGamesColumnName;
    @Column(name="GROUPS_GAMES_START_INDEX")
    private Integer groupGamesStartIndex;
    @Column(name="GROUP_GAMES_NUMBER")
    private Integer groupGamesNumber;
    @Column(name="GROUPS_GAMES_HOME_INDEX")
    private Integer groupGamesHomeIndex;
    @Column(name="GROUPS_GAMES_AWAY_INDEX")
    private Integer awayGamesHomeIndex;
    @Column(name="GROUPS_GAMES_DATE_INDEX")
    private Integer groupsGamesDateIndex;
    @Column(name="GROUPS_GAMES_HOUR_INDEX")
    private Integer groupsGamesHourIndex;
    @Column(name="GROUPS_GAMES_DATE_FORMAT")
    private String groupsGamesDateFormat;

    @Column(name="PLAYOFFS_GAMES_DATE_INDEX")
    private Integer playoffsGamesDateIndex;
    @Column(name="PLAYOFFS_GAMES_HOUR_INDEX")
    private Integer playoffsGamesHourIndex;
    @Column(name="PLAYOFFS_GAMES_DATE_FORMAT")
    private String playoffsGamesDateFormat;

    @Column(name="ROUND_OF_16_GAMES_COLUMN_NAME")
    private String roundOf16GamesColumnName;
    @Column(name="ROUND_OF_16_GAMES_START_INDEX")
    private Integer roundOf16GamesStartIndex;
    @Column(name="ROUND_OF_16_GAMES_JUMP_SIZE")
    private Integer roundOf16GamesJumpSize;

    @Column(name="QUARTER_FINALS_GAMES_COLUMN_NAME")
    private String quarteFinalsGamesColumnName;
    @Column(name="QUARTER_FINALS_GAMES_START_INDEX")
    private Integer quarteFinalsGamesStartIndex;
    @Column(name="QUARTER_FINALS_GAMES_JUMP_SIZE")
    private Integer quarteFinalsGamesJumpSize;

    @Column(name="SEMI_FINALS_GAMES_COLUMN_NAME")
    private String semiFinalsGamesColumnName;
    @Column(name="SEMI_FINALS_GAMES_START_INDEX")
    private Integer semiFinalsGamesStartIndex;
    @Column(name="SEMI_FINALS_GAMES_JUMP_SIZE")
    private Integer semiFinalsGamesJumpSize;

    @Column(name="FINAL_GAMES_COLUMN_NAME")
    private String finalGamesColumnName;
    @Column(name="FINAL_GAMES_START_INDEX")
    private Integer finalGamesStartIndex;

    @Column(name="GROUPS_NAME")
    private String groupsName;
    @Column(name="ROUND_OF_16_NAME")
    private String roundOf16Name;
    @Column(name="QUARTER_FINALS_NAME")
    private String quarteFinalsName;
    @Column(name="SEMIFINALS_NAME")
    private String semiFinalsName;
    @Column(name="FINAL_NAME")
    private String finalName;

    public CompetitionParserProperties() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTeamsSheetName() {
        return teamsSheetName;
    }

    public void setTeamsSheetName(String teamsSheetName) {
        this.teamsSheetName = teamsSheetName;
    }

    public String getTeamsColumnName() {
        return teamsColumnName;
    }

    public void setTeamsColumnName(String teamsColumnName) {
        this.teamsColumnName = teamsColumnName;
    }

    public Integer getTeamsStartIndex() {
        return teamsStartIndex;
    }

    public void setTeamsStartIndex(Integer teamsStartIndex) {
        this.teamsStartIndex = teamsStartIndex;
    }

    public Integer getTeamsNumber() {
        return teamsNumber;
    }

    public void setTeamsNumber(Integer teamsNumber) {
        this.teamsNumber = teamsNumber;
    }

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public String getGamesSheetName() {
        return gamesSheetName;
    }

    public void setGamesSheetName(String gamesSheetName) {
        this.gamesSheetName = gamesSheetName;
    }

    public String getGroupGamesColumnName() {
        return groupGamesColumnName;
    }

    public void setGroupGamesColumnName(String groupGamesColumnName) {
        this.groupGamesColumnName = groupGamesColumnName;
    }

    public Integer getGroupGamesStartIndex() {
        return groupGamesStartIndex;
    }

    public void setGroupGamesStartIndex(Integer groupGamesStartIndex) {
        this.groupGamesStartIndex = groupGamesStartIndex;
    }

    public Integer getGroupGamesNumber() {
        return groupGamesNumber;
    }

    public void setGroupGamesNumber(Integer groupGamesNumber) {
        this.groupGamesNumber = groupGamesNumber;
    }

    public String getGroupsName() {
        return groupsName;
    }

    public void setGroupsName(String groupsName) {
        this.groupsName = groupsName;
    }
    public String getRoundOf16Name() {
        return roundOf16Name;
    }

    public void setRoundOf16Name(String roundOf16Name) {
        this.roundOf16Name = roundOf16Name;
    }

    public String getQuarteFinalsName() {
        return quarteFinalsName;
    }

    public void setQuarteFinalsName(String quarteFinalsName) {
        this.quarteFinalsName = quarteFinalsName;
    }

    public String getSemiFinalsName() {
        return semiFinalsName;
    }

    public void setSemiFinalsName(String semiFinalsName) {
        this.semiFinalsName = semiFinalsName;
    }

    public String getFinalName() {
        return finalName;
    }

    public void setFinalsName(String finalName) {
        this.finalName = finalName;
    }

    public Integer getGroupGamesHomeIndex() {
        return groupGamesHomeIndex;
    }

    public void setGroupGamesHomeIndex(Integer groupGamesHomeIndex) {
        this.groupGamesHomeIndex = groupGamesHomeIndex;
    }

    public Integer getAwayGamesHomeIndex() {
        return awayGamesHomeIndex;
    }

    public void setAwayGamesHomeIndex(Integer awayGamesHomeIndex) {
        this.awayGamesHomeIndex = awayGamesHomeIndex;
    }

    public Integer getGroupsGamesDateIndex() {
        return groupsGamesDateIndex;
    }

    public void setGroupsGamesDateIndex(Integer groupsGamesDateIndex) {
        this.groupsGamesDateIndex = groupsGamesDateIndex;
    }

    public Integer getGroupsGamesHourIndex() {
        return groupsGamesHourIndex;
    }

    public void setGroupsGamesHourIndex(Integer groupsGamesHourIndex) {
        this.groupsGamesHourIndex = groupsGamesHourIndex;
    }

    public String getGroupsGamesDateFormat() {
        return groupsGamesDateFormat;
    }

    public void setGroupsGamesDateFormat(String groupsGamesDateFormat) {
        this.groupsGamesDateFormat = groupsGamesDateFormat;
    }

    public Integer getPlayoffsGamesDateIndex() {
        return playoffsGamesDateIndex;
    }

    public void setPlayoffsGamesDateIndex(Integer playoffsGamesDateIndex) {
        this.playoffsGamesDateIndex = playoffsGamesDateIndex;
    }

    public Integer getPlayoffsGamesHourIndex() {
        return playoffsGamesHourIndex;
    }

    public void setPlayoffsGamesHourIndex(Integer playoffsGamesHourIndex) {
        this.playoffsGamesHourIndex = playoffsGamesHourIndex;
    }

    public String getPlayoffsGamesDateFormat() {
        return playoffsGamesDateFormat;
    }

    public void setPlayoffsGamesDateFormat(String playoffsGamesDateFormat) {
        this.playoffsGamesDateFormat = playoffsGamesDateFormat;
    }

    public String getRoundOf16GamesColumnName() {
        return roundOf16GamesColumnName;
    }

    public void setRoundOf16GamesColumnName(String roundOf16GamesColumnName) {
        this.roundOf16GamesColumnName = roundOf16GamesColumnName;
    }

    public Integer getRoundOf16GamesStartIndex() {
        return roundOf16GamesStartIndex;
    }

    public void setRoundOf16GamesStartIndex(Integer roundOf16GamesStartIndex) {
        this.roundOf16GamesStartIndex = roundOf16GamesStartIndex;
    }

    public String getQuarteFinalsGamesColumnName() {
        return quarteFinalsGamesColumnName;
    }

    public void setQuarteFinalsGamesColumnName(String quarteFinalsGamesColumnName) {
        this.quarteFinalsGamesColumnName = quarteFinalsGamesColumnName;
    }

    public Integer getQuarteFinalsGamesStartIndex() {
        return quarteFinalsGamesStartIndex;
    }

    public void setQuarteFinalsGamesStartIndex(Integer quarteFinalsGamesStartIndex) {
        this.quarteFinalsGamesStartIndex = quarteFinalsGamesStartIndex;
    }

    public String getSemiFinalsGamesColumnName() {
        return semiFinalsGamesColumnName;
    }

    public void setSemiFinalsGamesColumnName(String semiFinalsGamesColumnName) {
        this.semiFinalsGamesColumnName = semiFinalsGamesColumnName;
    }

    public Integer getSemiFinalsGamesStartIndex() {
        return semiFinalsGamesStartIndex;
    }

    public void setSemiFinalsGamesStartIndex(Integer semiFinalsGamesStartIndex) {
        this.semiFinalsGamesStartIndex = semiFinalsGamesStartIndex;
    }

    public String getFinalGamesColumnName() {
        return finalGamesColumnName;
    }

    public void setFinalGamesColumnName(String finalGamesColumnName) {
        this.finalGamesColumnName = finalGamesColumnName;
    }

    public Integer getFinalGamesStartIndex() {
        return finalGamesStartIndex;
    }

    public void setFinalGamesStartIndex(Integer finalGamesStartIndex) {
        this.finalGamesStartIndex = finalGamesStartIndex;
    }

    public void setFinalName(String finalName) {
        this.finalName = finalName;
    }

    public Integer getRoundOf16GamesJumpSize() {
        return roundOf16GamesJumpSize;
    }

    public void setRoundOf16GamesJumpSize(Integer roundOf16GamesJumpSize) {
        this.roundOf16GamesJumpSize = roundOf16GamesJumpSize;
    }

    public Integer getQuarteFinalsGamesJumpSize() {
        return quarteFinalsGamesJumpSize;
    }

    public void setQuarteFinalsGamesJumpSize(Integer quarteFinalsGamesJumpSize) {
        this.quarteFinalsGamesJumpSize = quarteFinalsGamesJumpSize;
    }

    public Integer getSemiFinalsGamesJumpSize() {
        return semiFinalsGamesJumpSize;
    }

    public void setSemiFinalsGamesJumpSize(Integer semiFinalsGamesJumpSize) {
        this.semiFinalsGamesJumpSize = semiFinalsGamesJumpSize;
    }
}
