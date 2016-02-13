package felpo.multix.android;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;

import felpo.multix.R;
import felpo.multix.core.History;
import felpo.multix.core.Multix;

public class MainActivity extends ActionBarActivity {
    private Common common;
    private EditText vPlaca;
    private Button vConsultar;
    private ListView vMultas;
    private TextView vNoMultas;
    private String placa;
    private History history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        common = new Common(this);
        setContentView(R.layout.activity_main);
        findViews();
        showInitialGui();
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
        consultarMultas();
    }



    private void startWaitService(){
        startService(new Intent(this,WaitService.class));
    }

    private void consultarMultas(){
        extractPlaca();
        if(validatePlacaSize()){
            Thread thread = new Thread(new RunRequestMultasConsultar());
            thread.start();
        }else{
            showEmptyGui();
        }

    }

    private void extractPlaca(){
        placa = vPlaca.getText().toString().toUpperCase();
    }

    private void showMultasOnGui(){
        if(history.multas.size()>0){
            MultaListAdapter adapter = new MultaListAdapter(history.multas, this);
            vMultas.setAdapter(adapter);
            vNoMultas.setVisibility(View.INVISIBLE);
            vMultas.setVisibility(View.VISIBLE);

        }else{
            vMultas.setVisibility((View.INVISIBLE));
            vNoMultas.setText(getString(R.string.no_tienes_multas));
            vNoMultas.setVisibility(View.VISIBLE);
        }

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

    private void showInitialGui(){
        showEmptyGui();
    }

    private void launchMultaDescription(){

    }

    private boolean validatePlacaSize(){
        if(placa!=null){
            int size = placa.length();
            if(size >= Multix.MIN_PLACA_SIZE)
                return true;
            showPlacaSizeMessageError();
        }
        return false;
    }

    private void requestMultasConsultar(){
        try {
            history = Multix.requestHistory(placa);
            runOnUiThread(new RunShowMultasOnGui());
            ATool.d(" "+common.defaultPlacaExists());
            if(!common.defaultPlacaExists()){
                common.setDefaultPlaca(placa);
                runOnUiThread(new RunShowDefaultPlacaConfirmationMessage());
                common.saveOldHistory(history);
            }
            else if(common.getDefaultPlaca().equals(placa)){
                common.saveOldHistory(history);
            }
        } catch (IOException e) {
            runOnUiThread(new RunShowErrorMessage());
            runOnUiThread(new RunShowEmptyGui());
            e.printStackTrace();
        }
    }

    private class RunRequestMultasConsultar implements Runnable{
        @Override
        public void run() {
            requestMultasConsultar();
        }
    }

    private class RunShowMultasOnGui implements Runnable {

        @Override
        public void run() {
            showMultasOnGui();
        }
    }

    private class RunShowErrorMessage implements Runnable {

        @Override
        public void run() {
            showErrorMessage();
        }
    }

    private class RunShowDefaultPlacaConfirmationMessage implements  Runnable{

        @Override
        public void run() {
            showDefaultPlacaConfirmationMessage();
        }
    }

    private class RunShowEmptyGui implements Runnable{

        @Override
        public void run() {
            showEmptyGui();
        }
    }
}
