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


### Login/SignIn 

<img src="https://github.com/JorgeBarradoGonzalez/Discover/blob/images/2_Login.PNG" width="300">

This is a standard LogIn/SignIn Activity. However, I would like to mention that the HTTP requests are achieved using the HTTP Volley. <a>https://developer.android.com/training/volley</a>


### MainActivity no info

<img src="https://github.com/JorgeBarradoGonzalez/Discover/blob/images/3_EmptyMainActivity.PNG" width="300">

This is Discover's Main Activity. It is empty because the user has not posted anything yet and neither are they following another user who has. If one of these was the opposite, the screen would show the corresponding "Shares"


### MainActivity with info

<img src="https://github.com/JorgeBarradoGonzalez/Discover/blob/images/4_MainFilled.PNG" width="300">

This is Discover's Main Activity but with posts shared by users. These posts  are called "Shares" and they contain the information of the post made by one user. 

The lists are built using RecyclerView <a>https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView</a>


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

    1 The video contained in the 'Share' card, is 
