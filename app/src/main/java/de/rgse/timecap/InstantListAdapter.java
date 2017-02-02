package de.rgse.timecap;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.rgse.timecap.model.Timeevent;

public class InstantListAdapter extends BaseAdapter {

    private static final int WHITE = R.color.white;
    private static final int HOLO_BLUE = R.color.holo_blue_light;

    private Context context;

    private List<Timeevent> data;

    private int color;

    public InstantListAdapter(Context c) {
        context = c;
        this.data = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            grid = new View(context);
            grid = inflater.inflate(R.layout.instant_detail, null);

            Timeevent timeevent = data.get(position);

            TextView date = (TextView) grid.findViewById(R.id.grid_date);
            date.setText(timeevent.getDate());

            TextView time = (TextView) grid.findViewById(R.id.grid_time);
            time.setText(timeevent.getTime());

            TextView location = (TextView) grid.findViewById(R.id.grid_location);
            location.setText(timeevent.getLocationId());


            if(position != 0 && position % 2 == 0) {
                color =  color == WHITE ? HOLO_BLUE : WHITE;
            }
            grid.setBackgroundColor(ContextCompat.getColor(context, color));

        } else {
            grid = (View) convertView;
        }

        return grid;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        color = WHITE;
    }

    public void setData(List<Timeevent> data) {
        this.data = data;
        notifyDataSetChanged();
    }
}