package com.h6ah4i.android.example.advrecyclerview.common.data;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.swiftkaydevelopment.testing.HttpsClient;
import com.swiftkaydevelopment.testing.R;
import com.swiftkaydevelopment.testing.RecyclerListViewFragment;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by khaines178 on 9/22/15.
 */
public class DataProvider extends AbstractDataProvider {


    class Friends{
        int fId;    //"id" element of "friends" array in json
        String fname; //"name" element of "friends" array in json

    }

    private List<CustomData> mData;
    private CustomData mLastRemovedData;
    private int mLastRemovedPosition = -1;
    Context context;

    public DataProvider(Context context) {
        this.context = context;

        mData = new LinkedList<>();
        //keystore info
        final char[] STORE_PASS = new char[]{'1', '7', '8', '1', '1', '7', '8', '1'};
        final int STORE_ACCESS = R.raw.sndraccess;
        final int STORE_DENIED = R.raw.deniedaccess; //this is for testing purposes only

        new SSLPinnedConnect(context.getResources(), STORE_ACCESS, STORE_PASS).execute();

    }

    @Override
    public int getCount() {
        Log.d("test","getCount: " + Integer.toString(mData.size()));
        return mData.size();

    }

    @Override
    public AbstractDataProvider.Data getItem(int index) {
        if (index < 0 || index >= getCount()) {
            throw new IndexOutOfBoundsException("index = " + index);
        }

        return mData.get(index);
    }

    @Override
    public int undoLastRemoval() {
        if (mLastRemovedData != null) {
            int insertedPosition;
            if (mLastRemovedPosition >= 0 && mLastRemovedPosition < mData.size()) {
                insertedPosition = mLastRemovedPosition;
            } else {
                insertedPosition = mData.size();
            }

            mData.add(insertedPosition, mLastRemovedData);

            mLastRemovedData = null;
            mLastRemovedPosition = -1;

            return insertedPosition;
        } else {
            return -1;
        }
    }

    @Override
    public void moveItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }

        final CustomData item = mData.remove(fromPosition);

        mData.add(toPosition, item);
        mLastRemovedPosition = -1;
    }

    @Override
    public void removeItem(int position) {
        //noinspection UnnecessaryLocalVariable
        final CustomData removedItem = mData.remove(position);

        mLastRemovedData = removedItem;
        mLastRemovedPosition = position;
    }


    public static final class CustomData extends AbstractDataProvider.Data {


        private final long mId;
        private final int mViewType;
        private final int mSwipeReaction;
        private boolean mPinnedToSwipeLeft;




        //initialize values returned from SNDR connection
        String dID;     //json "_id" value
        int dindex;     //json "index" value
        String dguid;   //json "guid" value
        boolean disActive;  //json "isActive" value
        String dpicture;    //json "picture" value
        String dfirstname;   //from json value "first" in array "name"
        String dlastname;   //from json value "last" in array "name"
        String demail;      //json "email" value
        String dphone;      //json "phone" value
        String daddress;    //json "address" value
        String[] dtags;     //array from "tags" array in json
        List<Friends> flist;
        String dnotification;//json "notification" value
        String html;        //string of html set by user to include richtext "notes" about contact

        public CustomData(long id, int viewType, int swipeReaction){
            flist = new ArrayList<Friends>();
            mId = id;
            mViewType = viewType;
            mSwipeReaction = swipeReaction;
        }

        @Override
        public String getDataId() {
            return dID;
        }
        @Override
        public String getHTML(){return html;}

        @Override
        public void setHTML(String html) {
            this.html = html;
        }

        @Override
        public int getIndex() {
            return dindex;
        }

        @Override
        public boolean getIsActive() {
            return disActive;
        }

        @Override
        public String getGUID() {
            return dguid;
        }

        @Override
        public String getPicture() {
            return dpicture;
        }

        @Override
        public String getFirstname() {
            return dfirstname;
        }

        @Override
        public String getLastname() {
            return dlastname;
        }

        @Override
        public String getEmail() {
            return demail;
        }

        @Override
        public String getPhone() {
            return dphone;
        }

        @Override
        public String getAddress() {
            return daddress;
        }

        @Override
        public String[] getTags() {
            return dtags;
        }

        @Override
        public List<Friends> getFriends() {
            return flist;
        }

        @Override
        public String getNotifications() {
            return dnotification;
        }


        @Override
        public long getId() {
            return mId;
        }

        @Override
        public boolean isSectionHeader() {
            return false;
        }

        @Override
        public int getViewType() {
            return mViewType;
        }

        @Override
        public int getSwipeReactionType() {
            return mSwipeReaction;
        }

        @Override
        public void setPinnedToSwipeLeft(boolean pinned) {

            mPinnedToSwipeLeft = pinned;
        }

        @Override
        public boolean isPinnedToSwipeLeft() {
            return mPinnedToSwipeLeft;
        }
    }


    class SSLPinnedConnect extends AsyncTask<Void, Void, String> {
        ProgressDialog pDialog;

        private InputStream stream;
        private char[] password;

        public SSLPinnedConnect(Resources resources, int certificateRawResource, char[] password) {
            this.stream = resources.openRawResource(certificateRawResource);
            this.password = password;
        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = null;

            try {
                String url = "https://sndr.com/test092015.json";
                assert (null != url);

                InputStream inputStream = null;


                // create HttpClient this uses custom httpsclient to check pinned ssl certificate
                HttpClient httpclient = new HttpsClient(stream, password);

                // make GET request to the given URL
                HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

                // receive response as inputStream
                inputStream = httpResponse.getEntity().getContent();

                // convert inputstream to string
                if (inputStream != null) {
                    result = convertInputStreamToString(inputStream);
                } else {
                    result = "Did not work! May be error on your internet connection.";
                }


            } catch (javax.net.ssl.SSLHandshakeException ex) {
                ex.printStackTrace();

                // Log error
                Log.e("doInBackground", ex.toString());

                // Prepare return value
                result = "handshake_exception";
            }
            catch (Exception e){
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if(pDialog.isShowing())
                pDialog.dismiss();

            Log.e("test", "SSLPinnedConnect result: " + result);
            assert (null != result);
            if (null == result)
                return;

            if (result.equals("handshake_exception")) {
                Toast.makeText(context, "SSL Handshake Exception, invalid certificate", Toast.LENGTH_LONG).show();
            } else {

                final int viewType = 0;
                final int swipeReaction = RecyclerViewSwipeManager.REACTION_CAN_SWIPE_LEFT | RecyclerViewSwipeManager.REACTION_CAN_SWIPE_RIGHT;

                try {

                    //initialize json
                    JSONArray jarray = new JSONArray(result);


                    for (int i = 0; i < jarray.length(); i++) {
                        final long id = mData.size();
                        Log.d("test", "id: " + String.valueOf(id));
                        JSONObject childJSONObject = jarray.getJSONObject(i);
                        mData.add(new CustomData(id, viewType, swipeReaction));
                        mData.get(mData.size() - 1).dID = childJSONObject.getString("_id");
                        mData.get(mData.size() - 1).dindex = childJSONObject.getInt("index");
                        mData.get(mData.size() - 1).dguid = childJSONObject.getString("guid");
                        mData.get(mData.size() - 1).disActive = childJSONObject.getBoolean("isActive");
                        mData.get(mData.size() - 1).dpicture = childJSONObject.getString("picture");
                        mData.get(mData.size() - 1).demail = childJSONObject.getString("email");
                        mData.get(mData.size() - 1).dphone = childJSONObject.getString("phone");
                        mData.get(mData.size() - 1).daddress = childJSONObject.getString("address");
                        mData.get(mData.size() - 1).dfirstname = childJSONObject.getJSONObject("name").getString("first");
                        mData.get(mData.size() - 1).dlastname = childJSONObject.getJSONObject("name").getString("last");
                        mData.get(mData.size() - 1).dtags = new String[childJSONObject.getJSONArray("tags").length()];
                        for(int ind = 0;ind<mData.get(mData.size() - 1).dtags.length;ind++){
                            mData.get(mData.size() - 1).dtags[ind] = childJSONObject.getJSONArray("tags").get(ind).toString();

                        }
                        mData.get(mData.size() - 1).dnotification = childJSONObject.getString("notification");

                        JSONArray friendsjsonarray = childJSONObject.getJSONArray("friends");
                        List<Friends> friends = new ArrayList<Friends>();
                        for(int ind = 0; ind < friendsjsonarray.length();ind++){
                            friends.add(new Friends());
                            friends.get(friends.size() - 1).fId = friendsjsonarray.getJSONObject(ind).getInt("id");
                            friends.get(friends.size() - 1).fname = friendsjsonarray.getJSONObject(ind).getString("name");

                        }
                        mData.get(mData.size() - 1).flist.addAll(friends);


                    }
                    Log.d("test","mData Sized on post execute: " + Integer.toString(mData.size()));
                    RecyclerListViewFragment.mRecyclerView.getAdapter().notifyDataSetChanged();

                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        }


        // convert inputstream to String
        private String convertInputStreamToString(InputStream inputStream) throws IOException {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            String result = "";
            while ((line = bufferedReader.readLine()) != null)
                result += line;

            inputStream.close();
            return result;

        }

    }
}
