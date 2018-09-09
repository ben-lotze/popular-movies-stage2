# Uncle Ben's Popular Movies Database (Stage 2)

## To reviewers and potential users
Please add your TMDb API key in gradle.properties: TmdbApiKey="your_api_key"


## Features

#### User Interface
* Choose between most popular and highest rated movies
* Movies are displayed in the main layout via a grid of their corresponding movie poster thumbnails.
* UI contains a screen for displaying the details for a selected movie.
    * contains title, release date, movie poster, vote average, and plot synopsis.
    * section for displaying trailer videos and user reviews.
    * user can tap a button to mark it as a Favorite.

#### Network / Threading / Background Threads
* Queries the /movie/popular or /movie/top_rated API for the sort criteria specified in the settings menu.
* Requests related videos for a selected movie via the /movie/{id}/videos endpoint.
* Requests user reviews for a selected movie via the /movie/{id}/reviews endpoint.


#### Data persistence
* Titles and IDs of the user’s favorite movies are stored in a native SQLite database and are exposed via a ContentProvider.
* Saving instance state on rotation (implemented with ViewModel from android.arch.lifecycle)

#### Additional features (not required)
* Navigation Drawer
* Endless scrolling for movies from web source (automatic loading of more pages on background thread)
* Storing additional information about favorites to allow sorting by title, release date or timestamp when favorite has been added.
    * Re-selecting the same sorting reverses the order.
* Swipe to remove favorite
* Selecting a favorite from Favorites downloads th movie details in a background thread from TMDb's /movie endpoint by id (no movie data that could change is stored in SQLite database for a favorite)
* Loading additional data in background threads for movie details: Youtube trailer thumbnails, additional movie information (imdb id, runtime, budget, genres).
* Settings
    * Language for movie data
    * JSON parsing engine (Google Gson or "bare hands")
* Search movie on other web services (Rottom Tomatoes, Amazon, Google, IMDb, Youtube)




## Screenshots

##### Navigation Drawer & movie grid:
<img src="/screenshots/main_nav_drawer.jpg" width="250">  <img src="/screenshots/main_movies_grid.jpg" width="250">

##### Movie details with trailer previews and search:
<img src="/screenshots/movie_details.jpg" width="250"> <img src="/screenshots/movie_details_trailers_and_search.jpg" width="250"> <img src="/screenshots/movie_details_less_trailers.jpg" width="250"> <img src="/screenshots/movie_details_less_trailers_no_reviews.jpg" width="250">

##### Favorites with sorting and swipe-to-delete functionality:
<img src="/screenshots/favorites_sorting.jpg" width="250"> <img src="/screenshots/favorites_swipe_to_delete.gif" width="250">

##### Settings
<img src="/screenshots/settings.jpg" width="250">