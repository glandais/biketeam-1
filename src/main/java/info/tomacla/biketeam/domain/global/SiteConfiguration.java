package info.tomacla.biketeam.domain.global;

import info.tomacla.biketeam.common.Strings;
import info.tomacla.biketeam.common.Timezone;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "site_configuration")
public class SiteConfiguration {

    @Id
    private Long id = 1L;
    private String timezone;
    @ElementCollection
    @CollectionTable(
            name = "SITE_CONFIGURATION_DEFAULT_SEARCH_TAGS",
            joinColumns=@JoinColumn(name = "site_configuration_id", referencedColumnName = "id")
    )
    private List<String> defaultSearchTags;

    protected SiteConfiguration() {

    }

    public SiteConfiguration(String timezone, List<String> defaultSearchTags) {
        setTimezone(timezone);
        setDefaultSearchTags(defaultSearchTags);
    }

    public Long getId() {
        return id;
    }

    protected void setId(Long id) {
        this.id = 1L;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = Strings.requireNonBlankOrDefault(timezone, Timezone.DEFAULT_TIMEZONE);
    }

    public List<String> getDefaultSearchTags() {
        return defaultSearchTags;
    }

    public void setDefaultSearchTags(List<String> defaultSearchTags) {
        this.defaultSearchTags = Objects.requireNonNullElse(defaultSearchTags, new ArrayList<>());
    }
}
