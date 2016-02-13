package felpo.multix.android;


import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import felpo.multix.R;
import felpo.multix.core.Multa;
import felpo.multix.core.Multix;

public class MultaListAdapter extends BaseAdapter{
    private List<Multa> multas;
    private Activity context;
    private LayoutInflater layoutInflater;

    private static final int MOTIVO_TEXT_LENGTH = 40;


    public MultaListAdapter(List<Multa> multas, Activity context) {
        this.multas = multas;
        this.context = context;
        layoutInflater = context.getLayoutInflater();
    }

    @Override
    public int getCount() {
        return multas.size();
    }

    @Override
    public Object getItem(int position) {
        return multas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHandler h = null;
        if(view==null){
            view = layoutInflater.inflate(R.layout.multa_item_layout,null);
            h = new ViewHandler(view);
            view.setTag(h);
        }

        h = (ViewHandler) view.getTag();
        fillView(h, position);
        return view;
    }

    private void fillView(ViewHandler h, int position){
        h.vFecha.setText(multas.get(position).fecha);
        h.vMotivo.setText(multas.get(position).motivo.substring(0,MOTIVO_TEXT_LENGTH));
        h.vStatus.setText(multas.get(position).status.toUpperCase());

        if(multas.get(position).status.equals(Multix.PAGADA)){
            h.vStatus.setTextColor(Color.argb(255,0,200,0));
        }else{
            h.vStatus.setTextColor(Color.RED);
        }
    }

    private class ViewHandler{
        public final TextView vFecha;
        public final TextView vMotivo;
        public final TextView vStatus;

        private ViewHandler(View v) {
            this.vFecha = (TextView) v.findViewById(R.id.textView2);
            this.vMotivo = (TextView) v.findViewById(R.id.textView3);
            this.vStatus = (TextView) v.findViewById(R.id.textView4);
        }


    }
}
