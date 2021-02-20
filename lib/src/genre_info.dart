part of flutter_music_query;

/// Class that holds genre metadata information.
class GenreInfo extends DataModel {
  GenreInfo._(Map<dynamic, dynamic> map) : super._(map);

  /// The genre name
  String get name => _data['name'];
}
