package ufc.pet.bustracker;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Map;

import ufc.pet.bustracker.tools.ConnectionManager;
import ufc.pet.bustracker.ufc.pet.bustracker.types.Bus;
import ufc.pet.bustracker.ufc.pet.bustracker.types.Route;

public class MapActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleMap.OnPolylineClickListener
{

    // Tag para os logs
    public static final String TAG = MapActivity.class.getName();

    // Elementos da interface
    private GoogleMap mMap;
    private Toolbar mToolbar;
    private TextView mInfoTitle;
    private TextView mInfoDescription;
    private Button mUpdateButton;
    private ArrayList<Route> routes;
    public Handler handler = new Handler();
    private ArrayList<Marker> busOnScreen; // marcadores que estão no mapa

    // Gerenciador de conectividade
    private ConnectionManager connectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Localiza elementos da interface
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mInfoTitle = (TextView) findViewById(R.id.info_title);
        mInfoDescription = (TextView) findViewById(R.id.info_description);
        mUpdateButton = (Button) findViewById(R.id.update_button);

        connectionManager = new ConnectionManager(getApplicationContext(), getResources().getString(R.string.host_prefix));

        // Configura elementos da interface
        setSupportActionBar(mToolbar);
        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawRoutesOnMap();
            }
        });
        drawRoutesOnMap();

        // Atribui mapa ao elemento fragment na interface
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        handler.postDelayed(updateBus, 500);
        handler.postDelayed(notificar, 1000);
        busOnScreen = new ArrayList<>();

    }


    /**
     * Ações ao clicar em um polyline
     */
    public void onPolylineClick(Polyline p){
        int selected = ContextCompat.getColor(getApplicationContext(), R.color.colorAccent);
        int unselected = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
        for(Route r : routes){
            if(r.isActiveOnMap()){
                Polyline routePoly = r.getAssociatedPolyline();
                if(routePoly.hashCode() == p.hashCode()) {
                    mInfoTitle.setText(r.getName());
                    mInfoDescription.setText(r.getDescription());
                }
                else {
                    routePoly.setColor(unselected);
                }
            }
        }
        p.setColor(selected);
        LatLngBounds.Builder b = new LatLngBounds.Builder();
        for(LatLng l : p.getPoints()){
            b.include(l);
        }
        LatLngBounds bounds = b.build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 60));
    }

    /**
     * Desenha as rotas e armazena os polylines associados
     */
    public void drawRoutesOnMap(){
        connectionManager.getRoutesFromServer();
        routes = connectionManager.getRoutes();
        for(Route r : routes){
            if(!r.isActiveOnMap()) {
                Polyline p = mMap.addPolyline(
                        new PolylineOptions()
                                .addAll(r.getPoints())
                                .clickable(true)
                                .color(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))
                );
                r.setAssociatedPolyline(p);
            }
        }
    }

    /**
     * Colocar os marcadores dos ônibus no mapa
     * Também verifica se já existe marcadores no mapa
     * Se sim, deleta-os para colocar os novos
     * @param r rota no mapa
     */
    public void putBusOnMap(Route r){
        connectionManager.getBusesFromRoute(r);
        ArrayList<Bus> buses = connectionManager.getListBuses(r);
        if(busOnScreen != null){
            for(int i = 0; i < busOnScreen.size(); i++){
                busOnScreen.get(i).remove();
            }
            busOnScreen.clear();
        }
        for(int i = 0; i < buses.size(); i++){
            Bus b = buses.get(i);
            Marker marker = mMap.addMarker(new MarkerOptions().title(String.valueOf(b.getId())).snippet("Ônibus manolo").position(b.getCoordinates()));
            busOnScreen.add(marker);
        }
    }

    /**
     * Fornece um manipulador para o objeto do mapa
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnPolylineClickListener(this);
        // Posiciona o mapa em Sobral
        LatLng sobral = new LatLng(-3.6906438,-40.3503957);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sobral, 15));
    }

    /**
     * Cria um menu de opções na barra superior
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Atualizar os ônibus automaticamente no mapa
     */
    Runnable updateBus = new Runnable(){
        @Override
        public void run(){
            for(Route r : routes){
                if(r.isActiveOnMap()) {
                    putBusOnMap(r);
                }
            }
            handler.postDelayed(updateBus, 3000);
        }
    };

    Runnable notificar = new Runnable(){
        @Override
        public void run(){
            notification();
            handler.postDelayed(notificar, 10000);
        }
    };

    public void notification(){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Teste notificação")
                        .setContentText("Eita funciona");

        Intent resultIntent = new Intent(this, MapActivity.class);


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addParentStack(MapActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int mId = 1;
        mNotificationManager.notify(mId, mBuilder.build());
    }
}
