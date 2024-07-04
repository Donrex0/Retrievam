package com.example.retrievam;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class newPostFragment extends Fragment {

    DatabaseReference userId;
    private final int GALLERY_REQ_CODE=1;
    ImageView ivItemPic;
    Button btnGallery, btnPost;
    TextInputEditText etItemName, etDescription,etLocation,etMessege;
    RadioButton rbLost,rbFound;
    private Uri imageUri;
    StorageReference storageRef;
    ProgressBar progressBarNewPost;

    String status;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_post, container, false);

        init(rootView);

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,GALLERY_REQ_CODE);
            }
        });

        rbLost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = "LOST";
            }
        });

        rbFound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = "FOUND";
            }
        });


        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });


        return rootView;
    }
    protected void init(View rootView)
    {
        ivItemPic=rootView.findViewById(R.id.ivItemPic);
        btnGallery=rootView.findViewById(R.id.btnGallery);
        etItemName=rootView.findViewById(R.id.etItemName);
        etDescription=rootView.findViewById(R.id.etDescription);
        etLocation=rootView.findViewById(R.id.etLocation);
        etMessege=rootView.findViewById(R.id.etPostMessege);
        btnPost=rootView.findViewById(R.id.btnPost);
        rbLost=rootView.findViewById(R.id.rbLost);
        rbFound=rootView.findViewById(R.id.rbFound);
        progressBarNewPost=rootView.findViewById(R.id.progressBarNewPost);

        storageRef = FirebaseStorage.getInstance().getReference();


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQ_CODE && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            ivItemPic.setImageURI(data.getData());
        }
    }

    void getData()
    {

        progressBarNewPost.setVisibility(View.VISIBLE);

        etItemName.setEnabled(false);
        etDescription.setEnabled(false);
        etLocation.setEnabled(false);
        etMessege.setEnabled(false);
        btnPost.setEnabled(false);
        btnGallery.setEnabled(false);

        String itemName= Objects.requireNonNull(etItemName.getText()).toString().trim();
        String description= Objects.requireNonNull(etDescription.getText()).toString();
        String location= Objects.requireNonNull(etLocation.getText()).toString();
        String messege= Objects.requireNonNull(etMessege.getText()).toString();



        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            userId = FirebaseDatabase.getInstance().getReference().child("User").child(currentUser.getUid());
            userId.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String fullName = snapshot.child("fullName").getValue(String.class);
                        String profileUrl = snapshot.child("profilePicUrl").getValue(String.class);
                        String id = snapshot.child("id").getValue(String.class);


                        if (imageUri != null) {
                            // Upload image to Firebase Storage
                            StorageReference fileReference = storageRef.child("ItemPost_pictures/" + System.currentTimeMillis()
                                    + "." + getFileExtension(imageUri));
                            fileReference.putFile(imageUri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Task<Uri> downloadUriTask = taskSnapshot.getStorage().getDownloadUrl();
                                        downloadUriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri downloadUri) {

                                                String postUrl = downloadUri.toString();
                                                DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference().child("Posts").push();
                                                modelPosts newPost = new modelPosts(itemName, fullName, description, messege, status, location,profileUrl,id,postUrl);

                                                postsRef.setValue(newPost).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            progressBarNewPost.setVisibility(View.GONE);

                                                            etItemName.setEnabled(true);
                                                            etDescription.setEnabled(true);
                                                            etLocation.setEnabled(true);
                                                            etMessege.setEnabled(true);
                                                            btnPost.setEnabled(true);
                                                            btnGallery.setEnabled(true);
                                                            Toast.makeText(getActivity(), "Post added successfully", Toast.LENGTH_SHORT).show();

                                                            etItemName.setText("");
                                                            etDescription.setText("");
                                                            etLocation.setText("");
                                                            etMessege.setText("");

                                                            ivItemPic.setImageResource(R.drawable.profile);


                                                            //to be fixed

//                                                            FragmentManager fragmentManager = getSupportFragmentManager();
//                                                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//                                                            Fragment newFragment = new NewFragment();
//                                                            fragmentTransaction.replace(R.id.fragment_container, newFragment);
//                                                            fragmentTransaction.addToBackStack(null); // Optional: add transaction to back stack
//                                                            fragmentTransaction.commit();


                                                        } else {
                                                            Toast.makeText(getActivity(), "Failed to add post", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                            }
                                        });
                                    }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Handle unsuccessful image upload
                                        }
                                    });

                        }
                    }

                    else {
                        Toast.makeText(getActivity(), "User data not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(), "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private String getFileExtension(Uri uri) {
        if (getContext() != null) {
            ContentResolver contentResolver = getContext().getContentResolver();
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
        } else {
            // Handle the case where getContext() returns null
            return null; // or throw an exception, depending on your requirements
        }

    }

}