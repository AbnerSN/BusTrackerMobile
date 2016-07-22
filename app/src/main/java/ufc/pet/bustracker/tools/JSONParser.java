package ufc.pet.bustracker.tools;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;
import com.google.maps.android.PolyUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

import ufc.pet.bustracker.MapActivity;
import ufc.pet.bustracker.ufc.pet.bustracker.types.Bus;
import ufc.pet.bustracker.ufc.pet.bustracker.types.Route;

public class JSONParser {
    public Route parseRoute(JSONObject ob){
        Route r = new Route();
        try{
            r.setId_routes(ob.getInt("id_routes"));
            r.setName(ob.getString("name"));
            r.setDescription(ob.getString("description"));
            JSONArray id_buses = ob.getJSONArray("id_buses");
            String polylineCode = ob.getJSONObject("googleRoute").getJSONArray("routes").getJSONObject(0).getJSONObject("overview_polyline").getString("points");
            r.setPoints(PolyUtil.decode(polylineCode));
            for(int i = 0; i < id_buses.length(); i++){
                int id = id_buses.getInt(i);
                r.getId_buses().add(id);
            }

        } catch (Exception e){
            Log.e(MapActivity.TAG, e.getMessage());
        }
        return r;
    }

    /**
     * Entende o JSONObject de bus
     * @param ob JSON bus
     * @return objeto bus correspondente
     */
    public Bus parseBus(JSONObject ob){
        Bus b = new Bus();
        try{
            b.setId(ob.getInt("id_bus"));
            int velocity = ob.getInt("velocity");
            JSONArray local = ob.getJSONArray("lastLocalizations");
            for(int i = 0; i < 1; i++){
                double longitude = local.getJSONObject(i).getDouble("longitude");
                double latitude = local.getJSONObject(i).getDouble("latitude");
                SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date update = ft.parse(local.getJSONObject(i).getString("date"));
                b.updateLocation(latitude, longitude, velocity, update);
            }
        }
        catch(Exception e){
            Log.e(MapActivity.TAG, e.getMessage());
        }
        return b;
    }
}
