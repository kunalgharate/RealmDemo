package com.example.realmdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.realmdemo.model.Person;

import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    private Realm realm;
    EditText ed_name;
    Button btn_save;
    TextView tv_result;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         realm = Realm.getDefaultInstance(); // opens "myrealm.realm"

        ed_name = findViewById(R.id.ed_name);
        btn_save = findViewById(R.id.btn_save);
        tv_result = findViewById(R.id.tv_result);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ed_name.getText().toString()!=null)
                saveIntoDatabase(ed_name.getText().toString());

                readData();
            }
        });

        }

    private void readData() {

        String output="";
        RealmResults<Person> realmResults =realm.where(Person.class)
                .findAll();

        for (Person person : realmResults)
        {
            output+=person.toString();
        }

        tv_result.setText(output);
    }

    private void saveIntoDatabase(final String nameString) {

          realm.executeTransactionAsync(new Realm.Transaction() {
              @Override
              public void execute(Realm realm) {
                  Person person = realm.createObject(Person.class, System.currentTimeMillis() / 1000);
                  person.setName(nameString);
                 // person.setId((int) );
                 //person.set("Kunal");

              }
          }, new Realm.Transaction.OnSuccess() {
              @Override
              public void onSuccess() {
                  Log.d(TAG, "onSuccess: ");
                  readData();

              }
          }, new Realm.Transaction.OnError() {
              @Override
              public void onError(Throwable error) {
                  Log.d(TAG, "onError: "+error.getMessage());

              }
          });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
