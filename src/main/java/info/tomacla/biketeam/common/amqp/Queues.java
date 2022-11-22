package info.tomacla.biketeam.common.amqp;

public interface Queues {

    String QUEUE_PREFIX = "biketeam.";
    String PUBLICATION_PUBLISHED = QUEUE_PREFIX + RoutingKeys.PUBLICATION_PUBLISHED;
    String RIDE_MESSAGE_PUBLISHED = QUEUE_PREFIX + RoutingKeys.RIDE_MESSAGE_PUBLISHED;
    String TRIP_MESSAGE_PUBLISHED = QUEUE_PREFIX + RoutingKeys.TRIP_MESSAGE_PUBLISHED;
    String RIDE_PUBLISHED = QUEUE_PREFIX + RoutingKeys.RIDE_PUBLISHED;
    String TRIP_PUBLISHED = QUEUE_PREFIX + RoutingKeys.TRIP_PUBLISHED;
    String TASK_PUBLISH_RIDES = QUEUE_PREFIX + RoutingKeys.TASK_PUBLISH_RIDES;
    String TASK_PUBLISH_TRIPS = QUEUE_PREFIX + RoutingKeys.TASK_PUBLISH_TRIPS;
    String TASK_PUBLISH_PUBLICATIONS = QUEUE_PREFIX + RoutingKeys.TASK_PUBLISH_PUBLICATIONS;
    String TASK_CLEAN_TMP_FILES = QUEUE_PREFIX + RoutingKeys.TASK_CLEAN_TMP_FILES;
    String TASK_CLEAN_TEAM_FILES = QUEUE_PREFIX + RoutingKeys.TASK_CLEAN_TEAM_FILES;
    String TASK_GENERATE_HEATMAPS = QUEUE_PREFIX + RoutingKeys.TASK_GENERATE_HEATMAPS;
    String TASK_DOWNLOAD_PROFILE_IMAGE = QUEUE_PREFIX + RoutingKeys.TASK_DOWNLOAD_PROFILE_IMAGE;

    String TASK_CLEAN_NOTIFICATIONS = QUEUE_PREFIX + RoutingKeys.TASK_CLEAN_NOTIFICATIONS;


}
