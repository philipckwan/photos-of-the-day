Photos of the Day
=================

Photos-of-the-Day is an application that helps you to show the photos that you have stored in your own photo archive.  
Nowadays, people often take a lot of pictures, they will then just store the photos in some storage (i.e. external harddrive) and forget about it.  
This application promotes the viewing of your photos by helping you to randomly pick photos out of your photo archive.  
A typical use is to then share the chosen photos on Dropbox Photos folder.  

Pre-requisites
-------------

At this stage, Photos-of-the-Day is a very simple application that randomly choose photos, with a certain assumptions made.  
In order to use this properly and get a good user experience, here I list the pre-requisites for using this application.  
More explanation will be followed to explain the pre-requisites.  

* You have to organize your photo archive in a certain way
* And you should be responsible first to organize your photos in a manner so that it is meaningful for this application to choose photos from
* You need to define the destination of where the chosen photos should go, one recommendation is to share it on Dropbox's Photos folder

Terminology
-----------

Some terms used in this readme:

source, source directory - the directory of the photo archive, this should point to the root directory where all sub-folders of this directory are the directories of your photos
destination, destination directory - the directory where the chosen photos will be placed.
input config file - an input file for you to specify configurations when running this application. The filename is "input.txt"
email template file - a text file that is generated with some information about a run of this application

How to use this application
-------------------------------

This application is java based. You will need a jvm to run it.  At this stage, there is no compiled jar built for this application project yet so you will also need a jdk to compile this application project.

1. Configure the input config file (input.txt) with settings that suits your environment and your preferences. Ensure the source directory is pointing to your photo archive and the destination directory is pointing to an empty directory so that chosen files will be copied there
2. Compile this application project
3. Run the main method of PhotosOfTheDay class
4. The destination directory should now contained copies of the chosen photos, the email template file is also generated and you can see some details about this run


