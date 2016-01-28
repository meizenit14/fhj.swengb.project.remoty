# fhj.swengb.project.remoty

###Final Project in SWENGB which represents a (Remote)FileExplorer written in Scala and JavaFX.
The members of the developer team are:
- Bajric Amar
- Lagger Christian
- Meizenitsch Georg


![Screenshot](remoty-picture.png)

##Documentation

###Functions
* TreeView
* Root can be set with a DirectoryChooser
* Icons are set for the most important file-types in the TreeCell
* Refresh button which refreshes the whole content of the TreeView
* Show attributes of files (Last modified, size, creation time)
* TextView for plain text files
* ImageView for all common image file-types
* ContextMenu for a directory:
    - Expand
    - Expand All
    - Rename
    - Add directory
    - Delete
* ContextMenu for files:
    - Open (with the default programme set)
    - Rename
    - Delete
* Drag & Drop for moving files
* Music player for music files


###Development
- We first tried to figure out how to use a simple TreeView in order learn more about the TreeView in JavaFX.
- In the next step we tried to figure out how to use the java.nio.File library and to create a TreeView which shows the content of our System drive.
- In the last few steps we started to implement features like Deleting, Adding Directories, Renaming TreeItems. This can be done easily thanks to the implemented functions of the java.nio.File library and the TreeCell functions.
- At the end we also implemented minor features like images for the TreeCells and also a TextView for simple text files or an ImageView for image files.
