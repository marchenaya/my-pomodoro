import SwiftUI
import Shared

@main
struct iOSApp: App {
    init() {
        KoinIos.shared.initialize()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
