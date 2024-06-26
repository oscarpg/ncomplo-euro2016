package org.jgayoso.ncomplo.business.entities;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.jgayoso.ncomplo.business.util.I18nUtils;


@Entity
@Table(name="BET_TYPE")
public class BetType implements I18nNamedEntity {

    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    
    @Column(name="NAME",nullable=false,length=1000)
    private String name;
    
    
    @ElementCollection(fetch=FetchType.EAGER,targetClass=java.lang.String.class)
    @CollectionTable(name="BET_TYPE_NAME_I18N",joinColumns=@JoinColumn(name="BET_TYPE_ID"))
    @MapKeyColumn(name="LANG",nullable=false,length=20)
    @Column(name="NAME", nullable=false,length=1000)
    private final Map<String,String> namesByLang = new LinkedHashMap<>();

    
    @Column(name="SPEC",nullable=false)
    @Lob
    @Type(type="org.hibernate.type.StringClobType")
    private String spec;
    
    
    @ManyToOne
    @JoinColumn(name="COMPETITION_ID",nullable=false)
    private Competition competition;

    
    @Column(name="SIDES_MATTER",nullable=false)
    private boolean sidesMatter;
    
    
    @Column(name="SCORE_MATTER",nullable=false)
    private boolean scoreMatter;
    
    
    @Column(name="RESULT_MATTER",nullable=false)
    private boolean resultMatter;

    


    public BetType() {
        super();
    }



    public Integer getId() {
        return this.id;
    }



    public Competition getCompetition() {
        return this.competition;
    }



    public void setCompetition(final Competition competition) {
        this.competition = competition;
    }

    
    
    @Override
    public String getName(final Locale locale) {
        return I18nUtils.getTextForLocale(locale, this.namesByLang, this.name);
    }



    @Override
    public String getName() {
        return this.name;
    }



    public void setName(final String name) {
        this.name = name;
    }



    @Override
    public Map<String, String> getNamesByLang() {
        return this.namesByLang;
    }



    public String getSpec() {
        return this.spec;
    }



    public void setSpec(final String spec) {
        this.spec = spec;
    }



    public boolean isSidesMatter() {
        return this.sidesMatter;
    }



    public void setSidesMatter(final boolean sidesMatter) {
        this.sidesMatter = sidesMatter;
    }



    public boolean isScoreMatter() {
        return this.scoreMatter;
    }



    public void setScoreMatter(final boolean scoreMatter) {
        this.scoreMatter = scoreMatter;
    }


    
    public boolean isResultMatter() {
        return this.resultMatter;
    }

    

    public void setResultMatter(final boolean winnerMatter) {
        this.resultMatter = winnerMatter;
    }


    @Override
    public String toString() {
        return "BetType{" +
                "name='" + name + '\'' +
                ", sidesMatter=" + sidesMatter +
                ", scoreMatter=" + scoreMatter +
                ", resultMatter=" + resultMatter +
                '}';
    }
}
