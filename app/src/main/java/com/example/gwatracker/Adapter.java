package com.example.gwatracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{
    List<Subject> list;
    private DatabaseHelper db;
    private Context context;

    public Adapter(Context context, List<Subject> list) {
        this.list = list;
        this.context = context;
        this.db = new DatabaseHelper(context.getApplicationContext());
    }

    public Adapter(){}

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.grades_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Subject s = list.get(position);

        holder.sub.setText(s.getSub());
        holder.grades.setText(String.valueOf(s.getGrades()));
        String units = "";

        if (s.getUnits() > 1){
            units = s.getUnits() + " Units";
        } else {
            units = s.getUnits() + " Unit";
        }

        holder.units.setText(units);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView sub, grades, units;

        public ViewHolder(final View view){
            super(view);

            sub = view.findViewById(R.id.txtSubjectView);
            grades = view.findViewById(R.id.txtGradesView);
            units = view.findViewById(R.id.txtUnitsView);
        }
    }
}
