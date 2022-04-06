package com.atlanticcity.app.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.gdata.data.Link;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.data.contacts.GroupMembershipInfo;
import com.google.gdata.data.extensions.Email;
import com.google.gdata.data.extensions.ExtendedProperty;
import com.google.gdata.data.extensions.Im;
import com.google.gdata.data.extensions.Name;
import com.google.gdata.util.ServiceException;
import com.atlanticcity.app.R;
import com.atlanticcity.app.Utils.ContactsService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class GoogleContacts extends AppCompatActivity {
    private GoogleSignInClient mGoogleSignInClient;
    public String TAG = "GoogleContacts";
    String email,token;
    private static final int RC_SIGN_IN = 9001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_contacts);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope("https://www.google.com/m8/feeds/"))
                .requestScopes(new Scope("https://www.googleapis.com/auth/contacts.readonly"))
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            Toast.makeText(this, account.getEmail(), Toast.LENGTH_SHORT).show();
            email = account.getEmail();
            token = account.getIdToken();

            ContactsService contactsService = new ContactsService("Atlantic City");
            printAllContacts(contactsService);

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
         //   updateUI(null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    public static void printAllContacts(ContactsService myService)
            throws ServiceException, IOException {
        // Request the feed
    //   URL feedUrl = new URL("https://www.google.com/m8/feeds/contacts/default/full");
        //

        try {

             new DownloadFilesTask().execute(myService);

        }catch (Exception ex){
            ex.printStackTrace();
        }

        // Print the results

    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleSignInClient.signOut();
    }


    private static class DownloadFilesTask extends AsyncTask<ContactsService, Integer, Long> {
        protected Long doInBackground(ContactsService... abc) {
            ContactsService contactsService = new ContactsService("Atlantic City");
            int count = abc.length;
            long totalSize = 0;

            try {
                URL  feedUrl = new URL("https://www.google.com/m8/feeds/contacts/default/full");


                ContactFeed resultFeed = contactsService.getFeed(feedUrl, ContactFeed.class);
                System.out.println(resultFeed.getTitle().getPlainText());
                for (ContactEntry entry : resultFeed.getEntries()) {
                    if (entry.hasName()) {
                        Name name = entry.getName();
                        if (name.hasFullName()) {
                            String fullNameToDisplay = name.getFullName().getValue();
                            if (name.getFullName().hasYomi()) {
                                fullNameToDisplay += " (" + name.getFullName().getYomi() + ")";
                            }
                            System.out.println("\t\t" + fullNameToDisplay);
                        } else {
                            System.out.println("\t\t (no full name found)");
                        }
                        if (name.hasNamePrefix()) {
                            System.out.println("\t\t" + name.getNamePrefix().getValue());
                        } else {
                            System.out.println("\t\t (no name prefix found)");
                        }
                        if (name.hasGivenName()) {
                            String givenNameToDisplay = name.getGivenName().getValue();
                            if (name.getGivenName().hasYomi()) {
                                givenNameToDisplay += " (" + name.getGivenName().getYomi() + ")";
                            }
                            System.out.println("\t\t" + givenNameToDisplay);
                        } else {
                            System.out.println("\t\t (no given name found)");
                        }
                        if (name.hasAdditionalName()) {
                            String additionalNameToDisplay = name.getAdditionalName().getValue();
                            if (name.getAdditionalName().hasYomi()) {
                                additionalNameToDisplay += " (" + name.getAdditionalName().getYomi() + ")";
                            }
                            System.out.println("\t\t" + additionalNameToDisplay);
                        } else {
                            System.out.println("\t\t (no additional name found)");
                        }
                        if (name.hasFamilyName()) {
                            String familyNameToDisplay = name.getFamilyName().getValue();
                            if (name.getFamilyName().hasYomi()) {
                                familyNameToDisplay += " (" + name.getFamilyName().getYomi() + ")";
                            }
                            System.out.println("\t\t" + familyNameToDisplay);
                        } else {
                            System.out.println("\t\t (no family name found)");
                        }
                        if (name.hasNameSuffix()) {
                            System.out.println("\t\t" + name.getNameSuffix().getValue());
                        } else {
                            System.out.println("\t\t (no name suffix found)");
                        }
                    } else {
                        System.out.println("\t (no name found)");
                    }
                    System.out.println("Email addresses:");
                    for (Email email : entry.getEmailAddresses()) {
                        System.out.print(" " + email.getAddress());
                        if (email.getRel() != null) {
                            System.out.print(" rel:" + email.getRel());
                        }
                        if (email.getLabel() != null) {
                            System.out.print(" label:" + email.getLabel());
                        }
                        if (email.getPrimary()) {
                            System.out.print(" (primary) ");
                        }
                        System.out.print("\n");
                    }
                    System.out.println("IM addresses:");
                    for (Im im : entry.getImAddresses()) {
                        System.out.print(" " + im.getAddress());
                        if (im.getLabel() != null) {
                            System.out.print(" label:" + im.getLabel());
                        }
                        if (im.getRel() != null) {
                            System.out.print(" rel:" + im.getRel());
                        }
                        if (im.getProtocol() != null) {
                            System.out.print(" protocol:" + im.getProtocol());
                        }
                        if (im.getPrimary()) {
                            System.out.print(" (primary) ");
                        }
                        System.out.print("\n");
                    }
                    System.out.println("Groups:");
                    for (GroupMembershipInfo group : entry.getGroupMembershipInfos()) {
                        String groupHref = group.getHref();
                        System.out.println("  Id: " + groupHref);
                    }
                    System.out.println("Extended Properties:");
                    for (ExtendedProperty property : entry.getExtendedProperties()) {
                        if (property.getValue() != null) {
                            System.out.println("  " + property.getName() + "(value) = " +
                                    property.getValue());
                        } else if (property.getXmlBlob() != null) {
                            System.out.println("  " + property.getName() + "(xmlBlob)= " +
                                    property.getXmlBlob().getBlob());
                        }
                    }
                    Link photoLink = entry.getContactPhotoLink();
                    String photoLinkHref = photoLink.getHref();
                    System.out.println("Photo Link: " + photoLinkHref);
                    if (photoLink.getEtag() != null) {
                        System.out.println("Contact Photo's ETag: " + photoLink.getEtag());
                    }
                    System.out.println("Contact's ETag: " + entry.getEtag());
                }




            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ServiceException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return totalSize;
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(Long result) {

        }
    }




}
