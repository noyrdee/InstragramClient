package com.codepath.instragramclient;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.lang.String;
public class PhotosActivity extends Activity {
    private static final String CLIENT_ID = "0da6540c2e9c4c9bbc0d55736b35c170";
    private ArrayList<InstagramPhoto> photos;
    private InstagramPhotosAdapter aPhotos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
      // Send out ApI Request to popular Photos
        photos = new ArrayList<>();
        // create the adapter linking it the source
        aPhotos = new InstagramPhotosAdapter(this, photos);
        // 2. Find the listview from the layout
        ListView lvPhotos = (ListView)findViewById(R.id.lvPhotos);
        // 3. Set the adapter binding it to the listview
        lvPhotos.setAdapter(aPhotos);
      // Fetch the popular Photos
        fetchPopularPhotos();

    }
     //trigger API request

     public void fetchPopularPhotos() {
         //..........................................
         //..........................................
         String url = "https://api.instagram.com/v1/media/popular?client_id="+ CLIENT_ID;
         //Create the network client
         AsyncHttpClient client = new AsyncHttpClient();
         // Trigger the GET request
         client.get(url, null, new JsonHttpResponseHandler() {
             // onSuccess (worked)

             @Override
             public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                  //Expecting a JSON Object
                 //Type..........
                 //URL...........
                 //Caption.......
                 //Author name.......
                 // Iterate each of the photo items and decode the item into a java object
                 JSONArray photosJSON =null;
                 try{
                     photosJSON = response.getJSONArray("data");
                     //iterate array of posts
                     for (int i=0;i<photosJSON.length();i++){
                         // get the json object at that position
                         JSONObject photoJSON = photosJSON.getJSONObject(i);
                         // decode the attributes of the json into a data model
                         InstagramPhoto photo = new InstagramPhoto();
                         // Author Name ("data"=>[x]=>"user"=>"username")
                         photo.Username = photoJSON.getJSONObject("user").getString("username");
                         //Caption:{"data"=>[x]=>"caption"=>"text"}
                         photo.caption = photoJSON.getJSONObject("caption").getString("text");
                         // Type: {"data"=>[x]=>"type"}("image" or"video" )
                         //photo.type=photoJSON.getJSONObject("type").getString("text");
                         //URL:{"data"=>[x]"image"=>"Standard_resolution"=>"url")}
                         photo.ImageUrl = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
                         photo.imageHeight = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getInt("height");
                         photo.likesCount = photoJSON.getJSONObject("likes").getInt("count");
                         // Add decoded object to the photos
                         photos.add(photo);

                     }
                 }catch (JSONException e){
                     e.printStackTrace();
                 }
              // Callback
                 aPhotos.notifyDataSetChanged();
             }
             //onFailure(fail)


             public void onFailure(int statusCode, Header[] headers, Throwable throwable) {
                  // DO SOMETHING
             }
         });
     }
         @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photos, menu);
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
}
