package org.jgayoso.ncomplo.web.admin.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

public class GameBean implements Serializable {

    private static final long serialVersionUID = 9180456877660425744L;

    @NotNull
    private Integer id;

    @NotNull
    @Length(min = 3, max = 200)
    private String name;

    @NotNull
    private final List<LangBean> namesByLang = new ArrayList<>();

    @NotNull
    private Integer order;

    @NotNull
    private Integer roundId;

    @NotNull
    private Integer defaultBetTypeId;

    private Date date;

    private String time;

    private Integer gameSideAId;

    private Integer gameSideBId;

    private Integer scoreA;

    private Integer scoreB;

    // ---- Bracket source for Side A ----
    private String sideASourceType;  // null, GROUP_POSITION, GAME_WINNER, GAME_LOSER
    private Integer sideAGroupId;
    private Integer sideAPosition;
    private Integer sideASourceGameId;

    // ---- Bracket source for Side B ----
    private String sideBSourceType;
    private Integer sideBGroupId;
    private Integer sideBPosition;
    private Integer sideBSourceGameId;

    public GameBean() {
        super();
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Integer getRoundId() {
        return this.roundId;
    }

    public void setRoundId(final Integer roundId) {
        this.roundId = roundId;
    }

    public Integer getOrder() {
        return this.order;
    }

    public void setOrder(final Integer order) {
        this.order = order;
    }

    public Integer getDefaultBetTypeId() {
        return this.defaultBetTypeId;
    }

    public void setDefaultBetTypeId(final Integer defaultBetTypeId) {
        this.defaultBetTypeId = defaultBetTypeId;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(final Date date) {
        this.date = date;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(final String time) {
        this.time = time;
    }

    public Integer getGameSideAId() {
        return this.gameSideAId;
    }

    public void setGameSideAId(final Integer gameSideAId) {
        this.gameSideAId = gameSideAId;
    }

    public Integer getGameSideBId() {
        return this.gameSideBId;
    }

    public void setGameSideBId(final Integer gameSideBId) {
        this.gameSideBId = gameSideBId;
    }

    public Integer getScoreA() {
        return this.scoreA;
    }

    public void setScoreA(final Integer scoreA) {
        this.scoreA = scoreA;
    }

    public Integer getScoreB() {
        return this.scoreB;
    }

    public void setScoreB(final Integer scoreB) {
        this.scoreB = scoreB;
    }

    public List<LangBean> getNamesByLang() {
        return this.namesByLang;
    }

    // ---- Bracket source accessors (Side A) ----

    public String getSideASourceType() {
        return this.sideASourceType;
    }

    public void setSideASourceType(final String sideASourceType) {
        this.sideASourceType = sideASourceType;
    }

    public Integer getSideAGroupId() {
        return this.sideAGroupId;
    }

    public void setSideAGroupId(final Integer sideAGroupId) {
        this.sideAGroupId = sideAGroupId;
    }

    public Integer getSideAPosition() {
        return this.sideAPosition;
    }

    public void setSideAPosition(final Integer sideAPosition) {
        this.sideAPosition = sideAPosition;
    }

    public Integer getSideASourceGameId() {
        return this.sideASourceGameId;
    }

    public void setSideASourceGameId(final Integer sideASourceGameId) {
        this.sideASourceGameId = sideASourceGameId;
    }

    // ---- Bracket source accessors (Side B) ----

    public String getSideBSourceType() {
        return this.sideBSourceType;
    }

    public void setSideBSourceType(final String sideBSourceType) {
        this.sideBSourceType = sideBSourceType;
    }

    public Integer getSideBGroupId() {
        return this.sideBGroupId;
    }

    public void setSideBGroupId(final Integer sideBGroupId) {
        this.sideBGroupId = sideBGroupId;
    }

    public Integer getSideBPosition() {
        return this.sideBPosition;
    }

    public void setSideBPosition(final Integer sideBPosition) {
        this.sideBPosition = sideBPosition;
    }

    public Integer getSideBSourceGameId() {
        return this.sideBSourceGameId;
    }

    public void setSideBSourceGameId(final Integer sideBSourceGameId) {
        this.sideBSourceGameId = sideBSourceGameId;
    }

}
