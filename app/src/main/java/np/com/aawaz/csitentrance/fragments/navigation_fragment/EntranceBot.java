package np.com.aawaz.csitentrance.fragments.navigation_fragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.adapters.BotAdapter;
import np.com.aawaz.csitentrance.objects.Message;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static bolts.Task.delay;

public class EntranceBot extends Fragment {
    RecyclerView recyclerView;
    View intro;
    BotAdapter adapter;
    private LinearLayout chatContainer;
    OkHttpClient client;

    public EntranceBot() {
        // Required empty public constructor
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        client = new OkHttpClient();
        recyclerView = view.findViewById(R.id.recyclerViewBot);
        chatContainer = view.findViewById(R.id.botContainer);
        intro = view.findViewById(R.id.intro_view);
        final EditText messageEditText = view.findViewById(R.id.message_edit);
        view.findViewById(R.id.message_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = messageEditText.getText().toString();
                if (message.length() != 0) {
                    adapter.add(new Message(message, 0));
                    messageEditText.setText("");
                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                    sendToAPI(message);
                }
            }
        });
        view.findViewById(R.id.startMessaging).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatContainer.setVisibility(View.VISIBLE);
                intro.setVisibility(View.GONE);
                adapter.add(new Message("Hello", 0));
                sendToAPI("Hello");
            }
        });
    }

    private void sendToAPI(String message) {
        String url = "https://api.dialogflow.com/v1/query?v=20150910&lang=en&query=" + message + "&sessionId=12345&timezone=America/New_York";
        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer 27067ba8b5a64ac3ab6ed72f1aabd3d3")
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {

            Handler mainHandler = new Handler(getContext().getMainLooper());

            @Override
            public void onFailure(Call call, IOException e) {
                delay(100);
                adapter.add(new Message("Humm... It seems there no internet connection.", 1));
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            }

            @Override
            public void onResponse(Call call, final Response response) {
                if (response.isSuccessful()) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                adapter.add(new Message(getMessageFromString(response.body().string()), 1));
                                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }
            }
        });
    }

    private String getMessageFromString(String jsonData) {
        String parsedMessage;
        try {
            parsedMessage = new JSONObject(jsonData).getJSONObject("result").getJSONObject("fulfillment").getString("speech");
        } catch (JSONException e) {
            e.printStackTrace();
            parsedMessage = "Something is going fishy inside. Try again later.";
        }
        return parsedMessage;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new BotAdapter(getContext());
        recyclerView.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_entrance_bot, container, false);
    }
}
