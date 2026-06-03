import SwiftUI
import Shared
import UserNotifications

class NotificationDelegate: NSObject, UNUserNotificationCenterDelegate {
    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification,
        withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void
    ) {
        completionHandler([.banner, .sound, .badge])
    }

    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse,
        withCompletionHandler completionHandler: @escaping () -> Void
    ) {
        if response.actionIdentifier == "next_step_action" {
            KoinIos.shared.startNextStep(completionHandler: completionHandler)
        } else {
            completionHandler()
        }
    }
}

let notificationDelegate = NotificationDelegate()

@main
struct iOSApp: App {
    init() {
        KoinIos.shared.initialize()
        requestNotificationPermission()
        registerNotificationCategories()
        UNUserNotificationCenter.current().delegate = notificationDelegate
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }

    private func requestNotificationPermission() {
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .badge, .sound]) { _, _ in }
    }

    private func registerNotificationCategories() {
        let nextStepAction = UNNotificationAction(
            identifier: "next_step_action",
            title: "Next Step",
            options: []
        )

        let timerCategory = UNNotificationCategory(
            identifier: "timer_category",
            actions: [nextStepAction],
            intentIdentifiers: [],
            options: []
        )

        UNUserNotificationCenter.current().setNotificationCategories([timerCategory])
    }
}
