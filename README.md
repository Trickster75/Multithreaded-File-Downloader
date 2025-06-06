# Multithreaded-File-Downloader
This Java program is a **Multithreaded File Downloader with a JavaFX GUI**, designed to allow users to download multiple files from the internet simultaneously through a user-friendly interface. Each file download runs in a separate thread using Java's concurrency framework, ensuring that the UI remains responsive. The application provides real-time updates on download progress, speed, and estimated time remaining using observable properties and progress bars. Users can pause, resume, or cancel downloads, and each download is neatly displayed in its own panel within a scrollable view. The program efficiently handles network and file I/O operations, making it a practical example of combining JavaFX, multithreading, and network programming in a modern desktop application.


To run the java code in VSCode here are Step-by-Step Instructions:


1.Download JavaFX SDK(if not installed) and Update JDK.
(Make sure both have same versions)   

2.Create a new folder:
    Create and select a folder called "MyDownloader".

3.Create a subfolder called src:
    Create a new folder named "src" within "MyDownloader" folder.
    Copy all the folders of "src" folder which is inside "javafx-sdk" folder.

4.Create and paste the Java file:

    Inside subfolder "src" of "Mydownloader", paste the file: "MultithreadedDownloaderFX.java"

5.Create the .vscode subfolder:

    Create a new folder named ".vscode" within MyDownloader folder.
    (Make sure it starts with a dot .)

6.Inside .vscode:

    Paste the launch.json file.
    (Change only the module-path from: "D:/java/openjfx-24.0.1_windows-x64_bin-sdk/javafx-sdk-24.0.1/lib" to your specific path of "lib" folder inside the "javafx-sdk" folder.)

    (E.g.-Suppose your path is :"C:/openjfx-24.0.1_windows-x64_bin-sdk/javafx-sdk-24.0.1/lib"
     So changes will be done as follows -> "--module-path C:/openjfx-24.0.1_windows-x64_bin-sdk/javafx-sdk-24.0.1/lib --add-modules javafx.controls,javafx.fxml")
    
    Paste the settings.json file.
    (Change only the path from: "D:/java/openjfx-24.0.1_windows-x64_bin-sdk/javafx-sdk-24.0.1/lib" to your specific path of "lib" folder inside the "javafx-sdk" folder.)

    (E.g.-Suppose your path is :"C:/openjfx-24.0.1_windows-x64_bin-sdk/javafx-sdk-24.0.1/lib"
     So changes will be done as follows -> "C:/openjfx-24.0.1_windows-x64_bin-sdk/javafx-sdk-24.0.1/lib/**/*.jar")
     
