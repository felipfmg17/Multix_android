package felpo.multix.android;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import felpo.multix.R;
import felpo.multix.core.Multix;

public class MainActivity extends ActionBarActivity {
    private Common common;
    private EditText vPlaca;
    private Button vConsultar;
    private ListView vMultas;
    private TextView vNoMultas;
    private String placa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void onClickConsultar(View v){

    }



    private void consultarMultas(){

    }

    private void showMultasOnGui(){

    }

    private void showEmptyGui(){
        vPlaca.setText(getString(R.string.blank));
        vMultas.setVisibility(View.INVISIBLE);
        vNoMultas.setText(getString(R.string.blank));
        vNoMultas.setVisibility(View.VISIBLE);
    }

    private void showPlaca(){
        if(placa!=null)
            vPlaca.setText(placa);
    }

    private void findViews(){
        vPlaca = (EditText) findViewById(R.id.editText);
        vMultas = (ListView) findViewById(R.id.listView);
        vConsultar = (Button) findViewById(R.id.button);
        vNoMultas = (TextView) findViewById(R.id.textView);
    }

    private void showPlacaSizeMessageError(){
        ATool.toast(this,getString(R.string.placa_size_error));
    }

    private void showErrorMessage(){
        ATool.toast(this,getString(R.string.process_error));
    }

    private void showDefaultPlacaConfirmationMessage(){
        ATool.toast(this,placa+": "+getString(R.string.placa_default_confirmation));
    }

    private void launchMultaDescription(){

    }

    private boolean validatePlacaSize(){
        if(placa!=null){
            int size = placa.length();
            if(size >= Multix.MIN_PLACA_SIZE){
                showPlacaSizeMessageError();
                return true;
            }
        }
        return false;
    }
}
