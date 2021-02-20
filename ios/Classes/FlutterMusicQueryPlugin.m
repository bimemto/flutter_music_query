#import "FlutterMusicQueryPlugin.h"
#if __has_include(<flutter_music_query/flutter_music_query-Swift.h>)
#import <flutter_music_query/flutter_music_query-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "flutter_music_query-Swift.h"
#endif

@implementation FlutterMusicQueryPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterMusicQueryPlugin registerWithRegistrar:registrar];
}
@end
