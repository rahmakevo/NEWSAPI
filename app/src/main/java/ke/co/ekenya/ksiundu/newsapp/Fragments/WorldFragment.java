package ke.co.ekenya.ksiundu.newsapp.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ke.co.ekenya.ksiundu.newsapp.R;
import ke.co.ekenya.ksiundu.newsapp.adapter.WorldAdapter;
import ke.co.ekenya.ksiundu.newsapp.model.WorldModel;
import ke.co.ekenya.ksiundu.newsapp.services.ApiService;

public class WorldFragment extends Fragment {
    private SweetAlertDialog mWorldNewsDialog;
    private ArrayList<WorldModel> mWorldList = new ArrayList<>();
    private RecyclerView mWorldRecycler;
    public WorldFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_world, container, false);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new getWorldResponse().execute();
            }
        });
        mWorldRecycler = mView.findViewById(R.id.recyclerWorld);
        return mView;
    }

    private class getWorldResponse extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mWorldNewsDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
            mWorldNewsDialog.setTitleText("Fetching News")
                    .setContentText("Dear Client, Kindly wait as we fetch the latest News from around the World")
                    .setCancelable(false);
            mWorldNewsDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String url = "https://newsapi.org/v2/everything?q=all&apiKey=390a63f3032e4dd9ac906401f6140e7e";
            ApiService mService = new ApiService();
            String response = mService.getWorldNews(url);

            try {
                JSONObject mObject = new JSONObject(response);
                JSONArray mArray = mObject.getJSONArray("articles");

                for (int i = 0; i < mArray.length(); i++) {
                    JSONObject mResponseObject = mArray.getJSONObject(i);

                    JSONObject mSourceObject = mResponseObject.getJSONObject("source");
                    String name = mSourceObject.getString("name");

                    String title = mResponseObject.getString("title");
                    String image = mResponseObject.getString("urlToImage");
                    String content = mResponseObject.getString("content");

                    if (!name.isEmpty() && !title.isEmpty() && !image.isEmpty() && !content.isEmpty()) {

                        WorldModel mModel = new WorldModel();
                        mModel.setName(name);
                        mModel.setTitle(title);
                        mModel.setImage(image);
                        mModel.setContent(content);

                        mWorldList.add(mModel);
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            WorldAdapter mAdapter = new WorldAdapter(mWorldList);
            // specify an adapter (see also next example)
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            mWorldRecycler.setHasFixedSize(true);

            // use a linear layout manager
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mWorldRecycler.setLayoutManager(layoutManager);
            mWorldRecycler.setItemAnimator(new DefaultItemAnimator());
            mWorldRecycler.setAdapter(mAdapter);
            mWorldNewsDialog.dismiss();
        }
    }
}
