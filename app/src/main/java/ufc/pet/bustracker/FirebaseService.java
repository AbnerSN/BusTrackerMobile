package ufc.pet.bustracker;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Isaben on 06/09/2016.
 * Classe para serviço de mensagens online do Firebase. É um serviço que fica
 * "escutando" sempre e age ao receber algum pacote.
 */
public class FirebaseService extends FirebaseMessagingService {
    /**
     * Método que é chamado sempre que uma mensagem é recebida.
     * @param remoteMessage objeto da mensagem recebida
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d("Teste", "From: " + remoteMessage.getFrom());

        if (remoteMessage.getNotification() != null) {
            Log.d("Mermão", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        SharedPreferences pref = getSharedPreferences(getString(R.string.preferences), MODE_PRIVATE);
        if(pref.getBoolean(getString(R.string.notifications), true))
            notificar("manolo IT IS WORKING!!!!");
    }

    private void notificar(String messageBody) {
        Intent intent = new Intent(this, MapActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Teste")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
