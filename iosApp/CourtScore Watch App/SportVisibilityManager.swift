import SwiftUI
import Combine

class SportVisibilityManager: ObservableObject {
    static let shared = SportVisibilityManager()

    @Published var enabledSports: Set<String> {
        didSet {
            UserDefaults.standard.set(Array(enabledSports), forKey: "enabledSports")
        }
    }

    private init() {
        if let stored = UserDefaults.standard.stringArray(forKey: "enabledSports") {
            self.enabledSports = Set(stored)
        } else {
            self.enabledSports = ["PADEL", "FOOTBALL"]
        }
    }

    func toggle(_ sport: String) {
        if enabledSports.contains(sport) {
            if enabledSports.count > 1 {
                enabledSports.remove(sport)
            }
        } else {
            enabledSports.insert(sport)
        }
    }

    func isEnabled(_ sport: String) -> Bool {
        enabledSports.contains(sport)
    }
}
