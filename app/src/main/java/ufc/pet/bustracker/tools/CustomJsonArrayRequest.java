package ufc.pet.bustracker.tools;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Isaben on 22/07/2016.
 */
public class CustomJsonArrayRequest extends JsonArrayRequest {
    public CustomJsonArrayRequest(int method, String url, JSONArray jsonRequest, Response.Listener listener, Response.ErrorListener errorListener)
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
