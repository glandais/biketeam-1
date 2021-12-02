package info.tomacla.biketeam.web.team.templates;

import info.tomacla.biketeam.common.datatype.Dates;
import info.tomacla.biketeam.common.datatype.Strings;
import info.tomacla.biketeam.common.geo.Point;

import java.time.LocalTime;

public class NewRideGroupTemplateForm {

    private String id = "new";
    private String name = "";
    private double lowerSpeed = 28;
    private double upperSpeed = 30;
    private String meetingLocation = "";
    private double meetingPointLat = 0.0;
    private double meetingPointLng = 0.0;
    private String meetingTime = "12:00";

    public static NewRideGroupTemplateFormBuilder builder() {
        return new NewRideGroupTemplateFormBuilder();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = Strings.requireNonBlankOrDefault(id, "new");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Strings.requireNonBlankOrDefault(name, "");
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

    public String getMeetingLocation() {
        return meetingLocation;
    }

    public void setMeetingLocation(String meetingLocation) {
        this.meetingLocation = Strings.requireNonBlankOrDefault(meetingLocation, "");
    }

    public double getMeetingPointLat() {
        return meetingPointLat;
    }

    public void setMeetingPointLat(double meetingPointLat) {
        this.meetingPointLat = meetingPointLat;
    }

    public double getMeetingPointLng() {
        return meetingPointLng;
    }

    public void setMeetingPointLng(double meetingPointLng) {
        this.meetingPointLng = meetingPointLng;
    }

    public String getMeetingTime() {
        return meetingTime;
    }

    public void setMeetingTime(String meetingTime) {
        this.meetingTime = Strings.requireNonBlankOrDefault(meetingTime, "12:00");
    }

    public NewRideGroupTemplateFormParser parser() {
        return new NewRideGroupTemplateFormParser(this);
    }

    public static class NewRideGroupTemplateFormParser {

        private final NewRideGroupTemplateForm form;

        public NewRideGroupTemplateFormParser(NewRideGroupTemplateForm form) {
            this.form = form;
        }

        public String getId() {
            return Strings.requireNonBlankOrNull(form.getId());
        }

        public String getName() {
            return Strings.requireNonBlankOrNull(form.getName());
        }

        public double getLowerSpeed() {
            return form.getLowerSpeed();
        }

        public double getUpperSpeed() {
            return form.getUpperSpeed();
        }

        public String getMeetingLocation() {
            return Strings.requireNonBlankOrNull(form.getMeetingLocation());
        }

        public Point getMeetingPoint() {
            if (form.getMeetingPointLat() != 0.0 && form.getMeetingPointLng() != 0.0) {
                return new Point(form.getMeetingPointLat(), form.getMeetingPointLng());
            }
            return null;
        }

        public LocalTime getMeetingTime() {
            return LocalTime.parse(form.getMeetingTime());
        }

    }

    public static class NewRideGroupTemplateFormBuilder {

        private final NewRideGroupTemplateForm form;

        public NewRideGroupTemplateFormBuilder() {
            this.form = new NewRideGroupTemplateForm();
        }

        public NewRideGroupTemplateFormBuilder withId(String id) {
            form.setId(id);
            return this;
        }

        public NewRideGroupTemplateFormBuilder withName(String name) {
            form.setName(name);
            return this;
        }

        public NewRideGroupTemplateFormBuilder withLowerSpeed(double lowerSpeed) {
            form.setLowerSpeed(lowerSpeed);
            return this;
        }

        public NewRideGroupTemplateFormBuilder withUpperSpeed(double upperSpeed) {
            form.setUpperSpeed(upperSpeed);
            return this;
        }

        public NewRideGroupTemplateFormBuilder withMeetingLocation(String meetingLocation) {
            form.setMeetingLocation(meetingLocation);
            return this;
        }

        public NewRideGroupTemplateFormBuilder withMeetingPoint(Point meetingPoint) {
            if (meetingPoint != null) {
                form.setMeetingPointLat(meetingPoint.getLat());
                form.setMeetingPointLng(meetingPoint.getLng());
            }
            return this;
        }

        public NewRideGroupTemplateFormBuilder withMeetingTime(LocalTime meetingTime) {
            if (meetingTime != null) {
                form.setMeetingTime(Dates.formatTime(meetingTime));
            }
            return this;
        }

        public NewRideGroupTemplateForm get() {
            return form;
        }

    }

}