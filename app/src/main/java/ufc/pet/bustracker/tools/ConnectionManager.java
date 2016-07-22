package ufc.pet.bustracker.tools;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import android.support.v4.util.ArrayMap;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.JsonObjectRequest;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;



import ufc.pet.bustracker.MapActivity;
import ufc.pet.bustracker.ufc.pet.bustracker.types.Bus;
import ufc.pet.bustracker.ufc.pet.bustracker.types.Route;

public class ConnectionManager{
    private RequestQueue requestQueue;
    private final String serverPrefix;
    private Context context;
    private ArrayList<Route> routes;
    private HashMap<Integer, ArrayList<Bus>> buses;  // key é a id da routa, o array list são os ônibus daquela rota

    public ConnectionManager(Context context, String serverPrefix){
        this.serverPrefix = serverPrefix;
        this.context = context;
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        routes = new ArrayList<>(0);
        buses = new HashMap<>();
        buses.put(86, new ArrayList<Bus>());
    }

    public void getRoutesFromServer(){
        String url = serverPrefix + "/routes/86";

        JsonObjectRequest jreq = new CustomJsonObjectRequest(JsonObjectRequest.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONParser parser = new JSONParser();
                        try {

                            Route r = parser.parseRoute(response);
                            routes.add(r);

                            Toast.makeText(context, "Rotas obtidas!", Toast.LENGTH_SHORT).show();
                        } catch (Exception e){
                            Log.e(MapActivity.TAG, e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        requestQueue.add(jreq);
    }

    /**
     * Pega os ônibus da rota parâmetro, e salva no map buses.
     * @param r rota a ser pega
     */
    public void getBusesFromRoute(Route r){
        final Integer id_route = r.getId_routes();
        String url = serverPrefix + "/routes/"+ id_route.toString() + "/buses/?localizations=1";

        JsonArrayRequest jreq = new CustomJsonArrayRequest(JsonObjectRequest.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response){
                        JSONParser parser = new JSONParser();
                        ArrayList<Bus> update_buses = new ArrayList<Bus>();

                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject bus = response.getJSONObject(i);
                                update_buses.add(parser.parseBus(bus));
                            }
                        }
                        catch(Exception e){
                            Log.e(MapActivity.TAG, e.getMessage());
                        }

                        buses.put(id_route, update_buses);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        requestQueue.add(jreq);
    }

    public ArrayList<Route> getRoutes() {
        return routes;
    }
    public ArrayList<Bus> getListBuses(Route r){
        ArrayList<Bus> bus = buses.get(r.getId_routes());
        return bus;
    }
}


//precisamos extender JsonObjectRequest para criar um header personalizado que tera o campo 'Token'
class CustomJsonObjectRequest extends JsonObjectRequest{
        public CustomJsonObjectRequest(int method, String url, JSONObject jsonRequest, Response.Listener listener, Response.ErrorListener errorListener)
        {
            super(method, url, jsonRequest, listener, errorListener);
        }

        @Override
        public Map getHeaders() throws AuthFailureError {
            Map headers = new HashMap();
            //adicionamos o token do usuario santana que é um administrador
            headers.put("Token", "Rs1cFdbn9yOY7V\\/SYWmVIbj3PYvVZo+H7WhLd9GOQ3lCwMhgPzot2WRm4hx25i+wrrmhkX5InH5ZlbaLJ2hPLiK9ThVwgSAWSY5T\\/v1hoztNESEWtTPX+2YcwfJ\\/p7kDftRatLTDcpFZ2Dn\\/7LhEwNUn35OafWyA+Cus9wRQ0n3I6xMoWSjGXj1IAgJi44BrKex\\/PMS7lWm0ZK261Wksx4Vj0\\/YRQJgzsGMluG8HNes=");
            return headers;
        }
}

class CustomJsonArrayRequest extends JsonArrayRequest{
    public CustomJsonArrayRequest(int method, String url, JSONArray jsonRequest, Response.Listener listener, Response.ErrorListener errorListener)
    {
        super(method, url, jsonRequest, listener, errorListener);
    }

    @Override
    public Map getHeaders() throws AuthFailureError{
        Map headers = new HashMap();
        //adicionamos o token do usuario santana que é um administrador
        headers.put("Token", "Rs1cFdbn9yOY7V\\/SYWmVIbj3PYvVZo+H7WhLd9GOQ3lCwMhgPzot2WRm4hx25i+wrrmhkX5InH5ZlbaLJ2hPLiK9ThVwgSAWSY5T\\/v1hoztNESEWtTPX+2YcwfJ\\/p7kDftRatLTDcpFZ2Dn\\/7LhEwNUn35OafWyA+Cus9wRQ0n3I6xMoWSjGXj1IAgJi44BrKex\\/PMS7lWm0ZK261Wksx4Vj0\\/YRQJgzsGMluG8HNes=");
        return headers;
    }
}