package jp.naotiki.kakeibo;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TestAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private int layoutID;
    private String[] names;
    private int[] imageIDs;

    static class ViewHolder {
        ImageView imageView;
        TextView textView;
    }

    TestAdapter(Context context,
                int itemLayoutId,
                String[] spinnerItems,
                String[] spinnerImages ){

        inflater = LayoutInflater.from(context);
        layoutID = itemLayoutId;
        names = spinnerItems;
        imageIDs = new int[spinnerImages.length];
        Resources res = context.getResources();

        // 最初に画像IDを配列で取っておく
        for( int i = 0; i< spinnerImages.length; i++){
            imageIDs[i] = res.getIdentifier(spinnerImages[i],
                    "mipmap", context.getPackageName());
        }
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(layoutID, null);
            holder = new ViewHolder();

            holder.imageView = convertView.findViewById(R.id.image_view);
            holder.textView = convertView.findViewById(R.id.text_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.imageView.setImageResource(imageIDs[position]);
        holder.textView.setText(names[position]);

        return convertView;
    }

    @Override
    public int getCount() {
        return names.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}