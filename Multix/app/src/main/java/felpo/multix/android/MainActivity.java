package felpo.multix.android;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import felpo.multix.R;
import felpo.multix.core.History;
import felpo.multix.core.Multa;
import felpo.multix.core.Multix;

public class MainActivity extends ActionBarActivity {
    private Common common;
    private EditText vPlaca;
    private Button vConsultar;
    private ListView vMultas;
    private TextView vNoMultas;
    private String placa;
    private History history;
    private ExecutorService worker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        init();
        startWaitService();
        onLaunchRequestDefaultMultas();
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
            launchSettingsActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void launchSettingsActivity(){
        startActivity(new Intent(this,MyPreferences.class));
    }

    public void onClickConsultar(View v){
        consultarMultas();
    }

    private void init(){
        common = new Common(this);
        worker = Executors.newSingleThreadExecutor();
    }

    private void onLaunchRequestDefaultMultas(){
        if(common.defaultPlacaExists()){
            placa = common.getDefaultPlaca();
            showPlaca();
            worker.submit(new RunRequestDefaultMultas());
        }
        else{
            showEmptyGui();
        }
    }

    private void requestDefaultMultas(){
        try {
            history = Multix.requestHistory(placa);
            runOnUiThread(new RunShowMultasOnGui());
            common.saveOldHistory(history);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startWaitService(){
        startService(new Intent(this,WaitService.class));
    }

    private void consultarMultas(){
        extractPlaca();
        if(validatePlacaSize()){
            worker.submit(new RunRequestMultasConsultar());
        }else{
            showEmptyGui();
        }

    }

    private void extractPlaca(){
        placa = vPlaca.getText().toString().toUpperCase();
    }

    private void showMultasOnGui(){
        if(history==null)
            showEmptyGui();
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
        vPlaca.clearFocus();
        addItemClickListener();
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

    private void launchMultaDescription(Multa multa){
        Intent intent = new Intent(this,MultaDescriptionActivity.class);
        intent.putExtra(getString(R.string.bundle_multa),multa);
        startActivity(intent);
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

    private void addItemClickListener(){
        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                launchMultaDescription(history.multas.get(position));
            }
        };

        vMultas.setOnItemClickListener(listener);
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

    private class RunRequestDefaultMultas implements  Runnable{

        @Override
        public void run() {
            requestDefaultMultas();
        }
    }
}
