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
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import org.jgayoso.ncomplo.business.util.I18nUtils;

@Entity
@Table(name = "ROUND")
public class Round implements Comparable<Round> {

  @ElementCollection(fetch = FetchType.LAZY, targetClass = java.lang.String.class)
  @CollectionTable(name = "ROUND_NAME_I18N", joinColumns = @JoinColumn(name = "ROUND_ID"))
  @MapKeyColumn(name = "LANG", nullable = false, length = 20)
  @Column(name = "NAME", nullable = false, length = 200)
  private final Map<String, String> namesByLang = new LinkedHashMap<>();

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @Column(name = "NAME", nullable = false, length = 200)
  private String name;

  @ManyToOne
  @JoinColumn(name = "COMPETITION_ID", nullable = false)
  private Competition competition;

  @Column(name = "ROUND_ORDER", nullable = false)
  private Integer order;

  public Round() {
    super();
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public Competition getCompetition() {
    return this.competition;
  }

  public void setCompetition(final Competition competition) {
    this.competition = competition;
  }

  public Integer getId() {
    return this.id;
  }

  public Map<String, String> getNamesByLang() {
    return this.namesByLang;
  }

  public String getName(final Locale locale) {
    return I18nUtils.getTextForLocale(locale, this.namesByLang, this.name);
  }

  public Integer getOrder() {
    return this.order;
  }

  public void setOrder(final Integer order) {
    this.order = order;
  }

  @Override
  public int compareTo(final Round o) {
    return this.getOrder().compareTo(o.getOrder());
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.getCompetition() == null) ? 0 : this.getCompetition().hashCode());
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Round other = (Round) obj;
    if (this.getCompetition() == null) {
      if (other.getCompetition() != null) return false;
    } else if (!this.getCompetition().equals(other.getCompetition())) return false;
    if (this.getId() == null) {
      if (other.getId() != null) return false;
    } else if (!this.getId().equals(other.getId())) return false;
    return true;
  }

  @Override
  public String toString() {
    return "Round{" + "name='" + name + '\'' + '}';
  }
}
