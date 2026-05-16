package org.jgayoso.ncomplo.web.admin.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CompetitionGroupBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String name;

    private Integer roundId;

    private int teamsQualifying = 2;

    private List<Integer> teamIds = new ArrayList<>();

    public CompetitionGroupBean() {
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

    public int getTeamsQualifying() {
        return this.teamsQualifying;
    }

    public void setTeamsQualifying(final int teamsQualifying) {
        this.teamsQualifying = teamsQualifying;
    }

    public List<Integer> getTeamIds() {
        return this.teamIds;
    }

    public void setTeamIds(final List<Integer> teamIds) {
        this.teamIds = teamIds;
    }
}
