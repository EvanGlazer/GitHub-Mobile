package com.evanglazer.githubmobile;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public class MainActivity extends AppCompatActivity {
    private class GistFile
    {
        public String type;
        public String fileName;
    }

    private class Gist{

        public String id;
        public HashMap<String, GistFile> files;

        @Override
        public String toString()
        {

            String output = id + ": ";
            for(Map.Entry<String , GistFile> file: files.entrySet())
            {

                output +=  file.getKey() + " =" + file.getValue().type + ", ";
            }

            return output;
        }
    }

    private class UserSummary{

        public String login;
        public String id;

        @Override
        public String toString() {
            return "UserSummary{" +
                    "login='" + login + '\'' +
                    ", id='" + id + '\'' +
                    '}';
        }
    }

    private class UsersSearchResult
    {
        public int total_count;
        public boolean incomplete_results;
        public List<UserSummary> items;
    }

    private class UserDetails
    {

        public String id;
        public String location;

        @Override
        public String toString() {
            return "UserDetails{" +
                    "id='" + id + '\'' +
                    ", location='" + location + '\'' +
                    '}';
        }


    }


    // interface, generates class methods call to api
    private interface GithubService{

        @GET("/gists/public")
        List<Gist> getPublicGists();

        @GET("/search/users")
        UsersSearchResult searchUsers(@Query("q") String query);

        @GET("/users/{username}")
        UserDetails getUser(@Path("username") String username);

        @GET("/users/{username}")
        void getUsertAsync(@Path("username") String username, Callback<UserDetails> callback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint("https://api.github.com")
                .build();

        final GithubService service = adapter.create(GithubService.class);

        final ArrayAdapter<Object> listAdapter = new ArrayAdapter<Object>(this, android.R.layout.simple_list_item_1);
        ListView listView = (ListView) findViewById(R.id.activity_main_listView);
        listView.setAdapter(listAdapter);

        listAdapter.addAll(service.searchUsers("evanglazer").items);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserSummary summary = (UserSummary) listAdapter.getItem(position);
                service.getUsertAsync(summary.login, new Callback<UserDetails>() {
                    @Override
                    public void success(UserDetails userDetails, Response response) {
                        Toast.makeText(MainActivity.this, userDetails.location, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
            }
            });
    }
}