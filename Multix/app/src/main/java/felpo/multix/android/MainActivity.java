package felpo.multix.android;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PersistableBundle;
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
    private ProgressDialog dialog;


    // Activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ATool.d("create");
        setContentView(R.layout.activity_main);
        findViews();
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(common.isDefaultOn())
            worker.submit(new Runnable() {
                @Override
                public void run() {
                    searchMultasDefault();
                }
            });
        else if(common.temporalHistoryExists())
            worker.submit(new Runnable() {
                @Override
                public void run() {
                    loadTemporalHistory();
                }
            });

        common.turnOffDefault();

        ATool.d("resumme");
    }

    @Override
    protected void onPause() {
        super.onPause();
        worker.submit(new Runnable() {
            @Override
            public void run() {
                saveTemporalHistory();
            }
        });
        ATool.d("pause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        worker.shutdown();
        ATool.d("destroy");
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
            startSettingsActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    
    // Configuration

    private void findViews(){
        vPlaca = (EditText) findViewById(R.id.editText);
        vMultas = (ListView) findViewById(R.id.listView);
        vConsultar = (Button) findViewById(R.id.button);
        vNoMultas = (TextView) findViewById(R.id.textView);
        vPlaca.clearFocus();
        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startMultaDescription(history.multas.get(position));
            }
        };
        vMultas.setOnItemClickListener(listener);
    }

    private void init(){
        common = new Common(this);
        worker = Executors.newSingleThreadExecutor();
        startService(new Intent(this,WaitService.class));
    }


    // Starts intents

    private void startSettingsActivity(){
        startActivity(new Intent(this,MyPreferences.class));
    }

    private void startMultaDescription(Multa multa){
        Intent intent = new Intent(this,MultaDescriptionActivity.class);
        intent.putExtra(getString(R.string.bundle_multa),multa);
        startActivity(intent);
    }


    // Triggered Actions

    public void onClickConsultar(View v){
        worker.submit(new Runnable() {
            @Override
            public void run() {
                searchMultasConsultar();
            }
        });
    }



    // Main procedures
    
    private void searchMultasDefault(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showEmptyGui();
            }
        });
        try {
            if(common.defaultPlacaExists()){
                placa = common.getDefaultPlaca();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showPlaca();
                    }
                });
                history = Multix.requestHistory(placa);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showMultasOnGui();
                    }
                });
                common.saveOldHistory(history);
                common.erasePreviousHistories();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void searchMultasConsultar(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showEmptyGui();
            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showPlaca();
            }
        });
        if(!common.isNetworkAvailable()){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showNoInternetConnection();
                }
            });
            return;
        }
        extractPlaca();
        if(!Multix.validatePlaca(placa)){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showPlacaSizeMessageError();
                }
            });
            return;
        }
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showProgressDialog();
                }
            });
            history = Multix.requestHistory(placa);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showMultasOnGui();
                }
            });
            if(!common.defaultPlacaExists()){
                common.setDefaultPlaca(placa);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showDefaultPlacaConfirmation();
                    }
                });
            }
            if(common.getDefaultPlaca().equals(placa) ){
                common.saveOldHistory(history);
            }

        }catch (IOException e){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showErrorMessage();
                }
            });
            e.printStackTrace();
        }finally {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cancelProgressDialog();
                }
            });
        }
    }


    // User Interface modifiers

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

    private void showProgressDialog(){
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage(getString(R.string.downloading_wait_message));
        dialog.show();
    }

    private void cancelProgressDialog(){
        if(dialog!=null)
            dialog.cancel();
        dialog = null;
    }

    private void showNoInternetConnection(){
        ATool.toastD(this, getString(R.string.no_internet_conection));
    }

    private void showPlacaSizeMessageError(){
        ATool.toastD(this, getString(R.string.placa_size_error));
    }

    private void showErrorMessage(){
        ATool.toastD(this, getString(R.string.process_error));
    }

    private void showDefaultPlacaConfirmation(){
        ATool.toastD(this, placa + ": " + getString(R.string.placa_default_confirmation));
    }


    // Help Methods

    private void extractPlaca(){
        placa = vPlaca.getText().toString().toUpperCase();
    }

    private void loadTemporalHistory(){
        try {
            history = common.loadTemporalHistory();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showMultasOnGui();
                }
            });
            placa = history.placa;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showPlaca();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void saveTemporalHistory(){
        if(history!=null){
            try {
                common.saveTemporalHistory(history);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
