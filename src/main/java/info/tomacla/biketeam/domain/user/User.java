package info.tomacla.biketeam.domain.user;

import info.tomacla.biketeam.common.datatype.Strings;
import info.tomacla.biketeam.domain.team.Team;
import info.tomacla.biketeam.domain.userrole.UserRole;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "user_account")
public class User {

    @Id
    private String id = UUID.randomUUID().toString();
    private boolean admin = false;
    @Column(name = "strava_id", unique = true)
    private Long stravaId;
    @Column(name = "facebook_id", unique = true)
    private String facebookId;
    @Column(name = "google_id", unique = true)
    private String googleId;
    @Column(name = "strava_user_name", unique = true)
    private String stravaUserName;
    @Column(name = "first_name")
    private String firstName = "Inconnu";
    @Column(name = "last_name")
    private String lastName = "Inconnu";
    @Column(name = "city")
    private String city;
    @Column(name = "email")
    private String email;
    @Column(name = "email_publish_trips")
    private boolean emailPublishTrips;
    @Column(name = "email_publish_rides")
    private boolean emailPublishRides;
    @Column(name = "email_publish_publications")
    private boolean emailPublishPublications;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<UserRole> roles = new HashSet<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = Objects.requireNonNull(id, "id is null");
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public Long getStravaId() {
        return stravaId;
    }

    public void setStravaId(Long stravaId) {
        this.stravaId = stravaId;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getStravaUserName() {
        return stravaUserName;
    }

    public void setStravaUserName(String stravaUserName) {
        this.stravaUserName = stravaUserName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = Strings.requireNonBlank(firstName, "firstname is null");
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = Strings.requireNonBlank(lastName, "lastname is null");
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = Strings.requireNonBlankOrNull(city);
    }

    public Set<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<UserRole> roles) {
        this.roles = Objects.requireNonNullElse(roles, new HashSet<>());
    }

    public void removeTeam(Team team) {
        this.roles.removeIf(role -> role.getTeam().equals(team));
    }

    public String getIdentity() {
        return getFirstName() + " " + getLastName();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (Strings.isEmail(email)) {
            this.email = email.toLowerCase();
        } else {
            this.email = null;
        }
    }

    public boolean isEmailPublishTrips() {
        return emailPublishTrips;
    }

    public void setEmailPublishTrips(boolean emailPublishTrips) {
        this.emailPublishTrips = emailPublishTrips;
    }

    public boolean isEmailPublishRides() {
        return emailPublishRides;
    }

    public void setEmailPublishRides(boolean emailPublishRides) {
        this.emailPublishRides = emailPublishRides;
    }

    public boolean isEmailPublishPublications() {
        return emailPublishPublications;
    }

    public void setEmailPublishPublications(boolean emailPublishPublications) {
        this.emailPublishPublications = emailPublishPublications;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
