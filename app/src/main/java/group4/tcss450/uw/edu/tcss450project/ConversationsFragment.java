package group4.tcss450.uw.edu.tcss450project;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import group4.tcss450.uw.edu.tcss450project.model.Conversation;
import group4.tcss450.uw.edu.tcss450project.utils.ConversationsAdapter;
import group4.tcss450.uw.edu.tcss450project.utils.SendPostAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConversationsFragment extends Fragment implements ConversationsAdapter.OnConversationDeleteInteractionListener{
    private OnConversationViewInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SharedPreferences mPref;
    private String mUsername;
    private String mSendUrl;
    private String mDeleteUrl;
    private ArrayList<Conversation> mDataset;
    private int mDeletePosition;

    public ConversationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversations, container, false);
        //Make the FAB appear on this fragment
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);

        //Initialize recycler view with an empty dataset
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerSelectConversations);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        //Start with an empty dataset.
        mDataset = new ArrayList();
        mAdapter = new ConversationsAdapter(mDataset,mListener,this);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        if (!prefs.contains(getString(R.string.keys_prefs_username))) {
            throw new IllegalStateException("No username in prefs!");
        }
        mUsername = prefs.getString(getString(R.string.keys_prefs_username), "");

        mSendUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_get_conversations))
                .build()
                .toString();
        mDeleteUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_delete_conversation))
                .build()
                .toString();
        getConversationsList();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnConversationViewInteractionListener) {
            mListener = (OnConversationViewInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = null;

    }


    private void getConversationsList() {

        JSONObject messageJson = new JSONObject();

        try {
            messageJson.put(getString(R.string.keys_json_username), mUsername);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new SendPostAsyncTask.Builder(mSendUrl, messageJson)
                .onPostExecute(this::createConversationsList)
                .onCancelled(this::handleError)
                .build().execute();

    }

    private void handleError(final String msg) {
        Log.e("Conversation ERROR!!!", msg.toString());
    }

    private void createConversationsList(final String result) {
        try {
            JSONObject res = new JSONObject(result);
            if(res.get(getString(R.string.keys_json_success)).toString()
                    .equals(getString(R.string.keys_json_success_value_true))) {

                ArrayList<Conversation> conversations = new ArrayList<>();

                if(res.has(getString(R.string.keys_json_chats))){
                    JSONArray chats = res.getJSONArray(getString(R.string.keys_json_chats));

                    for (int i = 0; i < chats.length(); i++) {
                        JSONObject chat = chats.getJSONObject(i);
                        int chatId = chat.getInt(getString(R.string.keys_json_chatid));
                        conversations.add(new Conversation(chatId,null));
                    }
                    //Update the recycler view
                    mDataset.addAll(conversations);
                    mAdapter.notifyItemRangeInserted(0,mDataset.size() - 1);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConversationDeleted(int conversationID, int position) {
        mDeletePosition = position;
        JSONObject messageJson = new JSONObject();

        try {
            messageJson.put(getString(R.string.keys_json_username), mUsername);
            messageJson.put(getString(R.string.keys_json_chat_id), conversationID);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new SendPostAsyncTask.Builder(mDeleteUrl, messageJson)
                .onPostExecute(this::deleteConversation)
                .onCancelled(this::handleError)
                .build().execute();

    }

    private void deleteConversation(final String result) {
        try {
            JSONObject res = new JSONObject(result);
            if(res.get(getString(R.string.keys_json_success)).toString()
                    .equals(getString(R.string.keys_json_success_value_true))) {

                mDataset.remove(mDeletePosition);
                mAdapter.notifyItemRemoved(mDeletePosition);
                mAdapter.notifyItemRangeChanged(mDeletePosition, mDataset.size() - mDeletePosition);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface OnConversationViewInteractionListener {
        void onConversationSelected(int conversationID);
    }

}
