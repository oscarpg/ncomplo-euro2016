package org.jgayoso.ncomplo.web.admin.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;


public class CompetitionBean implements Serializable {
    
    private static final long serialVersionUID = 7297004126853517111L;
    
    @NotNull
    private Integer id;
    
    @NotNull
    @Length(min=3,max=200)
    private String name;
    
    @NotNull
    private final List<LangBean> namesByLang = new ArrayList<>();

    @NotNull
    private boolean active = true;

	private String updaterUri;

	private boolean updateProperties = false;
    private String teamsSheetName;
    private String teamsColumnName;
    private Integer teamsStartIndex;
    private Integer teamsNumber;
    private String gamesSheetName;
    private String groupGamesColumnName;
    private Integer groupGamesStartIndex;
    private Integer groupGamesNumber;
    private Integer groupsGamesHomeIndex;
    private Integer groupsGamesAwayIndex;
    private Integer groupsGamesDateIndex;
    private Integer groupsGamesHourIndex;
    private String groupsGamesDateFormat;
    private Integer playoffsGamesDateIndex;
    private Integer playoffsGamesHourIndex;
    private String playoffsGamesDateFormat;
    private String roundOf16GamesColumnName;
    private Integer roundOf16GamesStartIndex;
    private Integer roundOf16GamesJumpSize;
    private String quarteFinalsGamesColumnName;
    private Integer quarteFinalsGamesStartIndex;
    private Integer quarteFinalsGamesJumpSize;
    private String semiFinalsGamesColumnName;
    private Integer semiFinalsGamesStartIndex;
    private Integer semiFinalsGamesJumpSize;
    private String finalGamesColumnName;
    private Integer finalGamesStartIndex;
    private String groupsName;
    private String roundOf16Name;
    private String quarterFinalsName;
    private String semiFinalsName;
    private String finalName;
    
    
    public CompetitionBean() {
        super();
    }

    

    public Integer getId() {
        return this.id;
    }


    public void setId(final Integer id) {
        this.id = id;
    }


    public boolean isActive() {
        return this.active;
    }


    public void setActive(final boolean active) {
        this.active = active;
    }



    public String getName() {
        return this.name;
    }



    public void setName(final String name) {
        this.name = name;
    }



    public List<LangBean> getNamesByLang() {
        return this.namesByLang;
    }



	public String getUpdaterUri() {
		return this.updaterUri;
	}



	public void setUpdaterUri(String updaterUri) {
		this.updaterUri = updaterUri;
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

    public Integer getGroupsGamesHomeIndex() {
        return groupsGamesHomeIndex;
    }

    public void setGroupsGamesHomeIndex(Integer groupsGamesHomeIndex) {
        this.groupsGamesHomeIndex = groupsGamesHomeIndex;
    }

    public Integer getGroupsGamesAwayIndex() {
        return groupsGamesAwayIndex;
    }

    public void setGroupsGamesAwayIndex(Integer groupsGamesAwayIndex) {
        this.groupsGamesAwayIndex = groupsGamesAwayIndex;
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

    public Integer getRoundOf16GamesJumpSize() {
        return roundOf16GamesJumpSize;
    }

    public void setRoundOf16GamesJumpSize(Integer roundOf16GamesJumpSize) {
        this.roundOf16GamesJumpSize = roundOf16GamesJumpSize;
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

    public Integer getQuarteFinalsGamesJumpSize() {
        return quarteFinalsGamesJumpSize;
    }

    public void setQuarteFinalsGamesJumpSize(Integer quarteFinalsGamesJumpSize) {
        this.quarteFinalsGamesJumpSize = quarteFinalsGamesJumpSize;
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

    public Integer getSemiFinalsGamesJumpSize() {
        return semiFinalsGamesJumpSize;
    }

    public void setSemiFinalsGamesJumpSize(Integer semiFinalsGamesJumpSize) {
        this.semiFinalsGamesJumpSize = semiFinalsGamesJumpSize;
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

    public String getQuarterFinalsName() {
        return quarterFinalsName;
    }

    public void setQuarterFinalsName(String quarterFinalsName) {
        this.quarterFinalsName = quarterFinalsName;
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

    public void setFinalName(String finalName) {
        this.finalName = finalName;
    }

    public boolean isUpdateProperties() {
        return updateProperties;
    }

    public void setUpdateProperties(boolean updateProperties) {
        this.updateProperties = updateProperties;
    }

    public String getGamesSheetName() {
        return gamesSheetName;
    }

    public void setGamesSheetName(String gamesSheetName) {
        this.gamesSheetName = gamesSheetName;
    }
}
