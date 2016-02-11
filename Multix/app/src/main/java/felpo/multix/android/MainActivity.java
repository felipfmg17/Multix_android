package felpo.multix.android;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import felpo.multix.R;

public class MainActivity extends ActionBarActivity {
    private Common common;

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



    private void consultarMulta(){
        
    }

    private void showMultasOnGui(){

    }

    private void showEmptyGui(){

    }

    private void findViews(){

    }

    private void showPlacaSizeMessage(){

    }

    private void showErrorMessage(){

    }

    private void showDefaultPlacaConfirmationMessage(){

    }

    private void launchMultaDescription(){

    }
}
