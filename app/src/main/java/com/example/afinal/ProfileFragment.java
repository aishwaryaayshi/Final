package com.example.afinal;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.security.Key;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.storage.FirebaseStorage.getInstance;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    //storage
    StorageReference storageReference;
    //path where images of user profile and cover will be stored
    String storagePath="Users_Profile_Cover_Imgs/";

    //views from xml
    ImageView avatarIv,coverIv;
    TextView nameTv,emailTv,phoneTv;
    FloatingActionButton fab;
    //progress dialog

    ProgressDialog pd;

    //permission constants
    private static final int CAMERA_REQUEST_CODE=100;
    private static final int STORAGE_REQUEST_CODE=200;
    private static final int IMAGE_PICK_GALLERY_CODE=300;
    private static final int IMAGE_PICK_CAMERA_CODE=400;

    //ARRAYS OF PERMISSIONS TO BE REQESTED
    String cameraPermissions[];
    String storagePermissions[];
    //uri of picked image
    Uri image_uri;
//for checking profile or cover photo
    String profileOrCoverPhoto;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_profile, container, false);
      firebaseAuth=FirebaseAuth.getInstance();
      user=firebaseAuth.getCurrentUser();
      firebaseDatabase=FirebaseDatabase.getInstance();
      databaseReference=firebaseDatabase.getReference("Users");
      storageReference=getInstance().getReference();


      //init arrays of permissions
        cameraPermissions=new String[]{Manifest.permission.CAMERA ,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        //init views
        avatarIv=view.findViewById(R.id.avatarIv);
       coverIv=view.findViewById(R.id.coverIv);
        nameTv=view.findViewById(R.id.nameTv);
        emailTv=view.findViewById(R.id.emailTv);
       phoneTv=view.findViewById(R.id.phoneTv);
       fab=view.findViewById(R.id.fab);

       //init progress dialog
        pd=new ProgressDialog(getActivity());

        Query query=databaseReference.orderByChild("email").equalTo(user.getEmail());
         query.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//CHECK UNTIL REQUIRED DATA
                 for (DataSnapshot ds:dataSnapshot.getChildren())
                 {
                     String name=""+ds.child("name").getValue();
                     String email=""+ds.child("email").getValue();
                     String phone=""+ds.child("phone").getValue();
                     String image=""+ds.child("image").getValue();
                     String cover=""+ds.child("cover").getValue();
                //set data
                     nameTv.setText(name);
                     emailTv.setText(email);
                     phoneTv.setText(phone);
                     try {
                         Picasso.get().load(image).into(avatarIv);
                     }
                     catch (Exception e){
                         Picasso.get().load(R.drawable.ic_default_img_white).into(avatarIv);

                     }
                     try {
                         Picasso.get().load(cover).into(coverIv);
                     }
                     catch (Exception e){


                     }
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });

         //fab button clock
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });

        return view;

    }

    private boolean checkStoragePermission(){
        boolean result= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==(PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private  void  requestStoragePermission(){
        requestPermissions(storagePermissions,STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){

        boolean result= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA)
                ==(PackageManager.PERMISSION_GRANTED);

        boolean result1= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==(PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    private  void  requestCameraPermission(){
        requestPermissions(cameraPermissions,CAMERA_REQUEST_CODE);
    }

    private void showEditProfileDialog() {
        String options[]={"Edit Profile Picture","Edit Cover Photo","Edit Name","Edit Phone"};
        //alart dialog
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        //set title
        builder.setTitle("Choose Action");
        //set item to dialog
        builder.setItems(options,  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//handle dialog item clicks
                if (which==0){
                  //edit profile clicked
                    pd.setMessage("Updating Profile Picture");
                   profileOrCoverPhoto="image";
                    showImagePicDialog();
                }
                else if (which==1){
                    //edit cover clicked
                    pd.setMessage("Updating Cover Picture");
                profileOrCoverPhoto="cover";
                showImagePicDialog();
                }
                else if (which==2){
                    //edit name clicked
                    pd.setMessage("Updating Name ");
                    showNamePhoneUpdateDialog("name");
                }
                else if (which==3){
                    //edit phone clicked
                    pd.setMessage("Updating Phone");
                    showNamePhoneUpdateDialog("phone");
                }
            }
        });
        //create and show dialog
        builder.create().show();

    }

    private void showNamePhoneUpdateDialog(final String key) {
//customer dialog
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("Update "+ key);
        //set layout dialog
        LinearLayout linearLayout=new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10,10,10,10);
        //ADD EDIT TEXT
        final EditText editText=new EditText(getActivity());
        editText.setHint("enter"+key);
        linearLayout.addView(editText);

        builder.setView(linearLayout);

        //add button in dialog TO UPADE
        builder.setPositiveButton("Upade", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//INPUT TEXT FROM EDIT TEXT
                String value=editText.getText().toString().trim();
                if (!TextUtils.isEmpty(value)){
                    pd.show();
                    HashMap<String,Object> result=new HashMap<>();
                    result.put(key,value);

                    databaseReference.child(user.getUid()).updateChildren(result)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                  pd.dismiss();
                                  Toast.makeText(getActivity(),"Updated....",Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    pd.dismiss();
                                    Toast.makeText(getActivity(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else {
                    Toast.makeText(getActivity(),"Please enter"+key,Toast.LENGTH_SHORT).show();

                }
            }
        });
        //add button in dialog TO cancel
        builder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        //create ans show dialog
        builder.create().show();
    }

    private void showImagePicDialog() {
        String options[]={"Camera","Gallery"};
        //alart dialog
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        //set title
        builder.setTitle("Pick Image From");
        //set item to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//handle dialog item clicks
                if (which==0){
                    //camera clicked

                  if (!checkCameraPermission()){
                      requestCameraPermission();
                  }
                  else {
                      pickFromCamera();
                  }

                }
                else if (which==1){
                    //gallery clicked

                    if (!checkStoragePermission()){
                        requestStoragePermission();
                    }
                    else {
                        pickFromGallery();
                    }

                }

            }
        });
        //create and show dialog
        builder.create().show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

       switch (requestCode)
       {
           case CAMERA_REQUEST_CODE:{
               if (grantResults.length>0){
                   boolean cameraAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                   boolean writeStorageAccepted=grantResults[1]==PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted){
                        pickFromCamera();
                    }
                    else {
                        Toast.makeText(getActivity(),"Please enable camera & storage permission",Toast.LENGTH_SHORT).show();
                    }
               }

           }
           break;
           case STORAGE_REQUEST_CODE:{

               if (grantResults.length>0){
                   boolean writeStorageAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                   if ( writeStorageAccepted){
                       pickFromGallery();
                   }
                   else {
                       Toast.makeText(getActivity(),"Please enable storage permission",Toast.LENGTH_SHORT).show();
                   }
               }

           }
           break;
       }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode==RESULT_OK){

            if(requestCode==IMAGE_PICK_GALLERY_CODE){

                image_uri=data.getData();

                uploadProfileCoverPhoto(image_uri);
            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE){

                uploadProfileCoverPhoto(image_uri);

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfileCoverPhoto(Uri uri) {
        //show process
        pd.show();
        //path and name of image to be stored in firebase storage
        String filePathAndName=storagePath+ ""+ profileOrCoverPhoto + "_"+ user.getUid();
 StorageReference storageReference2nd=storageReference.child(filePathAndName);
 storageReference2nd.putFile(uri)
         .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
             @Override
             public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                 Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                 while (!uriTask.isSuccessful());
                 Uri downloadUri=uriTask.getResult();
                 //check if image is uploaded or not url is received
                 if (uriTask.isSuccessful()){
                     //image uploaded
                     //ADD/UPDATE uri in users's database
                     HashMap<String,Object>results=new HashMap<>();
                     results.put(profileOrCoverPhoto,downloadUri.toString());
                 databaseReference.child(user.getUid()).updateChildren(results)
                         .addOnSuccessListener(new OnSuccessListener<Void>() {
                             @Override
                             public void onSuccess(Void aVoid) {


                                 pd.dismiss();
                                 Toast.makeText(getActivity(),"Image Updated....",Toast.LENGTH_SHORT).show();
                             }
                         }).addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {

                         pd.dismiss();
                         Toast.makeText(getActivity(),"Error  Updating Image....",Toast.LENGTH_SHORT).show();
                     }
                 });
                 }
                 else {
                     //error
                     pd.dismiss();
                     Toast.makeText(getActivity(),"Some error occured",Toast.LENGTH_SHORT).show();
                 }
             }
         }).addOnFailureListener(new OnFailureListener() {
     @Override
     public void onFailure(@NonNull Exception e) {

         pd.dismiss();
         Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
     }
 });

    }

    private void pickFromCamera() {

        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Temp Description");
        //put image uri
        image_uri=getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        //intent to start camera
        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_CODE);


    }
    private void pickFromGallery() {

        Intent galleryIntent=new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,IMAGE_PICK_GALLERY_CODE);
    }

    private  void checkUserStatus(){
        //get current user
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if (user !=null)
        {

        }
        else {
            startActivity(new Intent(getActivity(),MainActivity.class));
            getActivity().finish();
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);//to show menu optoin in fragment
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main,menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();
        if (id==R.id.action_logout)
        {
            firebaseAuth.signOut();
            checkUserStatus();
        }
        if (id==R.id.action_add_post)
        {
            startActivity(new Intent(getActivity(),AddPostActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
