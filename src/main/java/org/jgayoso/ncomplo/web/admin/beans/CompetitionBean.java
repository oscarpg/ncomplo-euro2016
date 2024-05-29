package org.jgayoso.ncomplo.web.admin.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
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

    private String teamsSheetName;

    private String teamsColumnName;

    private Integer teamsStartIndex;

    private Integer teamsNumber;

    
    
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
}
