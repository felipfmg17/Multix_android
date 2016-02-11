package felpo.multix.android;


import android.content.Context;

public class Common {
    Context context;

    public Common(Context context){
        this.context = context;
    }

    public boolean defaultPlacaExists(){
        return false;
    }

    public String  getdefaultPlaca(){
        return null;
    }
}
