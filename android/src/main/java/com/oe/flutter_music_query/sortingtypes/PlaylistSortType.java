package com.oe.flutter_music_query.sortingtypes;

public enum PlaylistSortType {

    /**
     * The Default sort order for playlist. DEFAULT value the playlist
     * query will return playlists sorted by alphabetically
     */
    DEFAULT,

    /**
     * The most recent playlists will come first in playlist queries.
     *
     */
    NEWEST_FIRST,
    /**
     * The most old playlists will come first in playlist queries.
     */
    OLDEST_FIRST
}
