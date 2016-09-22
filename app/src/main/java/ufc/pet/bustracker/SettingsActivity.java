package ufc.pet.bustracker;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    Spinner intervalos;
    Toolbar mToolbar;
    Switch notifica;
    int update_time;
    boolean notification;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_settings);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        notifica = (Switch) findViewById(R.id.notificacoes_switch);

        //Spinner com os valores possíveis de intervalos
        intervalos = (Spinner) findViewById(R.id.intervalos);

        // Configuração da Spinner
        List<String> lista_intervalos = new ArrayList<String>();
        lista_intervalos.add("1");
        lista_intervalos.add("3");
        lista_intervalos.add("5");
        lista_intervalos.add("10");
        lista_intervalos.add("15");
        lista_intervalos.add("20");
        lista_intervalos.add("30");

        ArrayAdapter<String> intervalos_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lista_intervalos);
        intervalos_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        intervalos.setAdapter(intervalos_adapter);

        // Pega as preferências salvas
        SharedPreferences pref = getSharedPreferences(getString(R.string.preferences), MODE_PRIVATE);
        update_time = pref.getInt(getString(R.string.update_time), 3);
        notification = pref.getBoolean(getString(R.string.notifications), true);

        switch(update_time){
            case 3:
                intervalos.setSelection(1);
                break;
            case 5:
                intervalos.setSelection(2);
                break;
            case 10:
                intervalos.setSelection(3);
                break;
            case 15:
                intervalos.setSelection(4);
                break;
            case 20:
                intervalos.setSelection(5);
                break;
            case 30:
                intervalos.setSelection(6);
                break;
            default:
                intervalos.setSelection(0);
                break;
        }
        notifica.setChecked(notification);
    }

    /**
     * Salva as preferências do usuário ao sair da activity de configurações.
     */
    @Override
    public void onDestroy(){
        super.onDestroy();
        SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.preferences), MODE_PRIVATE).edit();
        editor.putInt(getString(R.string.update_time), Integer.valueOf(intervalos.getSelectedItem().toString()));
        editor.putBoolean(getString(R.string.notifications), notifica.isChecked());
        editor.apply();

        Log.e("It is", "WORRRKING");
    }


}
