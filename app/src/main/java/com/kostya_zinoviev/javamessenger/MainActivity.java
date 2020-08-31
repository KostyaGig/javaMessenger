package com.kostya_zinoviev.javamessenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int MAX_lENGTH = 100;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    private ArrayList<String> messages;

    private Button sendMessageB;
    private EditText messageInput;

    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DataAdapter adapter = new DataAdapter(this,messages);
        recyclerView.setAdapter(adapter);
        //Уведомляем адаптер,о том что он должен быть обновлен

        adapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(messages.size());



        sendMessageB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageInput.getText().toString().trim();
                if (TextUtils.isEmpty(message) && message.equals("")){
                    Toast.makeText(MainActivity.this, "Напиши сообщение!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (message.length() > MAX_lENGTH){
                    Toast.makeText(MainActivity.this, "Слишком большое сообщение!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Если все проверки проходят,то...
                //reference.setValue(Object) - с помощью этого метода мы мы заменяем предыдущее сообщение

                //Чтобы мы не заменяли предыдущее сообщение есть метод push(),все сообщения будут выводится
                //Столбиком в базе данных

                reference.push().setValue(message);
                //После отправки сообщения очищаем наш EditText
                messageInput.setText("");
            }
        });


        reference.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //Вызывается когда выполняется пуш в нашу коллекцию arrayList - messages
                //Тоесть когда мы добавляем мессэдж в бд ,вызывается этот метод


                //Извлекаем строку из бд,где String.class - вид данных,который хранится вв бд
                //После этого добавляем это сообщение в наш arrayList

                String msg = dataSnapshot.getValue(String.class);
                messages.add(msg);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void init() {
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("messages");

        sendMessageB = findViewById(R.id.sendMessageB);
        messageInput = findViewById(R.id.messageInput);

        messages = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
    }
}
