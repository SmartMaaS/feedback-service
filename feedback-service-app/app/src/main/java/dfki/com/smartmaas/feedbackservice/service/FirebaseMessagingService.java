package dfki.com.smartmaas.feedbackservice.service;


import dfki.com.smartmaas.feedbackservice.intrface.Inotification;

//import com.google.firebase.messaging.FirebaseMessaging;
//import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService /*extends com.google.firebase.messaging.FirebaseMessagingService implements Serializable*/ {
    private static final String TAG = FirebaseMessagingService.class.getName();
    private static final String CHANNEL_ID = "notification_channel_id";
    public static Inotification inotification;

//    @Override
//    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);
//        RemoteMessage.Notification notification = remoteMessage.getNotification();
//        assert notification != null;
//
//        if (notification.getBody() != null && notification.getBody().contains(getResources()
//                .getString(R.string.notification_divider))) {
//            String[] body = notification.getBody().split(getResources()
//                    .getString(R.string.notification_divider));
//            String stopNumber = body[0];
//            String radius = body[1];
//            String page = body[2];
//            Utils.saveStringToPreferences(this,
//                    getResources().getString(R.string.latest_page_SH_PR_key), page);
//            Utils.saveStringToPreferences(getApplicationContext(), getResources()
//                    .getString(R.string.stops_amount_SH_PR_key), stopNumber);
//
//            if (!page.equals(getResources().getString(R.string.first_page_from_feedbackWS))) {
//                Intent intent = new Intent(this, ActivityStops.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.putExtra(getResources().getString(R.string.firebase_message_key_for_intent),
//                        remoteMessage.getData().toString());
//                startActivity(intent);
//            } else {
//                NotificationManager notificationManager =
//                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//
//                createNotificationChannel(notificationManager);
//                String notificationContent = getResources().getString(R.string.error_notification_message);
//
//                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
//                notificationBuilder.setAutoCancel(true)
//                        .setContentTitle(notification.getTitle())
//                        .setDefaults(Notification.DEFAULT_ALL)
//                        .setContentText(notificationContent)
//                        .setWhen(System.currentTimeMillis())
//                        .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark_focused);
//                notificationContent = getResources().getString(R.string.notification_content_message, stopNumber, radius);
//                notificationBuilder.setContentText(notificationContent);
//                if (!stopNumber.equalsIgnoreCase(getResources().getString(R.string.word_no))) {
//                    Utils.saveStringToPreferences(getApplicationContext(), getResources()
//                                    .getString(R.string.nearby_stops_data_SH_PR_key),
//                            remoteMessage.getData().toString());
//                    PendingIntent pendingIntent =
//                            PendingIntent.getActivity(this, 0,
//                                    new Intent(this, ActivityStops.class), 0);
//                    notificationBuilder.setContentIntent(pendingIntent);
//                }
//
//                if (notificationManager == null) {
//                    Log.e(tag,"Notification Manager is null. That is why can't NOTIFY user.");
//                } else
//                    notificationManager.notify(100, notificationBuilder.build());
//            }
//        }
//
//    }
//
//    @Override
//    public void onNewToken(@NonNull String s) {
//        super.onNewToken(s);
//        FirebaseMessaging.getInstance().subscribeToTopic("all");
//        Utils.saveStringToPreferences(getApplicationContext(),
//                getApplication().getResources().getString(R.string.firebase_token_SH_PR_key), s);
//    }
//
//    private void createNotificationChannel(NotificationManager notificationManager) {
//        NotificationChannel channel = null;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = getString(R.string.channel_name);
//            String description = getString(R.string.channel_description);
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            channel = new NotificationChannel(CHANNEL_ID, name, importance);
//            channel.setDescription(description);
//            notificationManager.createNotificationChannel(channel);
//        }
//    }

}
