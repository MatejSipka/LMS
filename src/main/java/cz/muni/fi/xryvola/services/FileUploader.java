package cz.muni.fi.xryvola.services;

import com.vaadin.server.FileResource;
import com.vaadin.ui.*;
import cz.muni.fi.xryvola.MyVaadinUI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by adam on 29.11.14.
 */
public class FileUploader extends CustomComponent implements Upload.Receiver, Upload.FailedListener, Upload.SucceededListener {

    VerticalLayout root;         // Root element for contained components.
    File  file;         // File to write to.

    public FileUploader() {
        root = new VerticalLayout();
        setCompositionRoot(root);

        // Create the Upload component.
        final Upload upload =
                new Upload("Vyberte profilový obrázek", this);

        // Use a custom button caption instead of plain "Upload".
        upload.setButtonCaption("Nahrát na server");

        // Listen for events regarding the success of upload.
        upload.addListener((Upload.SucceededListener) this);
        upload.addListener((Upload.FailedListener) this);

        root.addComponent(upload);
    }

    // Callback method to begin receiving the upload.
    public OutputStream receiveUpload(String filename,
                                      String MIMEType) {
        FileOutputStream fos = null; // Output stream to write to
        file = new File(MyVaadinUI.MYFILEPATH + "images/"+ ((MyVaadinUI)UI.getCurrent()).getCurrentUser().getUsername() +".png");
        try {
            // Open the file for writing.
            fos = new FileOutputStream(file);
        } catch (final java.io.FileNotFoundException e) {
            // Error while opening the file. Not reported here.
            e.printStackTrace();
            return null;
        }

        return fos; // Return the output stream to write to
    }

    // This is called if the upload is finished.
    public void uploadSucceeded(Upload.SucceededEvent event) {
        String cmd = "convert " + MyVaadinUI.MYFILEPATH +  "images/"+ ((MyVaadinUI)UI.getCurrent()).getCurrentUser().getUsername() +".png -resize 100x100 "+ MyVaadinUI.MYFILEPATH + "images/" + ((MyVaadinUI)UI.getCurrent()).getCurrentUser().getUsername() + ".png";
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            process.waitFor();

            Image he = new Image();
            Image she = new Image();
            Image own = new Image();

            he.setSource(new FileResource(new File(MyVaadinUI.MYFILEPATH + "images/he.jpg")));
            she.setSource(new FileResource(new File(MyVaadinUI.MYFILEPATH + "images/she.jpg")));
            own.setSource(new FileResource(new File(MyVaadinUI.MYFILEPATH + "images/" + ((MyVaadinUI)UI.getCurrent()).getCurrentUser().getUsername() + ".png")));

            HorizontalLayout images = new HorizontalLayout();
            images.addComponent(he);
            images.addComponent(she);
            images.addComponent(own);
            root.addComponent(images);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // This is called if the upload fails.
    public void uploadFailed(Upload.FailedEvent event) {
        // Log the failure on screen.
        root.addComponent(new Label("Uploading "
                + event.getFilename() + " of type '"
                + event.getMIMEType() + "' failed."));
    }

    public void copyProfilePic(String username){
        String cmd = "cp " + MyVaadinUI.MYFILEPATH + "images/user-default.png " + MyVaadinUI.MYFILEPATH + "images/" + username + ".png";
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
