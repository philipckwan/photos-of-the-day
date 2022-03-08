Photos of the Day
=

Photos-of-the-Day is an application that helps you to show the photos that you have stored in your own photo archive.  
Nowadays, people often take a lot of pictures, they will then just store the photos in some storage (i.e. external harddrive) and forget about it.  
This application promotes the viewing of your photos by helping you to randomly pick photos out of your photo archive.  
A typical use is to then share the chosen photos on Dropbox Photos folder.  

Pre-requisites
-

At this stage, Photos-of-the-Day is a very simple application that randomly choose photos, with a certain assumptions made.  
In order to use this properly and get a good user experience, here I list the pre-requisites for using this application.  
More explanation will be followed to explain the pre-requisites.  

* You have to organize your photo archive in a certain way
* And you should be responsible first to organize your photos in a manner so that it is meaningful for this application to choose photos from
* You need to define the destination of where the chosen photos should go, one recommendation is to share it on Dropbox's Photos folder

Terminology
-

Some terms used in this readme:

source, source directory - the directory of the photo archive, this should point to the root directory where all sub-folders of this directory are the directories of your photos  
destination, destination directory - the directory where the chosen photos will be placed  
input config file - an input file for you to specify configurations when running this application. The filename is "input.txt"  
email template file - a text file that is generated with some information about a run of this application  

How to use this application
-

This application is java based. You will need a jvm to run it.  
At this stage, there is no compiled jar built for this application project yet so you will also need a jdk to compile this application project.

1. Configure the input config file (input.txt) with settings that suits your environment and your preferences. Ensure the source directory is pointing to your photo archive and the destination directory is pointing to an empty directory so that chosen files will be copied there
2. Compile this application project
3. Run the main method of PhotosOfTheDay class
	>java PhotosOfTheDay
4. The destination directory should now contained copies of the chosen photos, the email template file is also generated and you can see some details about this run

Organizing your photo archive
-

This application assumes a 2 level directory structure, for example:  
source directory (root)/  
--L1-A/  
----L2-A-1/  
------(photos)  
----L2-A-2/  
------(photos)  
--L1-B/  
----L2-B-1/  
------(photos)  
----L2-B-2/  
------(photos)  
----L2-B-3/  
------(photos)  
--L1-C/  
----L2-C-1/  
------(photos)  
----L2-C-2/  
------(photos)  

Each run of the application, a folder from the 2nd level will be chosen, in the above example, all folders with name "L2-*" have a fair chance of being chosen.
Then, all photos in that folder will be randomly chosen, up to the number specified in "howManyPhotosToPick" in the input config file.
Then, all photos will be copied to the destination directory, and a email template file will be generated with some information of this run.

Given the above explanation, user should structure their photo archive accordingly.

One example of how to organize the photo archive folders is as follows:
The 1st level folder is the year when the photos are taken
The 2nd level folder is an event in that year where the photos are taken

For example:  
photo_archive_root/  
--2004/  
----20040520-my_college_graduation/  
----20040713-summer_trip_to_Spain/  
----20041128-Thanksgiving_gathering_at_Johns/  
----20041224-chrismas_party_and_roadtrip/  
----20041231-new_year_eve/  
--2005/  
----20050120-chinse_new_year_gathering/  
----20050420-my_birthday_party/  
--2006/  
----20060101-new_year_party/  
----20060310-my_farewell_at_work/  
----20061128-Thanksgiving_party_at_Peters/  
	
Examples of running this application
-

There are 4 modes currently:

* PICK_PHOTOS  
This is the general use case for this application. Pick photos randomly from a photo folder, they are copied to the destination folder, and an email template will be generated
* MOVE_DUPLICATES  
This is a utility function added, it find duplicate photos from the source folder, and move them to the "duplicate" folder.
The limitation of this function is assuming all files in the source folder, but not traversing to subfolders.
As this function make changes to the source folder, by moving files to the "duplicate" folder, use this function with caution.
See the next 2 functions for enhancments
* RECURSIVE_FIND_DUPLICATES  
This is a utility function. Taking the source folder, it will traverse this folder and all subfolders, and find any duplicate photos (or actually any duplicate files), and list them out for user to take action on it.
In other words, this function doesn't make any changes to the source folder, it will mot move or remove any files
* RECURSIVE_MOVE_DUPLICATES  
This is a utitlity function, similar to the RECURSIVE_FIND_DUPLICATES, but, it make changes to the source folder by moving one of the duplicate file to the "duplicate" folder. Therefore, use this function with caution.

Motivation to better organize your photo archive
-

Hope you will find some value in using this Photos-of-the-Day application!  
While I am using it, some motivations arise, I hope you also get some inspirations and motivations

* Motivates me to better organize my photos  
A lot of my recent photos are exported from my iPhone, and they are really not organized.  
They just lumped up into a single folder when I export them to my photos archive.  
If I have to use this application more effectively I really need to organize my photos better, by putting them into events.  
* Motivates me to delete bad pictures  
Since this application chooses photos from a folder randomly, I really need to delete the bad photos because they don't show any value when I share them.  
* Motivates me to take less and better pictures  
Given the motivation above, the better way should be to take less but better quality shots!  
