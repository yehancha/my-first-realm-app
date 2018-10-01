package io.realm.todo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.SyncUser;
import io.realm.todo.model.Project;
import io.realm.todo.ui.ProjectsRecyclerAdapter;

public class ProjectsActivity extends AppCompatActivity {
    private Realm realm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        setSupportActionBar(findViewById(R.id.toolbar));

        findViewById(R.id.fab).setOnClickListener(view -> {
            View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_task, null);
            EditText taskText = dialogView.findViewById(R.id.task);
            new AlertDialog.Builder(ProjectsActivity.this)
                    .setTitle("Add a new project")
                    .setView(dialogView)
                    .setPositiveButton("Add", (dialog, which) -> realm.executeTransactionAsync(realm -> {
                        Project project = new Project();
                        String userId = SyncUser.current().getIdentity();
                        String name = taskText.getText().toString();

                        project.setId(UUID.randomUUID().toString());
                        project.setOwner(userId);
                        project.setName(name);
                        project.setTimestamp(new Date());

                        realm.insert(project);
                    }))
                    .setNegativeButton("Cancel", null)
                    .create()
                    .show();
        });

        // using the current SyncUser#id, perform a partial query to obtain
        // only projects belonging to this SyncUser.
        realm = Realm.getDefaultInstance();
        RealmResults<Project> projects = realm
                .where(Project.class)
                .equalTo("owner", SyncUser.current().getIdentity())
                .sort("timestamp", Sort.DESCENDING)
                .findAllAsync();

        final ProjectsRecyclerAdapter itemsRecyclerAdapter = new ProjectsRecyclerAdapter(this, projects);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(itemsRecyclerAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            SyncUser syncUser = SyncUser.current();
            if (syncUser != null) {
                syncUser.logOut();
                Intent intent = new Intent(this, WelcomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}