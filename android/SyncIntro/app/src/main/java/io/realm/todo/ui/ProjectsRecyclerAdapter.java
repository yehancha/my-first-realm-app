package io.realm.todo.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.todo.ItemsActivity;
import io.realm.todo.model.Project;

public class ProjectsRecyclerAdapter extends RealmRecyclerViewAdapter<Project, ProjectsRecyclerAdapter.MyViewHolder> {
    private final Context context;

    public ProjectsRecyclerAdapter(Context context, OrderedRealmCollection<Project> data) {
        super(data, true);
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position)  {
        final Project project = getItem(position);
        if (project != null) {
            holder.textView.setText(project.getName());
            holder.textView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ItemsActivity.class);
                intent.putExtra("project_id", project.getId());
                context.startActivity(intent);
            });
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        MyViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}