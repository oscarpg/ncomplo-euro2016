package org.jgayoso.ncomplo.web.admin.beans;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class LeagueGroupBean implements Serializable {

    @NotNull
    private Integer id;

    @NotNull
    @Length(min = 3, max = 200)
    private String name;

    private Integer[] leagueIds;

    public LeagueGroupBean() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer[] getLeagueIds() {
        return this.leagueIds;
    }


    public void setLeagueIds(final Integer[] leagueIds) {
        this.leagueIds = leagueIds;
    }
}
