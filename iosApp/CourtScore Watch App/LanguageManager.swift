import SwiftUI
import Combine

struct LanguageOption: Identifiable {
    let id = UUID()
    let name: String
    let code: String
    let flagView: AnyView
}

class LanguageManager: ObservableObject {
    static let shared = LanguageManager()

    @Published var selectedLanguageCode: String {
        didSet {
            UserDefaults.standard.set(selectedLanguageCode, forKey: "selectedLanguage")
            applyLanguage()
        }
    }

    private init() {
        self.selectedLanguageCode = UserDefaults.standard.string(forKey: "selectedLanguage") ?? "en"
        applyLanguage()
    }

    let languages: [LanguageOption] = [
        LanguageOption(name: NSLocalizedString("english", comment: ""), code: "en", flagView: AnyView(UKFlagView())),
        LanguageOption(name: NSLocalizedString("spanish", comment: ""), code: "es", flagView: AnyView(SpainFlagView())),
        LanguageOption(name: NSLocalizedString("catalan", comment: ""), code: "ca", flagView: AnyView(CataloniaFlagView()))
    ]

    var selectedLanguage: LanguageOption {
        languages.first { $0.code == selectedLanguageCode } ?? languages[0]
    }

    private func applyLanguage() {
        UserDefaults.standard.set([selectedLanguageCode], forKey: "AppleLanguages")
        UserDefaults.standard.synchronize()
    }
}

extension String {
    func localized() -> String {
        guard let path = Bundle.main.path(forResource: LanguageManager.shared.selectedLanguageCode, ofType: "lproj"),
              let bundle = Bundle(path: path) else {
            return NSLocalizedString(self, comment: "")
        }
        return NSLocalizedString(self, bundle: bundle, comment: "")
    }
}

