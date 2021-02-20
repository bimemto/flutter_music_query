package com.oe.flutter_music_query.loaders;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oe.flutter_music_query.BuildConfig;
import com.oe.flutter_music_query.loaders.tasks.AbstractLoadTask;
import com.oe.flutter_music_query.sortingtypes.GenreSortType;
import com.oe.flutter_music_query.sortingtypes.SongSortType;

import io.flutter.plugin.common.MethodChannel;

public class GenreLoader extends AbstractLoader {

    private static final String[] GENRE_PROJECTION = {
//            "name",
            MediaStore.Audio.GenresColumns.NAME,
            // MediaStore.Audio.Genres._ID,
            //MediaStore.Audio.GenresColumns.NAME,
    };

    public GenreLoader(Context context) {
        super(context);
    }

    @Override
    protected GenreLoadTask createLoadTask(
            final MethodChannel.Result result, final String selection,
            final String[] selectionArgs, final String sortOrder, final int type) {

        return new GenreLoadTask(result, getContentResolver(),
                selection, selectionArgs, sortOrder);
    }


    /**
     * This method is used to parse GenreSortType object into a string
     * that will be used in SQL to query data in a specific sort mode
     *
     * @param sortType GenreSortType The type of sort desired.
     * @return A String for SQL language query usage.
     */
    private String parseSortOrder(GenreSortType sortType) {
        String sortOrder;

        switch (sortType) {

            default:
            case DEFAULT:
                sortOrder = GENRE_PROJECTION[0] + " ASC";
                //sortOrder = MediaStore.Audio.Genres.DEFAULT_SORT_ORDER;
                break;
        }

        return sortOrder;
    }

    /**
     * This method queries for all genre available on device storage.
     *
     * @param result   MethodChannel.Result object to send reply for dart
     * @param sortType GenreSortType object to define sort type for data queried.
     */
    public void getGenres(final MethodChannel.Result result, final GenreSortType sortType) {
        createLoadTask(result, null, null, parseSortOrder(sortType),
                QUERY_TYPE_DEFAULT).execute();
    }

    /**
     * This method makes a query that search genre by name with
     * nameQuery as query String.
     *
     * @param results    MethodChannel.Result object to send reply for dart
     * @param namedQuery The query param that will match genre name
     * @param sortType   GenreSortType object to define sort type for data queried.
     */
    public void searchGenres(final MethodChannel.Result results, final String namedQuery,
                             final GenreSortType sortType) {

        String[] args = new String[]{namedQuery + "%"};
        createLoadTask(results, GENRE_PROJECTION[0] + " like ?", args,
                parseSortOrder(sortType), QUERY_TYPE_DEFAULT).execute();
    }

    static class GenreLoadTask extends AbstractLoadTask<List<Map<String, Object>>> {

        /**
         * Constructor for AbstractLoadTask.
         *
         * @param selection     SQL selection param. WHERE clauses.
         * @param selectionArgs SQL Where clauses query values.
         * @param sortOrder     Ordering.
         */
        private MethodChannel.Result m_result;
        private ContentResolver m_resolver;

        GenreLoadTask(MethodChannel.Result result, ContentResolver resolver, String selection,
                      String[] selectionArgs, String sortOrder) {
            super(selection, selectionArgs, sortOrder);

            m_resolver = resolver;
            m_result = result;
        }

        @Override
        protected List<Map<String, Object>> loadData(String selection, String[] selectionArgs,
                                                     String sortOrder) {

            List<Map<String, Object>> dataList = new ArrayList<>();
            Cursor genreCursor = null;

            try {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    genreCursor = m_resolver.query(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
                            new String[]{"Distinct " + GENRE_PROJECTION[0]}, selection,
                            selectionArgs, sortOrder);
                    if (genreCursor != null) {
                        while (genreCursor.moveToNext()) {
                            Map<String, Object> data = new HashMap<>();
                            for (String column : genreCursor.getColumnNames()) {
                                String genreName = genreCursor.getString(
                                        genreCursor.getColumnIndex(column));
                                data.put(MediaStore.Audio.GenresColumns.NAME, genreName);
                            }
                            dataList.add(data);
                        }
                        genreCursor.close();
                    }
                } else {
                    genreCursor = m_resolver.query(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
                            new String[]{GENRE_PROJECTION[0]}, selection,
                            selectionArgs, sortOrder);
                    if (genreCursor != null) {
                        while (genreCursor.moveToNext()) {
                            Map<String, Object> data = new HashMap<>();
                            for (String column : genreCursor.getColumnNames()) {
                                String genreName = genreCursor.getString(
                                        genreCursor.getColumnIndex(column));
                                if (!data.containsValue(genreName)) {
                                    data.put(MediaStore.Audio.GenresColumns.NAME, genreName);
                                }
                            }
                            dataList.add(data);
                        }
                        genreCursor.close();
                    }
                }

            } catch (RuntimeException ex) {
                Log.e(TAG_ERROR, "GenreLoader::loadData method exception");
                //Log.e(TAG_ERROR, ex.getMessage() );
            }

            return dataList;
        }

        @Override
        protected void onPostExecute(List<Map<String, Object>> data) {
            super.onPostExecute(data);
            m_result.success(data);
            m_result = null;
            m_resolver = null;
        }

        @Override
        protected String createMultipleValueSelectionArgs(String column, String[] params) {
            return null;
        }
    }
}