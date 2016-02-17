package felpo.multix.android;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import felpo.multix.R;
import felpo.multix.core.Multa;
import felpo.multix.core.Multix;

public class MultaDescriptionActivity extends Activity {
    private TextView folio;
    private TextView fecha;
    private TextView sancion;
    private TextView motivo;
    private TextView status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multa_description);
        findViews();
        fillFields();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_multa_description, menu);
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

    private void findViews(){
        folio = (TextView) findViewById(R.id.textView5);
        fecha = (TextView) findViewById(R.id.textView6);
        sancion = (TextView) findViewById(R.id.textView7);
        motivo = (TextView) findViewById(R.id.textView8);
        status = (TextView) findViewById(R.id.textView9);
    }

    private void fillFields(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Multa multa = (Multa) bundle.get(getString(R.string.bundle_multa));
        folio.setText(multa.folio);
        fecha.setText(multa.fecha);
        if(Multix.isSancionConvertibleToPesos(multa.sancion))
            sancion.setText(multa.sancion+ ", " +Multix.calculateSancionInPesos(multa.sancion));
        else
            sancion.setText(multa.sancion);
        motivo.setText(multa.motivo);
        status.setText(multa.status);
    }
}
