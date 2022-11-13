package org.jgayoso.ncomplo.business.entities;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "LEAGUES_GROUP")
public class LeagueGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "NAME", nullable = false, length = 200)
    private String name;

    @OneToMany(cascade=CascadeType.DETACH, mappedBy="leagueGroup")
    private final Set<League> leagues = new LinkedHashSet<>();

    public LeagueGroup() {
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

    public Set<League> getLeagues() {
        return leagues;
    }

    @Override
    public String toString() {
        return "LeagueGroup{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

}
