package info.tomacla.biketeam.domain.ride;

import info.tomacla.biketeam.common.Point;
import info.tomacla.biketeam.common.Strings;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "ride_group")
public class RideGroup {

    @Id
    private String id;
    @ManyToOne(fetch = FetchType.EAGER)
    private Ride ride;
    private String name;
    @Column(name = "lower_speed")
    private double lowerSpeed;
    @Column(name = "upper_speed")
    private double upperSpeed;
    @Column(name = "map_id")
    private String mapId;
    @Column(name = "meeting_location")
    private String meetingLocation;
    @Column(name = "meeting_time")
    private LocalTime meetingTime;
    @AttributeOverrides({
            @AttributeOverride(name = "lat", column = @Column(name = "meeting_point_lat")),
            @AttributeOverride(name = "lng", column = @Column(name = "meeting_point_lng"))
    })
    @Embedded
    private Point meetingPoint;

    protected RideGroup() {

    }

    public RideGroup(String name,
                     double lowerSpeed,
                     double upperSpeed,
                     String mapId,
                     String meetingLocation,
                     LocalTime meetingTime,
                     Point meetingPoint) {
        this.id = UUID.randomUUID().toString();
        setName(name);
        setLowerSpeed(lowerSpeed);
        setUpperSpeed(upperSpeed);
        setMapId(mapId);
        setMeetingLocation(meetingLocation);
        setMeetingTime(meetingTime);
        setMeetingPoint(meetingPoint);
    }

    public String getId() {
        return id;
    }

    protected void setId(String id) {
        this.id = id;
    }

    public Ride getRide() {
        return ride;
    }

    protected void setRide(Ride ride) {
        this.ride = Objects.requireNonNull(ride);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Strings.requireNonBlank(name, "name is null");
    }

    public double getLowerSpeed() {
        return lowerSpeed;
    }

    public void setLowerSpeed(double lowerSpeed) {
        this.lowerSpeed = lowerSpeed;
    }

    public double getUpperSpeed() {
        return upperSpeed;
    }

    public void setUpperSpeed(double upperSpeed) {
        this.upperSpeed = upperSpeed;
    }

    public String getMapId() {
        return mapId;
    }

    public void setMapId(String mapId) {
        this.mapId = Strings.requireNonBlankOrNull(mapId);
    }

    public String getMeetingLocation() {
        return meetingLocation;
    }

    public void setMeetingLocation(String meetingLocation) {
        this.meetingLocation = Strings.requireNonBlankOrNull(meetingLocation);
    }

    public LocalTime getMeetingTime() {
        return meetingTime;
    }

    public void setMeetingTime(LocalTime meetingTime) {
        this.meetingTime = Objects.requireNonNull(meetingTime);
    }

    public Point getMeetingPoint() {
        return meetingPoint;
    }

    public void setMeetingPoint(Point meetingPoint) {
        this.meetingPoint = meetingPoint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RideGroup rideGroup = (RideGroup) o;
        return id.equals(rideGroup.id) && ride.equals(rideGroup.ride);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ride);
    }
}