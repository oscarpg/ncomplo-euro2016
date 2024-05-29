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
    @Column(name="TEAMS_COLUMN_NAME")
    private String teamsColumnName;
    @Column(name="TEAMS_START_INDEX")
    private Integer teamsStartIndex;
    @Column(name="TEAMS_NUMBER")
    private Integer teamsNumber;

    public CompetitionParserProperties() {
        super();
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
}
