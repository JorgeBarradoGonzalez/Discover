README FILE IS A WORK IN PROGRESS
# Discover

My final year project when studying app development. Discover is a social platform where you can share and discover new artists. For the music aspect of it, Discover uses the Youtube, Spotify and LastFM API. The app is built with the following main technologies:

* Java 8+
* Springboot
* Hibernate
* Maven
* MySQL
* Tomcat
* 20+ External libraries
    
### Discover Logo

<img src="https://github.com/JorgeBarradoGonzalez/Discover/blob/images/0_SiluetaDiscover.png" width="300">

Being Discover a social network, it has some of the main components we all know:

* User Profiles
* Posts
* Likes
* Comments
* Notifications
* Follow/Unfollow Users

<br>

### Login/SignIn 

<img src="https://github.com/JorgeBarradoGonzalez/Discover/blob/images/2_Login.PNG" width="300">

This is a standard LogIn/SignIn Activity. However, I would like to mention that the HTTP requests are achieved using the HTTP Volley. <a>https://developer.android.com/training/volley</a>

<br>

### MainActivity no info

<img src="https://github.com/JorgeBarradoGonzalez/Discover/blob/images/3_EmptyMainActivity.PNG" width="300">

This is Discover's Main Activity. It is empty because the user has not posted anything yet and neither are they following another user who has. If one of these was the opposite, the screen would show the corresponding "Shares"

<br>

### MainActivity with info

<img src="https://github.com/JorgeBarradoGonzalez/Discover/blob/images/4_MainFilled.PNG" width="300">

This is Discover's Main Activity but with posts shared by users. These posts  are called "Shares" and they contain the information of the post made by one user. 

The lists are built using RecyclerView <a>https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView</a>

<br>

### 'Shares'

<img src="https://github.com/JorgeBarradoGonzalez/Discover/blob/images/5_LikeRICKY.PNG" width="300">

"Shares" are the container that holds all the information about a user's post. These contained elements are:

* Artist and song title
* Video connected to the song
* Name of the user who posted it
* Comment the user wanted to attach to the post
* 'See Artist' Button
* 'Like Share' Button
* 'See comments' Button

A couple of annotation regarding Shares:

1. The video contained in the 'Share' card, is playable thanks to the library android-youtube-player of the user PierfrancescoSoffritti <a>https://github.com/PierfrancescoSoffritti/android-youtube-player</a> The insertion of the video was intended also with Youtube API but it was not possible. I looked for alternatives and this was the best I found. I completely recommend the library. It is flexible so you can modify it if you so need.

2. Shares information is filled in the RecyclerView process. Several threads are called for this purpouse, including if the Share the user is seeing is already liked by them. Depending on the result, the like button shows a different color.

<br>

### 'See Artist' Button

<img src="https://github.com/JorgeBarradoGonzalez/Discover/blob/images/6_Spotify.jpg" width="300">

Discover uses the Spotify API. If the user clicks on the 'See Artist' icon attached to every Share, Discover will communicate with the Spotify app if the user has it installed on their device. If that is the case, the activity above will pop-up, so it can register your authorization for modifying your Spotify account.

This modication exists because from Discover you can follow or unfollow an artist on Spotify. Discover also uses the Spotify API to manage some of the artist information displayed on the screen, like the artist image. You can see the artist profile in the next screenshot.

<br>

### Artist Profile\Bio

<img src="https://github.com/JorgeBarradoGonzalez/Discover/blob/images/7_DenzelProfile.PNG" width="300">

The artist's bio is shown using LastFM API <a>https://www.last.fm/api/</a> and parsing the received info. To clarify, the Artist Profile does not need an Spotify account to access the artist profile, but if that is absent,the follow button and the artist's profile image will not be shown.

### Artist Profile\Music

<img src="https://github.com/JorgeBarradoGonzalez/Discover/blob/images/8_DenzelMoreVideos.PNG" width="300">

The artist's bio is selected by default. If you select Music, the inferior panel will change the bio information and will show popular music of the artist, according to the Youtube Search Result.

<br>

### Sharing content

<img src="https://github.com/JorgeBarradoGonzalez/Discover/blob/images/10_ShareDenzel.PNG" width="300">

The button placed on the inferior right corner of the main activity is the "Share Button". If pressed, it will open a new activity with a search bar. This searched bar acts a a Youtube Search bar. You can input and search whatever the user feels like, but only videdos following the format 'Artist/s - Song Title' will be accepted to be shared and sent to the server.

Users can attach a comment to the post. I actually have to change the color palette on the comment input dialog.

<br>

### Comments

<img src="https://github.com/JorgeBarradoGonzalez/Discover/blob/images/20_CommentsPanel.png" width="600">

If the 'See Comments' button is pressed, the Comments Activity will open. With the post info, every view will be inflated and filled with the corresponding information. Again, if the post is liked by the user, this is translated to the interface. This activity is a standard comment activity.


<br>

### Main Activity Actions

<img src="https://github.com/JorgeBarradoGonzalez/Discover/blob/images/14_Opciones.PNG" width="300">

There are activities non related to posts. These are:

* Find User
* Notifications
* Profile
* End Session (Action)

<br>

### Search users

<img src="https://github.com/JorgeBarradoGonzalez/Discover/blob/images/21_SearchUserPanel.png" width="300">

This activity has a search bar that will look for users registered in the Discover app.

* Find User
* Notifications
* Profile
* End Session (Action)

<br>

### User Profile

<img src="https://github.com/JorgeBarradoGonzalez/Discover/blob/images/22_UserProfilePanel.png" width="600">

This activity shows information about a user: Posts, Likes and Users Followed. All the information here is of course interactive and uploadable to the server. For example: If a user likes one of the posts the profile shows, the given like is uploaded in the server and the 'Like Icon' will be updated.
