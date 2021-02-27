package com.ncertguruji.magicalexo;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class customCommentsList extends ArrayAdapter {
//    private final String[] chapterName;
//    private final String[] chapterDescription;

    List<String> chapterName = new ArrayList<String>();
    List<String> chapterDescription = new ArrayList<String>();
//    private final Integer[] imageId;
    private final Activity context;

    public customCommentsList(Activity context, ArrayList chapterName, ArrayList chapterDescription) {
        super(context, R.layout.row_item, chapterName);
        this.context = context;
        this.chapterName = chapterName;
        this.chapterDescription = chapterDescription;
//        this.imageId = imageId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row=convertView;
        LayoutInflater inflater = context.getLayoutInflater();
        if(convertView==null)
            row = inflater.inflate(R.layout.row_item, null, true);
        TextView textViewChapter = (TextView) row.findViewById(R.id.chapterName);
        TextView textViewDescription = (TextView) row.findViewById(R.id.chapterDescription);
//        ImageView imageFlag = (ImageView) row.findViewById(R.id.chapterImage);

        textViewChapter.setText(chapterName.get(position));
        textViewDescription.setText(chapterDescription.get(position));
//        imageFlag.setImageResource(imageId[position]);
        return  row;
    }
}
